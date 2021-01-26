package capstone.library.controllers.web;

import capstone.library.dtos.request.ScannedRFIDBooksRequestDto;
import capstone.library.dtos.response.CheckoutBookResponseDto;
import capstone.library.dtos.response.ReturnBookResponseDto;
import capstone.library.services.LibrarianService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/librarian")
public class LibrarianController
{
    @Autowired
    LibrarianService librarianService;

    @PostMapping("/checkout")
    @ApiOperation(value = "Checkout a list of books")
    public List<CheckoutBookResponseDto> checkoutBooks(@RequestBody ScannedRFIDBooksRequestDto scannedRFIDBooksRequestDto)
    {
        return librarianService.checkout(scannedRFIDBooksRequestDto);
    }

    @PostMapping("/return")
    @ApiOperation(value = "return a list of books")
    public List<ReturnBookResponseDto> returnBooks(@RequestBody ScannedRFIDBooksRequestDto scannedRFIDBooksRequestDto)
    {
        return librarianService.returnBooks(scannedRFIDBooksRequestDto);
    }
}
