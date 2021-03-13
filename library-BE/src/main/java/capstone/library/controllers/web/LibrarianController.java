package capstone.library.controllers.web;

import capstone.library.dtos.request.ScannedRFIDCopiesRequestDto;
import capstone.library.dtos.response.*;
import capstone.library.services.LibrarianService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

import static capstone.library.util.constants.SecurityConstant.ADMIN;
import static capstone.library.util.constants.SecurityConstant.LIBRARIAN;

@RestController
@RequestMapping("/librarian")
public class LibrarianController {
    @Autowired
    LibrarianService librarianService;

    @PostMapping("/checkout")
    @ApiOperation(value = "Checkout a list of book copies")
    public CheckoutResponseDto checkoutBookCopies(@RequestBody ScannedRFIDCopiesRequestDto request) {
        return librarianService.checkout(request);
    }

    @PostMapping("/checkout/validate")
    @ApiOperation(value = "Validate policy for a list of book copies")
    public CheckoutPolicyValidationResponseDto validateCheckoutPolicy(@RequestBody ScannedRFIDCopiesRequestDto request) {
        return librarianService.validateCheckoutPolicy(request);
    }

    @GetMapping("/return/validate")
    @ApiOperation(value = "Validate return request")
    public ReturnBookResponseDto valildateReturnRequest(String rfid) {
        return librarianService.validateReturnRequest(rfid);
    }

    @GetMapping("/return/validate/{rfidOrBarcode}")
    @ApiOperation(value = "Validate return request by rfid or barcode")
    @Secured({ADMIN, LIBRARIAN})
    public ReturnBookResponseDto valildateReturnRequestByRfidOrBarcode(@PathVariable("rfidOrBarcode") @NotEmpty String value) {
        return librarianService.validateReturnRequestByRfidOrBarcode(value);
    }


    @PostMapping("/return")
    @ApiOperation(value = "Return (Checkin) a list of book copies")
    public ReturnBooksResponse returnBookCopies(@RequestBody ScannedRFIDCopiesRequestDto request) {
        request.setCheckin(true);
        ReturnBooksResponse rs = new ReturnBooksResponse();
        rs.setReturnedBooks(librarianService.returnBookCopies(request));
        return rs;
    }

    @GetMapping("/overdue/{patronId}")
    @ApiOperation(value = "return a list of overdue books based on borrower")
    public List<BookResponseDto> getOverdueCopiesByBorrower(@PathVariable int patronId) {
        return librarianService.getOverdueBooksByBorrower(patronId);
    }

    @GetMapping("/barcodes/generate")
    @ApiOperation(value = "return a list of barcodes based on book id")
    public GenerateBarcodesResponseDto generateBarcodes(int numberOfCopies, String isbn, int copyTypeId) {
        return librarianService.generateBarcodes(numberOfCopies, isbn, copyTypeId);
    }


}
