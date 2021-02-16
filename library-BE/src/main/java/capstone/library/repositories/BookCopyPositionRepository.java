package capstone.library.repositories;

import capstone.library.entities.BookCopyPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyPositionRepository extends JpaRepository<BookCopyPosition, Integer> {

    List<BookCopyPosition> findByBookCopyTypeIdOrderByFromCallNumberAsc (int bookCopyTypeId);

    List<BookCopyPosition> findByOrderByFromCallNumberAsc ();

}
