package capstone.library.controllers.web;

import capstone.library.dtos.common.ErrorDto;
import capstone.library.dtos.request.ProfileUpdateReqDto;
import capstone.library.dtos.response.BookBorrowingResDto;
import capstone.library.dtos.response.ExtendHistoryResDto;
import capstone.library.dtos.response.PatronCheckoutInfoResponseDto;
import capstone.library.dtos.response.ProfileAccountResDto;
import capstone.library.services.PatronService;
import capstone.library.util.ApiPageable;
import capstone.library.util.constants.ConstantUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/patron")
public class PatronController
{

    @Autowired
    private PatronService patronService;

    @ApiOperation(value = "This API create new wishlist")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/wishlist/addWishlist")
    public ResponseEntity<?> addWishlist(@RequestParam(required = true, value = "bookID") Integer bookId,
                                         @RequestParam(required = true, value = "patronID") Integer patronId)
    {

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
    @GetMapping("/profile/getProfile/{patronId}")
    public ProfileAccountResDto getProfile(@PathVariable Integer patronId)
    {
        return patronService.getProfile(patronId);
    }

    @ApiOperation(value = "This API update profile of patron by its ID")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/profile/updateProfile/{patronId}")
    public ResponseEntity<?> updateProfile(@PathVariable Integer patronId,
                                           @Valid @RequestBody(required = true) ProfileUpdateReqDto newProfile)
    {
        boolean bool = patronService.updateProfile(patronId, newProfile);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL SERVER ERROR",
                "Failed to update profile");

        return new ResponseEntity(bool ? ConstantUtil.UPDATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiPageable
    @ApiOperation(value = "This API get extend history of 1 borrowing book by bookBorrowingId")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @GetMapping("/extendHistory/getExtendHistories/{bookBorrowingId}")
    public Page<ExtendHistoryResDto> getExtendHistories(@PathVariable Integer bookBorrowingId,
                                                        @ApiIgnore("Ignored because swagger ui shows the wrong params") Pageable pageable)
    {
        return patronService.getExtendHistories(bookBorrowingId, pageable);
    }

    @ApiOperation(value = "This API extend due date of 1 borrowing book by bookBorrowingId")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/extendHistory/createExtendHistory/{bookBorrowingId}")
    public ResponseEntity<?> AddNewExtendedDueDate(@PathVariable Integer bookBorrowingId,
                                                   @RequestParam(required = false, value = "librarianId") Integer librarianId,
                                                   @RequestParam(required = false, value = "numberOfDayToPlus") Integer numberOfDayToPlus)
    {

        boolean bool = patronService.addNewExtendHistory(bookBorrowingId, librarianId, numberOfDayToPlus);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL SERVER ERROR",
                "Failed to Added new extended due date");

        return new ResponseEntity(bool ? ConstantUtil.CREATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiPageable
    @ApiOperation(value = "This API get borrowing history of 1 patron by patronId")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @GetMapping("/borrowingHistory/getBorrowingHistories/{patronId}")
    public Page<BookBorrowingResDto> getBorrowingHistories(@ApiIgnore("Ignored because swagger ui shows the wrong params") Pageable pageable,
                                                           @PathVariable Integer patronId)
    {
        return patronService.getBorrowingHistories(patronId, pageable);
    }

    @ApiOperation(value = "Get patron info by RFID (Patron Card)")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @GetMapping("/profile/getCheckoutPatron/{rfid}")
    public PatronCheckoutInfoResponseDto getProfileByRfid(@PathVariable @NotEmpty String rfid)
    {
        return patronService.getProfileByRfid(rfid);
    }
}

