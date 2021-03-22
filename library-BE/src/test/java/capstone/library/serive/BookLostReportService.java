package capstone.library.serive;

import capstone.library.entities.*;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.BookBorrowingRepository;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.BookLostReportRepository;
import capstone.library.services.MailService;
import capstone.library.services.impl.BookLostReportServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static capstone.library.util.constants.ConstantUtil.CREATE_SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(MockitoJUnitRunner.class)
public class BookLostReportService {

    @Mock
    private BookBorrowingRepository bookBorrowingRepository;

    @Mock
    private BookLostReportRepository bookLostReportRepository;

    @Mock
    private MailService mailService;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private BookLostReportServiceImpl bookLostReportService;


    /**
     * Find book lost of patron
     */

    @Test
    public void testReportBookLostOfPatron_whenStartDateAfterEndDate(){
        LocalDateTime startDate = LocalDateTime.of(2021, 3, 18, 0,0,0);
        LocalDateTime endDate = LocalDateTime.of(2021, 3, 10, 0,0,0);

        Account borrower = new Account();
        borrower.setEmail("abc@fpt.edu.vn");

        Book book = new Book();
        book.setTitle("Hobbit");
        book.setSubtitle("test");
        book.setEdition(1);
        book.setIsbn("08367358327");

        BookCopy copy = new BookCopy();
        copy.setBook(book);
        copy.setBarcode("0172632763232");

        Borrowing borrowing = new Borrowing();
        borrowing.setBorrower(borrower);
        borrowing.setBorrowedAt(startDate);

        BookBorrowing bookBorrowing = new BookBorrowing();
        bookBorrowing.setBorrowing(borrowing);
        bookBorrowing.setBookCopy(copy);

        Mockito.when(bookBorrowingRepository
        .findById(anyInt()))
                .thenReturn(Optional.of(bookBorrowing));
        String msg = bookLostReportService.reportLostByPatron(1);
        Mockito.verify(bookLostReportRepository).save(any(BookLostReport.class));
        Mockito.verify(bookBorrowingRepository).save(any(BookBorrowing.class));
        Mockito.verify(bookCopyRepository).save(any(BookCopy.class));
        Assert.assertEquals(msg, CREATE_SUCCESS);
     }

    @Test(expected = ResourceNotFoundException.class)
    public void testReportBookLostOfPatron_whenBookBorrowingIsNull(){

        Mockito.when(bookBorrowingRepository
                .findById(anyInt()))
                .thenReturn(Optional.empty());
        bookLostReportService.reportLostByPatron(1);
        Mockito.verify(bookLostReportRepository, Mockito.times(0)).save(any(BookLostReport.class));
        Mockito.verify(bookBorrowingRepository, Mockito.times(0)).save(any(BookBorrowing.class));
        Mockito.verify(bookCopyRepository, Mockito.times(0)).save(any(BookCopy.class));
    }

}
