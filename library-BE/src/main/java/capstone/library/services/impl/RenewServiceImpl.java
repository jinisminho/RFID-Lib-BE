package capstone.library.services.impl;

import capstone.library.dtos.response.ValidateRenewDto;
import capstone.library.entities.Account;
import capstone.library.entities.BookBorrowing;
import capstone.library.entities.BookCopy;
import capstone.library.entities.BorrowPolicy;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.BookBorrowingRepository;
import capstone.library.repositories.BorrowPolicyRepository;
import capstone.library.repositories.BorrowingRepository;
import capstone.library.services.RenewService;
import capstone.library.util.tools.DateTimeUtils;
import capstone.library.util.tools.OverdueBooksFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RenewServiceImpl implements RenewService {
    @Autowired
    BookBorrowingRepository bookBorrowingRepository;
    @Autowired
    BorrowingRepository borrowingRepository;
    @Autowired
    BorrowPolicyRepository borrowPolicyRepository;
    @Autowired
    OverdueBooksFinder overdueBooksFinder;
    DateTimeUtils dateTimeUtils;

    private static final String BOOK_BORROWING_NOT_FOUND_ERROR = "Cannot find this book in borrowing list";
    private static final String OVERDUE_PATRON_ERROR = "This patron is keeping overdue books";
    private static final String PATRON_TYPE_COPY_TYPE_ERROR = "This patron type can no longer borrow this book copy type";
    private static final String EXCEEDS_MAX_RENEW_TIME = "Exceeding renew limit for this copy";

    /*Validate if able to renew a book copy*/
    @Override
    public ValidateRenewDto validateRenew(int bookBorrowingId) {
        ValidateRenewDto response = new ValidateRenewDto();
        List<String> reasons = new ArrayList<>();
        boolean ableToRenew = true;
        String newDueDate = "";

        Optional<BookBorrowing> bookBorrowingOptional = bookBorrowingRepository.findById(bookBorrowingId);
        if (bookBorrowingOptional.isPresent()) {
            BookBorrowing bookBorrowing = bookBorrowingOptional.get();

            //Cannot renew if patron is keeping any overdue books
            Account patron = bookBorrowing.getBorrowing().getBorrower();
            List<BookCopy> overdueBooks = overdueBooksFinder.findOverdueBookCopiesByPatronId(patron.getId());
            if (!overdueBooks.isEmpty()) {
                ableToRenew = false;
                reasons.add(OVERDUE_PATRON_ERROR);
            }

            //Check if this patron can still borrow this book copy type
            Optional<BorrowPolicy> borrowPolicyOptional = borrowPolicyRepository.findByPatronTypeIdAndBookCopyTypeId(
                    patron.getPatronType().getId(), bookBorrowing.getBookCopy().getBookCopyType().getId());
            if (borrowPolicyOptional.isEmpty()) {
                ableToRenew = false;
                reasons.add(PATRON_TYPE_COPY_TYPE_ERROR);
            } else {

                //Cannot renew more than max_extend_time
                int currentExtendIndex = bookBorrowing.getExtendIndex();
                int maxRenewTime = borrowPolicyOptional.orElse(new BorrowPolicy()).getMaxExtendTime();
                if (currentExtendIndex >= maxRenewTime) {
                    ableToRenew = false;
                    reasons.add(EXCEEDS_MAX_RENEW_TIME + " (" + currentExtendIndex + "/" + maxRenewTime + ")");
                }
            }

            //New due date = today + extend_due_durations (excluding saturdays and sundays)
            if (ableToRenew) {
                LocalDate dueAt = bookBorrowing.getDueAt();
                dueAt = dueAt.plusDays(borrowPolicyOptional.get().getMaxExtendTime());
                while (dueAt.getDayOfWeek().equals(DayOfWeek.SATURDAY) || dueAt.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                    dueAt = dueAt.plusDays(1);
                }
                newDueDate = dueAt.toString();
            }
        } else {
            throw new ResourceNotFoundException("Book Borrowing", BOOK_BORROWING_NOT_FOUND_ERROR);
        }

        //prepare response
        response.setReasons(reasons);
        response.setAbleToRenew(ableToRenew);
        response.setNewDueDate(newDueDate);

        return response;
    }
}
