package capstone.library.controllers.web;

import capstone.library.dtos.common.ErrorDto;
import capstone.library.dtos.request.ProfileUpdateReqDto;
import capstone.library.dtos.response.BookBorrowingResDto;
import capstone.library.dtos.response.ExtendHistoryResDto;
import capstone.library.dtos.response.PatronCheckoutInfoResponseDto;
import capstone.library.dtos.response.ProfileAccountResDto;
import capstone.library.enums.BorrowingStatus;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

import static capstone.library.util.constants.SecurityConstant.*;

@RestController
@RequestMapping("/patron")
public class PatronController {

    @Autowired
    private PatronService patronService;


    @ApiOperation(value = "This API get profile of patron by its ID")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @GetMapping("/profile/getProfile/{patronId}")
    @Secured({ADMIN, LIBRARIAN, PATRON})
    public ProfileAccountResDto getProfile(@PathVariable Integer patronId) {
        return patronService.getProfile(patronId);
    }

    @ApiOperation(value = "This API update profile of patron by its ID")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/profile/updateProfile/{patronId}")
    @Secured({ADMIN, LIBRARIAN, PATRON})
    public ResponseEntity<?> updateProfile(@PathVariable Integer patronId,
                                           @Valid @RequestBody(required = true) ProfileUpdateReqDto newProfile) {
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
    @Secured({ADMIN, LIBRARIAN, PATRON})
    public Page<ExtendHistoryResDto> getExtendHistories(@PathVariable Integer bookBorrowingId,
                                                        @ApiIgnore("Ignored because swagger ui shows the wrong params") Pageable pageable) {
        return patronService.getExtendHistories(bookBorrowingId, pageable);
    }


    @ApiPageable
    @ApiOperation(value = "This API get borrowing history of 1 patron by patronId")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @GetMapping("/borrowingHistory/getBorrowingHistoriesWithStatus/{patronId}")
    @Secured({ADMIN, LIBRARIAN, PATRON})
    public Page<BookBorrowingResDto> getBorrowingHistoriesWithStatus(@ApiIgnore("Ignored because swagger ui shows the wrong params") Pageable pageable,
                                                                     @PathVariable Integer patronId,
                                                                     @RequestParam(required = false, value = "status") BorrowingStatus status) {
        return patronService.getBorrowingHistoriesWithStatus(patronId, pageable, status);
    }

//    @ApiPageable
//    @ApiOperation(value = "This API get borrowing history with  of 1 patron by patronId")
//    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
//    @GetMapping("/borrowingHistory/getBorrowingHistories/{patronId}")
//    public BookBorrowingsResDto getBorrowingHistories(@ApiIgnore("Ignored because swagger ui shows the wrong params") Pageable pageable,
//                                                      @PathVariable Integer patronId) {
//        return patronService.getBorrowingHistories(patronId, pageable);
//    }

//    @ApiOperation(value = "Get patron info by RFID (Patron Card)")
//    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
//    @GetMapping("/profile/getCheckoutPatron/{rfid}")
//    @Secured({ADMIN, LIBRARIAN})
//    public PatronCheckoutInfoResponseDto getProfileByRfid(@PathVariable @NotEmpty String rfid) {
//        return patronService.getCheckoutAccountByRfid(rfid);
//    }

    @ApiOperation(value = "Get patron info by RFID or Email (Patron Card)")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @GetMapping("/profile/getCheckoutPatron/{rfidOrEmail}")
    @Secured({ADMIN, LIBRARIAN})
    public PatronCheckoutInfoResponseDto getProfileByRfidOrEmail(@PathVariable("rfidOrEmail") @NotEmpty String key) {
        return patronService.getCheckoutAccountByRfidOrEmail(key);
    }

    @ApiOperation(value = "This API get profile of patron by its RFID or Email")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @GetMapping("/profile/findProfile/")
    @Secured({ADMIN, LIBRARIAN})
    public ProfileAccountResDto findProfileByRfidOrEmail(@RequestParam String searchValue) {
        return patronService.findProfileByRfidOrEmail(searchValue);
    }

}

