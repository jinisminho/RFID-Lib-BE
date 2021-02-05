package capstone.library.demo.services;

import capstone.library.demo.dtos.request.CheckOutBookRequest;
import capstone.library.demo.dtos.response.BookCheckOutResponse;
import capstone.library.demo.dtos.response.BookReturnResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface BookBorrowingService {

    List<BookCheckOutResponse> checkout (int patronId, List<CheckOutBookRequest> bookCodeList);

    List<BookReturnResponse> returnBookByBatch(List<String> bookCodeList);

    BookReturnResponse returnBookOneByOne (String rfidCode, LocalDateTime curdateTime);
}
