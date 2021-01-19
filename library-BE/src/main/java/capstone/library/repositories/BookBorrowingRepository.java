package capstone.library.repositories;

import capstone.library.entities.BookBorrowing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookBorrowingRepository extends JpaRepository<BookBorrowing, Integer> {
    Optional<BookBorrowing> findByBorrower_IdAndAndBookCopy_Id(Integer patronId, Integer bookCopyId);
}
