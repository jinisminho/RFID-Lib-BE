package capstone.library.repositories;

import capstone.library.entities.BookLostReport;
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
}