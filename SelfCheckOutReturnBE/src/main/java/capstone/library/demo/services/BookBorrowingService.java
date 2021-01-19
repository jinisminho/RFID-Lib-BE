package capstone.library.demo.services;

import capstone.library.demo.dtos.response.BookCheckOutResponse;

import java.util.List;

public interface BookBorrowingService {

    List<BookCheckOutResponse> checkout (int patronId, List<String> BookCodeList);
}
