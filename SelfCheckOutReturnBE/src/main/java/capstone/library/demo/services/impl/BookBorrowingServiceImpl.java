package capstone.library.demo.services.impl;

import capstone.library.demo.dtos.response.BookCheckOutResponse;
import capstone.library.demo.entities.Account;
import capstone.library.demo.entities.BookBorrowing;
import capstone.library.demo.entities.BookCopy;
import capstone.library.demo.entities.BorrowPolicy;
import capstone.library.demo.enums.BookCopyStatus;
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
}
