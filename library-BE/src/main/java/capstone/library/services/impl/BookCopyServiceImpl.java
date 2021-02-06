package capstone.library.services.impl;

import capstone.library.dtos.common.MyAccountDto;
import capstone.library.dtos.common.MyBookDto;
import capstone.library.dtos.request.CreateCopiesRequestDto;
import capstone.library.dtos.response.CheckCopyPolicyResponseDto;
import capstone.library.dtos.response.CopyResponseDto;
import capstone.library.entities.*;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.BookStatus;
import capstone.library.enums.ErrorStatus;
import capstone.library.enums.RoleIdEnum;
import capstone.library.exceptions.CustomException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.*;
import capstone.library.services.BookCopyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class BookCopyServiceImpl implements BookCopyService
{
    @Autowired
    BookCopyRepository bookCopyRepository;
    @Autowired
    MyBookRepository myBookRepository;
    @Autowired
    BookCopyTypeRepository bookCopyTypeRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    BorrowPolicyRepository borrowPolicyRepository;
    @Autowired
    BookBorrowingRepository bookBorrowingRepository;
    @Autowired
    ObjectMapper objectMapper;

    private static final String PATRON_NOT_FOUND = "Cannot find this patron in database";
    private static final String ACCOUNT_NOT_FOUND = "Cannot find this account in database";
    private static final String COPY_NOT_FOUND = "Cannot find this book copy in database";
    private static final String BOOK_COPY_NOT_FOUND = "Cannot find this book copy in the database";
    private static final String BOOK_COPY = "Book copy";
    private static final String BOOK = "Book";
    private static final String POLICY_PATRON_TYPE_COPY_TYPE = "This patron cannot borrow this copy";
    private static final String POLICY_BOOK_STATUS = "This book is not in circulation";
    private static final String POLICY_COPY_STATUS = "This copy is not available";
    private static final BookCopyStatus NEW_COPY_STATUS = BookCopyStatus.IN_PROCESS;

    @Override
    @Transactional
    public String createCopies(CreateCopiesRequestDto request)
    {
        Book book;
        BookCopyType bookCopyType;
        Account creator;

        /*Get Book from db throw exception if book is not found
        Get Copy type from db throw exception if copy type is not found
        Get Account (creator) from db throw exception if it is not found*/
        Optional<Book> bookOptional = myBookRepository.findById(request.getBookId());
        Optional<BookCopyType> bookCopyTypeOptional = bookCopyTypeRepository.findById(request.getCopyTypeId());
        Optional<Account> accountOptional = accountRepository.findById(request.getCreatorId());
        if (bookOptional.isPresent() && bookCopyTypeOptional.isPresent() && accountOptional.isPresent())
        {
            book = bookOptional.get();
            bookCopyType = bookCopyTypeOptional.get();
            creator = accountOptional.get();
        } else
        {
            throw new ResourceNotFoundException("Book", ErrorStatus.RESOURCE_NOT_FOUND.getReason());
        }

        try
        {
            insertCopies(request.getBarcodes(), request.getPrice(), book, bookCopyType, creator);
            updateBookNumberOfCopy(book);
        } catch (Exception e)
        {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorStatus.COMMON_DATABSE_ERROR.getReason(), e.getLocalizedMessage());
        }

        return "Success";
    }

    @Override
    public Page<CopyResponseDto> getCopiesList(Pageable pageable)
    {
        List<CopyResponseDto> response = new ArrayList<>();
        Page<BookCopy> bookCopiesPage = bookCopyRepository.findAll(pageable);
        for (BookCopy copy : bookCopiesPage.getContent())
        {
            CopyResponseDto dto;
            dto = objectMapper.convertValue(copy, CopyResponseDto.class);
            dto.getBook().setAuthors(copy.getBook().getBookAuthors().
                    toString().replace("]", "").replace("[", ""));
            dto.getBook().setGenres(copy.getBook().getBookGenres().
                    toString().replace("]", "").replace("[", ""));
            dto.setCopyType(copy.getBookCopyType().getName());
            response.add(dto);
        }
//        return new PageImpl<CopyResponseDto>(response, pageable, response.size());
        return new PageImpl<CopyResponseDto>(response, pageable, bookCopiesPage.getTotalElements());
    }

    @Override
    public String tagCopy(String barcode, String rfid)
    {
        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findByBarcode(barcode);
        if (bookCopyOptional.isPresent())
        {
            BookCopy bookCopy = bookCopyOptional.get();
            Optional<Book> bookOptional = myBookRepository.findById(bookCopy.getBook().getId());
            if (bookOptional.isPresent())
            {
                Book book = bookOptional.get();
                bookCopy.setRfid(rfid);
                if (book.getStatus().equals(BookStatus.IN_CIRCULATION))
                {
                    bookCopy.setStatus(BookCopyStatus.AVAILABLE);
                } else if (book.getStatus().equals(BookStatus.OUT_OF_CIRCULATION))
                {
                    bookCopy.setStatus(BookCopyStatus.OUT_OF_CIRCULATION);
                } else if (book.getStatus().equals(BookStatus.LIB_USE_ONLY))
                {
                    bookCopy.setStatus(BookCopyStatus.LIB_USE_ONLY);
                }
                try
                {
                    bookCopyRepository.save(bookCopy);
                } catch (Exception e)
                {
                    throw new CustomException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            ErrorStatus.COMMON_DATABSE_ERROR.getReason(), e.getLocalizedMessage());
                }
            }

        } else
        {
            throw new ResourceNotFoundException(BOOK_COPY, COPY_NOT_FOUND + ": " + barcode);
        }
        return "Success";
    }

    @Override
    public CheckCopyPolicyResponseDto validateCopyByRFID(String rfid, int patronId)
    {
        boolean violatePolicy = false;
        List<String> reasons = new ArrayList<>();
        CheckCopyPolicyResponseDto response = new CheckCopyPolicyResponseDto();
        Account patron;
        BookCopy bookCopy;

        /*Get Patron and Copy*/
        Optional<Account> patronOptional = accountRepository.findById(patronId);
        if (patronOptional.isPresent())
        {
            patron = patronOptional.get();
            if (patron.getRole().getId() != RoleIdEnum.ROLE_PATRON.getRoleId())
            {
                throw new ResourceNotFoundException("Patron", PATRON_NOT_FOUND);
            }
        } else
        {
            throw new ResourceNotFoundException("Account", ACCOUNT_NOT_FOUND);
        }

        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findByRfid(rfid);
        if (bookCopyOptional.isPresent())
        {
            bookCopy = bookCopyOptional.get();
        } else
        {
            throw new ResourceNotFoundException("Copy", COPY_NOT_FOUND);
        }
        /*=====================*/

        /* 1. Check policy
         * 2. Check book status
         * 3. Check copy status*/
        // 1
        Optional<BorrowPolicy> borrowPolicyOptional = borrowPolicyRepository.
                findByPatronTypeIdAndBookCopyTypeId(patron.getPatronType().getId(), bookCopy.getBookCopyType().getId());
        if (borrowPolicyOptional.isEmpty())
        {
            violatePolicy = true;
            reasons.add(POLICY_PATRON_TYPE_COPY_TYPE);
        }
        // 2
        if (!bookCopy.getBook().getStatus().equals(BookStatus.IN_CIRCULATION))
        {
            violatePolicy = true;
            reasons.add(POLICY_BOOK_STATUS);
        }
        // 3
        if (!bookCopy.getStatus().equals(BookCopyStatus.AVAILABLE))
        {
            violatePolicy = true;
            reasons.add(POLICY_COPY_STATUS);
        }
        /*===========*/

        /*Prepare response*/
        response.setCopy(objectMapper.convertValue(bookCopy.getBook(), MyBookDto.class));
        response.getCopy().setGenres(bookCopy.getBook().getBookGenres().toString().
                replace("]", "").replace("[", ""));
        response.getCopy().setAuthors(bookCopy.getBook().getBookAuthors().toString().
                replace("]", "").replace("[", ""));
        response.setViolatePolicy(violatePolicy);
        response.setReasons(reasons);
        /*=================*/


        return response;
    }

    @Override
    public CopyResponseDto getCopyByBarcode(String barcode)
    {
        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findByBarcode(barcode);
        return getCopyResponseDto(bookCopyOptional);
    }

    @Override
    public CopyResponseDto getCopyByRfid(String rfid)
    {
        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findByRfid(rfid);
        return getCopyResponseDto(bookCopyOptional);
    }

    private CopyResponseDto getCopyResponseDto(Optional<BookCopy> bookCopyOptional)
    {
        if (bookCopyOptional.isPresent())
        {
            BookCopy copy = bookCopyOptional.get();
            CopyResponseDto dto = objectMapper.convertValue(copy, CopyResponseDto.class);
            dto.getBook().setAuthors(copy.getBook().getBookAuthors().
                    toString().replace("]", "").replace("[", ""));
            dto.getBook().setGenres(copy.getBook().getBookGenres().
                    toString().replace("]", "").replace("[", ""));
            dto.setCopyType(copy.getBookCopyType().getName());
            if (copy.getStatus().equals(BookCopyStatus.BORROWED))
            {
                Optional<BookBorrowing> bookBorrowingOptional =
                        bookBorrowingRepository.findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(copy.getId());
                if (bookBorrowingOptional.isPresent())
                {
                    Account borrower = bookBorrowingOptional.get().getBorrower();
                    dto.setBorrower(objectMapper.convertValue(borrower, MyAccountDto.class));
                    dto.getBorrower().setPatronTypeName(borrower.getPatronType().getName());
                    dto.getBorrower().setRoleName(borrower.getRole().getName());
                }
            }
            return dto;
        }
        throw new ResourceNotFoundException("Book Copy", BOOK_COPY_NOT_FOUND);
    }

    private void insertCopies(Set<String> barcodes, double price, Book book, BookCopyType bookCopyType, Account creator) throws Exception
    {
        Set<BookCopy> bookCopies = new HashSet<>();
        for (String barcode : barcodes)
        {
            BookCopy bookCopy = new BookCopy();
            bookCopy.setBook(book);
            bookCopy.setBookCopyType(bookCopyType);
            bookCopy.setPrice(price);
            bookCopy.setStatus(NEW_COPY_STATUS);
            bookCopy.setCreator(creator);
            bookCopy.setBarcode(barcode.toUpperCase().replace(" ", ""));
            bookCopies.add(bookCopy);
        }
        bookCopyRepository.saveAll(bookCopies);
    }


    private void updateBookNumberOfCopy(Book book) throws Exception
    {
        book.setNumberOfCopy(bookCopyRepository.findByBookId(book.getId()).size());
        myBookRepository.save(book);
    }
}
