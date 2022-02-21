package capstone.library.demo.repositories;

import capstone.library.demo.entities.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {

    Optional<BookCopy> findByRfid (String rfid);
}
