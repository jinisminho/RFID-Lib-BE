package capstone.library.services.impl;

import capstone.library.dtos.response.ValidateRenewDto;
import capstone.library.entities.*;
import capstone.library.exceptions.CustomException;
import capstone.library.exceptions.InvalidRequestException;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.*;
import capstone.library.services.RenewService;
import capstone.library.util.constants.ConstantUtil;
import capstone.library.util.tools.DateTimeUtils;
import capstone.library.util.tools.OverdueBooksFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Autowired
    private ExtendHistoryRepository extendHistoryRepository;
    @Autowired
    private AccountRepository accountRepository;


    DateTimeUtils dateTimeUtils;

    private static final String BOOK_BORROWING_NOT_FOUND_ERROR = "Cannot find this book in borrowing list";
    private static final String OVERDUE_PATRON_ERROR = "This patron is keeping overdue books";
    private static final String PATRON_TYPE_COPY_TYPE_ERROR = "This patron type can no longer borrow this book copy type";
    private static final String EXCEEDS_MAX_RENEW_TIME = "Exceeding renew limit for this copy";
    private static final String PATRON_INACTIVE = "This patron is inactive";

    /*Validate if able to renew a book copy*/
    @Override
    public ValidateRenewDto validateRenew(int bookBorrowingId) {
        ValidateRenewDto response = new ValidateRenewDto();
        List<String> reasons = new ArrayList<>();
        boolean violatePolicy = false;
        boolean ableToRenew = true;
        LocalDate newDueDate = LocalDate.now();

        Optional<BookBorrowing> bookBorrowingOptional = bookBorrowingRepository.findById(bookBorrowingId);
        if (bookBorrowingOptional.isPresent()) {
            BookBorrowing bookBorrowing = bookBorrowingOptional.get();

            Account patron = bookBorrowing.getBorrowing().getBorrower();
            List<BookCopy> overdueBooks = overdueBooksFinder.findOverdueBookCopiesByPatronId(patron.getId());

            //Cannot renew if patorn is inactive
            if (!patron.isActive()) {
                ableToRenew = false;
                reasons.add(PATRON_INACTIVE);
            }

            // Warn if patron is keeping any overdue books
            if (!overdueBooks.isEmpty()) {
                violatePolicy = true;
                reasons.add(OVERDUE_PATRON_ERROR);
            }

            //Check if this patron can still borrow this book copy type
            Optional<BorrowPolicy> borrowPolicyOptional = borrowPolicyRepository.findByPatronTypeIdAndBookCopyTypeId(
                    patron.getPatronType().getId(), bookBorrowing.getBookCopy().getBookCopyType().getId());
            if (borrowPolicyOptional.isEmpty()) {
                violatePolicy = true;
                ableToRenew = false;
                reasons.add(PATRON_TYPE_COPY_TYPE_ERROR);
            } else {

                //Warn if renewing more than max_extend_time
                int currentExtendIndex = bookBorrowing.getExtendIndex();
                int maxRenewTime = borrowPolicyOptional.orElse(new BorrowPolicy()).getMaxExtendTime();
                if (currentExtendIndex >= maxRenewTime) {
                    violatePolicy = true;
                    ableToRenew = false;
                    reasons.add(EXCEEDS_MAX_RENEW_TIME + " (" + currentExtendIndex + "/" + maxRenewTime + ")");
                }
            }

            if (ableToRenew) {
                LocalDate dueAt = bookBorrowing.getDueAt();
                dueAt = dueAt.plusDays(borrowPolicyOptional.get().getExtendDueDuration());
                while (dueAt.getDayOfWeek().equals(DayOfWeek.SATURDAY) || dueAt.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                    dueAt = dueAt.plusDays(1);
                }
                newDueDate = dueAt;
            }
        } else {
            throw new ResourceNotFoundException("Book Borrowing", BOOK_BORROWING_NOT_FOUND_ERROR);
        }

        //prepare response
        response.setReasons(reasons);
        response.setViolatePolicy(violatePolicy);
        response.setAbleToRenew(ableToRenew);
        response.setNewDueDate(newDueDate);

        return response;
    }

    @Override
    public boolean addNewExtendHistory(Integer bookBorrowingId, Integer librarianId, Integer numberOfDayToPlus, String reason) {
        List<String> policiViolations = new ArrayList();

        if (bookBorrowingId == null) {
            throw new MissingInputException("Missing input");
        }


        //Find and check if bookBorrowing exists
        BookBorrowing bookBorrowing = bookBorrowingRepository.findById(bookBorrowingId).orElseThrow(() -> new ResourceNotFoundException("BookBorrowing", "BookBorrowing with Id " + bookBorrowingId + " not found"));

        //If bookBorrowing exists
        if (bookBorrowing != null) {

            //Check if borrower is inactive
            if (!bookBorrowing.getBorrowing().getBorrower().isActive()) {
                throw new InvalidRequestException(PATRON_INACTIVE);
            }

            //Check if it is overdue
            if (bookBorrowing.getDueAt().isBefore(LocalDate.now())) {
//                    throw new CustomException(HttpStatus.BAD_REQUEST, ConstantUtil.EXCEPTION_POLICY_VIOLATION, "The requested item is overdue");
                policiViolations.add("The requested item is overdue");
            }

            //Validate this request
            ValidateRenewDto validateRenewDto = this.validateRenew(bookBorrowingId);
            policiViolations.addAll(validateRenewDto.getReasons());

            //Check if this patron type can still borrow this book copy type
            if (!validateRenewDto.isAbleToRenew() &&
                    validateRenewDto.getReasons().contains(PATRON_TYPE_COPY_TYPE_ERROR)) {
                throw new InvalidRequestException(PATRON_TYPE_COPY_TYPE_ERROR);
            }

            if (validateRenewDto.isViolatePolicy() || librarianId != null) {
                //if issued by Librarian get the librarian account, if not get the patron one
                /*Hoang*/
                Account isssuedBy =
                        librarianId != null
                                ? accountRepository.findById(librarianId).orElseThrow(() -> new ResourceNotFoundException("Account", "Librarian Account with Id " + librarianId + " not found"))
                                : bookBorrowing.getBorrowing().getBorrower();
                /*===========*/

                //Get extendHistory if it exists
                ExtendHistory extendHistory = extendHistoryRepository.findFirstByBookBorrowing_IdOrderByDueAtDesc(bookBorrowing.getId())
                        .orElse(new ExtendHistory());

                //If the bookBorrowing is extended for the first time, create the first history
                if (bookBorrowing.getExtendIndex() == 0) {
                    /*Hoang*/
                    extendHistory.setBorrowedAt(bookBorrowing.getBorrowing().getBorrowedAt());
                    /*========*/
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
                if (librarianId != null && (reason != null ? !reason.isEmpty() : false)) {
                    newExtendHistory.setNote(reason);
                }

                //Get Day to Plus by request or policy. Default is policy
                newExtendHistory.setDueAt(numberOfDayToPlus != null ? extendHistory.getDueAt().plusDays(numberOfDayToPlus) : validateRenewDto.getNewDueDate());

                //------------------------ Create new history END

                //Update bookBorrowing
                bookBorrowing.setExtendedAt(newExtendHistory.getExtendedAt());
                bookBorrowing.setExtendIndex(newExtendHistory.getExtendIndex());
                bookBorrowing.setDueAt(newExtendHistory.getDueAt());

                extendHistoryRepository.save(newExtendHistory);
                bookBorrowingRepository.save(bookBorrowing);
                return true;
            }
        }

        if (!policiViolations.isEmpty()) {
            String errorStr = "";
            int i = 1;
            for (String violation : policiViolations) {
                errorStr += "[" + i + "]" + violation + ";";
            }
            throw new CustomException(HttpStatus.BAD_REQUEST, ConstantUtil.EXCEPTION_POLICY_VIOLATION, "Policy violation: " + errorStr);
        }

        return false;

    }
}
