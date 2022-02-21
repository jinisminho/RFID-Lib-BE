package capstone.library.repositories;

import capstone.library.entities.Borrowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Integer> {
    Page<Borrowing> findAllByBorrowedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
