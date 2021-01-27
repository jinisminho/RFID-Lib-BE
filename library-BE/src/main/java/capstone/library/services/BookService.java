package capstone.library.services;

import capstone.library.dtos.response.BookResDto;
import capstone.library.dtos.response.BookResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService
{

    Page<BookResDto> findBooks(String searchValue, Pageable pageable);

    boolean tagRfidToBookCopy(Integer bookCopyId, String rfid);

    boolean reindexAll();

    List<BookResponseDto> findAllBooks(Pageable pageable);
}
