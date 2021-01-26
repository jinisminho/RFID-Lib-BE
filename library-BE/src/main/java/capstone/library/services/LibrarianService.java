package capstone.library.services;

import capstone.library.dtos.request.ScannedRFIDBooksRequestDto;
import capstone.library.dtos.response.BookCheckoutResponseDto;
import capstone.library.dtos.response.BookReturnResponseDto;

import java.util.List;

public interface LibrarianService
{

    List<BookCheckoutResponseDto> checkout(ScannedRFIDBooksRequestDto scannedRFIDBooksRequestDto);

    List<BookReturnResponseDto> returnBooks(ScannedRFIDBooksRequestDto scannedRFIDBooksRequestDto);
}
