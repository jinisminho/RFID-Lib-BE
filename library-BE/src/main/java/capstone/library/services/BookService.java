package capstone.library.services;

import capstone.library.dtos.response.BookResDto;

import java.util.List;

public interface BookService {

    List<BookResDto> findBooks(String searchValue);

    boolean tagRfidToBookCopy(Integer bookCopyId, String rfid);

    boolean reindexAll();
}
