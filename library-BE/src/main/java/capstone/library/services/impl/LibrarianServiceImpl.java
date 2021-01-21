package capstone.library.services.impl;

import capstone.library.dtos.request.BookCheckoutRequestDto;
import capstone.library.dtos.response.BookCheckoutResponseDto;
import capstone.library.entities.Account;
import capstone.library.entities.BookBorrowing;
import capstone.library.entities.BookCopy;
import capstone.library.entities.BorrowPolicy;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.RoleIdEnum;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.exceptions.UnauthorizedException;
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

    private static final int POLICY_ID = 1;
    private static final int DEFAULT_EXTEND_INDEX = 0;


    @Override
    @Transactional
    public List<BookCheckoutResponseDto> checkout(BookCheckoutRequestDto bookCheckoutRequestDto)
    {
        List<BookCheckoutResponseDto> bookCheckoutResponseDtos = new ArrayList<>();
        List<String> rfidTags = bookCheckoutRequestDto.getBookRfidTags();
        Optional<BorrowPolicy> policyOptional = borrowPolicyRepository.findById(POLICY_ID);

        /*Calculate the due date to add to borrowing record*/
        if (policyOptional.isEmpty())
        {
            ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException();
            resourceNotFoundException.setResourceName("Borrow Policy");
            resourceNotFoundException.setMessage("Policy with id: " + POLICY_ID + " does not exist");
            throw resourceNotFoundException;
        }
        int borrowDuration = policyOptional.get().getDueDuration();
        LocalDate dueAt = LocalDate.now().plusDays(borrowDuration);
        /*====================*/

        /*Get the borrowing patron to add to borrowed_by in book_borrowing table*/
        Optional<Account> patronOptional = accountRepository.findById(bookCheckoutRequestDto.getPatronId());
        //Return 404 if no patron with 'patronId' is found
        if (patronOptional.isEmpty())
        {
            ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException();
            resourceNotFoundException.setResourceName("Patron");
            resourceNotFoundException.setMessage("Patron with id: " + bookCheckoutRequestDto.getPatronId() + " does not exist");
            throw resourceNotFoundException;
        }
        Account borrowingPatron = patronOptional.get();
        /*========================*/
        /*Return 403 if this is not a patron id*/
        if (!borrowingPatron.getRole().getId().equals(RoleIdEnum.ROLE_PATRON.getRoleId()))
        {
            throw new UnauthorizedException("This is not a patron ID card");
        }
        /*====================================*/

        /*Get the issuing librarian to add to issued_by in book_borrowing table*/
        Optional<Account> librarianOptional = accountRepository.findById(bookCheckoutRequestDto.getLibrarianId());
        //Return 404 if no patron with 'getLibrarianId' is found
        if (librarianOptional.isEmpty())
        {
            ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException();
            resourceNotFoundException.setResourceName("Librarian");
            resourceNotFoundException.setMessage("Librarian with id: " + bookCheckoutRequestDto.getPatronId() + " does not exist");
            throw resourceNotFoundException;
        }
        /*========================*/
        Account issuingLibrarian = librarianOptional.get();
        /*Return 403 if this is not a patron id*/
        if (!issuingLibrarian.getRole().getId().equals(RoleIdEnum.ROLE_LIBRARIAN.getRoleId()))
        {
            throw new UnauthorizedException("This is not a librarian account");
        }
        /*====================================*/

        for (String rfidTag :
                rfidTags)
        {
            BookCheckoutResponseDto copyDto = new BookCheckoutResponseDto();
            Optional<BookCopy> bookCopyOptional = bookCopyRepository.findByRfid(rfidTag);
            if (bookCopyOptional.isPresent())
            {
                BookCopy bookCopy = bookCopyOptional.get();
                if (bookCopy.getStatus().equals(BookCopyStatus.AVAILABLE))
                {
                    /*Update book copy status in db*/
                    bookCopy.setStatus(BookCopyStatus.BORROWED);
                    /*============================*/

                    /*Create new book borrowing record in db*/
                    BookBorrowing bookBorrowing = new BookBorrowing();
                    bookBorrowing.setBorrower(borrowingPatron);
                    bookBorrowing.setIssued_by(issuingLibrarian);
                    bookBorrowing.setBookCopy(bookCopy);
                    bookBorrowing.setBorrowedAt(LocalDateTime.now());
                    bookBorrowing.setDueAt(dueAt);
                    bookBorrowing.setExtendIndex(DEFAULT_EXTEND_INDEX);
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

                //Add bookBorrowing to response dto
                copyDto.setRfid(rfidTag);
                copyDto.setDueDate(dueAt.toString());
                copyDto.setTitle(bookCopy.getBook().getTitle());
                bookCheckoutResponseDtos.add(copyDto);
            } else
            {
                //Add bookBorrowing to response dto
                copyDto.setRfid(rfidTag);
                copyDto.setReason("Cannot find this book in database");
                copyDto.setDueDate("");
                copyDto.setTitle("");
                bookCheckoutResponseDtos.add(copyDto);
            }
        }
        return bookCheckoutResponseDtos;
    }
}
