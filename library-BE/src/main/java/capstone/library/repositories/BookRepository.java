package capstone.library.repositories;

import capstone.library.entities.Book;
import capstone.library.enums.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository {

    Page<Book> findBooks(String searchValue, Pageable pageable);

    Page<Book> findBooksWithStatus(String searchValue, List<BookStatus> status, Pageable pageable);

    void reindexAll();

}