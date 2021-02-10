package capstone.library.repositories;

import capstone.library.entities.BookCopy;
import capstone.library.enums.BookCopyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyMoreRepository {
    Page<BookCopy> findBookCopies(String searchValue, Pageable pageable);

    Page<BookCopy> findBookCopiesWithStatus(String searchValue, BookCopyStatus status, Pageable pageable);

    void reindexAll();
}
