package capstone.library.services.impl;

import capstone.library.dtos.request.ScannedRFIDCopiesRequestDto;
import capstone.library.dtos.response.BookResponseDto;
import capstone.library.dtos.response.CheckoutBookResponseDto;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    DateTimeUtils dateTimeUtils = new DateTimeUtils();

    private static final String NOT_FOUND = " not found";

    /*Renew Index is used to determine if this book has been renew this time.
     * If the book has not been renewed, then Renew index is 0*/
    private static final int DEFAULT_RENEW_INDEX = 0;


    @Override
    @Transactional
    public List<CheckoutBookResponseDto> checkout(ScannedRFIDCopiesRequestDto scannedRFIDCopiesRequestDto)
    {
        List<CheckoutBookResponseDto> checkoutBookResponseDtos = new ArrayList<>();
        List<String> rfidTags = scannedRFIDCopiesRequestDto.getBookRfidTags();

        /*Get the librarian to add to issued_by in book_borrowing table*/
        Optional<Account> librarianOptional = accountRepository.findByIdAndRoleId(scannedRFIDCopiesRequestDto.getLibrarianId(), RoleIdEnum.LIBRARIAN.getRoleId());
        //Return 404 if no patron with 'getLibrarianId' is found
        if (librarianOptional.isEmpty())
        {
            throw new ResourceNotFoundException(
                    "Librarian",
                    "Librarian with id: " + scannedRFIDCopiesRequestDto.getPatronId() + NOT_FOUND);
        }
        Account issuingLibrarian = librarianOptional.get();
        /*========================*/

        /*Get the borrowing patron to add to borrowed_by in book_borrowing table*/
        Optional<Account> patronOptional = accountRepository.findByIdAndRoleId(scannedRFIDCopiesRequestDto.getPatronId(), RoleIdEnum.PATRON.getRoleId());
        //Return 404 if no patron with 'patronId' is found
        if (patronOptional.isEmpty())
        {
            throw new ResourceNotFoundException(
                    "Patron", "Patron with id: " + scannedRFIDCopiesRequestDto.getPatronId() + NOT_FOUND);
        }
        Account borrowingPatron = patronOptional.get();
        /*========================*/

        //Checkout books
        LocalDateTime now = LocalDateTime.now();
        for (String rfidTag :
                rfidTags)
        {
            CheckoutBookResponseDto copyDto = new CheckoutBookResponseDto();
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
                    copyDto.setAbleToBorrow(false);
                    copyDto.setReason(borrowingPatron.getRole().getName() + " cannot borrow this copy");
                } else
                {
                    int borrowDuration = policyOptional.get().getDueDuration();
                    dueAt = dueAt.plusDays(borrowDuration);
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
                        /*===========================*/

                        //Save bookBorrowing to db
                        bookBorrowingRepository.save(bookBorrowing);

                        //Add this book copy as is AbleToBorrow to response List
                        copyDto.setAbleToBorrow(true);
                        copyDto.setReason("");
                    } else
                    {
                        //Add this book copy as not AbleToBorrow to response List
                        copyDto.setAbleToBorrow(false);
                        copyDto.setReason("Book is not available");
                    }
                }
                /*====================*/

                //Add bookBorrowing to response dto
                copyDto.setRfid(rfidTag);
                copyDto.setDueDate(dueAt.toString());
                copyDto.setTitle(bookCopy.getBook().getTitle());
                copyDto.setSubtitle(bookCopy.getBook().getSubtitle());
                String authors = bookCopy.getBook().getBookAuthors().toString();
                authors = authors.replace("[", "");
                authors = authors.replace("]", "");
                copyDto.setAuthor(authors);
            } else
            {
                //Add bookBorrowing to response dto
                copyDto.setRfid(rfidTag);
                copyDto.setReason("Cannot find this book in database");
                copyDto.setDueDate("");
                copyDto.setTitle("");
            }
            checkoutBookResponseDtos.add(copyDto);
        }
        return checkoutBookResponseDtos;
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
//                int overdueDays = Period.between(bookBorrowing.getDueAt(), LocalDate.now()).getDays();
                int overdueDays = (int) dateTimeUtils.getOverdueDays(bookBorrowing.getDueAt(), LocalDate.now());
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
}
