package capstone.library.services;

import capstone.library.dtos.request.CreateBookRequestDto;
import capstone.library.dtos.request.UpdateBookInfoRequestDto;
import capstone.library.dtos.response.BookResDto;
import capstone.library.dtos.response.BookResponseDto;
import capstone.library.enums.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService
{

    Page<BookResDto> findBooks(String searchValue, Pageable pageable);

    boolean tagRfidToBookCopy(Integer bookCopyId, String rfid);

    boolean reindexAll();

    Page<BookResponseDto> findAllBooks(Pageable pageable);

    String addBook(CreateBookRequestDto request);

    String updateBookStatus(int id, BookStatus status);

    String updateBookInfo(UpdateBookInfoRequestDto request);
}
