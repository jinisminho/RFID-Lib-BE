package capstone.library.repositories;

import capstone.library.entities.Book;
import capstone.library.enums.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookJpaRepository extends JpaRepository<Book, Integer> {

    Optional<Book> getBookById(Integer bookId);

    Page<Book> findAll(Pageable pageable);

    Page<Book> findAllByStatusIn(List<BookStatus> status, Pageable pageable);
}