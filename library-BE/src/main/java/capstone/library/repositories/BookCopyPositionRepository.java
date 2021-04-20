package capstone.library.repositories;

import capstone.library.entities.BookCopyPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyPositionRepository extends JpaRepository<BookCopyPosition, Integer> {
    @Query(value = "SELECT * FROM book_copy_position GROUP BY shelf",
            nativeQuery = true)
    List<BookCopyPosition> findAllGroupsByShelf();

    List<BookCopyPosition> findByShelf(String shelf);

    Optional<BookCopyPosition> findByRfid(String rfid);

    Page<BookCopyPosition> findAllByShelfAndLine(String shelf, int line, Pageable pageable);

    Page<BookCopyPosition> findAllByShelf(String shelf, Pageable pageable);

    Page<BookCopyPosition> findAllByLine(int line, Pageable pageable);
}
