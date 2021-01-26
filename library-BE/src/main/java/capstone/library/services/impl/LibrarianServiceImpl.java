package capstone.library.services.impl;

import capstone.library.dtos.request.ScannedRFIDBooksRequestDto;
import capstone.library.dtos.response.CheckoutBookResponseDto;
import capstone.library.dtos.response.ReturnBookResponseDto;
import capstone.library.entities.Account;
import capstone.library.entities.BookBorrowing;
import capstone.library.entities.BookCopy;
import capstone.library.entities.BorrowPolicy;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.RoleIdEnum;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.BookBorrowingRepository;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.BorrowPolicyRepository;
import capstone.library.services.LibrarianService;
import org.springframework.beans.factory.annotation.Autowired;
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

    /*Renew Index is used to determine if this book has been renew this time.
     * If the book has not been renewed, then Renew index is 0*/
    private static final int DEFAULT_RENEW_INDEX = 0;


    @Override
    @Transactional
    public List<CheckoutBookResponseDto> checkout(ScannedRFIDBooksRequestDto scannedRFIDBooksRequestDto)
    {
        List<CheckoutBookResponseDto> checkoutBookResponseDtos = new ArrayList<>();
        List<String> rfidTags = scannedRFIDBooksRequestDto.getBookRfidTags();

        /*Get the librarian to add to issued_by in book_borrowing table*/
        Optional<Account> librarianOptional = accountRepository.findByIdAndRoleId(scannedRFIDBooksRequestDto.getLibrarianId(), RoleIdEnum.LIBRARIAN.getRoleId());
        //Return 404 if no patron with 'getLibrarianId' is found
        if (librarianOptional.isEmpty())
        {
            ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException();
            resourceNotFoundException.setResourceName("Librarian");
            resourceNotFoundException.setMessage("Librarian with id: " + scannedRFIDBooksRequestDto.getPatronId() + " does not exist");
            throw resourceNotFoundException;
        }
        Account issuingLibrarian = librarianOptional.get();
        /*========================*/

        /*Get the borrowing patron to add to borrowed_by in book_borrowing table*/
        Optional<Account> patronOptional = accountRepository.findByIdAndRoleId(scannedRFIDBooksRequestDto.getPatronId(), RoleIdEnum.PATRON.getRoleId());
        //Return 404 if no patron with 'patronId' is found
        if (patronOptional.isEmpty())
        {
            ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException();
            resourceNotFoundException.setResourceName("Patron");
            resourceNotFoundException.setMessage("Patron with id: " + scannedRFIDBooksRequestDto.getPatronId() + " does not exist");
            throw resourceNotFoundException;
        }
        Account borrowingPatron = patronOptional.get();
        /*========================*/

        //Checkout books
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
                        bookBorrowing.setBorrowedAt(LocalDateTime.now());
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

    @Override
    @Transactional
    public List<ReturnBookResponseDto> returnBooks(ScannedRFIDBooksRequestDto scannedRFIDBooksRequestDto)
    {
        List<BookCopy> bookCopies = new ArrayList<>();
        List<ReturnBookResponseDto> responseDtos = new ArrayList<>();

        /*Get librarian*/
        Optional<Account> librarianOptional = accountRepository.findById(scannedRFIDBooksRequestDto.getLibrarianId());
        if (librarianOptional.isEmpty())
        {
            ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException();
            resourceNotFoundException.setResourceName("Librarian");
            resourceNotFoundException.setMessage("Librarian with id: " + scannedRFIDBooksRequestDto.getLibrarianId() + " does not exist");
            throw resourceNotFoundException;
        }
        Account librarian = librarianOptional.get();

        /*Check to make sure book copy for each rfidTag is in DB.
         * Only update DB for book copies that are found*/
        for (String rfidTag : scannedRFIDBooksRequestDto.getBookRfidTags())
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
        for (BookCopy bookCopy : bookCopies)
        {
            ReturnBookResponseDto dto = new ReturnBookResponseDto();
            dto.setOverdue(false);
            Optional<BookBorrowing> bookBorrowingOptional = bookBorrowingRepository.
                    findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(bookCopy.getId());
            if (bookBorrowingOptional.isPresent())
            {
                BookBorrowing bookBorrowing = bookBorrowingOptional.get();
                int patronTypeId = bookBorrowing.getBorrower().getId();
                double fineRate;
                double fine = 0;
                //Returns >0 if today has passed overdue date
                int overdueDays = LocalDate.now().compareTo(bookBorrowing.getDueAt());
                if (overdueDays > 0)
                {
                    Optional<BorrowPolicy> borrowPolicyOptional = borrowPolicyRepository.
                            findByPatronTypeIdAndBookCopyTypeId(patronTypeId, bookCopy.getBookCopyType().getId());
                    if (borrowPolicyOptional.isPresent())
                    {
                        double bookCopyPrice = bookCopy.getPrice();
                        fineRate = borrowPolicyOptional.get().getOverdueFinePerDay();
                        fine = fineRate * overdueDays;
                        if (fine >= bookCopyPrice)
                        {
                            fine = bookCopyPrice;
                        }
                        dto.setFine(fine);
                        dto.setReason("Return late: " + overdueDays + " (days)");
                        dto.setOverdue(true);
                    }
                }
                /*Update borrowing_book table
                 * Add return date and fine*/
                bookBorrowing.setReturn_by(librarian);
                bookBorrowing.setReturnedAt(LocalDateTime.now());
                bookBorrowing.setFine(fine);
                bookBorrowingRepository.save(bookBorrowing);

                /*Update book_copy status from BORROWED to AVAILABLE*/
                bookCopy.setStatus(BookCopyStatus.AVAILABLE);
                bookCopyRepository.save(bookCopy);

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
}
