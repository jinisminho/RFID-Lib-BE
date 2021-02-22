package capstone.library.repositories;

import capstone.library.entities.BookBorrowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookBorrowingRepository extends JpaRepository<BookBorrowing, Integer> {

    Optional<BookBorrowing> findById(Integer bookBorrowingId);

    /* Trâm fix lại của Kiên
     * Update(Hoang): remove ";" from "borrower_id;" + rename to finAllByBorrowerId*/
    @Query(
            value = "select d.* \n" +
                    "from book_borrowing d\n" +
                    "join borrowing b on d.borrow_id = b.id\n" +
                    "where b.borrowed_by = :borrower_id",
            countQuery = "select count(*) \n" +
                    "from book_borrowing d\n" +
                    "join borrowing b on d.borrow_id = b.id\n" +
                    "where b.borrowed_by = :borrower_id",
            nativeQuery = true
    )
    Page<BookBorrowing> findAllByBorrowerId(@Param("borrower_id") Integer patronId, Pageable pageable);
    /*==========================*/

    Optional<BookBorrowing> findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(int bookCopyId);

    @Query(
            value = "select d.* \n" +
                    "from book_borrowing d\n" +
                    "join borrowing b on d.borrow_id = b.id\n" +
                    "where b.borrowed_by = :borrower_id and d.returned_at is null and d.lost_at is null;",
            nativeQuery = true
    )
    List<BookBorrowing> findByBorrowerIdAndReturnedAtIsNullAndLostAtIsNull(@Param("borrower_id") int patronId);

    List<BookBorrowing> findByDueAtAndReturnedAtIsNullAndLostAtIsNull(LocalDate dueAt);

    @Query("SELECT b FROM BookBorrowing b WHERE b.returnedAt IS NOT NULL AND b.borrowing.borrower.id = :borrower_id")
    Page<BookBorrowing> findAllByBorrowerIdAndReturnedAtIsNotNull(@Param("borrower_id") Integer patronId, Pageable pageable);

    @Query("SELECT b FROM BookBorrowing b WHERE b.returnedAt IS NULL AND b.borrowing.borrower.id = :borrower_id AND b.dueAt <= CURRENT_DATE")
    Page<BookBorrowing> findAllByBorrowerIdAndReturnedAtIsNullAndDueAtBeforeCurrentDate(@Param("borrower_id") Integer patronId, Pageable pageable);

    @Query("SELECT b FROM BookBorrowing b WHERE b.returnedAt IS NULL AND b.borrowing.borrower.id = :borrower_id AND b.dueAt > CURRENT_DATE")
    Page<BookBorrowing> findAllByBorrowerIdAndReturnedAtIsNullAndDueAtAfterCurrentDate(@Param("borrower_id") Integer patronId, Pageable pageable);

}
