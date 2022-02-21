package capstone.library.serive;

import capstone.library.dtos.common.CheckoutCopyDto;
import capstone.library.dtos.request.ScannedRFIDCopiesRequestDto;
import capstone.library.dtos.response.CheckoutResponseDto;
import capstone.library.entities.*;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.RoleIdEnum;
import capstone.library.exceptions.InvalidRequestException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.*;
import capstone.library.services.LibrarianService;
import capstone.library.services.SecurityGateService;
import capstone.library.services.impl.LibrarianServiceImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LibrarianCheckoutTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private FeePolicyRepository feePolicyRepository;
    @Mock
    private BookCopyRepository bookCopyRepository;
    @Mock
    private BorrowPolicyRepository borrowPolicyRepository;
    @Mock
    private BookBorrowingRepository bookBorrowingRepository;
    @Mock
    private SecurityDeactivatedCopyRepository securityDeactivatedCopyRepository;
    @Mock
    private SecurityGateService securityGateService;

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
    private static final String BOOK_NOT_FOUND_MESSAGE = "Cannot find this book in database";
    private static final String NOT_FOUND = " not found";
    private static final String PATRON_INACTIVE = "This patron is inactive";

    ScannedRFIDCopiesRequestDto request;
    CheckoutResponseDto response;
    Account librarianAccount;
    Account patronAccount;
    FeePolicy feePolicy;
    List<FeePolicy> feePolicies;
    BookCopy bookCopy;
    BookBorrowing bookBorrowing;
    SecurityDeactivatedCopy securityDeactivatedCopy;
    BorrowPolicy borrowPolicy;
    PatronType patronType;
    BookCopyType bookCopyType;
    BookAuthor bookAuthor;
    Book book;
    Author author;
    CheckoutCopyDto dto;
    Role role;

    public LibrarianCheckoutTest() {
    }

    @Before
    public void init() {
        request = new ScannedRFIDCopiesRequestDto();

        response = new CheckoutResponseDto();

        librarianAccount = new Account();
        librarianAccount.setId(LIBRARIAN_ID);

        patronAccount = new Account();
        patronAccount.setId(PATRON_ID);

        feePolicy = new FeePolicy();
        feePolicy.setId(DEFAULT_ID);
        feePolicies = new ArrayList<>();
        feePolicies.add(feePolicy);

        bookCopy = new BookCopy();
        bookCopy.setId(DEFAULT_ID);

        borrowPolicy = new BorrowPolicy();

        bookBorrowing = new BookBorrowing();

        securityDeactivatedCopy = new SecurityDeactivatedCopy();

        patronType = new PatronType();

        bookCopyType = new BookCopyType();

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

        dto = new CheckoutCopyDto();

        role = new Role();
        role.setName(ROLE_NAME);

    }

    @Test
    public void checkoutSuccess() {
        //Mock librarian account
        when(accountRepository.findById(LIBRARIAN_ID)).thenReturn(Optional.of(librarianAccount));
        //Mock patron account
        patronAccount.setActive(true);
        patronType.setId(DEFAULT_ID);
        patronAccount.setPatronType(patronType);
        when(accountRepository.findByIdAndRoleId(PATRON_ID, RoleIdEnum.ROLE_PATRON.getRoleId()))
                .thenReturn(Optional.of(patronAccount));
        //Mock fee policy list
        when(feePolicyRepository.findAllByOrderByCreatedAtAsc()).thenReturn(feePolicies);
        //Mock book copy
        bookCopy.setBook(book);
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopy.setRfid(RFID);
        bookCopyType.setId(DEFAULT_ID);
        bookCopy.setBookCopyType(bookCopyType);
        when(bookCopyRepository.findByRfid(RFID)).thenReturn(Optional.of(bookCopy));
        //Mock borrow policy
        borrowPolicy.setDueDuration(DUE_DURATION);
        when(borrowPolicyRepository.
                findByPatronTypeIdAndBookCopyTypeId(patronAccount.getPatronType().getId(),
                        bookCopy.getBookCopyType().getId())).
                thenReturn(Optional.of(borrowPolicy));
        //Mock book borrowing
        bookBorrowing.setBookCopy(bookCopy);
        //assert results
        //set test request
        List<String> rfids = new ArrayList<>();
        rfids.add(RFID);
        request.setBookRfidTags(rfids);
        request.setPatronId(PATRON_ID);
        request.setLibrarianId(LIBRARIAN_ID);
        request.setCheckoutNote("");
        setSuccessResponse();
        assertEquals(response.getCheckoutCopyDto().get(0).isAbleToBorrow(),
                librarianService.checkout(request).getCheckoutCopyDto().get(0).isAbleToBorrow());
        assertEquals(response.getCheckoutCopyDto().get(0).getAuthor(),
                librarianService.checkout(request).getCheckoutCopyDto().get(0).getAuthor());
        assertEquals(response.getCheckoutCopyDto().get(0).getTitle(),
                librarianService.checkout(request).getCheckoutCopyDto().get(0).getTitle());
    }

    @Test
    public void notAbleToBorrow() {
        //Mock librarian account
        when(accountRepository.findById(LIBRARIAN_ID)).thenReturn(Optional.of(librarianAccount));
        //Mock patron account
        patronAccount.setRole(role);
        patronAccount.setActive(true);
        patronType.setId(DEFAULT_ID);
        patronAccount.setPatronType(patronType);
        when(accountRepository.findByIdAndRoleId(PATRON_ID, RoleIdEnum.ROLE_PATRON.getRoleId()))
                .thenReturn(Optional.of(patronAccount));
        //Mock fee policy list
        when(feePolicyRepository.findAllByOrderByCreatedAtAsc()).thenReturn(feePolicies);
        //Mock book copy
        bookCopy.setBook(book);
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopy.setRfid(RFID);
        bookCopyType.setId(DEFAULT_ID);
        bookCopy.setBookCopyType(bookCopyType);
        when(bookCopyRepository.findByRfid(RFID)).thenReturn(Optional.of(bookCopy));
        //Mock borrow policy
        when(borrowPolicyRepository.
                findByPatronTypeIdAndBookCopyTypeId(patronAccount.getPatronType().getId(),
                        bookCopy.getBookCopyType().getId())).
                thenReturn(Optional.empty());
        //assert results
        //set test request
        List<String> rfids = new ArrayList<>();
        rfids.add(RFID);
        request.setBookRfidTags(rfids);
        request.setPatronId(PATRON_ID);
        request.setLibrarianId(LIBRARIAN_ID);
        request.setCheckoutNote("");
        setSuccessResponse();
        assertFalse(librarianService.checkout(request).getCheckoutCopyDto().get(0).isAbleToBorrow());
    }

    @Test
    public void bookNotFound() {
        //Mock librarian account
        when(accountRepository.findById(LIBRARIAN_ID)).thenReturn(Optional.of(librarianAccount));
        //Mock patron account
        patronAccount.setRole(role);
        patronAccount.setActive(true);
        patronType.setId(DEFAULT_ID);
        patronAccount.setPatronType(patronType);
        when(accountRepository.findByIdAndRoleId(PATRON_ID, RoleIdEnum.ROLE_PATRON.getRoleId()))
                .thenReturn(Optional.of(patronAccount));
        //Mock fee policy list
        when(feePolicyRepository.findAllByOrderByCreatedAtAsc()).thenReturn(feePolicies);
        //Mock book copy
        bookCopy.setBook(book);
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopy.setRfid(RFID);
        bookCopyType.setId(DEFAULT_ID);
        bookCopy.setBookCopyType(bookCopyType);
        when(bookCopyRepository.findByRfid(RFID)).thenReturn(Optional.empty());
        //assert results
        //set test request
        List<String> rfids = new ArrayList<>();
        rfids.add(RFID);
        request.setBookRfidTags(rfids);
        request.setPatronId(PATRON_ID);
        request.setLibrarianId(LIBRARIAN_ID);
        request.setCheckoutNote("");
        setSuccessResponse();
        assertEquals(BOOK_NOT_FOUND_MESSAGE, librarianService.checkout(request).getCheckoutCopyDto().get(0).getReason());
    }

    @Test
    public void librarianNotFound() {
        //Mock librarian account
        when(accountRepository.findById(LIBRARIAN_ID)).thenReturn(Optional.empty());
        expectedException.expect(ResourceNotFoundException.class);
        expectedException.expectMessage("Librarian with id: " + librarianAccount.getId() + NOT_FOUND);
        //set test request
        request.setLibrarianId(LIBRARIAN_ID);
        given(librarianService.checkout(request)).willThrow(new ResourceNotFoundException());
    }

    @Test
    public void patronNotFound() {
        //Mock librarian account
        when(accountRepository.findById(LIBRARIAN_ID)).thenReturn(Optional.of(librarianAccount));
        //Mock patron account
        when(accountRepository.findByIdAndRoleId(PATRON_ID, RoleIdEnum.ROLE_PATRON.getRoleId()))
                .thenReturn(Optional.empty());
        expectedException.expect(ResourceNotFoundException.class);
        expectedException.expectMessage("Patron with id: " + patronAccount.getId() + NOT_FOUND);
        //set test request
        request.setPatronId(PATRON_ID);
        request.setLibrarianId(LIBRARIAN_ID);
        given(librarianService.checkout(request)).willThrow(new ResourceNotFoundException());
    }

    @Test
    public void feePolicyNotFound() {
        //Mock librarian account
        when(accountRepository.findById(LIBRARIAN_ID)).thenReturn(Optional.of(librarianAccount));
        //Mock patron account
        patronAccount.setRole(role);
        patronAccount.setActive(true);
        patronType.setId(DEFAULT_ID);
        patronAccount.setPatronType(patronType);
        when(accountRepository.findByIdAndRoleId(PATRON_ID, RoleIdEnum.ROLE_PATRON.getRoleId()))
                .thenReturn(Optional.of(patronAccount));
        //Mock fee policy list
        when(feePolicyRepository.findAllByOrderByCreatedAtAsc()).thenReturn(new ArrayList<>());
        expectedException.expect(ResourceNotFoundException.class);
        expectedException.expectMessage("Fee Policy " + NOT_FOUND);
        //set test request
        request.setPatronId(PATRON_ID);
        request.setLibrarianId(LIBRARIAN_ID);
        given(librarianService.checkout(request)).willThrow(new ResourceNotFoundException());
    }

    @Test
    public void patronInactive() {
        //Mock librarian account
        when(accountRepository.findById(LIBRARIAN_ID)).thenReturn(Optional.of(librarianAccount));
        //Mock patron account
        patronAccount.setRole(role);
        patronAccount.setActive(false);
        patronType.setId(DEFAULT_ID);
        patronAccount.setPatronType(patronType);
        when(accountRepository.findByIdAndRoleId(PATRON_ID, RoleIdEnum.ROLE_PATRON.getRoleId()))
                .thenReturn(Optional.of(patronAccount));
        expectedException.expect(InvalidRequestException.class);
        expectedException.expectMessage(PATRON_INACTIVE);
        //set test request
        request.setPatronId(PATRON_ID);
        request.setLibrarianId(LIBRARIAN_ID);
        given(librarianService.checkout(request)).willThrow(new InvalidRequestException());
    }

    private void setSuccessResponse() {
        dto.setRfid(RFID);
        dto.setTitle(BOOK_TITLE);
        dto.setSubtitle(BOOK_SUBTITLE);
        dto.setAuthor(AUTHOR_NAME);
        dto.setPublisher(BOOK_PUBLISHER);
        dto.setPublishYear(PUBLISH_YEAR);
        dto.setEdition(BOOK_EDITION);
        dto.setAbleToBorrow(true);
        dto.setReason("");
        List<CheckoutCopyDto> dtos = new ArrayList<>();
        dtos.add(dto);
        response.setCheckoutCopyDto(dtos);
    }

}
