package capstone.library.services.impl;

import capstone.library.dtos.common.BorrowPolicyDto;
import capstone.library.dtos.request.ProfileUpdateReqDto;
import capstone.library.dtos.response.*;
import capstone.library.entities.*;
import capstone.library.enums.WishListStatus;
import capstone.library.exceptions.CustomException;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.mappers.BookBorrowingMapper;
import capstone.library.mappers.BorrowPolicyMapper;
import capstone.library.mappers.ExtendHistoryMapper;
import capstone.library.mappers.ProfileMapper;
import capstone.library.repositories.*;
import capstone.library.services.PatronService;
import capstone.library.util.constants.ConstantUtil;
import capstone.library.util.tools.DateTimeUtils;
import capstone.library.util.tools.OverdueBooksFinder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatronServiceImpl implements PatronService
{

    @Autowired
    private WishlistRepository wishlistRepository;
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

    @Override
    public boolean addWishlist(Integer bookId, Integer patronId)
    {
        if (bookId == null || patronId == null)
        {
            throw new MissingInputException("Missing input");
        }

        WishlistBook wishList = new WishlistBook();
        Book book = bookJpaRepository.getBookById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "Book with id: " + bookId + " not found"));
        Account patron = accountRepository.findById(patronId).orElseThrow(() -> new ResourceNotFoundException("Patron", "Patron with id: " + patronId + " not found"));

        if (patron != null && book != null)
        {
            wishList.setBook(book);
            wishList.setBorrower(patron);
            wishList.setStatus(WishListStatus.NOT_EMAIL_YET);
            wishList.setEmail(patron.getEmail());
            wishlistRepository.save(wishList);
            return true;
        }

        return false;
    }

    public ProfileAccountResDto getProfile(Integer patronId)
    {
        if (patronId == null)
        {
            throw new MissingInputException("Missing input");
        }

        return profileMapper.toResDto(profileRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron", "Patron with id: " + patronId + " not found")));

    }

    public boolean updateProfile(Integer patronId, ProfileUpdateReqDto newProfile)
    {
        if (newProfile == null || patronId == null)
        {
            throw new MissingInputException("Missing input");
        }

        ProfileAccountResDto oldProfile = profileMapper.toResDto(profileRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron", "Patron with id: " + patronId + " not found")));

        if (oldProfile != null)
        {
            oldProfile.setPhone(newProfile.getPhone());
            Profile prof = profileMapper.toEntity(oldProfile);
            profileRepository.saveAndFlush(prof);
            return true;
        }

        return false;
    }

    @Override
    public Page<ExtendHistoryResDto> getExtendHistories(Integer bookBorrowingId, Pageable pageable)
    {
        if (bookBorrowingId == null)
        {
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
    public boolean addNewExtendHistory(Integer bookBorrowingId, Integer librarianId, Integer numberOfDayToPlus)
    {
        if (bookBorrowingId == null)
        {
            throw new MissingInputException("Missing input");
        }

        //Get policy
        int policyId = 1;
        BorrowPolicyDto policy = borrowPolicyMapper.toDto(borrowPolicyRepository.findById(policyId).orElseThrow(() -> new ResourceNotFoundException("BorrowPolicy", "BorrowPolicy with Id " + policyId + " not found")));

        //Find and check if bookBorrowing exists
        BookBorrowing bookBorrowing = bookBorrowingRepository.findById(bookBorrowingId).orElseThrow(() -> new ResourceNotFoundException("BookBorrowing", "BookBorrowing with Id " + bookBorrowingId + " not found"));

        //If bookBorrowing exists
        if (bookBorrowing != null)
        {

            //Check if it is overdue
            if (bookBorrowing.getDueAt().isBefore(LocalDate.now()))
            {
                throw new CustomException(HttpStatus.BAD_REQUEST, ConstantUtil.EXCEPTION_POLICY_VIOLATION, "The requested item is overdue");
            }

            //if issued by Librarian get the librarian account, if not get the patron one
            Account isssuedBy =
                    librarianId != null
                            ? accountRepository.findById(librarianId).orElseThrow(() -> new ResourceNotFoundException("Account", "Librarian Account with Id " + librarianId + " not found"))
                            : bookBorrowing.getBorrower();

            //Get extendHistory if it exists
            ExtendHistory extendHistory = extendHistoryRepository.findFirstByBookBorrowing_IdOrderByDueAtDesc(bookBorrowing.getId())
                    .orElse(new ExtendHistory());

            //Extend only if index under max extend time
            if (extendHistory.getExtendIndex() < policy.getMaxExtendTime())
            {
                //If the bookBorrowing is extended for the first time, create the first history
                if (bookBorrowing.getExtendIndex() == 0)
                {
                    extendHistory.setBorrowedAt(bookBorrowing.getBorrowedAt());
                    extendHistory.setExtendIndex(bookBorrowing.getExtendIndex());
                    extendHistory.setDueAt(bookBorrowing.getDueAt());
                    extendHistory.setBookBorrowing(bookBorrowing);
                    extendHistory.setIssuedBy(bookBorrowing.getIssued_by());
                    extendHistory = extendHistoryRepository.saveAndFlush(extendHistory);
                }

                //Create new history -----
                ExtendHistory newExtendHistory = new ExtendHistory();

                newExtendHistory.setBorrowedAt(extendHistory.getBorrowedAt());
                newExtendHistory.setExtendedAt(LocalDateTime.now());
                newExtendHistory.setExtendIndex(extendHistory.getExtendIndex() + 1);
                newExtendHistory.setBookBorrowing(bookBorrowing);
                newExtendHistory.setIssuedBy(isssuedBy);

                //Get Day to Plus by request or policy. Default is policy
                newExtendHistory.setDueAt(extendHistory.getDueAt().plusDays(numberOfDayToPlus != null ? numberOfDayToPlus : policy.getExtendDueDuration()));

                //------------------------ Create new history END

                //Update bookBorrowing
                bookBorrowing.setExtendedAt(newExtendHistory.getExtendedAt());
                bookBorrowing.setExtendIndex(newExtendHistory.getExtendIndex());
                bookBorrowing.setDueAt(newExtendHistory.getDueAt());

                extendHistoryRepository.save(newExtendHistory);
                bookBorrowingRepository.save(bookBorrowing);
                return true;
            } else
            {
                throw new CustomException(HttpStatus.BAD_REQUEST, ConstantUtil.EXCEPTION_POLICY_VIOLATION, "Reached the limit for extension");
            }
        }

        return false;
    }

    @Override
    public Page<BookBorrowingResDto> getBorrowingHistories(Integer patronId, Pageable pageable)
    {
        if (patronId == null)
        {
            throw new MissingInputException("Missing input");
        }
        Page<BookBorrowing> histories = bookBorrowingRepository
                .findAllByBorrower_Id(patronId, pageable);
        return new PageImpl<>(histories
                .stream()
                .map(bookBorrowing -> bookBorrowingMapper.toDto(bookBorrowing))
                .collect(Collectors.toList()), pageable, histories.getTotalElements());
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
        for (BookCopy bookCopy : overdueBooks)
        {
            ReturnBookResponseDto dto = new ReturnBookResponseDto();
            dto.setRfid(bookCopy.getRfid());
            dto.setEdition(bookCopy.getBook().getEdition());
            dto.setTitle(bookCopy.getBook().getTitle());
            dto.setSubtitle(bookCopy.getBook().getSubtitle());
            dto.setAuthors(bookCopy.getBook().getBookAuthors().
                    toString().replace("]", "").replace("[", ""));
            dto.setIsbn(bookCopy.getBook().getIsbn());
            Optional<BookBorrowing> bookBorrowingOptional =
                    bookBorrowingRepository.findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(bookCopy.getId());
            if (bookBorrowingOptional.isPresent())
            {
                BookBorrowing bookBorrowing = bookBorrowingOptional.get();
                int overdueDays = (int) dateTimeUtils.getOverdueDays(now, bookBorrowing.getDueAt());
                if (overdueDays > 0)
                {
                    dto.setOverdueDays(overdueDays);
                    dto.setOverdue(true);
                    //Calculate fine
                    Optional<FeePolicy> feePolicyOptional = feePolicyRepository.findById(bookBorrowing.getFeePolicy().getId());
                    if (feePolicyOptional.isPresent())
                    {
                        double fineRate;
                        double fine = 0;
                        double bookCopyPrice = bookCopy.getPrice();
                        fineRate = feePolicyOptional.get().getOverdueFinePerDay();
                        fine = fineRate * overdueDays;
                        int maxOverdueFinePercentage = feePolicyOptional.get().getMaxPercentageOverdueFine();
                        double maxOverdueFine = bookCopyPrice * ((double) maxOverdueFinePercentage / 100);
                        if (fine >= maxOverdueFine)
                        {
                            fine = maxOverdueFine;
                        }
                        dto.setFine(fine);
                        dto.setReason("Return late: " + overdueDays + " (days)");
                    }
                }
                dto.setBorrowedAt(dateTimeUtils.convertDateTimeToString(bookBorrowing.getBorrowedAt()));
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

}
