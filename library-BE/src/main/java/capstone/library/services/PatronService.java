package capstone.library.services;

import capstone.library.dtos.request.ProfileUpdateReqDto;
import capstone.library.dtos.response.ExtendHistoryResDto;
import capstone.library.dtos.response.ProfileResDto;

import java.util.List;


public interface PatronService {

    boolean addWishlist(Integer bookId, Integer patronId);

    ProfileResDto getProfile(Integer patronId);

    boolean updateProfile(Integer patronId, ProfileUpdateReqDto newProfile);

    List<ExtendHistoryResDto> getExtendHistories(Integer bookBorrowingId);

    boolean addNewExtendHistory(Integer bookBorrowingId, Integer librarianId, Integer numberOfDayToPlus);
}
