package capstone.library.controllers.web;

import capstone.library.dtos.ProfileDto;
import capstone.library.dtos.ProfileUpdateDto;
import capstone.library.services.PatronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web/patron")
public class PatronController {

    @Autowired
    private PatronService patronService;

    @PostMapping("/wishlist")
    public ResponseEntity<?> addWishlist(@RequestParam(required = true, value = "bookID") Integer bookId,
                                         @RequestParam(required = true, value = "patronID") Integer patronId) {
        boolean bool = patronService.addWishlist(bookId, patronId);
        return new ResponseEntity(bool ? "Added new wishlist" : "Failed to Added new wishlist",
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/profile/{patronId}")
    public ProfileDto getProfile(@PathVariable Integer patronId) {
        return patronService.getProfile(patronId);
    }

    @PostMapping("/profile/{patronId}")
    public ResponseEntity<?> updateProfile(@PathVariable Integer patronId,
                                           @RequestBody(required = true) ProfileUpdateDto newProfile) {
        boolean bool = patronService.updateProfile(patronId, newProfile);
        return new ResponseEntity(bool ? "Updated profile" : "Failed to update profile",
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }


}
