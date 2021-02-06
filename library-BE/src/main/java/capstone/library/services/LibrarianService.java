package capstone.library.services;

import capstone.library.dtos.request.ScannedRFIDCopiesRequestDto;
import capstone.library.dtos.response.BookResponseDto;
import capstone.library.dtos.response.CheckoutPolicyValidationResponseDto;
import capstone.library.dtos.response.CheckoutResponseDto;
import capstone.library.dtos.response.ReturnBookResponseDto;

import java.util.List;

public interface LibrarianService
{

    CheckoutResponseDto checkout(ScannedRFIDCopiesRequestDto scannedRFIDCopiesRequestDto);

    List<ReturnBookResponseDto> returnBookCopies(ScannedRFIDCopiesRequestDto scannedRFIDCopiesRequestDto);

    List<BookResponseDto> getOverdueBooksByBorrower(int patronId);

    CheckoutPolicyValidationResponseDto validateCheckoutPolicy(ScannedRFIDCopiesRequestDto scannedRFIDCopiesRequestDto);
}
