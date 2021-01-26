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


        for (CheckOutBookRequest book : bookCodeList){
            BookCopy copy = bookCopyRepo.findByRfid(book.getRfid())
                    .orElseThrow(() -> new ResourceNotFoundException("Book with rfid: " + book.getRfid() + " not found" ));
            BorrowPolicy policy = borrowPolicyRepo.findByPatronTypeIdAndBookCopyTypeId(patron.getPatronType().getId(), copy.getBookCopyType().getId())
                    .orElseThrow(() -> new InvalidPolicyException
                            (patron.getPatronType().getName() + " not allowed to borrow books in " + copy.getBookCopyType().getName()));

            BookCheckOutResponse dto = mapFromCopyToCheckOutBasically(copy);
            //if this copy able to borrow: set bookCopy status -> borrow; add bookBorrowing
            if(copy.getStatus() == BookCopyStatus.AVAILABLE){
                LocalDateTime now = LocalDateTime.now();
                LocalDate dueDate = LocalDate.now().plusDays(policy.getDueDuration());
                copy.setStatus(BookCopyStatus.BORROWED);
                dto.setAbleToBorrow(true);
                dto.setDueDate(dueDate.toString());
                dto.setBorrowedAt(DateTimeUtil.convertDateTimeToString(now));
                BookBorrowing borrowing = new BookBorrowing();
                borrowing.setBorrower(patron);
                borrowing.setBorrowedAt(now);
                borrowing.setDueAt(dueDate);
                borrowing.setIssued_by(patron);
                borrowing.setBookCopy(copy);
                bookBorrowingRepo.save(borrowing);
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
    public List<BookReturnResponse> returnBook(List<String> bookCodeList) {
        List<BookReturnResponse> rs = new ArrayList<>();
        for (String code : bookCodeList){
            BookCopy copy = bookCopyRepo.findByRfid(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Book with rfid: " + code + " not found" ));

            BookReturnResponse dto = mapFromCopyToReturnBasically(copy);

            if(copy.getStatus() != BookCopyStatus.BORROWED){
                dto.setStatus(BookReturnStatus.INVALID);
            }else{
                BookBorrowing borrowing = bookBorrowingRepo.findBorrowedTransactionByBookCopyId(copy.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(("Cannot find borrowing transaction with book id" + copy.getId())));

                int overdueDays = LocalDate.now().compareTo(borrowing.getDueAt());
                //check if overdue : not allow to return
                if(overdueDays > 0){
                    BorrowPolicy policy = borrowPolicyRepo
                            .findByPatronTypeIdAndBookCopyTypeId(borrowing.getBorrower().getPatronType().getId(),
                                    copy.getBookCopyType().getId())
                            .orElseThrow(() -> new InvalidPolicyException("Cannot find policy"));

                    double fine = overdueDays * policy.getOverdueFinePerDay();
                    double bookPrice = copy.getPrice();
                    if(fine >= bookPrice){
                        fine = bookPrice;
                    }
                    dto.setOverdueDay(overdueDays);
                    dto.setFine(fine);
                    dto.setStatus(BookReturnStatus.OVERDUE);
                }
                else{
                    BookStatus bookStatus = copy.getBook().getStatus();
                    if(bookStatus == BookStatus.OUT_OF_CIRCULATION){
                        copy.setStatus(BookCopyStatus.OUT_OF_CIRCULATION);
                    }else{
                        copy.setStatus(BookCopyStatus.AVAILABLE);
                    }
                    borrowing.setReturn_by(borrowing.getBorrower());
                    LocalDateTime now = LocalDateTime.now();
                    borrowing.setReturnedAt(now);
                    dto.setReturnedAt(DateTimeUtil.convertDateTimeToString(now));
                    dto.setStatus(BookReturnStatus.RETURNED);
                }
            }
            rs.add(dto);
        }
        return rs;
    }

    private BookReturnResponse mapFromCopyToReturnBasically(BookCopy copy){
        BookReturnResponse dto = new BookReturnResponse();
        dto.setRfid(copy.getRfid());
        dto.setTitle(copy.getBook().getTitle());
        dto.setSubtitle(copy.getBook().getSubtitle());
        String authors = copy.getBook().getBookAuthors()
                .stream()
                .map(a -> a.getAuthor().getName())
                .collect(Collectors.joining(", "));
        dto.setAuthors(authors);
        dto.setEdition(copy.getBook().getEdition());
        dto.setGroup(copy.getBookCopyType().getName());
        dto.setImg(copy.getBook().getImg());
        return dto;
    }

    private BookCheckOutResponse mapFromCopyToCheckOutBasically(BookCopy copy){
        BookCheckOutResponse dto = new BookCheckOutResponse();
        dto.setRfid(copy.getRfid());
        dto.setTitle(copy.getBook().getTitle());
        dto.setSubtitle(copy.getBook().getSubtitle());
        String authors = copy.getBook().getBookAuthors()
                .stream()
                .map(a -> a.getAuthor().getName())
                .collect(Collectors.joining(", "));
        dto.setAuthors(authors);
        dto.setEdition(copy.getBook().getEdition());
        dto.setGroup(copy.getBookCopyType().getName());
        dto.setImg(copy.getBook().getImg());
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

        if(bookCodeList.size() > patronType.getMaxNumberCopyBorrow()){
            throw new InvalidPolicyException
                    ("You're not allowed to borrow more than " + patronType.getMaxNumberCopyBorrow() + " books");
        }

        Map<Integer, BookGroup> bookGroupMap = new HashMap<>();
        for (CheckOutBookRequest book :  bookCodeList){
            int count = 1;
            if(bookGroupMap.containsKey(book.getGroupId())){
                count = bookGroupMap.get(book.getGroupId()).getCount() + 1;
            }
            bookGroupMap.put(book.getGroupId(), new BookGroup(book.getGroup(), count));
        }

        bookGroupMap.forEach(
                (k, v) -> {
                    System.out.println(k + "-" + patron.getPatronType().getId());
                    Optional<BorrowPolicy> policyOpt = borrowPolicyRepo
                            .findByPatronTypeIdAndBookCopyTypeId( patron.getPatronType().getId(),k);
                    if(policyOpt.isPresent()){
                        int maxBorrowNumber = policyOpt.get().getMaxNumberCopyBorrow();
                        if(v.getCount() > maxBorrowNumber){
                            String tmp = msg.get();
                            msg.set(tmp + " " + v.getGroup() + ": limit " + maxBorrowNumber + " books;");
                        }
                    }else{
                        msg.set(msg + " " + v.getGroup() + ": not allow to borrow;");
                    }

                }
        );

        return msg.get();

    }
}
