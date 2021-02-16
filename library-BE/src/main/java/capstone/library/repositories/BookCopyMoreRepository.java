package capstone.library.repositories;

import capstone.library.entities.BookCopy;
import capstone.library.enums.BookCopyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyMoreRepository {
    Page<BookCopy> findBookCopies(String searchValue, Pageable pageable);

    Page<BookCopy> findBookCopiesWithStatus(String searchValue, List<BookCopyStatus> status, Pageable pageable);

    void reindexAll();
}
