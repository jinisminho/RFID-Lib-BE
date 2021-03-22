package capstone.library.repositories;

import capstone.library.entities.BookCopyPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyPositionRepository extends JpaRepository<BookCopyPosition, Integer> {
    @Query(value = "SELECT * FROM book_copy_position GROUP BY shelf",
            nativeQuery = true)
    List<BookCopyPosition> findAllGroupsByShelf();

    List<BookCopyPosition> findByShelf(String shelf);
}
