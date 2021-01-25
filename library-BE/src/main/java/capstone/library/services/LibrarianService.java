package capstone.library.services;

import capstone.library.dtos.request.ScannedRFIDBooksRequestDto;
import capstone.library.dtos.response.BookCheckoutResponseDto;

import java.util.List;

public interface LibrarianService
{

    List<BookCheckoutResponseDto> checkout(ScannedRFIDBooksRequestDto scannedRFIDBooksRequestDto);
}
