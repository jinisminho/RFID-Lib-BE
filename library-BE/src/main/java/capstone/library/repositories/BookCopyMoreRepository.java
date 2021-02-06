package capstone.library.repositories;

import capstone.library.entities.BookCopy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyMoreRepository {
    Page<BookCopy> findBookCopies(String searchValue, Pageable pageable);

    void reindexAll();
}
