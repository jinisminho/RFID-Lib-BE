package capstone.library.controllers.web;

import capstone.library.dtos.request.ScannedRFIDCopiesRequestDto;
import capstone.library.dtos.response.BookResponseDto;
import capstone.library.dtos.response.CheckoutPolicyValidationResponseDto;
import capstone.library.dtos.response.CheckoutResponseDto;
import capstone.library.dtos.response.ReturnBookResponseDto;
import capstone.library.services.LibrarianService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/librarian")
public class LibrarianController
{
    @Autowired
    LibrarianService librarianService;

    @PostMapping("/checkout")
    @ApiOperation(value = "Checkout a list of book copies")
    public List<CheckoutResponseDto> checkoutBookCopies(@RequestBody ScannedRFIDCopiesRequestDto scannedRFIDCopiesRequestDto)
    {
        return librarianService.checkout(scannedRFIDCopiesRequestDto);
    }

    @PostMapping("/checkout/validate")
    @ApiOperation(value = "Validate policy for a list of book copies")
    public CheckoutPolicyValidationResponseDto validateCheckoutPolicy(@RequestBody ScannedRFIDCopiesRequestDto scannedRFIDCopiesRequestDto)
    {
        return librarianService.validateCheckoutPolicy(scannedRFIDCopiesRequestDto);
    }

    @PostMapping("/return")
    @ApiOperation(value = "Return (Checkin) a list of book copies")
    public List<ReturnBookResponseDto> returnBookCopies(@RequestBody ScannedRFIDCopiesRequestDto scannedRFIDCopiesRequestDto)
    {
        return librarianService.returnBookCopies(scannedRFIDCopiesRequestDto);
    }

    @GetMapping("/overdue/{patronId}")
    @ApiOperation(value = "return a list of overdue books based on borrower")
    public List<BookResponseDto> getOverdueCopiesByBorrower(@PathVariable int patronId)
    {
        return librarianService.getOverdueBooksByBorrower(patronId);
    }
}
