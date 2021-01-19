package capstone.library.repositories;

import capstone.library.entities.WishlistBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<WishlistBook, Integer> {


}