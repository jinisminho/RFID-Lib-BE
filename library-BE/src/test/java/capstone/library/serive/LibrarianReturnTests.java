package capstone.library.serive;

import capstone.library.dtos.common.MyBookDto;
import capstone.library.dtos.request.ScannedRFIDCopiesRequestDto;
import capstone.library.dtos.response.ReturnBookResponseDto;
import capstone.library.entities.*;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.BookStatus;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.BookBorrowingRepository;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.FeePolicyRepository;
import capstone.library.services.LibrarianService;
import capstone.library.services.SecurityGateService;
import capstone.library.services.impl.LibrarianServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LibrarianReturnTests {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private BookCopyRepository bookCopyRepository;
    @Mock
    private BookBorrowingRepository bookBorrowingRepository;
    @Mock
    private FeePolicyRepository feePolicyRepository;
    @Mock
    private SecurityGateService securityGateService;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private final LibrarianService librarianService = new LibrarianServiceImpl();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final int LIBRARIAN_ID = 2;
    private static final int PATRON_ID = 7;
    private static final int DEFAULT_ID = 1;
    private static final String RFID = "1234567890";
    private static final int DUE_DURATION = 7;
    private static final String BOOK_TITLE = "title";
    private static final String BOOK_SUBTITLE = "subtitle";
    private static final String BOOK_PUBLISHER = "publisher";
    private static final int BOOK_EDITION = 1;
    private static final int PUBLISH_YEAR = 2010;
    private static final String AUTHOR_NAME = "John";
    private static final String ROLE_NAME = "Student";
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);
    private static final LocalDateTime BORROWED_AT = LocalDateTime.now().minusDays(7);
    private static final double MAX_DELTA = 0;
    private static final double FINE_RATE = 5000;
    private static final double BOOK_PRICE_HIGH = 75000;
    private static final double BOOK_PRICE_LOW = 1000;
    private static final int MAX_OVERDUE_FINE_PERCENTAGE = 100;
    private static final int OVERDUE_DAYS = 3;
    private static final LocalDate OVERDUE_DATE = LocalDate.now().minusDays(OVERDUE_DAYS);
    private static final String NOT_FOUND = " not found";

    ScannedRFIDCopiesRequestDto request;
    ReturnBookResponseDto responseDto;
    Account librarianAccount;
    Account patronAccount;
    BookCopy bookCopy;
    BookAuthor bookAuthor;
    Book book;
    Author author;
    BookBorrowing bookBorrowing;
    FeePolicy feePolicy;
    Borrowing borrowing;
    BookCopyType bookCopyType;

    @Before
    public void init() {
        request = new ScannedRFIDCopiesRequestDto();

        responseDto = new ReturnBookResponseDto();

        librarianAccount = new Account();

        patronAccount = new Account();

        bookCopy = new BookCopy();

        author = new Author();
        author.setId(DEFAULT_ID);
        author.setName(AUTHOR_NAME);

        bookAuthor = new BookAuthor();
        bookAuthor.setAuthor(author);
        bookAuthor.setBook(book);
        bookAuthor.setId(DEFAULT_ID);

        book = new Book();
        book.setId(DEFAULT_ID);
        book.setTitle(BOOK_TITLE);
        book.setSubtitle(BOOK_SUBTITLE);
        book.setEdition(BOOK_EDITION);
        book.setPublisher(BOOK_PUBLISHER);
        book.setPublishYear(PUBLISH_YEAR);
        Set<BookAuthor> bookAuthors = new HashSet<>();
        bookAuthors.add(bookAuthor);
        book.setBookAuthors(bookAuthors);

        bookBorrowing = new BookBorrowing();

        feePolicy = new FeePolicy();

        borrowing = new Borrowing();

        bookCopyType = new BookCopyType();
    }

    private void setRequest() {
        request.setLibrarianId(LIBRARIAN_ID);
        request.setPatronId(PATRON_ID);
        List<String> rfids = new ArrayList<>();
        rfids.add(RFID);
        request.setBookRfidTags(rfids);
        request.setCheckin(true);
    }


    @Test
    public void returnSuccessBookInCirculation() {
        setRequest();
        //Mock borrower account
        patronAccount.setId(PATRON_ID);
        //Mock librarian account
        librarianAccount.setId(LIBRARIAN_ID);
        when(accountRepository.findById(request.getLibrarianId())).thenReturn(Optional.of(librarianAccount));
        //Mock book copies
        book.setStatus(BookStatus.IN_CIRCULATION);
        bookCopy.setBook(book);
        bookCopy.setId(DEFAULT_ID);
        bookCopy.setPrice(BOOK_PRICE_HIGH);
        bookCopyType.setId(DEFAULT_ID);
        bookCopy.setBookCopyType(bookCopyType);
        when(bookCopyRepository.findByRfidAndStatus(RFID, BookCopyStatus.BORROWED)).thenReturn(Optional.of(bookCopy));
        //Mock borrowing
        borrowing.setBorrowedAt(BORROWED_AT);
        borrowing.setBorrower(patronAccount);
        //Mock book borrowing
        bookBorrowing.setDueAt(TOMORROW);
        feePolicy.setId(DEFAULT_ID);
        feePolicy.setOverdueFinePerDay(FINE_RATE);
        feePolicy.setMaxPercentageOverdueFine(MAX_OVERDUE_FINE_PERCENTAGE);
        bookBorrowing.setFeePolicy(feePolicy);
        bookBorrowing.setBookCopy(bookCopy);
        bookBorrowing.setBorrowing(borrowing);
        when(bookBorrowingRepository.findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(bookCopy.getId())).
                thenReturn(Optional.of(bookBorrowing));
        //Mock object mapper
        when(objectMapper.convertValue(bookCopy.getBook(), MyBookDto.class)).thenReturn(new MyBookDto());
        //assert results
        List<ReturnBookResponseDto> response = new ArrayList<>();
        response.add(responseDto);
        assertEquals(response.get(0).getFine(), librarianService.returnBookCopies(request).get(0).getFine(), MAX_DELTA);
    }

    @Test
    public void returnSuccessBookDiscard() {
        setRequest();
        //Mock borrower account
        patronAccount.setId(PATRON_ID);
        //Mock librarian account
        librarianAccount.setId(LIBRARIAN_ID);
        when(accountRepository.findById(request.getLibrarianId())).thenReturn(Optional.of(librarianAccount));
        //Mock book copies
        book.setStatus(BookStatus.DISCARD);
        bookCopy.setBook(book);
        bookCopy.setId(DEFAULT_ID);
        bookCopy.setPrice(BOOK_PRICE_HIGH);
        bookCopyType.setId(DEFAULT_ID);
        bookCopy.setBookCopyType(bookCopyType);
        when(bookCopyRepository.findByRfidAndStatus(RFID, BookCopyStatus.BORROWED)).thenReturn(Optional.of(bookCopy));
        //Mock borrowing
        borrowing.setBorrowedAt(BORROWED_AT);
        borrowing.setBorrower(patronAccount);
        //Mock book borrowing
        bookBorrowing.setDueAt(TOMORROW);
        feePolicy.setId(DEFAULT_ID);
        feePolicy.setOverdueFinePerDay(FINE_RATE);
        feePolicy.setMaxPercentageOverdueFine(MAX_OVERDUE_FINE_PERCENTAGE);
        bookBorrowing.setFeePolicy(feePolicy);
        bookBorrowing.setBookCopy(bookCopy);
        bookBorrowing.setBorrowing(borrowing);
        when(bookBorrowingRepository.findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(bookCopy.getId())).
                thenReturn(Optional.of(bookBorrowing));
        //Mock object mapper
        when(objectMapper.convertValue(bookCopy.getBook(), MyBookDto.class)).thenReturn(new MyBookDto());
        //assert results
        List<ReturnBookResponseDto> response = new ArrayList<>();
        response.add(responseDto);
        assertEquals(response.get(0).getFine(), librarianService.returnBookCopies(request).get(0).getFine(), MAX_DELTA);
    }

    @Test
    public void returnSuccessBookLibUseOnly() {
        setRequest();
        //Mock borrower account
        patronAccount.setId(PATRON_ID);
        //Mock librarian account
        librarianAccount.setId(LIBRARIAN_ID);
        when(accountRepository.findById(request.getLibrarianId())).thenReturn(Optional.of(librarianAccount));
        //Mock book copies
        book.setStatus(BookStatus.LIB_USE_ONLY);
        bookCopy.setBook(book);
        bookCopy.setId(DEFAULT_ID);
        bookCopy.setPrice(BOOK_PRICE_HIGH);
        bookCopyType.setId(DEFAULT_ID);
        bookCopy.setBookCopyType(bookCopyType);
        when(bookCopyRepository.findByRfidAndStatus(RFID, BookCopyStatus.BORROWED)).thenReturn(Optional.of(bookCopy));
        //Mock borrowing
        borrowing.setBorrowedAt(BORROWED_AT);
        borrowing.setBorrower(patronAccount);
        //Mock book borrowing
        bookBorrowing.setDueAt(TOMORROW);
        feePolicy.setId(DEFAULT_ID);
        feePolicy.setOverdueFinePerDay(FINE_RATE);
        feePolicy.setMaxPercentageOverdueFine(MAX_OVERDUE_FINE_PERCENTAGE);
        bookBorrowing.setFeePolicy(feePolicy);
        bookBorrowing.setBookCopy(bookCopy);
        bookBorrowing.setBorrowing(borrowing);
        when(bookBorrowingRepository.findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(bookCopy.getId())).
                thenReturn(Optional.of(bookBorrowing));
        //Mock object mapper
        when(objectMapper.convertValue(bookCopy.getBook(), MyBookDto.class)).thenReturn(new MyBookDto());
        //assert results
        List<ReturnBookResponseDto> response = new ArrayList<>();
        response.add(responseDto);
        assertEquals(response.get(0).getFine(), librarianService.returnBookCopies(request).get(0).getFine(), MAX_DELTA);
    }

    @Test
    public void returnSuccessBookOverdue() {
        setRequest();
        //Mock borrower account
        patronAccount.setId(PATRON_ID);
        //Mock librarian account
        librarianAccount.setId(LIBRARIAN_ID);
        when(accountRepository.findById(request.getLibrarianId())).thenReturn(Optional.of(librarianAccount));
        //Mock book copies
        book.setStatus(BookStatus.IN_CIRCULATION);
        bookCopy.setBook(book);
        bookCopy.setId(DEFAULT_ID);
        bookCopy.setPrice(BOOK_PRICE_HIGH);
        bookCopyType.setId(DEFAULT_ID);
        bookCopy.setBookCopyType(bookCopyType);
        when(bookCopyRepository.findByRfidAndStatus(RFID, BookCopyStatus.BORROWED)).thenReturn(Optional.of(bookCopy));
        //Mock borrowing
        borrowing.setBorrowedAt(BORROWED_AT);
        borrowing.setBorrower(patronAccount);
        //Mock book borrowing
        bookBorrowing.setDueAt(OVERDUE_DATE);
        feePolicy.setId(DEFAULT_ID);
        feePolicy.setOverdueFinePerDay(FINE_RATE);
        feePolicy.setMaxPercentageOverdueFine(MAX_OVERDUE_FINE_PERCENTAGE);
        bookBorrowing.setFeePolicy(feePolicy);
        bookBorrowing.setBookCopy(bookCopy);
        bookBorrowing.setBorrowing(borrowing);
        when(bookBorrowingRepository.findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(bookCopy.getId())).
                thenReturn(Optional.of(bookBorrowing));
        //Mock fee policy
        when(feePolicyRepository.findById(bookBorrowing.getFeePolicy().getId())).thenReturn(Optional.of(feePolicy));
        //Mock object mapper
        when(objectMapper.convertValue(bookCopy.getBook(), MyBookDto.class)).thenReturn(new MyBookDto());
        //assert results
        responseDto.setFine(OVERDUE_DAYS * FINE_RATE);
        List<ReturnBookResponseDto> response = new ArrayList<>();
        response.add(responseDto);
        assertEquals(response.get(0).getFine(), librarianService.returnBookCopies(request).get(0).getFine(), MAX_DELTA);
    }

    @Test
    public void returnSuccessDueDateBoundary() {
        setRequest();
        //Mock borrower account
        patronAccount.setId(PATRON_ID);
        //Mock librarian account
        librarianAccount.setId(LIBRARIAN_ID);
        when(accountRepository.findById(request.getLibrarianId())).thenReturn(Optional.of(librarianAccount));
        //Mock book copies
        book.setStatus(BookStatus.IN_CIRCULATION);
        bookCopy.setBook(book);
        bookCopy.setId(DEFAULT_ID);
        bookCopy.setPrice(BOOK_PRICE_HIGH);
        bookCopyType.setId(DEFAULT_ID);
        bookCopy.setBookCopyType(bookCopyType);
        when(bookCopyRepository.findByRfidAndStatus(RFID, BookCopyStatus.BORROWED)).thenReturn(Optional.of(bookCopy));
        //Mock borrowing
        borrowing.setBorrowedAt(BORROWED_AT);
        borrowing.setBorrower(patronAccount);
        //Mock book borrowing
        bookBorrowing.setDueAt(TODAY);
        feePolicy.setId(DEFAULT_ID);
        feePolicy.setOverdueFinePerDay(FINE_RATE);
        feePolicy.setMaxPercentageOverdueFine(MAX_OVERDUE_FINE_PERCENTAGE);
        bookBorrowing.setFeePolicy(feePolicy);
        bookBorrowing.setBookCopy(bookCopy);
        bookBorrowing.setBorrowing(borrowing);
        when(bookBorrowingRepository.findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(bookCopy.getId())).
                thenReturn(Optional.of(bookBorrowing));
        //Mock object mapper
        when(objectMapper.convertValue(bookCopy.getBook(), MyBookDto.class)).thenReturn(new MyBookDto());
        //assert results
        responseDto.setFine(0);
        List<ReturnBookResponseDto> response = new ArrayList<>();
        response.add(responseDto);
        assertEquals(response.get(0).getFine(), librarianService.returnBookCopies(request).get(0).getFine(), MAX_DELTA);
    }

    @Test
    public void returnSuccessBookOverdueExceedsMaxFine() {
        setRequest();
        //Mock borrower account
        patronAccount.setId(PATRON_ID);
        //Mock librarian account
        librarianAccount.setId(LIBRARIAN_ID);
        when(accountRepository.findById(request.getLibrarianId())).thenReturn(Optional.of(librarianAccount));
        //Mock book copies
        book.setStatus(BookStatus.IN_CIRCULATION);
        bookCopy.setBook(book);
        bookCopy.setId(DEFAULT_ID);
        bookCopy.setPrice(BOOK_PRICE_LOW);
        bookCopyType.setId(DEFAULT_ID);
        bookCopy.setBookCopyType(bookCopyType);
        when(bookCopyRepository.findByRfidAndStatus(RFID, BookCopyStatus.BORROWED)).thenReturn(Optional.of(bookCopy));
        //Mock borrowing
        borrowing.setBorrowedAt(BORROWED_AT);
        borrowing.setBorrower(patronAccount);
        //Mock book borrowing
        bookBorrowing.setDueAt(OVERDUE_DATE);
        feePolicy.setId(DEFAULT_ID);
        feePolicy.setOverdueFinePerDay(FINE_RATE);
        feePolicy.setMaxPercentageOverdueFine(MAX_OVERDUE_FINE_PERCENTAGE);
        bookBorrowing.setFeePolicy(feePolicy);
        bookBorrowing.setBookCopy(bookCopy);
        bookBorrowing.setBorrowing(borrowing);
        when(bookBorrowingRepository.findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(bookCopy.getId())).
                thenReturn(Optional.of(bookBorrowing));
        //Mock fee policy
        when(feePolicyRepository.findById(bookBorrowing.getFeePolicy().getId())).thenReturn(Optional.of(feePolicy));
        //Mock object mapper
        when(objectMapper.convertValue(bookCopy.getBook(), MyBookDto.class)).thenReturn(new MyBookDto());
        //assert results
        responseDto.setFine(BOOK_PRICE_LOW);
        List<ReturnBookResponseDto> response = new ArrayList<>();
        response.add(responseDto);
        assertEquals(response.get(0).getFine(), librarianService.returnBookCopies(request).get(0).getFine(), MAX_DELTA);
    }

    @Test
    public void librarianNotFound() {
        setRequest();
        //Mock librarian account
        when(accountRepository.findById(request.getLibrarianId())).thenReturn(Optional.empty());
        expectedException.expect(ResourceNotFoundException.class);
        expectedException.expectMessage("Librarian with id: " + request.getLibrarianId() + NOT_FOUND);
        given(librarianService.returnBookCopies(request)).willThrow(new ResourceNotFoundException());
    }
}
