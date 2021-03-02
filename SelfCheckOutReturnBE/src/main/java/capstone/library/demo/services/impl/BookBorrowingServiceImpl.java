package capstone.library.demo.services.impl;

import capstone.library.demo.dtos.request.CheckOutBookRequest;
import capstone.library.demo.dtos.response.BookCheckOutResponse;
import capstone.library.demo.dtos.response.BookReturnResponse;
import capstone.library.demo.dtos.support.BookGroup;
import capstone.library.demo.entities.*;
import capstone.library.demo.enums.BookCopyStatus;
import capstone.library.demo.enums.BookReturnStatus;
import capstone.library.demo.enums.BookStatus;
import capstone.library.demo.exceptions.InvalidPolicyException;
import capstone.library.demo.exceptions.MissingInputException;
import capstone.library.demo.exceptions.ResourceNotFoundException;
import capstone.library.demo.repositories.*;
import capstone.library.demo.services.BookBorrowingService;
import capstone.library.demo.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class BookBorrowingServiceImpl implements BookBorrowingService {

    @Autowired
    AccountRepository accountRepo;

    @Autowired
    BookCopyRepository bookCopyRepo;

    @Autowired
    BookBorrowingRepository bookBorrowingRepo;

    @Autowired
    BorrowPolicyRepository borrowPolicyRepo;

    @Autowired
    PatronTypeRepository patronTypeRepo;

    @Autowired
    FeePolicyRepository feePolicyRepo;

    @Autowired
    BorrowingRepository borrowingRepo;

    @Override
    @Transactional
    public List<BookCheckOutResponse> checkout(int patronId, List<CheckOutBookRequest> bookCodeList) {
        String checkValid = checkPolicyBeforeCheckOut(patronId, bookCodeList);
        if(!checkValid.equals("")){
            throw new InvalidPolicyException(checkValid);
        }
        if(bookCodeList == null){
            throw new MissingInputException("bookCodeList in checkout function is missing");
        }
        List<BookCheckOutResponse> rs = new ArrayList<>();
        Account patron = accountRepo.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron with id " + patronId + " not found"));

        LocalDateTime curDateTime = LocalDateTime.now();
        LocalDate curDate = LocalDate.now();

        Borrowing borrowing = new Borrowing();
        borrowing.setBorrower(patron);
        borrowing.setBorrowedAt(curDateTime);

        for (CheckOutBookRequest book : bookCodeList){
            BookCopy copy = bookCopyRepo.findByRfid(book.getRfid())
                    .orElseThrow(() -> new ResourceNotFoundException("Book with rfid: " + book.getRfid() + " not found" ));
            Optional<BorrowPolicy> policyOptional = borrowPolicyRepo
                    .findByPatronTypeIdAndBookCopyTypeId(patron.getPatronType().getId(), copy.getBookCopyType().getId());
            BookCheckOutResponse dto = mapFromCopyToCheckOutBasically(copy);

            if(policyOptional.isPresent()){
                //if this copy able to borrow: set bookCopy status -> borrow; add bookBorrowing
                if(copy.getStatus() == BookCopyStatus.AVAILABLE){
                    LocalDate dueDate = curDate.plusDays(policyOptional.get().getDueDuration());
                /*overdue days excludes saturdays and sundays
	                    eg: If due date is on Sunday then when return on Monday, overdue days = 0*/
                    while (dueDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || dueDate.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                    {
                        dueDate = dueDate.plusDays(1);
                    }
                    copy.setStatus(BookCopyStatus.BORROWED);
                    dto.setAbleToBorrow(true);
                    dto.setDueDate(DateTimeUtil.convertDateToString(dueDate));
                    dto.setBorrowedAt(DateTimeUtil.convertDateTimeToString(curDateTime));

                    //get fee policy
                    FeePolicy feePolicy = feePolicyRepo.findAllByOrderByCreatedAtDesc()
                            .stream()
                            .findFirst()
                            .orElseThrow(() -> new InvalidPolicyException("Fee policy not fount"));

                    BookBorrowing borrowingDetail = new BookBorrowing();
                    borrowingDetail.setDueAt(dueDate);
                    borrowingDetail.setIssued_by(patron);
                    borrowingDetail.setBookCopy(copy);
                    borrowingDetail.setFeePolicy(feePolicy);
                    borrowingDetail.setNote("");
                    borrowingDetail.setBorrowing(borrowing);
                    bookBorrowingRepo.save(borrowingDetail);
                }else{
                    dto.setAbleToBorrow(false);
                    dto.setDueDate("");
                }
            }else{
                dto.setAbleToBorrow(false);
                dto.setDueDate("");
            }
            rs.add(dto);
        }
        return rs;
    }

    @Override
    @Transactional
    public List<BookReturnResponse> returnBookByBatch (List<String> bookCodeList) {
        if(bookCodeList == null){
            throw new MissingInputException("bookCodeList is missing");
        }
        LocalDateTime curDateTime = LocalDateTime.now();
        List<BookReturnResponse> rs = new ArrayList<>();
        for (String code : bookCodeList){
            BookReturnResponse dto = returnBookOneByOne(code, curDateTime);
            rs.add(dto);
        }
        return rs;
    }

    @Override
    @Transactional
    public BookReturnResponse returnBookOneByOne (String rfidCode, LocalDateTime curDateTime){
        if(curDateTime == null){
            throw new MissingInputException("curDateTime is missing");
        }
        BookCopy copy = bookCopyRepo.findByRfid(rfidCode)
                .orElseThrow(() -> new ResourceNotFoundException("Book with rfid: " + rfidCode + " not found" ));

        BookReturnResponse dto = mapFromCopyToReturnBasically(copy);

        if(copy.getStatus() != BookCopyStatus.BORROWED){
            dto.setStatus(BookReturnStatus.INVALID);
        }else{
            BookBorrowing bookBorrowing = bookBorrowingRepo.findBorrowedTransactionByBookCopyId(copy.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(("Cannot find bookBorrowing transaction with book id" + copy.getId())));
            dto.setPatron(bookBorrowing.getBorrowing().getBorrower().getEmail());
            long overdueDays = (int) DateTimeUtil.getOverdueDays(LocalDate.now(), bookBorrowing.getDueAt());
            //check if overdue : not allow to return
            if(overdueDays > 0){
                FeePolicy feePolicy = bookBorrowing.getFeePolicy();

                double fine = overdueDays * feePolicy.getOverdueFinePerDay();
                double maxFine = copy.getPrice() * feePolicy.getMaxPercentageOverdueFine() / 100;
                if(fine >= maxFine){
                    fine = maxFine;
                }
                dto.setOverdueDay((int) overdueDays);
                dto.setFine(fine);
                dto.setStatus(BookReturnStatus.OVERDUE);
            }
            else{
                BookStatus bookStatus = copy.getBook().getStatus();
                if(bookStatus == BookStatus.OUT_OF_CIRCULATION){
                    copy.setStatus(BookCopyStatus.OUT_OF_CIRCULATION);
                }else if (bookStatus == BookStatus.DISCARD){
                    copy.setStatus(BookCopyStatus.DISCARD);
                }else if (bookStatus == BookStatus.LIB_USE_ONLY){
                    copy.setStatus(BookCopyStatus.LIB_USE_ONLY);
                }else if (bookStatus == BookStatus.IN_CIRCULATION){
                    copy.setStatus(BookCopyStatus.AVAILABLE);
                }
                bookBorrowing.setReturn_by(bookBorrowing.getBorrowing().getBorrower());
                bookBorrowing.setReturnedAt(curDateTime);
                dto.setReturnedAt(DateTimeUtil.convertDateTimeToString(curDateTime));
                dto.setStatus(BookReturnStatus.RETURNED);
            }
        }
        return dto;
    }

    private BookReturnResponse mapFromCopyToReturnBasically(BookCopy copy){
        if(copy == null){
            throw new MissingInputException("copy is missing");
        }
        BookReturnResponse dto = new BookReturnResponse();
        dto.setRfid(copy.getRfid());
        dto.setTitle(copy.getBook().getTitle());
        dto.setSubtitle(copy.getBook().getSubtitle());
        String authors = copy.getBook().getBookAuthors()
                .stream()
                .map(a -> a.getAuthor().getName())
                .collect(Collectors.joining(", "));

        String genres = copy.getBook().getBookGenres()
                .stream()
                .map(g -> g.getGenre().getName())
                .collect(Collectors.joining(","));

        dto.setAuthors(authors);
        dto.setEdition(copy.getBook().getEdition());
        dto.setGroup(copy.getBookCopyType().getName());
        dto.setImg(copy.getBook().getImg());
        dto.setGenres(genres);
        return dto;
    }

    private BookCheckOutResponse mapFromCopyToCheckOutBasically(BookCopy copy){
        if(copy == null){
            throw new MissingInputException("copy is missing");
        }
        BookCheckOutResponse dto = new BookCheckOutResponse();
        dto.setRfid(copy.getRfid());
        dto.setTitle(copy.getBook().getTitle());
        dto.setSubtitle(copy.getBook().getSubtitle());
        String authors = copy.getBook().getBookAuthors()
                .stream()
                .map(a -> a.getAuthor().getName())
                .collect(Collectors.joining(", "));

        String genres = copy.getBook().getBookGenres()
                .stream()
                .map(g -> g.getGenre().getName())
                .collect(Collectors.joining(","));

        dto.setAuthors(authors);
        dto.setEdition(copy.getBook().getEdition());
        dto.setGroup(copy.getBookCopyType().getName());
        dto.setImg(copy.getBook().getImg());
        dto.setGenres(genres);
        return dto;
    }

    private String checkPolicyBeforeCheckOut(int patronId, List<CheckOutBookRequest> bookCodeList){
        AtomicReference<String> msg = new AtomicReference<>("");
        if(bookCodeList == null){
            throw new MissingInputException("bookCodeList in checkout function is missing");
        }
        List<BookCheckOutResponse> rs = new ArrayList<>();
        Account patron = accountRepo.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron with id " + patronId + " not found"));
        PatronType patronType = patronTypeRepo.findById(patron.getPatronType().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find the policy"));

        //books a patron are borrowing
        List<BookBorrowing> holdingBooks = bookBorrowingRepo
                .findByBorrowerIdAndReturnedAtIsNullAndLostAtIsNull(patronId);

        int numberOfHoldingAndWantToBorrow = bookCodeList.size() + holdingBooks.size();
        if(numberOfHoldingAndWantToBorrow > patronType.getMaxNumberCopyBorrow()){
            throw new InvalidPolicyException
                    ("[Over Total Limit] Checkout: " + bookCodeList.size()+ "; Holding: " + holdingBooks.size() + "; Total Limit: " +  patronType.getMaxNumberCopyBorrow() + "\n");
        }

        Map<Integer, Integer> holdingBookMap = new HashMap<>();
        for(BookBorrowing book: holdingBooks){
            int bookCopyTypeId = book.getBookCopy().getBookCopyType().getId();
            int count =  1;
            if(holdingBookMap.containsKey(bookCopyTypeId)){
                count = holdingBookMap.get(bookCopyTypeId) + 1;
            }
            holdingBookMap.put(bookCopyTypeId, count);
        }

        Map<Integer, BookGroup> bookGroupMap = new HashMap<>();
        for (CheckOutBookRequest book :  bookCodeList){
            int count = 1;
            if(bookGroupMap.containsKey(book.getGroupId())){
                count = bookGroupMap.get(book.getGroupId()).getCount() + 1;
            }
            bookGroupMap.put(book.getGroupId(), new BookGroup(book.getGroup(), count));
            //check duplicate
            BookBorrowing tmp = holdingBooks
                    .stream()
                    .filter(b -> b.getBookCopy().getBook().getId() == book.getBookId())
                    .findFirst()
                    .orElse(null);
            if(tmp != null){
                msg.set(msg + "[Duplicate] Holding: "  + tmp.getBookCopy().getBook().getTitle() + "\n");
            }
        }

        bookGroupMap.forEach(
                (k, v) -> {
                    System.out.println(k + "-" + patron.getPatronType().getId());
                    Optional<BorrowPolicy> policyOpt = borrowPolicyRepo
                            .findByPatronTypeIdAndBookCopyTypeId( patron.getPatronType().getId(),k);
                    if(policyOpt.isPresent()){
                        int maxBorrowNumber = policyOpt.get().getMaxNumberCopyBorrow();
                        int holdingBookCount = holdingBookMap.get(k) == null ? 0 : holdingBookMap.get(k);
                        int holdingAndWillCount = holdingBookCount + v.getCount();
                        if(holdingAndWillCount > maxBorrowNumber){
                            String tmp = msg.get();
                            msg.set(tmp + "[Over " + v.getGroup()+ " Limit] Checkout: " + v.getCount() + "; Holding: " + holdingBookCount+ "; Limit: " + maxBorrowNumber + "\n");
                        }
                    }
                }
        );

        return msg.get();

    }
}
