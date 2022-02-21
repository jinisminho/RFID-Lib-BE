package capstone.library.services.impl;

import capstone.library.dtos.common.CheckoutCopyDto;
import capstone.library.dtos.common.MyAccountDto;
import capstone.library.dtos.common.MyBookDto;
import capstone.library.dtos.request.ScannedRFIDCopiesRequestDto;
import capstone.library.dtos.response.*;
import capstone.library.entities.*;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.BookStatus;
import capstone.library.enums.RoleIdEnum;
import capstone.library.exceptions.InvalidRequestException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.*;
import capstone.library.services.BorrowingService;
import capstone.library.services.LibrarianService;
import capstone.library.services.SecurityGateService;
import capstone.library.util.tools.BookCopyBarcodeUtils;
import capstone.library.util.tools.DateTimeUtils;
import capstone.library.util.tools.OverdueBooksFinder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LibrarianServiceImpl implements LibrarianService {
    @Autowired
    BookCopyRepository bookCopyRepository;
    @Autowired
    BorrowPolicyRepository borrowPolicyRepository;
    @Autowired
    BookBorrowingRepository bookBorrowingRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    OverdueBooksFinder overdueBooksFinder;
    @Autowired
    FeePolicyRepository feePolicyRepository;
    @Autowired
    PatronTypeRepository patronTypeRepository;
    @Autowired
    MyBookRepository myBookRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    BookCopyBarcodeUtils bookCopyBarcodeUtils;
    @Autowired
    BorrowingService borrowingService;
    @Autowired
    SecurityGateService securityGateService;

    DateTimeUtils dateTimeUtils = new DateTimeUtils();

    private static final String NOT_FOUND = " not found";
    private static final String PATRON_NOT_FOUND = "Cannot find this patron in system";
    private static final String BOOK_NOT_FOUND = "Cannot find this book in database";
    private static final String COPY_NOT_AVAILABLE = "This book copy is not available (barcode): ";
    private static final String ACCOUNT_NOT_FOUND = "Cannot find this account in database";
    private static final String PATRON_TYPE_NOT_FOUND = "Cannot find this patron type in system";
    private static final String BORROW_COPY_NOT_FOUND = "Cannot find this book copy with status BORROWED";
    private static final String POLICY_KEEPING_OVERDUE = "This patron is keeping overdue book";
    private static final String POLICY_EXCEEDS_TOTAL_BORROW_ALLOWANCE = "Total borrow allowance for this patron is: ";
    private static final String POLICY_INVALID_RFID = "Cannot find copy based on this RFID: ";
    private static final String POLICY_EXCEEDS_TYPE_BORROW_ALLOWANCE = "Exceeding borrowing allowance for copy type: ";
    private static final String POLICY_DUPLICATE_BOOK = "Borrowing or keeping more than 1 copy of same book ISBN: ";
    private static final String POLICY_PATRON_TYPE_COPY_TYPE = "This patron cannot borrow this copy type: ";
    private static final String PATRON_INACTIVE = "This patron is inactive";
    private static final String BOOK_BORROWING_NOT_FOUND_ERROR = "Cannot find this book in borrowing history";

    /*Renew Index is used to determine if this book has been renew this time.
     * If the book has not been renewed, then Renew index is 0*/
    private static final int DEFAULT_RENEW_INDEX = 0;


    @Override
    @Transactional
    public CheckoutResponseDto checkout(ScannedRFIDCopiesRequestDto request) {
        List<String> rfidTags = request.getBookRfidTags();

        /*Get the librarian to add to issued_by in book_borrowing table*/
        Optional<Account> librarianOptional = accountRepository.findById(request.getLibrarianId());
        //Return 404 if no account with 'getLibrarianId' is found
        if (librarianOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Librarian",
                    "Librarian with id: " + request.getLibrarianId() + NOT_FOUND);
        }
        Account issuingLibrarian = librarianOptional.get();
        /*========================*/

        /*Get the borrowing patron to add to borrowed_by in book_borrowing table*/
        Optional<Account> patronOptional = accountRepository.findByIdAndRoleId(request.getPatronId(), RoleIdEnum.ROLE_PATRON.getRoleId());

        //Return 404 if no patron with 'patronId' is found
        if (patronOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Patron", "Patron with id: " + request.getPatronId() + NOT_FOUND);
        }
        Account borrowingPatron = patronOptional.get();
        //Return bad request if patron is inactive
        if (!borrowingPatron.isActive()) {
            throw new InvalidRequestException(PATRON_INACTIVE);
        }
        /*========================*/

        /*Get the latest Fee Policy*/
        List<FeePolicy> feePolicies = feePolicyRepository.findAllByOrderByCreatedAtAsc();
        if (feePolicies.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Fee Policy", "Fee Policy " + NOT_FOUND);
        }
        FeePolicy feePolicy = feePolicies.get(feePolicies.size() - 1);
        /*=========================*/

        //Checkout books
        CheckoutResponseDto response = new CheckoutResponseDto();
        List<CheckoutCopyDto> dtos = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        //Create new borrowing for book_borrowing
        Borrowing borrowing = new Borrowing();
        borrowing.setBorrower(borrowingPatron);
        borrowing.setBorrowedAt(now);
        borrowing.setNote(request.getCheckoutNote());
        for (String rfidTag :
                rfidTags) {
            CheckoutCopyDto dto = new CheckoutCopyDto();
            response.setCheckoutCopyDto(dtos);
            Optional<BookCopy> bookCopyOptional = bookCopyRepository.findByRfid(rfidTag);
            if (bookCopyOptional.isPresent()) {
                BookCopy bookCopy = bookCopyOptional.get();

                /*Get borrowing durations (days) from borrowing policy.
                    Borrowing policy is determined by Book Copy Type and Patron Type.
                    Calculate the due date by adding borrowing duration (days) to today*/
                Optional<BorrowPolicy> policyOptional = borrowPolicyRepository.findByPatronTypeIdAndBookCopyTypeId(borrowingPatron.getPatronType().getId(), bookCopy.getBookCopyType().getId());
                LocalDate dueAt = LocalDate.now();
                if (policyOptional.isEmpty()) {
                    /*Cannot find borrowing policy means this patron type not allow to borrow this book copy type
                    Add this book copy as not AbleToBorrow to response List*/
                    dto.setAbleToBorrow(false);
                    dto.setReason(borrowingPatron.getRole().getName() + " cannot borrow this copy");
                } else {
                    int borrowDuration = policyOptional.get().getDueDuration();
                    dueAt = dueAt.plusDays(borrowDuration);
                    /*overdue days excludes saturdays and sundays
	                    eg: If due date is on Sunday then when return on Monday, overdue days = 0*/
                    while (dueAt.getDayOfWeek().equals(DayOfWeek.SATURDAY) || dueAt.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                        dueAt = dueAt.plusDays(1);
                    }
                    /*==========================*/
                    if (bookCopy.getStatus().equals(BookCopyStatus.AVAILABLE)) {
                        //Update book copy status in db
                        bookCopy.setStatus(BookCopyStatus.BORROWED);

                        /*Create new book borrowing record in db*/
                        BookBorrowing bookBorrowing = new BookBorrowing();
                        bookBorrowing.setIssued_by(issuingLibrarian);
                        bookBorrowing.setBookCopy(bookCopy);
                        bookBorrowing.setDueAt(dueAt);
                        bookBorrowing.setExtendIndex(DEFAULT_RENEW_INDEX);
                        bookBorrowing.setFeePolicy(feePolicy);
                        bookBorrowing.setBorrowing(borrowing);
                        /*===========================*/

                        //Save bookBorrowing to db
                        bookBorrowingRepository.save(bookBorrowing);

                        //Deactivate security alarm for checked out copy
                        securityGateService.add(new SecurityDeactivatedCopy(bookBorrowing.getBookCopy().getRfid()));

                        //Add this book copy as is AbleToBorrow to response List
                        dto.setAbleToBorrow(true);
                        dto.setReason("");
                        dto.setDueDate(dueAt.toString());
                    } else {
                        //Add this book copy as not AbleToBorrow to response List
                        dto.setAbleToBorrow(false);
                        dto.setReason("Book is not available");
                    }
                }
                /*====================*/

                //Add bookBorrowing to response dto
                dto.setRfid(rfidTag);
                dto.setTitle(bookCopy.getBook().getTitle());
                dto.setSubtitle(bookCopy.getBook().getSubtitle());
                dto.setEdition(bookCopy.getBook().getEdition());
                dto.setPublisher(bookCopy.getBook().getPublisher());
                dto.setPublishYear(bookCopy.getBook().getPublishYear());
                String authors = bookCopy.getBook().getBookAuthors().toString();
                authors = authors.replace("[", "");
                authors = authors.replace("]", "");
                dto.setAuthor(authors);
                dto.setBorrowedAt(dateTimeUtils.convertDateTimeToString(now));
            } else {
                //Add bookBorrowing to response dto
                dto.setRfid(rfidTag);
                dto.setReason("Cannot find this book in database");
                dto.setDueDate("");
                dto.setTitle("");
            }
            dtos.add(dto);
        }
        response.setCheckoutCopyDto(dtos);
        response.setFeePolicy(feePolicy);
        return response;
    }


    @Override
    public ReturnBookResponseDto validateReturnRequest(String rfid) {
        {
            BookCopy bookCopy;

            /*Check to make sure book copy for each rfidTag is in DB.
             * Only update DB for book copies that are found*/
            Optional<BookCopy> bookCopyOptional = bookCopyRepository.
                    findByRfidAndStatus(rfid, BookCopyStatus.BORROWED);
            if (bookCopyOptional.isPresent()) {
                bookCopy = bookCopyOptional.get();
            } else {
                throw new ResourceNotFoundException("Book Copy", BORROW_COPY_NOT_FOUND);
            }

            /*Return book copies found in DB earlier
             * Update book_borrowing table & book_copy table
             * If book copy is overdue, calculate fine*/
            ReturnBookResponseDto dto = new ReturnBookResponseDto();
            dto.setOverdue(false);
            Optional<BookBorrowing> bookBorrowingOptional = bookBorrowingRepository.
                    findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(bookCopy.getId());
            if (bookBorrowingOptional.isPresent()) {
                BookBorrowing bookBorrowing = bookBorrowingOptional.get();
                double fineRate;
                double fine = 0;
                //Returns >0 if today has passed overdue date
                int overdueDays = (int) dateTimeUtils.getOverdueDays(LocalDate.now(), bookBorrowing.getDueAt());
                if (overdueDays > 0) {
                    Optional<FeePolicy> feePolicyOptional = feePolicyRepository.findById(bookBorrowing.getFeePolicy().getId());
                    if (feePolicyOptional.isPresent()) {
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
                        dto.setOverdue(true);
                    }
                }

                // Prepare dto
                MyBookDto myBookDto = objectMapper.convertValue(bookCopy.getBook(), MyBookDto.class);
                myBookDto.setAuthors(bookCopy.getBook().getBookAuthors().toString().
                        replace("]", "").replace("[", ""));
                myBookDto.setBarcode(bookCopy.getBarcode());
                myBookDto.setRfid(bookCopy.getRfid());
                dto.setBook(myBookDto);
                dto.setDueDate(bookBorrowing.getDueAt().toString());
                dto.setOverdueDays(overdueDays);
                dto.setBookPrice(bookCopy.getPrice());
                dto.setPrice(bookCopy.getPrice());
                dto.setBarcode(bookCopy.getBarcode());
                dto.setRfid(bookCopy.getRfid());
                dto.setId(bookCopy.getId());
                //Get Borrower and Borrowed_at in Borrowing table
                dto.setBorrowedAt(dateTimeUtils.convertDateTimeToString(bookBorrowing.getBorrowing().getBorrowedAt()));
                dto.setBorrower(objectMapper.convertValue(bookBorrowing.getBorrowing().getBorrower(), MyAccountDto.class));
                dto.getBorrower().setRoleName(bookBorrowing.getBorrowing().getBorrower().getRole().getName());
                dto.getBorrower().setPatronTypeName(bookBorrowing.getBorrowing().getBorrower().getPatronType().getName());
                dto.setCopyType(bookCopy.getBookCopyType().getName());
            }

            return dto;
        }
    }

    @Override
    public ReturnBookResponseDto validateReturnRequestByRfidOrBarcode(String value) {
        {
            /*Check to make sure book copy for each rfidTag and barcode is in DB.
             * Only update DB for book copies that are found*/
            BookCopy bookCopy = bookCopyRepository.
                    findByStatusAndRfidOrBarcode(BookCopyStatus.BORROWED, value, value)
                    .orElseThrow(() -> new ResourceNotFoundException("Book Copy", BORROW_COPY_NOT_FOUND));

            /*Return book copies found in DB earlier
             * Update book_borrowing table & book_copy table
             * If book copy is overdue, calculate fine*/
            BookBorrowing bookBorrowing = bookBorrowingRepository.
                    findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(bookCopy.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book borrowing", BOOK_BORROWING_NOT_FOUND_ERROR));


            ReturnBookResponseDto dto = new ReturnBookResponseDto();
            dto.setOverdue(false);
            //Returns >0 if today has passed overdue date
            double fineRate;
            double fine = 0;
            int overdueDays = (int) dateTimeUtils.getOverdueDays(LocalDate.now(), bookBorrowing.getDueAt());
            if (overdueDays > 0) {
                Optional<FeePolicy> feePolicyOptional = feePolicyRepository.findById(bookBorrowing.getFeePolicy().getId());
                if (feePolicyOptional.isPresent()) {
                    double bookCopyPrice = bookCopy.getPrice();
                    fineRate = feePolicyOptional.get().getOverdueFinePerDay();
                    fine = fineRate * overdueDays;
                    int maxOverdueFinePercentage = feePolicyOptional.get().getMaxPercentageOverdueFine();
                    double maxOverdueFine = bookCopyPrice * ((double) maxOverdueFinePercentage / 100);
                    if (fine >= maxOverdueFine) {
                        fine = maxOverdueFine;
                    }
                    dto.setReason("Return late: " + overdueDays + " (days)");
                } else {
                    dto.setReason("FeePolicy not found");
                }
                dto.setFine(fine);
                dto.setOverdue(true);
            }

            // Prepare dto
            MyBookDto myBookDto = objectMapper.convertValue(bookCopy.getBook(), MyBookDto.class);
            myBookDto.setAuthors(bookCopy.getBook().getBookAuthors().toString().
                    replace("]", "").replace("[", ""));
            myBookDto.setBarcode(bookCopy.getBarcode());
            myBookDto.setRfid(bookCopy.getRfid());
            dto.setBook(myBookDto);
            dto.setDueDate(bookBorrowing.getDueAt().toString());
            dto.setOverdueDays(overdueDays);
            dto.setBookPrice(bookCopy.getPrice());
            dto.setPrice(bookCopy.getPrice());
            dto.setBarcode(bookCopy.getBarcode());
            dto.setRfid(bookCopy.getRfid());
            dto.setId(bookCopy.getId());
            //Get Borrower and Borrowed_at in Borrowing table
            dto.setBorrowedAt(dateTimeUtils.convertDateTimeToString(bookBorrowing.getBorrowing().getBorrowedAt()));
            dto.setBorrower(objectMapper.convertValue(bookBorrowing.getBorrowing().getBorrower(), MyAccountDto.class));
            dto.getBorrower().setRoleName(bookBorrowing.getBorrowing().getBorrower().getRole().getName());
            dto.getBorrower().setPatronTypeName(bookBorrowing.getBorrowing().getBorrower().getPatronType().getName());
            dto.setCopyType(bookCopy.getBookCopyType().getName());

            return dto;
        }
    }

    /*Is for librarians and Admin
     * For returning multiple of book copies borrowed by patrons */
    @Override
    @Transactional
    public List<ReturnBookResponseDto> returnBookCopies(ScannedRFIDCopiesRequestDto request) {
        return returnCopies(request);
    }

    private List<ReturnBookResponseDto> returnCopies(ScannedRFIDCopiesRequestDto request) {

        List<BookCopy> bookCopies = new ArrayList<>();
        List<ReturnBookResponseDto> responseDtos = new ArrayList<>();

        /*Get librarian*/
        Optional<Account> librarianOptional = accountRepository.findById(request.getLibrarianId());
        if (librarianOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Librarian", "Librarian with id: " + request.getLibrarianId() + NOT_FOUND);
        }
        Account librarian = librarianOptional.get();

        /*Check to make sure book copy for each rfidTag is in DB.
         * Only update DB for book copies that are found*/
        for (String rfidTag : request.getBookRfidTags()) {
            Optional<BookCopy> bookCopyOptional = bookCopyRepository.
                    findByRfidAndStatus(rfidTag, BookCopyStatus.BORROWED);
            if (bookCopyOptional.isPresent()) {
                bookCopies.add(bookCopyOptional.get());
            } else {
                throw new ResourceNotFoundException("Book copy", BORROW_COPY_NOT_FOUND);
            }

        }

        /*Return book copies found in DB earlier
         * Update book_borrowing table & book_copy table
         * If book copy is overdue, calculate fine*/
        LocalDateTime now = LocalDateTime.now();
        for (BookCopy bookCopy : bookCopies) {
            ReturnBookResponseDto dto = new ReturnBookResponseDto();
            dto.setOverdue(false);
            Optional<BookBorrowing> bookBorrowingOptional = bookBorrowingRepository.
                    findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(bookCopy.getId());
            if (bookBorrowingOptional.isPresent()) {
                BookBorrowing bookBorrowing = bookBorrowingOptional.get();
                double fineRate;
                double fine = 0;
                //Returns >0 if today has passed overdue date
                int overdueDays = (int) dateTimeUtils.getOverdueDays(LocalDate.now(), bookBorrowing.getDueAt());
                if (overdueDays > 0) {
                    Optional<FeePolicy> feePolicyOptional = feePolicyRepository.findById(bookBorrowing.getFeePolicy().getId());
                    if (feePolicyOptional.isPresent()) {
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
                        dto.setOverdue(true);
                    }
                } else {
                    overdueDays = 0;
                }
                /*Update borrowing_book table
                 * Add return date and fine
                 * Reactivate alarm for returned book */
                if (request.isCheckin()) {
                    bookBorrowing.setReturn_by(librarian);
                    bookBorrowing.setReturnedAt(now);
                    bookBorrowing.setFine(fine);
                    bookBorrowingRepository.save(bookBorrowing);
                    //reactivate alarm
                    securityGateService.deleteByRfid(bookBorrowing.getBookCopy().getRfid());
                }

                /*update copy status based on book status:
                    if book is:
                        + IN_CIRCULATION => AVAILABLE
                        + OUT_OF_CIRCULATION => OUT_OF_CIRCULATION
                        + DISCARD => DISCARD
                        + LIB_USE_ONLY => LIB_USE_ONLY*/
                if (request.isCheckin()) {
                    if (bookCopy.getBook().getStatus().equals(BookStatus.IN_CIRCULATION)) {
                        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
                    } else if (bookCopy.getBook().getStatus().equals(BookStatus.OUT_OF_CIRCULATION)) {
                        bookCopy.setStatus(BookCopyStatus.OUT_OF_CIRCULATION);
                    } else if (bookCopy.getBook().getStatus().equals(BookStatus.DISCARD)) {
                        bookCopy.setStatus(BookCopyStatus.DISCARD);
                    } else if (bookCopy.getBook().getStatus().equals(BookStatus.LIB_USE_ONLY)) {
                        bookCopy.setStatus(BookCopyStatus.LIB_USE_ONLY);
                    }

                    //Insert return transaction to database
                    bookCopyRepository.save(bookCopy);
                }

                // Prepare dto
                MyBookDto myBookDto = objectMapper.convertValue(bookCopy.getBook(), MyBookDto.class);
                myBookDto.setAuthors(bookCopy.getBook().getBookAuthors().toString().
                        replace("]", "").replace("[", ""));
                dto.setBook(myBookDto);
                dto.setDueDate(bookBorrowing.getDueAt().toString());
                dto.setOverdueDays(overdueDays);
                dto.setBookPrice(bookCopy.getPrice());
                dto.setBorrowedAt(dateTimeUtils.convertDateTimeToString(bookBorrowing.getBorrowing().getBorrowedAt()));
                dto.setReturnedAt(dateTimeUtils.convertDateTimeToString(now));
                dto.setPrice(bookCopy.getPrice());
                dto.setBarcode(bookCopy.getBarcode());
                dto.setRfid(bookCopy.getRfid());
                dto.setId(bookCopy.getId());
                dto.setBorrower(objectMapper.convertValue(bookBorrowing.getBorrowing().getBorrower(), MyAccountDto.class));
                dto.setCopyType(bookCopy.getBookCopyType().getName());

                responseDtos.add(dto);
            }
        }

        return responseDtos;
    }

    @Override
    public List<BookResponseDto> getOverdueBooksByBorrower(int patronId) {
        return overdueBooksFinder.findOverdueBooksDTOByPatronId(patronId);
    }

    @Override
    public CheckoutPolicyValidationResponseDto validateCheckoutPolicy(ScannedRFIDCopiesRequestDto request) {
        boolean haveOverdueCopies = false;
        boolean violatePolicy = false;
        boolean duplicateBook = false;
        boolean copyIsAvailable = true;
        List<String> reasons = new ArrayList<>();

        /*Get patron account*/
        Account patron = getAccountInfo(request.getPatronId());
        /*=================*/

        /*Check if patron is keeping any overdue book*/
        List<BookResponseDto> overdueBooks = overdueBooksFinder.findOverdueBooksDTOByPatronId(patron.getId());
        if (!overdueBooks.isEmpty()) {
            haveOverdueCopies = true;
            reasons.add(POLICY_KEEPING_OVERDUE);
        }
        /*================*/

        /*Check if patron is borrowing exceeding total allowance*/
        PatronType patronType = getPatronTypeInfo(patron.getPatronType().getId());
        int totalMaxAllowance = patronType.getMaxBorrowNumber();
        List<BookBorrowing> borrowedCopies = bookBorrowingRepository.
                findByBorrowerIdAndReturnedAtIsNullAndLostAtIsNull(patron.getId());
        if ((request.getBookRfidTags().size() + borrowedCopies.size()) > totalMaxAllowance) {
            violatePolicy = true;
            reasons.add(POLICY_EXCEEDS_TOTAL_BORROW_ALLOWANCE + totalMaxAllowance + ". (This patron is keeping " +
                    borrowedCopies.size() + " books)");
        }
        /*===============*/

        /*Check:
            + Each copy status
            + if patron is borrowing exceeding allowance for each copy_type*/
        // key = copy type id; value = number of copies
        HashSet<BookCopyType> copyTypeIdHashSet = new HashSet<>();
        List<Integer> copyTypeIdList = new ArrayList<>();
        List<BookCopy> checkoutCopies = new ArrayList<>();
        for (String rfid : request.getBookRfidTags()) {
            BookCopy bookCopy = getBookCopyInfoByRFID(rfid);
            if (bookCopy.getId() != null) {
                checkoutCopies.add(bookCopy);
                copyTypeIdList.add(bookCopy.getBookCopyType().getId());
                copyTypeIdHashSet.add(bookCopy.getBookCopyType());
            } else {
                violatePolicy = true;
                reasons.add(POLICY_INVALID_RFID + rfid);
            }
            // check copy status
            if (!bookCopy.getStatus().equals(BookCopyStatus.AVAILABLE)) {
                violatePolicy = true;
                reasons.add(COPY_NOT_AVAILABLE + bookCopy.getBarcode());
            }
        }
        //check for if patron is borrowing exceeding allowance for each copy_type
        for (BookCopyType type : copyTypeIdHashSet) {
            BorrowPolicy tmp = getBorrowPolicy(patron.getPatronType().getId(), type.getId());
            int max = tmp.getMaxNumberCopyBorrow();
            if (max <= 0) {
                violatePolicy = true;
                reasons.add(POLICY_PATRON_TYPE_COPY_TYPE + type.getName());
            } else {
                int totalBorrowedOfEachCopyType = 0;
                for (BookBorrowing bookBorrowing : borrowedCopies) {
                    if (bookBorrowing.getBookCopy().getBookCopyType().equals(type)) {
                        totalBorrowedOfEachCopyType++;
                    }
                }
                int totalBorrowingOfEachCopyType =
                        Collections.frequency(copyTypeIdList, type.getId()) + totalBorrowedOfEachCopyType;
                if (totalBorrowingOfEachCopyType > max) {
                    violatePolicy = true;
                    StringBuilder violatingCopies = new StringBuilder();
                    for (BookCopy bookCopy : checkoutCopies) {
                        if (bookCopy.getBookCopyType().equals(type)) {
                            violatingCopies.append("'").append(bookCopy.getBook().getTitle()).append("' ");
                        }
                    }

                    reasons.add(POLICY_EXCEEDS_TYPE_BORROW_ALLOWANCE + type.getName() +
                            " (" + totalBorrowingOfEachCopyType + "/" + max + "). "
                            + violatingCopies.toString().trim());
                }
            }
        }
        /*==============*/

        /*Check if the patron is borrowing any duplicate copies of the same book*/
        HashSet<Book> bookHashSet = new HashSet<>();
        List<Integer> bookIdList = new ArrayList<>();
        //Add all scanned copy's book's ID to bookIdList (including all duplicates if present)
        //Add the copy's book to a HashSet (excluding duplicating books)
        for (String rfid : request.getBookRfidTags()) {
            BookCopy bookCopy = getBookCopyInfoByRFID(rfid);
            if (bookCopy.getId() != null) {
                bookIdList.add(bookCopy.getBook().getId());
                bookHashSet.add(bookCopy.getBook());
            }
        }

        //Add all BORROWED copy's book's ID to bookIdList (including all duplicates if present)
        //Add the BORROWED copy's book to a HashSet (excluding duplicating books)
        for (BookBorrowing bookBorrowing : borrowedCopies) {
            bookIdList.add(bookBorrowing.getBookCopy().getBook().getId());
            bookHashSet.add(bookBorrowing.getBookCopy().getBook());
        }

        //Check duplicate
        for (Book book : bookHashSet) {
            if (Collections.frequency(bookIdList, book.getId()) > 1) {
                violatePolicy = true;
                duplicateBook = true;
                reasons.add(POLICY_DUPLICATE_BOOK + book.getIsbn());
            }
        }
        /*===================*/

        /*Prepare response*/
        CheckoutPolicyValidationResponseDto response = new CheckoutPolicyValidationResponseDto();
        response.setHaveOverdueCopies(haveOverdueCopies);
        response.setViolatePolicy(violatePolicy);
        response.setDuplicateBook(duplicateBook);
        response.setReasons(reasons);
        /*===============*/

        return response;
    }

    @Override
    public GenerateBarcodesResponseDto generateBarcodes(int numberOfCopies, String isbn, int copyTypeId) {
        GenerateBarcodesResponseDto response = new GenerateBarcodesResponseDto();

        //Set Book Info
        Optional<Book> bookOptional = myBookRepository.findByIsbn(isbn);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            BookResponseDto dto = objectMapper.convertValue(book, BookResponseDto.class);
            dto.setBookId(book.getId());
            dto.setAuthors(book.getBookAuthors().toString().
                    replace("[", "").replace("]", ""));
            response.setBookInfo(dto);
        } else {
            throw new ResourceNotFoundException("Book", BOOK_NOT_FOUND);
        }

        //Generate barcodes
        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findFirstByOrderByIdDesc();
        if (bookCopyOptional.isPresent()) {
            BookCopy bookCopy = bookCopyOptional.get();
            response.setGeneratedBarcodes(bookCopyBarcodeUtils.
                    generateBookCopyBarcode(copyTypeId, bookCopy.getId(), numberOfCopies));
        } else {
            response.setGeneratedBarcodes(bookCopyBarcodeUtils.
                    generateBookCopyBarcode(copyTypeId, 0, numberOfCopies));
        }
//        bookCopyOptional.ifPresent(bookCopy -> response.setGeneratedBarcodes(bookCopyBarcodeUtils.
//                generateBookCopyBarcode(copyTypeId, bookCopy.getId(), numberOfCopies)));

        return response;
    }

    private Account getAccountInfo(int id) {
        return accountRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Account", ACCOUNT_NOT_FOUND));
    }

    private PatronType getPatronTypeInfo(int id) {
        return patronTypeRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Patron Type", PATRON_TYPE_NOT_FOUND));
    }

    private BookCopy getBookCopyInfoByRFID(String rfid) {
        return bookCopyRepository.findByRfid(rfid).
                orElse(new BookCopy());
    }

    private BorrowPolicy getBorrowPolicy(int patronTypeId, int copyTypeId) {
        return borrowPolicyRepository.findByPatronTypeIdAndBookCopyTypeId(patronTypeId, copyTypeId).
                orElse(new BorrowPolicy());
    }

}