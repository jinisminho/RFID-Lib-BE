package capstone.library.repositories;

import capstone.library.entities.WishlistBook;
import capstone.library.enums.WishListStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistBook, Integer> {

    List<WishlistBook> findByStatus(WishListStatus status);

    Page<WishlistBook> findAllByBorrower_IdAndStatus(Integer id, WishListStatus status, Pageable pageable);

    Optional<WishlistBook> findByBorrower_IdAndBook_IdAndStatus(Integer patronId, Integer bookId, WishListStatus status);

}