package capstone.library.demo.services;

import capstone.library.demo.dtos.request.CheckOutBookRequest;
import capstone.library.demo.dtos.response.BookCheckOutResponse;
import capstone.library.demo.dtos.response.BookReturnResponse;

import java.util.List;

public interface BookBorrowingService {

    List<BookCheckOutResponse> checkout (int patronId, List<CheckOutBookRequest> bookCodeList);

    List<BookReturnResponse> returnBook (List<String> bookCodeList);
}
