package capstone.library.services.impl;

import capstone.library.dtos.response.WishlistResDto;
import capstone.library.entities.Account;
import capstone.library.entities.Book;
import capstone.library.entities.WishlistBook;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.BookStatus;
import capstone.library.enums.WishListStatus;
import capstone.library.exceptions.InvalidRequestException;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.mappers.BookCopyMapper;
import capstone.library.mappers.WishlistMapper;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.BookJpaRepository;
import capstone.library.repositories.WishlistRepository;
import capstone.library.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private BookJpaRepository bookJpaRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private WishlistMapper wishlistMapper;
    @Autowired
    private BookCopyRepository bookCopyRepository;
    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Override
    public boolean addWishlist(Integer bookId, Integer patronId) {
        if (bookId == null || patronId == null) {
            throw new MissingInputException("Missing input");
        }

        Optional<WishlistBook> wishListOpt = wishlistRepository.findByBorrower_IdAndBook_IdAndStatus(patronId, bookId, WishListStatus.NOT_EMAIL_YET);
        wishListOpt.ifPresent(wishlistBook -> {
            throw new InvalidRequestException("This book already added to wishlist by Patron with id[" + patronId + "]");
        });
        WishlistBook wishList = new WishlistBook();
        Book book = bookJpaRepository.getBookById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "Book with id: " + bookId + " not found"));
        Account patron = accountRepository.findById(patronId).orElseThrow(() -> new ResourceNotFoundException("Patron", "Patron with id: " + patronId + " not found"));


        if (patron != null && book != null) {
            wishList.setBook(book);
            wishList.setBorrower(patron);
            wishList.setStatus(WishListStatus.NOT_EMAIL_YET);
            wishList.setEmail(patron.getEmail());
            wishlistRepository.save(wishList);
            return true;
        }

        return false;
    }

    @Override
    public Page<WishlistResDto> getWishlist(Integer patronId, Pageable pageable) {
        Page<WishlistBook> page = wishlistRepository.findAllByBorrower_IdAndStatus(patronId, WishListStatus.NOT_EMAIL_YET, pageable);
        List<WishlistResDto> list = page.stream().map(wishlist -> wishlistMapper.toResDto(wishlist)).collect(Collectors.toList());

        for (WishlistResDto wishList : list) {
            int stockSize = bookCopyRepository.findByBookIdAndStatus(wishList.getBook().getId(), BookCopyStatus.AVAILABLE).stream().map(copy -> bookCopyMapper.toResDto(copy)).collect(Collectors.toList()).size();
            stockSize += bookCopyRepository.findByBookIdAndStatus(wishList.getBook().getId(), BookCopyStatus.LIB_USE_ONLY).stream().map(copy -> bookCopyMapper.toResDto(copy)).collect(Collectors.toList()).size();
            if (stockSize > 0) {
                wishList.getBook().setStock(stockSize);
                wishList.getBook().setAvailable(true);
            } else {
                wishList.getBook().setAvailable(false);
                wishList.getBook().setStock(0);
            }
            if (wishList.getBook().getStatus().equals(BookStatus.LIB_USE_ONLY.toString()))
                wishList.getBook().setOnlyInLibrary(true);
        }

        Page<WishlistResDto> res = new PageImpl<WishlistResDto>(list, pageable, page.getTotalElements());
        return res;
    }
}
