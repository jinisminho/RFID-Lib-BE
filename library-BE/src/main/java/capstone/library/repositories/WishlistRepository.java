package capstone.library.repositories;

import capstone.library.entities.WishlistBook;
import capstone.library.enums.WishListStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<WishlistBook, Integer> {

    List<WishlistBook> findByStatus(WishListStatus status);

}