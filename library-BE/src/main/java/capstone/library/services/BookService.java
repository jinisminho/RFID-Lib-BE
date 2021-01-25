package capstone.library.services;

import capstone.library.dtos.response.BookResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    Page<BookResDto> findBooks(String searchValue, Pageable pageable);

    boolean tagRfidToBookCopy(Integer bookCopyId, String rfid);

    boolean reindexAll();
}
