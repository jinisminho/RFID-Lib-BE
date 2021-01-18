package capstone.library.services.impl;

import capstone.library.dtos.ProfileDto;
import capstone.library.dtos.ProfileUpdateDto;
import capstone.library.entities.Account;
import capstone.library.entities.Book;
import capstone.library.entities.WishlistBook;
import capstone.library.entities.Profile;
import capstone.library.enums.WishListStatus;
import capstone.library.mappers.ProfileMapper;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.BookJpaRepository;
import capstone.library.repositories.ProfileRepository;
import capstone.library.repositories.WishlistRepository;
import capstone.library.services.PatronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatronServiceImpl implements PatronService {

    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private BookJpaRepository bookJpaRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProfileRepository profileRepository;

    private static final String EMAIL_DEFAULT = "test@test.com";

    @Override
    public boolean addWishlist(Integer bookId, Integer patronId) {
        if (bookId == null || patronId == null) {
            return false;
        }

        WishlistBook wishList = new WishlistBook();
        Book book = bookJpaRepository.getBookById(bookId).orElse(null);
        Account patron = accountRepository.findById(patronId).orElse(null);

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

    public ProfileDto getProfile(Integer patronId) {
        return ProfileMapper.INSTANCE.toDto(profileRepository.findById(patronId).orElse(null));
    }

    public boolean updateProfile(Integer patronId, ProfileUpdateDto newProfile) {
        ProfileDto oldProfile = ProfileMapper.INSTANCE.toDto(profileRepository.findById(patronId).orElse(null));

        if (oldProfile != null) {
            oldProfile.setPhone(newProfile.getPhone());
            Profile prof = ProfileMapper.INSTANCE.toEntity(oldProfile);
            profileRepository.saveAndFlush(prof);
            return true;
        }

        return false;
    }

}
