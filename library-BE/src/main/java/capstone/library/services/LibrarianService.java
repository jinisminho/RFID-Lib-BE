package capstone.library.services;

import capstone.library.dtos.request.ScannedRFIDBooksRequestDto;
import capstone.library.dtos.response.CheckoutBookResponseDto;
import capstone.library.dtos.response.ReturnBookResponseDto;

import java.util.List;

public interface LibrarianService
{

    List<CheckoutBookResponseDto> checkout(ScannedRFIDBooksRequestDto scannedRFIDBooksRequestDto);

    List<ReturnBookResponseDto> returnBooks(ScannedRFIDBooksRequestDto scannedRFIDBooksRequestDto);
}
