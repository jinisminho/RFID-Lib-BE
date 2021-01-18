package capstone.library.controllers.web;

import capstone.library.dtos.ErrorDto;
import capstone.library.dtos.request.ProfileUpdateReqDto;
import capstone.library.dtos.response.ProfileResDto;
import capstone.library.services.PatronService;
import capstone.library.util.ConstantUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/web/patron")
public class PatronController {

    @Autowired
    private PatronService patronService;

    @ApiOperation(value = "This API create new wishlist")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/wishlist")
    public ResponseEntity<?> addWishlist(@RequestParam(required = true, value = "bookID") Integer bookId,
                                         @RequestParam(required = true, value = "patronID") Integer patronId) {

        boolean bool = patronService.addWishlist(bookId, patronId);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL SERVER ERROR",
                "Failed to Added new wishlist");

        return new ResponseEntity(bool ? ConstantUtil.CREATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(value = "This API get profile of patron by its ID")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @GetMapping("/profile/{patronId}")
    public ProfileResDto getProfile(@PathVariable Integer patronId) {
        return patronService.getProfile(patronId);
    }

    @ApiOperation(value = "This API update profile of patron by its ID")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/profile/{patronId}")
    public ResponseEntity<?> updateProfile(@PathVariable Integer patronId,
                                           @RequestBody(required = true) ProfileUpdateReqDto newProfile) {
        boolean bool = patronService.updateProfile(patronId, newProfile);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL SERVER ERROR",
                "Failed to update profile");

        return new ResponseEntity(bool ? ConstantUtil.UPDATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

