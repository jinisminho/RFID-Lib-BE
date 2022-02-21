package capstone.library.repositories;

import capstone.library.entities.ExtendHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExtendHistoryRepository extends JpaRepository<ExtendHistory, Integer> {
    Page<ExtendHistory> findAllByBookBorrowing_IdOrderByDueAtAsc(Integer bookBorrowingId, Pageable pageable);

    Optional<ExtendHistory> findFirstByBookBorrowing_IdOrderByDueAtDesc(Integer bookBorrowingId);
}
