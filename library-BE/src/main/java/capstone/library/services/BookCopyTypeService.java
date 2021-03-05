package capstone.library.services;

import capstone.library.dtos.common.BookCopyTypeDto;
import capstone.library.dtos.request.AddBookCopyTypeReqDto;
import capstone.library.dtos.request.BookCopyTypeReqDto;
import capstone.library.dtos.response.BookCopyTypeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookCopyTypeService {
    List<BookCopyTypeResponseDto> getAllCopyTypes();

    List<BookCopyTypeDto> getAllBookCopyType();

    boolean addBookCopyType(AddBookCopyTypeReqDto req);

    boolean updateBookCopyType(Integer id, BookCopyTypeReqDto req);

    boolean deleteBookCopyType(Integer id);

    Page<BookCopyTypeDto> getPatronType(Pageable pageable, String name);
}