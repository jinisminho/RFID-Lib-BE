package capstone.library.services;

import capstone.library.dtos.request.ScannedRFIDCopiesRequestDto;
import capstone.library.dtos.response.*;

import java.util.List;

public interface LibrarianService
{

    CheckoutResponseDto checkout(ScannedRFIDCopiesRequestDto scannedRFIDCopiesRequestDto);

    List<ReturnBookResponseDto> returnBookCopies(ScannedRFIDCopiesRequestDto scannedRFIDCopiesRequestDto);

    List<BookResponseDto> getOverdueBooksByBorrower(int patronId);

    CheckoutPolicyValidationResponseDto validateCheckoutPolicy(ScannedRFIDCopiesRequestDto scannedRFIDCopiesRequestDto);

    GenerateBarcodesResponseDto generateBarcodes(int numberOfCopies, String isbn, int copyTypeId);

    List<ReturnBookResponseDto> validateReturnRequest(ScannedRFIDCopiesRequestDto scannedRFIDCopiesRequestDto);
}
