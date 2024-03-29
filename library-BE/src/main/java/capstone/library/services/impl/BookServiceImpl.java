package capstone.library.services.impl;

import capstone.library.dtos.request.CreateBookRequestDto;
import capstone.library.dtos.request.UpdateBookInfoRequestDto;
import capstone.library.dtos.response.BookResDto;
import capstone.library.dtos.response.BookResponseDto;
import capstone.library.entities.*;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.BookStatus;
import capstone.library.exceptions.CustomException;
import capstone.library.exceptions.InvalidRequestException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.mappers.BookCopyMapper;
import capstone.library.mappers.BookMapper;
import capstone.library.repositories.*;
import capstone.library.services.BookCopyService;
import capstone.library.services.BookService;
import capstone.library.util.tools.CallNumberUtil;
import capstone.library.util.tools.GenreUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.EnumUtils;
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
    private BookJpaRepository bookJpaRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BookCopyService bookCopyService;
    CallNumberUtil callNumberUtil = new CallNumberUtil();

    private static final String SUCCESS_MESSAGE = "Success";
    private static final String DATABASE_ERROR = "Database error";
    private static final String BOOK_NOT_FOUND = "Cannot find this book in the system";
    private static final String UPDATER_NOT_FOUND = "Cannot find this updater account in the system";
    private static final String CREATOR_NOT_FOUND = "Cannot find this creator account in the system";
    private static final String CALL_NUMBER_INVALID_ERROR = "This call number is invalid (eg: ###.ABC)";
    private static final String UPDATE_DISCARD_BOOK_ERROR = "Cannot update DISCARD book";
    private static final String UPDATE_DUPLICATE_BOOK_STATUS_ERROR = "This book is already: ";
    private static final String CALL_NUMBER_NOT_UNIQUE_ERROR = "Call number must be unique";

    @Override
    public Page<BookResDto> findBooks(String searchValue, List<String> status, Pageable pageable) {
//        if (searchValue == null) {
//            throw new MissingInputException("Missing search value for search book");
//        }

        List<BookResDto> res = new ArrayList<>();
        long totalSize = 0;
        searchValue = searchValue == null ? "" : searchValue;
        searchValue = searchValue.trim();

        List<BookStatus> statusEnums = new ArrayList<>();
        if (status != null)
            status.forEach(s -> {
                if (s != null ? EnumUtils.isValidEnumIgnoreCase(BookStatus.class, s.trim()) : false)
                    statusEnums.add(BookStatus.valueOf(s.trim()));
                else
                    throw new InvalidRequestException(" Param [status:" + s + "] is not a valid book copy status enum.");
            });


        Page<Book> books = doFindBook(searchValue, statusEnums, pageable);

        totalSize = books.getTotalElements();
        res = books.stream().map(book -> bookMapper.toResDto(book)).collect(Collectors.toList());

        for (BookResDto book : res) {
            //Get Genre by ddc (floored)
            double ddc = 0;
            double ddcFloored = 0;
            try {

                ddc = Double.parseDouble(book.getCallNumber().split("[ ]", 0)[0]);
                ddcFloored = Math.floor(ddc / 100) * 100; //floor(double/100)*100 e.g.: 123.1 -> 100

                Optional<Genre> genreOpt = genreRepository.findByDdc(ddcFloored);
                if (genreOpt.isPresent()) {
                    book.setGenre(genreOpt.get().getName());
                }

            } catch (NullPointerException nullE) {
                //if the string was given to pareseDouble was null
            } catch (NumberFormatException numE) {
                //if the string was given to pareseDouble did not contain a parsable double
            }
            //Get Genre by ddc (floored) - end here

            //Count available items
            int stockSize = bookCopyRepository.findByBookIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE).stream().map(copy -> bookCopyMapper.toResDto(copy)).collect(Collectors.toList()).size();
            stockSize += bookCopyRepository.findByBookIdAndStatus(book.getId(), BookCopyStatus.LIB_USE_ONLY).stream().map(copy -> bookCopyMapper.toResDto(copy)).collect(Collectors.toList()).size();
            if (stockSize > 0) {
                book.setStock(stockSize);
                book.setAvailable(true);
            } else {
                book.setAvailable(false);
                book.setStock(0);
            }
            if (book.getStatus().equals(BookStatus.LIB_USE_ONLY.toString()))
                book.setOnlyInLibrary(true);
        }

        return new PageImpl<BookResDto>(res, pageable, totalSize);
    }

    private Page<Book> doFindBook(String searchValue, List<BookStatus> statusEnums, Pageable pageable) {
        Page<Book> books;
        if (searchValue.isEmpty()) {
            books = statusEnums == null || statusEnums.isEmpty() ? bookJpaRepository.findAll(pageable) : bookJpaRepository.findAllByStatusIn(statusEnums, pageable);
        } else {
            books = statusEnums == null || statusEnums.isEmpty() ? bookRepository.findBooks(searchValue, pageable) : bookRepository.findBooksWithStatus(searchValue, statusEnums, pageable);
        }
        return books;
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
            copies += bookCopyRepository.findByBookIdAndStatus(book.getId(), BookCopyStatus.LIB_USE_ONLY).size();
            if (copies > 0) {
                dto.setAvailableCopies(copies);
                dto.setAvailable(true);
            } else {
                dto.setAvailable(false);
            }
            dto.setAuthors(book.getBookAuthors().toString().
                    replace("]", "").replace("[", ""));
            //Tram added --
            List<Genre> genreList = genreRepository.findByOrderByDdcAsc();
            String genres = GenreUtil.getGenreFormCallNumber(book.getCallNumber(), genreList);
            //---
            dto.setGenres(genres);
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

            //Create call number from the request
//            StringBuilder authorName = new StringBuilder();
//            for (int id : request.getAuthorIds()) {
//                authorName.append(authorRepository.findById(id).orElse(new Author()).getName()).append(", ");
//            }

//            book.setCallNumber(callNumberUtil.
//                    createCallNumber(request.getDdc(), authorName.toString(), request.getPublishYear()));

            //(Hoang) 2-May-2021 Update: call number must be unique
            String callNumber = request.getCallNumber();
            if (!book.getCallNumber().equals(callNumber)) {
                List<Book> booksWithSameCallNumber = myBookRepository.findByCallNumber(callNumber);
                if (booksWithSameCallNumber.isEmpty()) {
                    book.setCallNumber(callNumber);
                } else {
                    throw new InvalidRequestException(CALL_NUMBER_NOT_UNIQUE_ERROR);
                }
            }

            /*Get account to add to updateBy*/
            Account updateBy = accountRepository.findById(request.getUpdateBy()).
                    orElseThrow(() -> new ResourceNotFoundException("Account", UPDATER_NOT_FOUND));
            book.setUpdater(updateBy);

            Set<BookAuthor> bookAuthorSet = new HashSet<>();

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

            /* Tram deleted ---
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

            ----- */

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

    @Override
    public BookResponseDto findByISBN(String isbn) {
        Optional<Book> bookOptional = myBookRepository.findByIsbn(isbn);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            BookResponseDto response = objectMapper.convertValue(book, BookResponseDto.class);
            response.setAuthors(book.getBookAuthors().toString().
                    replace("[", "").replace("]", ""));
            //Tram added ---
            List<Genre> genreList = genreRepository.findByOrderByDdcAsc();
            String genres = GenreUtil.getGenreFormCallNumber(book.getCallNumber(), genreList);
            // ---

            response.setGenres(genres);
            int availableCopies = bookCopyRepository.findByBookIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE).size();
            availableCopies += bookCopyRepository.findByBookIdAndStatus(book.getId(), BookCopyStatus.LIB_USE_ONLY).size();
            response.setAvailableCopies(availableCopies);
            return response;
        }
        throw new ResourceNotFoundException("Book", BOOK_NOT_FOUND);
    }

    /* Tram deleted ----------
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

   ---------  */

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
        if (request.getISBN() != null && !request.getISBN().isBlank()) {
            book.setIsbn(request.getISBN().trim().replaceAll(" +", " "));
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            book.setTitle(request.getTitle().trim().replaceAll(" +", " "));
        }
        if (request.getSubtitle() != null) {
            book.setSubtitle(request.getSubtitle().trim().replaceAll(" +", " "));
        }
        if (request.getPublisher() != null && !request.getPublisher().isBlank()) {
            book.setPublisher(request.getPublisher().trim().replaceAll(" +", " "));
        }
        if (request.getPublishYear() != null && !book.getPublishYear().equals(request.getPublishYear())) {
            book.setPublishYear(request.getPublishYear());
        }
        if (request.getEdition() != null && !book.getEdition().equals(request.getEdition()) && request.getEdition() > 0) {
            book.setEdition(request.getEdition());
        }
        if (request.getLanguage() != null && !request.getLanguage().isBlank()) {
            book.setLanguage(request.getLanguage().trim().replaceAll(" +", " "));
        }
        if (request.getPageNumber() != null && !book.getPageNumber().equals(request.getPageNumber()) && request.getPageNumber() > 0) {
            book.setPageNumber(request.getPageNumber());
        }
        if (request.getImg() != null && !request.getImg().isBlank()) {
            book.setImg(request.getImg().trim().replaceAll(" +", " "));
        }
        if (request.getStatus() != null) {
            updateBookStatus(request.getId(), request.getStatus());
        }
    }

    //Title, subtitle, publisher, language, call number is trimmed and removed of duplicate spaces
    private void transformCreateBookStringInput(Book book, CreateBookRequestDto request) {
        if (request.getIsbn() != null) {
            book.setIsbn(request.getIsbn().trim().replaceAll(" +", " "));
        }
        if (request.getTitle() != null) {
            book.setTitle(request.getTitle().trim().replaceAll(" +", " "));
        }
        if (request.getSubtitle() != null) {
            book.setSubtitle(request.getSubtitle().trim().replaceAll(" +", " "));
        }
        if (request.getPublisher() != null) {
            book.setPublisher(request.getPublisher().trim().replaceAll(" +", " "));
        }
        if (request.getLanguage() != null) {
            book.setLanguage(request.getLanguage().trim().replaceAll(" +", " "));
        }
        if (request.getImg() != null) {
            book.setImg(request.getImg().trim().replaceAll(" +", " "));
        }
    }

    @Override
    @Transactional
    public String addBook(CreateBookRequestDto request) {
        if (request.getPageNumber() == 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Page number is 0", "Page number must be more than 0");
        }
        Book book = objectMapper.convertValue(request, Book.class);

        transformCreateBookStringInput(book, request);

        //(Hoang) 2-May-2021 Update: call number must be unique
        String callNumber = request.getCallNumber();
        List<Book> booksWithSameCallNumber = myBookRepository.findByCallNumber(callNumber);
        if (booksWithSameCallNumber.isEmpty()) {
            book.setCallNumber(callNumber);
        } else {
            throw new InvalidRequestException(CALL_NUMBER_NOT_UNIQUE_ERROR);
        }

        Set<BookAuthor> bookAuthorSet = new HashSet<>();
        setBookAuthor(book, bookAuthorSet, request.getAuthorIds());
        if (bookAuthorSet.isEmpty()) {
            throw new ResourceNotFoundException("Author", "Author is not found");
        }
        book.setBookAuthors(bookAuthorSet);
        book.setCreator(accountRepository.findById(request.getCreatorId()).
                orElseThrow(() -> new ResourceNotFoundException("Account", CREATOR_NOT_FOUND)));
        if (request.getImg().isBlank()) {
            book.setImg("img_url");
        } else {
            book.setImg(request.getImg());
        }
        book.setNumberOfCopy(0);

        myBookRepository.save(book);

        return SUCCESS_MESSAGE;
    }

    @Override
    @Transactional
    public String updateBookStatus(int id, BookStatus status) {
        Optional<Book> bookOptional = myBookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();

            /*Cannot update discarded book*/
            if (book.getStatus().equals(BookStatus.DISCARD)) {
                throw new InvalidRequestException(UPDATE_DISCARD_BOOK_ERROR);
            }
            /*Update book status if book status is changed*/
            else if (!status.equals(book.getStatus())) {
                book.setStatus(status);

                myBookRepository.save(book);

                /*Update book's copies status to match new status
                 * Only update status of copies inside library, borrowed copies will be updated at return.
                 * Cannot update discarded or lost copies*/
                List<BookCopy> copies = bookCopyRepository.findBookCopyByBookId(book.getId());
                for (BookCopy copy : copies) {
                    bookCopyService.updateCopyStatusBasedOnBookStatus(copy, status);
                }
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

}
