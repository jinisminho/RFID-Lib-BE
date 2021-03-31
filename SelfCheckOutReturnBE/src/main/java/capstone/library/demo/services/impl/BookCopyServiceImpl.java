package capstone.library.demo.services.impl;

import capstone.library.demo.dtos.response.ScannedBookResponse;
import capstone.library.demo.dtos.response.ScannedReturnBookResponse;
import capstone.library.demo.entities.*;
import capstone.library.demo.enums.BookCopyStatus;
import capstone.library.demo.exceptions.ResourceNotFoundException;
import capstone.library.demo.repositories.BookBorrowingRepository;
import capstone.library.demo.repositories.BookCopyRepository;
import capstone.library.demo.repositories.GenreRepository;
import capstone.library.demo.services.BookCopyService;
import capstone.library.demo.util.GenreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookCopyServiceImpl implements BookCopyService {

    @Autowired
    BookCopyRepository bookCopyRepo;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    BookBorrowingRepository bookBorrowingRepo;

    @Override
    public ScannedBookResponse searchBookByRfid(String rfid) {
        BookCopy copy = bookCopyRepo.findByRfid(rfid)
                .orElseThrow(() -> new ResourceNotFoundException("Book with code: " + rfid + " not found. Please contact the librarian!"));

        Book book = copy.getBook();
        String authors = book.getBookAuthors()
                .stream()
                .map(a -> a.getAuthor().getName())
                .collect(Collectors.joining(", "));

        List<Genre> genreList = genreRepository.findByOrderByDdcAsc();
        String genres = GenreUtil.getGenreFormCallNumber(book.getCallNumber(), genreList);

        return new ScannedBookResponse(rfid,
                book.getTitle(),
                book.getEdition(),
                authors,
                book.getImg(),
                book.getSubtitle(),
                copy.getBookCopyType().getId(),
                copy.getBookCopyType().getName(),
                genres,
                book.getId()
                );
    }

    @Override
    public ScannedReturnBookResponse searchReturnBook(String rfid) {
        BookCopy copy = bookCopyRepo.findByRfid(rfid)
                .orElseThrow(() -> new ResourceNotFoundException("Book with code: " + rfid + " not found. Please contact the librarian!"));

        Book book = copy.getBook();
        String authors = book.getBookAuthors()
                .stream()
                .map(a -> a.getAuthor().getName())
                .collect(Collectors.joining(", "));

        List<Genre> genreList = genreRepository.findByOrderByDdcAsc();
        String genres = GenreUtil.getGenreFormCallNumber(book.getCallNumber(), genreList);
        String borrower = "";
        if(copy.getStatus().equals(BookCopyStatus.BORROWED)){
            BookBorrowing bookBorrowing = bookBorrowingRepo.findBorrowedTransactionByBookCopyId(copy.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(("Cannot find bookBorrowing transaction with book id" + copy.getId())));
            Account patron = bookBorrowing.getBorrowing().getBorrower();
            borrower = patron.getEmail();
        }
        return new ScannedReturnBookResponse(rfid,
                book.getTitle(),
                book.getEdition(),
                authors,
                book.getImg(),
                book.getSubtitle(),
                copy.getBookCopyType().getId(),
                copy.getBookCopyType().getName(),
                genres,
                book.getId(),
                borrower,
                copy.getStatus()
        );
    }
}
