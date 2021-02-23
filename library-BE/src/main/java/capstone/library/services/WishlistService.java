package capstone.library.services;

import capstone.library.dtos.response.WishlistResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishlistService {

    boolean addWishlist(Integer bookId, Integer patronId);

    Page<WishlistResDto> getWishlist(Integer patronId, Pageable pageable);

}
