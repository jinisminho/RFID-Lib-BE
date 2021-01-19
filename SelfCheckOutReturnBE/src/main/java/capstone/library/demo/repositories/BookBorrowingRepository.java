package capstone.library.demo.repositories;

import capstone.library.demo.entities.BookBorrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookBorrowingRepository extends JpaRepository<BookBorrowing, Integer> {

    @Query(
            value = "select * \n" +
                    "from  library_rfid.book_borrowing\n" +
                    "where borrowed_by = :borrowed_by and due_at < curdate()",
            nativeQuery = true
    )
    List<BookBorrowing> findOverDueTransactionByPatronId (@Param("borrowed_by") int patronId);

}
