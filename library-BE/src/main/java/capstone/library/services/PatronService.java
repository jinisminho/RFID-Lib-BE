package capstone.library.services;

import capstone.library.dtos.request.ProfileUpdateReqDto;
import capstone.library.dtos.response.BookBorrowingResDto;
import capstone.library.dtos.response.ExtendHistoryResDto;
import capstone.library.dtos.response.PatronCheckoutInfoResponseDto;
import capstone.library.dtos.response.ProfileAccountResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PatronService {

    boolean addWishlist(Integer bookId, Integer patronId);

    ProfileAccountResDto getProfile(Integer patronId);

    boolean updateProfile(Integer patronId, ProfileUpdateReqDto newProfile);

    Page<ExtendHistoryResDto> getExtendHistories(Integer bookBorrowingId, Pageable pageable);

    boolean addNewExtendHistory(Integer bookBorrowingId, Integer librarianId, Integer numberOfDayToPlus);

    Page<BookBorrowingResDto> getBorrowingHistories(Integer patronId, Pageable pageable);

    PatronCheckoutInfoResponseDto getProfileByRfid(String rfid);
}
