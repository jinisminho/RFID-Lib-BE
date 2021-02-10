package capstone.library.demo.repositories;

import capstone.library.demo.entities.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingRepository extends JpaRepository<Borrowing, Integer> {
}
