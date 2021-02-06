package capstone.library.services;

import capstone.library.dtos.response.BookCopyTypeResponseDto;
import capstone.library.dtos.common.BookCopyTypeDto;

import java.util.List;

public interface BookCopyTypeService
{
    List<BookCopyTypeResponseDto> getAllCopyTypes();
    List<BookCopyTypeDto> getAllBookCopyType();
}