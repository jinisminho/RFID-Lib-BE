package capstone.library.repositories;

import capstone.library.entities.BookBorrowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookBorrowingRepository extends JpaRepository<BookBorrowing, Integer>
{

    Optional<BookBorrowing> findById(Integer bookBorrowingId);

    Page<BookBorrowing> findAllByBorrower_Id(Integer patronId, Pageable pageable);

    Optional<BookBorrowing> findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(int bookCopyId);

    List<BookBorrowing> findByBorrowerIdAndReturnedAtIsNullAndLostAtIsNull(int patronId);

    List<BookBorrowing> findByDueAtAndReturnedAtIsNullAndLostAtIsNull(LocalDate dueAt);

}
