package capstone.library.repositories;

import capstone.library.entities.BookLostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookLostReportRepository extends JpaRepository<BookLostReport, Integer> {
}
