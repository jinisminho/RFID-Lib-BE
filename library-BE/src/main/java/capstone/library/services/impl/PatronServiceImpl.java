package capstone.library.services.impl;

import capstone.library.dtos.common.MyBookDto;
import capstone.library.dtos.request.ProfileUpdateReqDto;
import capstone.library.dtos.response.*;
import capstone.library.entities.*;
import capstone.library.enums.BorrowingStatus;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.mappers.BookBorrowingMapper;
import capstone.library.mappers.BorrowPolicyMapper;
import capstone.library.mappers.ExtendHistoryMapper;
import capstone.library.mappers.ProfileMapper;
import capstone.library.repositories.*;
import capstone.library.services.PatronService;
import capstone.library.util.tools.CommonUtil;
import capstone.library.util.tools.DateTimeUtils;
import capstone.library.util.tools.OverdueBooksFinder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatronServiceImpl implements PatronService {

    @Autowired
    private BookJpaRepository bookJpaRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ExtendHistoryRepository extendHistoryRepository;
    @Autowired
    private BookBorrowingRepository bookBorrowingRepository;
    @Autowired
    private BorrowPolicyRepository borrowPolicyRepository;
    @Autowired
    private BookBorrowingMapper bookBorrowingMapper;
    @Autowired
    private ProfileMapper profileMapper;
    @Autowired
    private ExtendHistoryMapper extendHistoryMapper;
    @Autowired
    private BorrowPolicyMapper borrowPolicyMapper;
    @Autowired
    private OverdueBooksFinder overdueBooksFinder;
    @Autowired
    FeePolicyRepository feePolicyRepository;
    @Autowired
    ObjectMapper objectMapper;
    DateTimeUtils dateTimeUtils = new DateTimeUtils();

    private static final String EMAIL_DEFAULT = "test@test.com";
    private static final String PATRON_NOT_FOUND = "Cannot find patron";
    private static final String NOT_PATRON = "This is not a patron account";


    public ProfileAccountResDto getProfile(Integer id) {
        if (id == null) {
            throw new MissingInputException("Missing input");
        }

        return profileMapper.toResDto(profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "Profile with id: " + id + " not found")));

    }

    public boolean updateProfile(Integer patronId, ProfileUpdateReqDto newProfile) {
        if (newProfile == null || patronId == null) {
            throw new MissingInputException("Missing input");
        }

        ProfileAccountResDto oldProfile = profileMapper.toResDto(profileRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron", "Patron with id: " + patronId + " not found")));

        if (oldProfile != null) {
            oldProfile.setPhone(newProfile.getPhone());
            Profile prof = profileMapper.toEntity(oldProfile);
            profileRepository.saveAndFlush(prof);
            return true;
        }

        return false;
    }

    @Override
    public Page<ExtendHistoryResDto> getExtendHistories(Integer bookBorrowingId, Pageable pageable) {
        if (bookBorrowingId == null) {
            throw new MissingInputException("Missing input");
        }

        Page<ExtendHistory> histories = extendHistoryRepository
                .findAllByBookBorrowing_IdOrderByDueAtAsc(bookBorrowingRepository.findById(bookBorrowingId)
                        .orElseThrow(() -> new ResourceNotFoundException("BookBorrowing", "BookBorrowing with Id: " + bookBorrowingId + " not found"))
                        .getId(), pageable);

        return new PageImpl<ExtendHistoryResDto>(histories
                .stream()
                .map(extendHistory -> extendHistoryMapper.toResDto(extendHistory))
                .collect(Collectors.toList()), pageable, histories.getTotalElements());
    }

    @Override
    public BookBorrowingsResDto getBorrowingHistories(Integer patronId, Pageable pageable) {
        if (patronId == null) {
            throw new MissingInputException("Missing input");
        }
        BookBorrowingsResDto res = new BookBorrowingsResDto();
        res.setBorrowing(this.getBorrowingHistoriesWithStatus(patronId, pageable, BorrowingStatus.BORROWING));
        res.setReturned(this.getBorrowingHistoriesWithStatus(patronId, pageable, BorrowingStatus.RETURNED));
        res.setOverdued(this.getBorrowingHistoriesWithStatus(patronId, pageable, BorrowingStatus.OVERDUED));

        return res;
    }

    @Override
    public Page<BookBorrowingResDto> getBorrowingHistoriesWithStatus(Integer patronId, Pageable pageable, BorrowingStatus status) {
        if (patronId == null) {
            throw new MissingInputException("Missing input");
        }

        Page<BookBorrowing> histories;

        switch (status) {
            case RETURNED:
                histories = bookBorrowingRepository.findAllByBorrowerIdAndReturnedAtIsNotNull(patronId, pageable);
                break;
            case BORROWING:
                histories = bookBorrowingRepository.findAllByBorrowerIdAndReturnedAtIsNullAndDueAtFromCurrentDateOnward(patronId, pageable);
                break;
            case OVERDUED:
                histories = bookBorrowingRepository.findAllByBorrowerIdAndReturnedAtIsNullAndDueAtBeforeCurrentDate(patronId, pageable);
                break;
            default:
                histories = new PageImpl<>(new ArrayList<>(), pageable, 0);
                break;
        }
        List<BookBorrowingResDto> list = histories
                .stream()
                .map(bookBorrowing -> bookBorrowingMapper.toResDtoWithoutBookBorrowingsInBorrowing(bookBorrowing))
                .collect(Collectors.toList());
        list.forEach(el -> setFineAndOverdueDays(el));
        return new PageImpl<>(list, pageable, histories.getTotalElements());
    }

    private void setFineAndOverdueDays(BookBorrowingResDto dto) {
        long overdueDays = DateTimeUtils.getOverdueDaysStatic(LocalDate.now(), dto.getDueAt());
        Double fine = CommonUtil.fineCalc(dto.getFeePolicy(), dto.getBookCopy().getPrice(), ((int) (overdueDays)));
        dto.setOverdueDays(overdueDays > 0 ? ((int) (overdueDays)) : 0);
        dto.setFine(fine > 0 ? fine : 0);
    }

    @Override
    public PatronCheckoutInfoResponseDto getCheckoutAccountByRfid(String rfid) {
        PatronCheckoutInfoResponseDto response = new PatronCheckoutInfoResponseDto();

        /*Get patron*/
        Optional<Account> patronOptional = accountRepository.findByRfid(rfid);
        if (patronOptional.isEmpty()) {
            throw new ResourceNotFoundException("Patron", PATRON_NOT_FOUND);
        }
        Account patron = patronOptional.get();
        if (patron.getPatronType() == null) {
            throw new ResourceNotFoundException("Patron", NOT_PATRON);
        }
        /*=========*/

        /*Get overdue books*/
        LocalDate now = LocalDate.now();
        List<BookCopy> overdueBooks = overdueBooksFinder.findOverdueBookCopiesByPatronId(patron.getId());
        List<ReturnBookResponseDto> dtos = new ArrayList<>();
        for (BookCopy bookCopy : overdueBooks) {
            ReturnBookResponseDto dto = new ReturnBookResponseDto();
            dto.setRfid(bookCopy.getRfid());
            dto.setBook(objectMapper.convertValue(bookCopy.getBook(), MyBookDto.class));
            dto.getBook().setAuthors(bookCopy.getBook().getBookAuthors().toString().
                    replace("]", "").replace("[", ""));
            dto.getBook().setGenres(bookCopy.getBook().getBookGenres().toString().
                    replace("]", "").replace("[", ""));
            Optional<BookBorrowing> bookBorrowingOptional =
                    bookBorrowingRepository.findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(bookCopy.getId());
            if (bookBorrowingOptional.isPresent()) {
                BookBorrowing bookBorrowing = bookBorrowingOptional.get();
                int overdueDays = (int) dateTimeUtils.getOverdueDays(now, bookBorrowing.getDueAt());
                if (overdueDays > 0) {
                    dto.setOverdueDays(overdueDays);
                    dto.setOverdue(true);
                    //Calculate fine
                    Optional<FeePolicy> feePolicyOptional = feePolicyRepository.findById(bookBorrowing.getFeePolicy().getId());
                    if (feePolicyOptional.isPresent()) {
                        double fineRate;
                        double fine = 0;
                        double bookCopyPrice = bookCopy.getPrice();
                        fineRate = feePolicyOptional.get().getOverdueFinePerDay();
                        fine = fineRate * overdueDays;
                        int maxOverdueFinePercentage = feePolicyOptional.get().getMaxPercentageOverdueFine();
                        double maxOverdueFine = bookCopyPrice * ((double) maxOverdueFinePercentage / 100);
                        if (fine >= maxOverdueFine) {
                            fine = maxOverdueFine;
                        }
                        dto.setFine(fine);
                        dto.setReason("Return late: " + overdueDays + " (days)");
                    }
                }
                /*Hoang*/
                dto.setBorrowedAt(dateTimeUtils.convertDateTimeToString(bookBorrowing.getBorrowing().getBorrowedAt()));
                /*========*/
                dto.setDueDate(bookBorrowing.getDueAt().toString());
            }
            dto.setBookPrice(bookCopy.getPrice());
            dtos.add(dto);
        }
        /*================*/

        /*Prepare response*/
        response.setPatronAccountInfo(objectMapper.convertValue(patron, AccountDetailResponseDto.class));
        response.setOverdueBooks(dtos);
        /*================*/
        return response;

    }

    @Override
    public ProfileAccountResDto findProfileByRfidOrEmail(String searchValue) {
        return profileMapper.toResDto(profileRepository.findByAccount_EmailOrAccount_Rfid(searchValue, searchValue)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "Profile with RFID/EMAIL[" + searchValue + "] not found")));
    }

}
