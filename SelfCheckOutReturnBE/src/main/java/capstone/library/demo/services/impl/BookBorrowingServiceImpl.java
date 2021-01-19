package capstone.library.demo.services.impl;

import capstone.library.demo.dtos.response.BookCheckOutResponse;
import capstone.library.demo.dtos.response.BookReturnResponse;
import capstone.library.demo.entities.Account;
import capstone.library.demo.entities.BookBorrowing;
import capstone.library.demo.entities.BookCopy;
import capstone.library.demo.entities.BorrowPolicy;
import capstone.library.demo.enums.BookCopyStatus;
import capstone.library.demo.enums.BookReturnStatus;
import capstone.library.demo.enums.BookStatus;
import capstone.library.demo.exceptions.ResourceNotFoundException;
import capstone.library.demo.repositories.AccountRepository;
import capstone.library.demo.repositories.BookBorrowingRepository;
import capstone.library.demo.repositories.BookCopyRepository;
import capstone.library.demo.repositories.BorrowPolicyRepository;
import capstone.library.demo.services.BookBorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    @Transactional
    public List<BookCheckOutResponse> checkout(int patronId, List<String> BookCodeList) {
        List<BookCheckOutResponse> rs = new ArrayList<>();
        Account patron = accountRepo.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron with id " + patronId + " not found"));
        BorrowPolicy policy = borrowPolicyRepo.findById(1)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find the policy"));

        for (String code : BookCodeList){
            BookCopy copy = bookCopyRepo.findByRfid(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Book with rfid: " + code + " not found" ));
            BookCheckOutResponse bookResponse = new BookCheckOutResponse();
            bookResponse.setRfid(code);
            bookResponse.setTitle(copy.getBook().getTitle());
            //if this copy able to borrow: set bookCopy status -> borrow; add bookBorrowing
            if(copy.getStatus() == BookCopyStatus.AVAILABLE){
                LocalDate dueDate = LocalDate.now().plusDays(policy.getDueDuration());
                copy.setStatus(BookCopyStatus.BORROWED);
                bookResponse.setAbleToBorrow(true);
                bookResponse.setDueDate(dueDate.toString());
                BookBorrowing borrowing = new BookBorrowing();
                borrowing.setBorrower(patron);
                borrowing.setBorrowedAt(LocalDateTime.now());
                borrowing.setDueAt(dueDate);
                borrowing.setExtendIndex(0);
                borrowing.setIssued_by(patron);
                borrowing.setBookCopy(copy);
                bookBorrowingRepo.save(borrowing);
            }else{
                bookResponse.setAbleToBorrow(false);
                bookResponse.setDueDate("");
            }
            rs.add(bookResponse);
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
            BookReturnResponse bookResponse = new BookReturnResponse();
            bookResponse.setRfid(code);
            bookResponse.setTitle(copy.getBook().getTitle());
            if(copy.getStatus() != BookCopyStatus.BORROWED){
                bookResponse.setStatus(BookReturnStatus.INVALID);
            }else{
                BookBorrowing borrowing = bookBorrowingRepo.findBorrowedTransactionByBookCopyId(copy.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(("Cannot find borrowing transaction with book id" + copy.getId())));
                //check if over due ko cho tra
                if(LocalDate.now().isAfter(borrowing.getDueAt())){
                    bookResponse.setStatus(BookReturnStatus.OVERDUE);
                }
                else{ //cho tra
                    BookStatus bookStatus = copy.getBook().getStatus();
                    if(bookStatus == BookStatus.OUT_OF_CIRCULATION){
                        copy.setStatus(BookCopyStatus.OUT_OF_CIRCULATION);
                    }else if (bookStatus == BookStatus.NOT_ALLOWED_TO_BORROWED){
                        copy.setStatus(BookCopyStatus.NOT_ALLOWED_TO_BORROWED);
                    }else{
                        copy.setStatus(BookCopyStatus.AVAILABLE);
                    }
                    bookResponse.setStatus(BookReturnStatus.RETURNED);
                    borrowing.setReturn_by(borrowing.getBorrower());
                    borrowing.setReturnedAt(LocalDateTime.now());
                }
            }
            rs.add(bookResponse);
        }
        return rs;
    }
}
