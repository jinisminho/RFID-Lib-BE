package capstone.library.repositories;

import capstone.library.entities.BookLostReport;
import capstone.library.enums.LostBookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BookLostReportRepository extends JpaRepository<BookLostReport, Integer> {

    Page<BookLostReport> findByLostAtBetweenOrderByLostAtDesc(LocalDateTime startDate,
                                                              LocalDateTime enDate,
                                                              Pageable pageable);

    Page<BookLostReport> findByStatusAndLostAtBetweenOrderByLostAtDesc(LostBookStatus status,
                                                                       LocalDateTime startDate,
                                                                       LocalDateTime enDate,
                                                                       Pageable pageable);

    Page<BookLostReport> findByBookBorrowing_Borrowing_Borrower_IdAndLostAtBetweenOrderByLostAtDesc(int id,
                                                                                                    LocalDateTime startDate,
                                                                                                    LocalDateTime enDate,
                                                                                                    Pageable pageable);

    Page<BookLostReport> findByBookBorrowing_Borrowing_Borrower_Id(int id,
                                                                   Pageable pageable);
}
