package capstone.library.repositories;

import capstone.library.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository {

    Page<Book> findBooks(String searchValue, Pageable pageable);

    void reindexAll();

}