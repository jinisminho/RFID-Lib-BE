package capstone.library.demo.services.impl;

import capstone.library.demo.dtos.response.ScannedBookResponse;
import capstone.library.demo.entities.Book;
import capstone.library.demo.entities.BookCopy;
import capstone.library.demo.exceptions.ResourceNotFoundException;
import capstone.library.demo.repositories.BookCopyRepository;
import capstone.library.demo.services.BookCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookCopyServiceImpl implements BookCopyService {

    @Autowired
    BookCopyRepository bookCopyRepo;

    @Override
    public ScannedBookResponse searchBookByRfid(String rfid) {
        BookCopy copy = bookCopyRepo.findByRfid(rfid)
                .orElseThrow(() -> new ResourceNotFoundException("Book with code: " + rfid + " not found. Please contact the librarian!"));

        Book book = copy.getBook();
        String authors = book.getBookAuthors()
                .stream()
                .map(a -> a.getAuthor().getName())
                .collect(Collectors.joining(", "));

        String genres = book.getBookGenres()
                .stream()
                .map(g -> g.getGenre().getName())
                .collect(Collectors.joining(","));

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
}
