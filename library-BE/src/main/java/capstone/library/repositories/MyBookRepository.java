package capstone.library.repositories;

import capstone.library.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyBookRepository extends JpaRepository<Book, Integer>
{
    Page<Book> findAll(Pageable pageable);
}
