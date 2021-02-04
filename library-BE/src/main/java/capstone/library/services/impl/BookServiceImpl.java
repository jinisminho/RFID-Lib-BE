package capstone.library.services.impl;

import capstone.library.dtos.request.CreateBookRequestDto;
import capstone.library.dtos.request.UpdateBookInfoRequestDto;
import capstone.library.dtos.response.BookCopyResDto;
import capstone.library.dtos.response.BookResDto;
import capstone.library.dtos.response.BookResponseDto;
import capstone.library.entities.*;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.BookStatus;
import capstone.library.exceptions.CustomException;
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
public class BookServiceImpl implements BookService {

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
    @Autowired
    private BookAuthorRepository bookAuthorRepository;
    @Autowired
    private BookGenreRepository bookGenreRepository;
    @Autowired
    private BookJpaRepository bookJpaRepository;

    private static final String SUCCESS_MESSAGE = "Success";
    private static final String DATABASE_ERROR = "Database error";

    @Override
    public Page<BookResDto> findBooks(String searchValue, Pageable pageable) {
//        if (searchValue == null) {
//            throw new MissingInputException("Missing search value for search book");
//        }

        List<BookResDto> res = new ArrayList<>();
        long totalSize = 0;
        searchValue = searchValue.trim();
        searchValue = searchValue == null ? "" : searchValue;
        Page<Book> books = searchValue.isEmpty() ? bookJpaRepository.findAll(pageable) : bookRepository.findBooks(searchValue, pageable);
        totalSize = books.getTotalElements();
        res = books.stream().map(book -> bookMapper.toResDto(book)).collect(Collectors.toList());

        for (BookResDto book : res) {
            List<BookCopyResDto> copies = bookCopyRepository.findByBookId(book.getId()).stream().map(copy -> bookCopyMapper.toResDto(copy)).collect(Collectors.toList());
            List<BookCopyResDto> stocks = copies.stream().filter(e -> e.getStatus().equals(BookCopyStatus.AVAILABLE)).collect(Collectors.toList());
            int copiesSize = copies.size();
            int stockSize = stocks.size();

            if (copiesSize > 0) {
                book.setTotalCopies(copiesSize);
                book.setStock(stockSize);
                book.setAvailable(true);
            } else {
                book.setAvailable(false);
            }
            if (book.getStatus().equals(BookStatus.LIB_USE_ONLY))
                book.setOnlyInLibrary(true);
        }

        return new PageImpl<BookResDto>(res, pageable, totalSize);
    }

    @Override
    public boolean reindexAll() {
        bookRepository.reindexAll();
        return true;
    }

    @Override
    public Page<BookResponseDto> findAllBooks(Pageable pageable) {
        Page<Book> books = myBookRepository.findAll(pageable);
        List<BookResponseDto> responseDtos = new ArrayList<>();

        /*Find if there's any copy of this book left
         * to check book's availability*/
        for (Book book : books.getContent()) {
            BookResponseDto dto = objectMapper.convertValue(book, BookResponseDto.class);
            int copies = bookCopyRepository.findByBookIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE).size();
            if (copies > 0) {
                dto.setAvailableCopies(copies);
                dto.setAvailable(true);
            } else {
                dto.setAvailable(false);
            }
            dto.setAuthors(book.getBookAuthors().toString().
                    replace("]", "").replace("[", ""));
            dto.setGenres(book.getBookGenres().toString().
                    replace("]", "").replace("[", ""));
            dto.setBookId(book.getId());
            responseDtos.add(dto);
        }

//        return new PageImpl<BookResponseDto>(responseDtos, pageable, responseDtos.size());
        return new PageImpl<BookResponseDto>(responseDtos, pageable, books.getTotalElements());
    }

    /*This API can be reuse for other use cases.
     * Eg: update img, update book authors, update book genres, etc
     * request DTO can be modified by FE to match their requirements*/
    @Override
    @Transactional
    public String updateBookInfo(UpdateBookInfoRequestDto request) {
        Optional<Book> bookOptional = myBookRepository.findById(request.getId());
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();

            /*Validate and set data from requestDto to new Book*/
            setBasicBookInfo(book, request);

            Set<BookAuthor> bookAuthorSet = new HashSet<>();
            Set<BookGenre> bookGenreSet = new HashSet<>();

            /*Remove old book authors and book relationships
             * to create new book authors and book relationships from the request*/
            if (request.getAuthorIds() != null) {
                setBookAuthor(book, bookAuthorSet, request.getAuthorIds());
                for (BookAuthor bookAuthor : book.getBookAuthors()) {
                    try {
                        bookAuthorRepository.deleteById(bookAuthor.getId());
                    } catch (Exception e) {
                        throw new CustomException(
                                HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR, e.getLocalizedMessage());
                    }
                }
                book.setBookAuthors(bookAuthorSet);
            }

            /*Remove old book genres and book relationships
             * to create new book genres and book relationships from the request*/
            if (request.getGenreIds() != null) {
                setBookGenre(book, bookGenreSet, request.getGenreIds());
                for (BookGenre bookGenre : book.getBookGenres()) {
                    try {
                        bookGenreRepository.deleteById(bookGenre.getId());
                    } catch (Exception e) {
                        throw new CustomException(
                                HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR, e.getLocalizedMessage());
                    }
                }
                book.setBookGenres(bookGenreSet);
            }

            try {
                myBookRepository.save(book);
                return SUCCESS_MESSAGE;
            } catch (Exception e) {
                throw new CustomException(
                        HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR, e.getLocalizedMessage());
            }
        } else {
            throw new ResourceNotFoundException("Book", "Book [" + request.getId() + "] not found");
        }

    }

    private void setBookGenre(Book book, Set<BookGenre> bookGenreSet, List<Integer> genreIds) {
        for (int id : genreIds) {
            Optional<Genre> genreOptional = genreRepository.findById(id);
            if (genreOptional.isPresent()) {
                BookGenre bookGenre = new BookGenre();
                bookGenre.setBook(book);
                bookGenre.setGenre(genreOptional.get());
                bookGenreSet.add(bookGenre);
            } else {
                throw new ResourceNotFoundException("Genre", "Genre is not found");
            }
        }

    }

    private void setBookAuthor(Book book, Set<BookAuthor> bookAuthorSet, List<Integer> authorIds) {
        for (int id : authorIds) {
            Optional<Author> authorOptional = authorRepository.findById(id);
            if (authorOptional.isPresent()) {
                BookAuthor bookAuthor = new BookAuthor();
                bookAuthor.setBook(book);
                bookAuthor.setAuthor(authorOptional.get());
                bookAuthorSet.add(bookAuthor);
            } else {
                throw new ResourceNotFoundException("Author", "Author is not found");
            }
        }
    }

    /*Validate request fields before saving request to database.
     * I didn't validate on DTO because this request dto can be modified for other use cases,
     * making this API reusable*/
    private void setBasicBookInfo(Book book, UpdateBookInfoRequestDto request) {
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            book.setTitle(request.getTitle());
        }
        if (request.getSubtitle() != null && !request.getSubtitle().isBlank()) {
            book.setSubtitle(request.getSubtitle());
        }
        if (request.getPublisher() != null && !request.getPublisher().isBlank()) {
            book.setPublisher(request.getPublisher());
        }
        if (request.getPublishYear() != null && !book.getPublishYear().equals(request.getPublishYear())) {
            book.setPublishYear(request.getPublishYear());
        }
        if (request.getEdition() != null && !book.getEdition().equals(request.getEdition()) && request.getEdition() > 0) {
            book.setEdition(request.getEdition());
        }
        if (request.getLanguage() != null && !request.getLanguage().isBlank()) {
            book.setLanguage(request.getLanguage());
        }
        if (request.getPageNumber() != null && !book.getPageNumber().equals(request.getPageNumber()) && request.getPageNumber() > 0) {
            book.setPageNumber(request.getPageNumber());
        }
        if (request.getCallNumber() != null && !request.getCallNumber().isBlank()) {
            book.setCallNumber(request.getCallNumber());
        }
        if (request.getNumberOfCopy() != null && !book.getNumberOfCopy().equals(request.getNumberOfCopy())) {
            book.setNumberOfCopy(request.getNumberOfCopy());
        }
        if (request.getImg() != null && !request.getImg().isBlank()) {
            book.setImg(request.getImg());
        }
    }

    @Override
    @Transactional
    public String addBook(CreateBookRequestDto request) {
        if (request.getPageNumber() == 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Page number is 0", "Page number must be more than 0");
        }
        Book book = objectMapper.convertValue(request, Book.class);
        Set<BookAuthor> bookAuthorSet = new HashSet<>();
        Set<BookGenre> bookGenreSet = new HashSet<>();
        setBookAuthor(book, bookAuthorSet, request.getAuthorIds());
        if (bookAuthorSet.isEmpty()) {
            throw new ResourceNotFoundException("Author", "Author is not found");
        }
        setBookGenre(book, bookGenreSet, request.getGenreIds());
        book.setBookAuthors(bookAuthorSet);
        book.setBookGenres(bookGenreSet);
        if (request.getImg().isBlank()) {
            book.setImg("img_url");
        } else {
            book.setImg(request.getImg());
        }
        book.setNumberOfCopy(0);

        try {
            myBookRepository.save(book);
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR, e.getLocalizedMessage());
        }

        return SUCCESS_MESSAGE;
    }

    @Override
    @Transactional
    public String updateBookStatus(int id, BookStatus status) {
        Optional<Book> bookOptional = myBookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            if (book.getStatus().equals(BookStatus.DISCARD)) {
                return "Cannot update DISCARD book";
            } else if (status.equals(book.getStatus())) {
                return "Book status is already " + status;
            }

            book.setStatus(status);

            try {
                myBookRepository.save(book);
            } catch (Exception e) {
                throw new CustomException(
                        HttpStatus.INTERNAL_SERVER_ERROR, DATABASE_ERROR, e.getLocalizedMessage());
            }
            return SUCCESS_MESSAGE;
        } else {
            throw new ResourceNotFoundException("Book", "Book [" + id + "] is not found");
        }
    }

    /*Only change status of copies that are "AVAILABLE"*/
    private void updateCopiesStatusOnBookStatusChange(Book book) {
        List<BookCopy> bookCopies = bookCopyRepository.findByBookId(book.getId());
        for (BookCopy bookCopy : bookCopies) {
            if (bookCopy.getStatus().equals(BookCopyStatus.AVAILABLE)) {
                if (book.getStatus().equals(BookStatus.LIB_USE_ONLY)) {
                    bookCopy.setStatus(BookCopyStatus.LIB_USE_ONLY);
                }
                if (book.getStatus().equals(BookStatus.OUT_OF_CIRCULATION)) {
                    bookCopy.setStatus(BookCopyStatus.OUT_OF_CIRCULATION);
                }
            }

        }
    }

//    @Override
//    public boolean tagRfidToBookCopy(Integer bookCopyId, String rfid) {
//        if (bookCopyId == null || rfid == null) {
//            throw new MissingInputException("Missing input");
//        }
//
//        BookCopyDto bookCopy = bookCopyMapper.toDto(bookCopyRepository.findById(bookCopyId)
//                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "BookCopy with id: " + bookCopyId + " not found")));
//
//        if (bookCopy != null) {
//            bookCopy.setRfid(rfid);
//            BookCopy result = bookCopyMapper.toEntity(bookCopy);
//            bookCopyRepository.saveAndFlush(result);
//            return true;
//        }
//
//        return false;
//    }

}
