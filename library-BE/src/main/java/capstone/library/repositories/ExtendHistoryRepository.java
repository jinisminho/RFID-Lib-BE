package capstone.library.repositories;

import capstone.library.entities.ExtendHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExtendHistoryRepository extends JpaRepository<ExtendHistory, Integer> {
    List<ExtendHistory> findAllByBookBorrowing_IdOrderByDueAtAsc(Integer bookBorrowingId);

    Optional<ExtendHistory> findFirstByBookBorrowing_IdOrderByDueAtDesc(Integer bookBorrowingId);
}
