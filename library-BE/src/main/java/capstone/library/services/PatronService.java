package capstone.library.services;

import capstone.library.dtos.request.ProfileUpdateReqDto;
import capstone.library.dtos.response.*;
import capstone.library.dtos.response.policiesForPatronView.PoliciesForPatronViewResDto;
import capstone.library.enums.BorrowingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PatronService {

    ProfileAccountResDto getProfile(Integer id);

    boolean updateProfile(Integer patronId, ProfileUpdateReqDto newProfile);

    Page<ExtendHistoryResDto> getExtendHistories(Integer bookBorrowingId, Pageable pageable);

    BookBorrowingsResDto getBorrowingHistories(Integer patronId, Pageable pageable);

    Page<BookBorrowingResDto> getBorrowingHistoriesWithStatus(Integer patronId, Pageable pageable, BorrowingStatus status);

//    PatronCheckoutInfoResponseDto getCheckoutAccountByRfid(String rfid);

    PatronCheckoutInfoResponseDto getCheckoutAccountByRfidOrEmail(String key);

    ProfileAccountResDto findProfileByRfidOrEmail(String searchValue);

    PoliciesForPatronViewResDto getAllPolicy();
}
