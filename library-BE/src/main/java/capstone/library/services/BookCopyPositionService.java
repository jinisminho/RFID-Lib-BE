package capstone.library.services;


import capstone.library.dtos.response.BookCopyPositionResponse;

import java.util.List;

public interface BookCopyPositionService {

    BookCopyPositionResponse findPositionForBookCopy (int bookCopyId);

    List<BookCopyPositionResponse> findPositionForBook(int bookId);
}
