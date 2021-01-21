package capstone.library.repositories;

import capstone.library.entities.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {

    Optional<BookCopy> findById(Integer bookCopyId);

}
