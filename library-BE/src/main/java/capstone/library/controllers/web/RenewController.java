package capstone.library.controllers.web;

import capstone.library.dtos.common.ErrorDto;
import capstone.library.dtos.response.ValidateRenewDto;
import capstone.library.services.RenewService;
import capstone.library.util.constants.ConstantUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/renew")
public class RenewController {
    @Autowired
    RenewService renewService;

    @ApiOperation(value = "Validate renew API")
    @GetMapping("/validate/{bookBorrowingId}")
    public ValidateRenewDto validateRenew(@PathVariable @NotEmpty int bookBorrowingId) {
        return renewService.validateRenew(bookBorrowingId);
    }

    @ApiOperation(value = "This API extend due date of 1 borrowing book by bookBorrowingId")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/createExtendHistory/{bookBorrowingId}")
    public ResponseEntity<?> AddNewExtendedDueDate(@PathVariable Integer bookBorrowingId,
                                                   @RequestParam(required = false, value = "librarianId") Integer librarianId,
                                                   @RequestParam(required = false, value = "numberOfDayToPlus") Integer numberOfDayToPlus,
                                                   @RequestBody(required = false) String reason) {

        boolean bool = renewService.addNewExtendHistory(bookBorrowingId, librarianId, numberOfDayToPlus, reason);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL SERVER ERROR",
                "Failed to Added new extended due date");

        return new ResponseEntity(bool ? ConstantUtil.CREATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
