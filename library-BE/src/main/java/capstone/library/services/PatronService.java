package capstone.library.services;

import capstone.library.dtos.ProfileDto;
import capstone.library.dtos.ProfileUpdateDto;

public interface PatronService {

    boolean addWishlist(Integer bookId, Integer patronId);

    ProfileDto getProfile(Integer patronId);

    boolean updateProfile(Integer patronId, ProfileUpdateDto newProfile);

}
