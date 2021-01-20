package capstone.library.services.impl;

import capstone.library.dtos.request.ProfileUpdateReqDto;
import capstone.library.dtos.response.ExtendHistoryResDto;
import capstone.library.dtos.response.ProfileResDto;
import capstone.library.entities.*;
import capstone.library.enums.WishListStatus;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.mappers.ExtendHistoryMapper;
import capstone.library.mappers.ProfileMapper;
import capstone.library.repositories.*;
import capstone.library.services.PatronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private ExtendHistoryRepository extendHistoryRepository;
    @Autowired
    private BookBorrowingRepository bookBorrowingRepository;

    private static final String EMAIL_DEFAULT = "test@test.com";

    @Override
    public boolean addWishlist(Integer bookId, Integer patronId) {
        if (bookId == null || patronId == null) {
            throw new MissingInputException("Missing input");
        }

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

    public ProfileResDto getProfile(Integer patronId) {
        if (patronId == null) {
            throw new MissingInputException("Missing input");
        }

        return ProfileMapper.INSTANCE.toDto(profileRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron", "Patron with id: " + patronId + " not found")));

    }

    public boolean updateProfile(Integer patronId, ProfileUpdateReqDto newProfile) {
        if (newProfile == null || patronId == null) {
            throw new MissingInputException("Missing input");
        }

        ProfileResDto oldProfile = ProfileMapper.INSTANCE.toDto(profileRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron", "Patron with id: " + patronId + " not found")));

        if (oldProfile != null) {
            oldProfile.setPhone(newProfile.getPhone());
            Profile prof = ProfileMapper.INSTANCE.toEntity(oldProfile);
            profileRepository.saveAndFlush(prof);
            return true;
        }

        return false;
    }

    @Override
    public List<ExtendHistoryResDto> getExtendHistories(Integer bookBorrowingId) {
        if (bookBorrowingId == null) {
            throw new MissingInputException("Missing input");
        }

        return extendHistoryRepository
                .findAllByBookBorrowing_IdOrderByDueAtAsc(bookBorrowingRepository.findById(bookBorrowingId)
                        .orElseThrow(() -> new ResourceNotFoundException("BookBorrowing", "BookBorrowing with Id" + bookBorrowingId + " not found"))
                        .getId())
                .stream()
                .map(extendHistory -> ExtendHistoryMapper.INSTANCE.toDto(extendHistory))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addNewExtendHistory(Integer bookBorrowingId, Integer librarianId, Integer numberOfDayToPlus) {
        if (bookBorrowingId == null) {
            throw new MissingInputException("Missing input");
        }
        Account librarian =
                librarianId != null
                        ? accountRepository.findById(librarianId).orElseThrow(() -> new ResourceNotFoundException("Account", "Librarian Account with Id" + librarianId + " not found"))
                        : null;

//        ExtendHistory extendHistory = new ExtendHistory();
        BookBorrowing bookBorrowing = bookBorrowingRepository.findById(bookBorrowingId).orElseThrow(() -> new ResourceNotFoundException("BookBorrowing", "BookBorrowing with Id" + bookBorrowingId + " not found"));

        if (bookBorrowing != null) {
            ExtendHistory extendHistory = extendHistoryRepository.findFirstByBookBorrowing_IdOrderByDueAtDesc(bookBorrowing.getId())
                    .orElse(new ExtendHistory());
            if (bookBorrowing.getExtendIndex() == 0) {
                extendHistory.setBorrowedAt(bookBorrowing.getBorrowedAt());
                extendHistory.setExtendIndex(bookBorrowing.getExtendIndex());
                extendHistory.setDueAt(bookBorrowing.getDueAt());
                extendHistory.setBookBorrowing(bookBorrowing);
//                extendHistory.setLibrarian(librarian);
                extendHistory = extendHistoryRepository.saveAndFlush(extendHistory);
            }

            ExtendHistory newExtendHistory = new ExtendHistory();

            newExtendHistory.setBorrowedAt(extendHistory.getBorrowedAt());
            newExtendHistory.setExtendedAt(LocalDateTime.now());
            newExtendHistory.setExtendIndex(extendHistory.getExtendIndex() + 1);
            newExtendHistory.setDueAt(extendHistory.getDueAt().plusDays(numberOfDayToPlus != null ? numberOfDayToPlus : 7));
            newExtendHistory.setBookBorrowing(bookBorrowing);
            newExtendHistory.setLibrarian(librarian);


            bookBorrowing.setExtendedAt(newExtendHistory.getExtendedAt());
            bookBorrowing.setExtendIndex(newExtendHistory.getExtendIndex());
            bookBorrowing.setDueAt(newExtendHistory.getDueAt());

            extendHistoryRepository.save(newExtendHistory);
            bookBorrowingRepository.save(bookBorrowing);
            return true;
        }

        return false;
    }

}
