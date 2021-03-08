package capstone.library.repositories;

import capstone.library.entities.BookCopy;
import capstone.library.enums.BookCopyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {
    Optional<BookCopy> findByRfid(String rfid);

    Optional<BookCopy> findByRfidAndStatus(String rfid, BookCopyStatus status);

    List<BookCopy> findByBookId(Integer id);

    List<BookCopy> findByBookIdAndStatus(Integer id, BookCopyStatus status);

    Optional<BookCopy> findByBarcode(String barcode);

    List<BookCopy> findByBookIdAndStatusIn(int bookId, Set<BookCopyStatus> status);

    Optional<BookCopy> findFirstByOrderByIdDesc();

    Page<BookCopy> findAllByStatusIn(List<BookCopyStatus> status, Pageable pageable);

    List<BookCopy> findBookCopyByBookId(int id);

    Optional<BookCopy> getTopByBookCopyType_Id(int id);
}
