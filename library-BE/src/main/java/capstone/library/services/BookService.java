package capstone.library.services;

import capstone.library.dtos.BookDto;

import java.util.List;

public interface BookService {

    List<BookDto> findBooks(String searchValue);

}
