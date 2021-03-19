package capstone.library.serive;

import capstone.library.dtos.response.BookResponseDto;
import capstone.library.entities.*;
import capstone.library.enums.BookCopyStatus;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.GenreRepository;
import capstone.library.repositories.MyBookRepository;
import capstone.library.services.BookService;
import capstone.library.services.impl.BookServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FindBookByISBNTest {
    @Mock
    private MyBookRepository myBookRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private BookCopyRepository bookCopyRepository;

    @InjectMocks
    private BookService bookService = new BookServiceImpl();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String ISBN = "1234567890";
    private static final String BOOK_NOT_FOUND = "Cannot find this book in the system";

    BookResponseDto dto;
    Book book;
    BookCopy bookCopy;
    Genre genre;
    BookAuthor bookAuthor;
    Author author;

    @Before
    public void init() {
        author = new Author();
        author.setName("John");
        dto = new BookResponseDto();
        book = new Book();
        bookAuthor = new BookAuthor();
        bookAuthor.setAuthor(author);
        Set<BookAuthor> bookAuthorSet = new HashSet<>();
        bookAuthorSet.add(bookAuthor);
        book.setBookAuthors(bookAuthorSet);
        book.setCallNumber("123.123 AAA 2001");

        bookCopy = new BookCopy();
        genre = new Genre();
        genre.setDdc(100.9);
        genre.setName("Book genre");
    }

    @Test
    public void findSuccess() {
        when(myBookRepository.findByIsbn(ISBN)).thenReturn(Optional.of(book));

        when(objectMapper.convertValue(book, BookResponseDto.class)).thenReturn(dto);

        List<Genre> genres = new ArrayList<>();
        genres.add(genre);
        when(genreRepository.findByOrderByDdcAsc()).thenReturn(genres);

        List<BookCopy> bookCopies = new ArrayList<>();
        bookCopies.add(bookCopy);
        when(bookCopyRepository.findByBookIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE)).thenReturn(bookCopies);

        assertEquals(dto, bookService.findByISBN(ISBN));
    }

    @Test
    public void bookNotFound() {
        when(myBookRepository.findByIsbn(ISBN)).thenReturn(Optional.empty());
        expectedException.expect(ResourceNotFoundException.class);
        expectedException.expectMessage(BOOK_NOT_FOUND);
        given(bookService.findByISBN(ISBN)).willThrow(new ResourceNotFoundException("Book", BOOK_NOT_FOUND));
    }


}
