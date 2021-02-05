package capstone.library.services.impl;

import capstone.library.dtos.common.CheckoutCopyDto;
import capstone.library.dtos.request.ScannedRFIDCopiesRequestDto;
import capstone.library.dtos.response.BookResponseDto;
import capstone.library.dtos.response.CheckoutPolicyValidationResponseDto;
import capstone.library.dtos.response.CheckoutResponseDto;
import capstone.library.dtos.response.ReturnBookResponseDto;
import capstone.library.entities.*;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.BookStatus;
import capstone.library.enums.ErrorStatus;
import capstone.library.enums.RoleIdEnum;
import capstone.library.exceptions.CustomException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.*;
import capstone.library.services.LibrarianService;
import capstone.library.util.tools.DateTimeUtils;
import capstone.library.util.tools.OverdueBooksFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LibrarianServiceImpl implements LibrarianService
{
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

    DateTimeUtils dateTimeUtils = new DateTimeUtils();

    private static final String NOT_FOUND = " not found";
    private static final String PATRON_NOT_FOUND = "Cannot find this patron in database";
    private static final String ACCOUNT_NOT_FOUND = "Cannot find this account in database";
    private static final String PATRON_TYPE_NOT_FOUND = "Cannot find this patron type in database";
    private static final String COPY_NOT_FOUND = "Cannot find this book copy in database";
    private static final String POLICY_KEEPING_OVERDUE = "This patron is keeping overdue book";
    private static final String POLICY_EXCEEDS_TOTAL_BORROW_ALLOWANCE = "Total borrow allowance for this patron is: ";
    private static final String POLICY_INVALID_RFID = "Cannot find copy based on this RFID: ";
    private static final String POLICY_EXCEEDS_TYPE_BORROW_ALLOWANCE = "Exceeding borrowing allowance for copy type: ";
    private static final String POLICY_DUPLICATE_BOOK = "Borrwing more than 1 copy of same book ISBN: ";
    private static final String POLICY_PATRON_TYPE_COPY_TYPE = "This patron cannot borrow this copy type: ";

    /*Renew Index is used to determine if this book has been renew this time.
     * If the book has not been renewed, then Renew index is 0*/
    private static final int DEFAULT_RENEW_INDEX = 0;


    @Override
    @Transactional
    public List<CheckoutResponseDto> checkout(ScannedRFIDCopiesRequestDto request)
    {
        List<CheckoutResponseDto> checkoutResponseDtos = new ArrayList<>();
        List<String> rfidTags = request.getBookRfidTags();

        /*Get the librarian to add to issued_by in book_borrowing table*/
        Optional<Account> librarianOptional = accountRepository.findByIdAndRoleId(request.getLibrarianId(), RoleIdEnum.ROLE_LIBRARIAN.getRoleId());
        //Return 404 if no patron with 'getLibrarianId' is found
        if (librarianOptional.isEmpty())
        {
            throw new ResourceNotFoundException(
                    "Librarian",
                    "Librarian with id: " + request.getPatronId() + NOT_FOUND);
        }
        Account issuingLibrarian = librarianOptional.get();
        /*========================*/

        /*Get the borrowing patron to add to borrowed_by in book_borrowing table*/
        Optional<Account> patronOptional = accountRepository.findByIdAndRoleId(request.getPatronId(), RoleIdEnum.ROLE_PATRON.getRoleId());
        //Return 404 if no patron with 'patronId' is found
        if (patronOptional.isEmpty())
        {
            throw new ResourceNotFoundException(
                    "Patron", "Patron with id: " + request.getPatronId() + NOT_FOUND);
        }
        Account borrowingPatron = patronOptional.get();
        /*========================*/

        /*Get the latest Fee Policy*/
        List<FeePolicy> feePolicies = feePolicyRepository.findAllByOrderByCreatedAtAsc();
        if (feePolicies.isEmpty())
        {
            throw new ResourceNotFoundException(
                    "Fee Policy", "Fee Policy " + NOT_FOUND);
        }
        FeePolicy feePolicy = feePolicies.get(feePolicies.size() - 1);
        /*=========================*/

        //Checkout books
        CheckoutResponseDto response = new CheckoutResponseDto();
        List<CheckoutCopyDto> dtos = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (String rfidTag :
                rfidTags)
        {
            CheckoutCopyDto dto = new CheckoutCopyDto();
            response.setCheckoutCopyDto(dtos);
            Optional<BookCopy> bookCopyOptional = bookCopyRepository.findByRfid(rfidTag);
            if (bookCopyOptional.isPresent())
            {
                BookCopy bookCopy = bookCopyOptional.get();

                /*Get borrowing durations (days) from borrowing policy.
                    Borrowing policy is determined by Book Copy Type and Patron Type.
                    Calculate the due date by adding borrowing duration (days) to today*/
                Optional<BorrowPolicy> policyOptional = borrowPolicyRepository.findByPatronTypeIdAndBookCopyTypeId(borrowingPatron.getPatronType().getId(), bookCopy.getBookCopyType().getId());
                LocalDate dueAt = LocalDate.now();
                if (policyOptional.isEmpty())
                {
                    /*Cannot find borrowing policy means this patron type not allow to borrow this book copy type
                    Add this book copy as not AbleToBorrow to response List*/
                    dto.setAbleToBorrow(false);
                    dto.setReason(borrowingPatron.getRole().getName() + " cannot borrow this copy");
                } else
                {
                    int borrowDuration = policyOptional.get().getDueDuration();
                    dueAt = dueAt.plusDays(borrowDuration);
                    /*overdue days excludes saturdays and sundays
	                    eg: If due date is on Sunday then when return on Monday, overdue days = 0*/
                    while (dueAt.getDayOfWeek().equals(DayOfWeek.SATURDAY) || dueAt.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                    {
                        dueAt = dueAt.plusDays(1);
                    }
                    /*==========================*/
                    if (bookCopy.getStatus().equals(BookCopyStatus.AVAILABLE))
                    {
                        //Update book copy status in db
                        bookCopy.setStatus(BookCopyStatus.BORROWED);

                        /*Create new book borrowing record in db*/
                        BookBorrowing bookBorrowing = new BookBorrowing();
                        bookBorrowing.setBorrower(borrowingPatron);
                        bookBorrowing.setIssued_by(issuingLibrarian);
                        bookBorrowing.setBookCopy(bookCopy);
                        bookBorrowing.setBorrowedAt(now);
                        bookBorrowing.setDueAt(dueAt);
                        bookBorrowing.setExtendIndex(DEFAULT_RENEW_INDEX);
                        bookBorrowing.setFeePolicy(feePolicy);
                        /*===========================*/

                        //Save bookBorrowing to db
                        bookBorrowingRepository.save(bookBorrowing);

                        //Add this book copy as is AbleToBorrow to response List
                        dto.setAbleToBorrow(true);
                        dto.setReason("");
                        dto.setDueDate(dueAt.toString());
                    } else
                    {
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
            } else
            {
                //Add bookBorrowing to response dto
                dto.setRfid(rfidTag);
                dto.setReason("Cannot find this book in database");
                dto.setDueDate("");
                dto.setTitle("");
            }
            dtos.add(dto);
        }
        response.setCheckoutCopyDto(dtos);
        checkoutResponseDtos.add(response);
        return checkoutResponseDtos;
    }


    /*Is for librarians use
     * For returning multiple of book copies borrowed by patrons */
    @Override
    @Transactional
    public List<ReturnBookResponseDto> returnBookCopies(ScannedRFIDCopiesRequestDto scannedRFIDCopiesRequestDto)
    {
        List<BookCopy> bookCopies = new ArrayList<>();
        List<ReturnBookResponseDto> responseDtos = new ArrayList<>();

        /*Get librarian*/
        Optional<Account> librarianOptional = accountRepository.findById(scannedRFIDCopiesRequestDto.getLibrarianId());
        if (librarianOptional.isEmpty())
        {
            throw new ResourceNotFoundException(
                    "Librarian", "Librarian with id: " + scannedRFIDCopiesRequestDto.getLibrarianId() + NOT_FOUND);
        }
        Account librarian = librarianOptional.get();

        /*Check to make sure book copy for each rfidTag is in DB.
         * Only update DB for book copies that are found*/
        for (String rfidTag : scannedRFIDCopiesRequestDto.getBookRfidTags())
        {
            Optional<BookCopy> bookCopyOptional = bookCopyRepository.
                    findByRfidAndStatus(rfidTag, BookCopyStatus.BORROWED);
            if (bookCopyOptional.isPresent())
            {
                bookCopies.add(bookCopyOptional.get());
            } else
            {
                ReturnBookResponseDto dto = new ReturnBookResponseDto();
                dto.setRfid(rfidTag);
                dto.setReason("Cannot find this book in borrowed-book list");
                responseDtos.add(dto);
            }

        }

        /*Return book copies found in DB earlier
         * Update book_borrowing table & book_copy table
         * If book copy is overdue, calculate fine*/
        LocalDateTime now = LocalDateTime.now();
        for (BookCopy bookCopy : bookCopies)
        {
            ReturnBookResponseDto dto = new ReturnBookResponseDto();
            dto.setOverdue(false);
            Optional<BookBorrowing> bookBorrowingOptional = bookBorrowingRepository.
                    findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(bookCopy.getId());
            if (bookBorrowingOptional.isPresent())
            {
                BookBorrowing bookBorrowing = bookBorrowingOptional.get();
                double fineRate;
                double fine = 0;
                //Returns >0 if today has passed overdue date
                int overdueDays = (int) dateTimeUtils.getOverdueDays(LocalDate.now(), bookBorrowing.getDueAt());
                System.out.println("OVERDUE DAYS: " + overdueDays);
                if (overdueDays > 0)
                {
                    Optional<FeePolicy> feePolicyOptional = feePolicyRepository.findById(bookBorrowing.getFeePolicy().getId());
                    if (feePolicyOptional.isPresent())
                    {
                        double bookCopyPrice = bookCopy.getPrice();
                        fineRate = feePolicyOptional.get().getOverdueFinePerDay();
                        fine = fineRate * overdueDays;
                        int maxOverdueFinePercentage = feePolicyOptional.get().getMaxPercentageOverdueFine();
                        double maxOverdueFine = bookCopyPrice * maxOverdueFinePercentage;
                        if (fine >= maxOverdueFine)
                        {
                            fine = maxOverdueFine;
                        }
                        dto.setFine(fine);
                        dto.setReason("Return late: " + overdueDays + " (days)");
                        dto.setOverdue(true);
                    }
                }
                /*Update borrowing_book table
                 * Add return date and fine*/
                bookBorrowing.setReturn_by(librarian);
                bookBorrowing.setReturnedAt(now);
                bookBorrowing.setFine(fine);
                bookBorrowingRepository.save(bookBorrowing);

                /*update copy status based on book status:
                    if book is:
                        + IN_CIRCULATION => AVAILABLE
                        + OUT_OF_CIRCULATION => OUT_OF_CIRCULATION
                        + DISCARD => DISCARD
                        + LIB_USE_ONLY => LIB_USE_ONLY*/
                if (bookCopy.getBook().getStatus().equals(BookStatus.IN_CIRCULATION))
                {
                    bookCopy.setStatus(BookCopyStatus.AVAILABLE);
                } else if (bookCopy.getBook().getStatus().equals(BookStatus.OUT_OF_CIRCULATION))
                {
                    bookCopy.setStatus(BookCopyStatus.OUT_OF_CIRCULATION);
                } else if (bookCopy.getBook().getStatus().equals(BookStatus.DISCARD))
                {
                    bookCopy.setStatus(BookCopyStatus.DISCARD);
                } else if (bookCopy.getBook().getStatus().equals(BookStatus.LIB_USE_ONLY))
                {
                    bookCopy.setStatus(BookCopyStatus.LIB_USE_ONLY);
                }

                //Insert return transaction to database
                try
                {
                    bookCopyRepository.save(bookCopy);
                } catch (Exception e)
                {
                    throw new CustomException(
                            HttpStatus.INTERNAL_SERVER_ERROR, ErrorStatus.COMMON_DATABSE_ERROR.getReason(), e.getLocalizedMessage());
                }

                dto.setDueDate(bookBorrowing.getDueAt().toString());
                dto.setTitle(bookCopy.getBook().getTitle());
                dto.setSubtitle(bookCopy.getBook().getSubtitle());
                dto.setOverdueDays(overdueDays);
                dto.setBookPrice(bookCopy.getPrice());
                String authors = bookCopy.getBook().getBookAuthors().toString();
                authors = authors.replace("[", "");
                authors = authors.replace("]", "");
                dto.setAuthors(authors);
                dto.setIsbn(bookCopy.getBook().getIsbn());

                responseDtos.add(dto);
            }
        }

        return responseDtos;
    }

    @Override
    public List<BookResponseDto> getOverdueBooksByBorrower(int patronId)
    {
        return overdueBooksFinder.findOverdueBooksByPatronId(patronId);
    }

    @Override
    public CheckoutPolicyValidationResponseDto validateCheckoutPolicy(ScannedRFIDCopiesRequestDto request)
    {
        boolean haveOverdueCopies = false;
        boolean violatePolicy = false;
        boolean duplicateBook = false;
        List<String> reasons = new ArrayList<>();

        /*Get patron account*/
        Account patron = getAccountInfo(request.getPatronId());
        /*=================*/

        /*Check if patron is keeping any overdue book*/
        List<BookResponseDto> overdueBooks = overdueBooksFinder.findOverdueBooksByPatronId(patron.getId());
        if (!overdueBooks.isEmpty())
        {
            haveOverdueCopies = true;
            reasons.add(POLICY_KEEPING_OVERDUE);
        }
        /*================*/

        /*Check if patron is borrowing exceeding total allowance*/
        PatronType patronType = getPatronTypeInfo(patron.getPatronType().getId());
        int totalMaxAllowance = patronType.getMaxBorrowNumber();
        if (request.getBookRfidTags().size() > totalMaxAllowance)
        {
            violatePolicy = true;
            reasons.add(POLICY_EXCEEDS_TOTAL_BORROW_ALLOWANCE + totalMaxAllowance);
        }
        /*===============*/

        /*Check if patron is borrowing exceeding allowance for each copy_type*/
        // key = copy type id; value = number of copies
        HashSet<BookCopyType> copyTypeIdHashSet = new HashSet<>();
        List<Integer> copyTypeIdList = new ArrayList<>();
        for (String rfid : request.getBookRfidTags())
        {
            BookCopy bookCopy = getBookCopyInfoByRFID(rfid);
            if (bookCopy.getId() != null)
            {
                copyTypeIdList.add(bookCopy.getBookCopyType().getId());
                copyTypeIdHashSet.add(bookCopy.getBookCopyType());
            } else
            {
                violatePolicy = true;
                reasons.add(POLICY_INVALID_RFID + rfid);
            }
        }
        for (BookCopyType type : copyTypeIdHashSet)
        {
            BorrowPolicy tmp = getBorrowPolicy(patron.getPatronType().getId(), type.getId());
            int max = tmp.getMaxNumberCopyBorrow();
            System.out.println(type.getName() + " - " + max);
            if (max <= 0)
            {
                violatePolicy = true;
                reasons.add(POLICY_PATRON_TYPE_COPY_TYPE + type.getName());
            } else
            {
                if (Collections.frequency(copyTypeIdList, type.getId()) > max)
                {
                    violatePolicy = true;
                    reasons.add(POLICY_EXCEEDS_TYPE_BORROW_ALLOWANCE + type.getName());
                }
            }
        }
        /*==============*/

        /*Check if the patron is borrowing any duplicate copies of the same book*/
        HashSet<Book> bookHashSet = new HashSet<>();
        List<Integer> bookIdList = new ArrayList<>();
        for (String rfid : request.getBookRfidTags())
        {
            BookCopy bookCopy = getBookCopyInfoByRFID(rfid);
            if (bookCopy.getId() != null)
            {
                bookIdList.add(bookCopy.getBook().getId());
                bookHashSet.add(bookCopy.getBook());
            }
        }
        for (Book book : bookHashSet)
        {
            if (Collections.frequency(bookIdList, book.getId()) > 1)
            {
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


    private Account getAccountInfo(int id)
    {
        return accountRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Account", ACCOUNT_NOT_FOUND));
    }

    private PatronType getPatronTypeInfo(int id)
    {
        return patronTypeRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Patron Type", PATRON_TYPE_NOT_FOUND));
    }

    private BookCopy getBookCopyInfoByRFID(String rfid)
    {
        return bookCopyRepository.findByRfid(rfid).
                orElse(new BookCopy());
    }

    private BorrowPolicy getBorrowPolicy(int patronTypeId, int copyTypeId)
    {
        return borrowPolicyRepository.findByPatronTypeIdAndBookCopyTypeId(patronTypeId, copyTypeId).
                orElse(new BorrowPolicy());
    }

}