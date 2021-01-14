package capstone.library.repositories;

import capstone.library.entities.Book;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository {

    List<Book> findBooks(String searchValue);

    void reindexAll();

}