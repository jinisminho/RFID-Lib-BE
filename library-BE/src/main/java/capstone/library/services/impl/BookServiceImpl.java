package capstone.library.services.impl;

import capstone.library.dtos.common.BookCopyDto;
import capstone.library.dtos.request.AddBookRequestDto;
import capstone.library.dtos.response.BookResDto;
import capstone.library.dtos.response.BookResponseDto;
import capstone.library.entities.*;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.BookStatus;
import capstone.library.exceptions.CustomException;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.mappers.BookCopyMapper;
import capstone.library.mappers.BookMapper;
import capstone.library.repositories.*;
import capstone.library.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService
{

    @Autowired
    private BookCopyRepository bookCopyRepository;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookCopyMapper bookCopyMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MyBookRepository myBookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private GenreRepository genreRepository;

    @Override
    public Page<BookResDto> findBooks(String searchValue, Pageable pageable)
    {
        if (searchValue == null)
        {
            throw new MissingInputException("Missing search value for search book");
        }

        List<BookResDto> books = bookRepository.findBooks(searchValue, pageable).stream().map(book -> bookMapper.toResDto(book)).collect(Collectors.toList());

        return new PageImpl<BookResDto>(books, pageable, books.size());
    }

    @Override
    public boolean reindexAll()
    {
        bookRepository.reindexAll();
        return true;
    }

    @Override
    public List<BookResponseDto> findAllBooks(Pageable pageable)
    {
        Page<Book> books = myBookRepository.findAll(pageable);
        List<BookResponseDto> responseDtos = new ArrayList<>();

        /*Find if there's any copy of this book left
         * to check book's availability*/
        for (Book book : books.getContent())
        {
            BookResponseDto dto = objectMapper.convertValue(book, BookResponseDto.class);
            int copies = bookCopyRepository.findByBookIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE).size();
            if (copies > 0)
            {
                dto.setAvailableCopies(copies);
                dto.setAvailable(true);
            } else
            {
                dto.setAvailable(false);
            }
            dto.setAuthors(book.getBookAuthors().toString().
                    replace("]", "").replace("[", ""));
            dto.setGenres(book.getBookGenres().toString().
                    replace("]", "").replace("[", ""));
            dto.setBookId(book.getId());
            responseDtos.add(dto);
        }
        return responseDtos;
    }

    @Override
    @Transactional
    public String addBook(AddBookRequestDto request)
    {
        Book book = objectMapper.convertValue(request, Book.class);
        Set<BookAuthor> bookAuthorSet = new HashSet<>();
        Set<BookGenre> bookGenreSet = new HashSet<>();
        for (int id : request.getAuthorIds())
        {
            Optional<Author> authorOptional = authorRepository.findById(id);
            if (authorOptional.isPresent())
            {
                BookAuthor bookAuthor = new BookAuthor();
                bookAuthor.setBook(book);
                bookAuthor.setAuthor(authorOptional.get());
                bookAuthorSet.add(bookAuthor);
            }
        }
        for (int id : request.getGenreIds())
        {
            Optional<Genre> genreOptional = genreRepository.findById(id);
            if (genreOptional.isPresent())
            {
                BookGenre bookGenre = new BookGenre();
                bookGenre.setBook(book);
                bookGenre.setGenre(genreOptional.get());
                bookGenreSet.add(bookGenre);
            }
        }
        book.setBookAuthors(bookAuthorSet);
        book.setBookGenres(bookGenreSet);
        book.setImg("img_url");
        book.setNumberOfCopy(0);

        try
        {
            myBookRepository.save(book);
        } catch (Exception e)
        {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Database error", e.getLocalizedMessage());
        }

        return "Success";
    }

    @Override
    public String updateBookStatus(int id, BookStatus status)
    {
        Optional<Book> bookOptional = myBookRepository.findById(id);
        if (bookOptional.isPresent())
        {
            Book book = bookOptional.get();
            if (status.equals(book.getStatus()))
            {
                return "Book status is already " + status;
            }

            book.setStatus(status);

            try
            {
                myBookRepository.save(book);
            } catch (Exception e)
            {
                throw new CustomException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Database error", e.getLocalizedMessage());
            }
            return "Success";
        } else
        {
            throw new ResourceNotFoundException("Book", "Book [" + id + "] is not found");
        }
    }

    @Override
    public boolean tagRfidToBookCopy(Integer bookCopyId, String rfid)
    {
        if (bookCopyId == null || rfid == null)
        {
            throw new MissingInputException("Missing input");
        }

        BookCopyDto bookCopy = bookCopyMapper.toDto(bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "BookCopy with id: " + bookCopyId + " not found")));

        if (bookCopy != null)
        {
            bookCopy.setRfid(rfid);
            BookCopy result = bookCopyMapper.toEntity(bookCopy);
            bookCopyRepository.saveAndFlush(result);
            return true;
        }

        return false;
    }

}
