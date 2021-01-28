package capstone.library.repositories;

import capstone.library.entities.BookCopy;
import capstone.library.enums.BookCopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Integer>
{
    Optional<BookCopy> findByRfid(String rfid);

    Optional<BookCopy> findByRfidAndStatus(String rfid, BookCopyStatus status);

    List<BookCopy> findByBookId(Integer id);

    List<BookCopy> findByBookIdAndStatus(Integer id, BookCopyStatus status);

}
