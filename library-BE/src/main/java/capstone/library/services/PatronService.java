package capstone.library.services;

import capstone.library.dtos.request.ProfileUpdateReqDto;
import capstone.library.dtos.response.ProfileResDto;


public interface PatronService {

    boolean addWishlist(Integer bookId, Integer patronId);

    ProfileResDto getProfile(Integer patronId);

    boolean updateProfile(Integer patronId, ProfileUpdateReqDto newProfile);

}
