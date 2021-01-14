package capstone.library.repositories;

import capstone.library.entities.BookWishList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<BookWishList, Integer> {


}