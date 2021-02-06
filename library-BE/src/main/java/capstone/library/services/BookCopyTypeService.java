package capstone.library.services;

import capstone.library.dtos.response.BookCopyTypeResponseDto;

import java.util.List;

public interface BookCopyTypeService
{
    List<BookCopyTypeResponseDto> getAllCopyTypes();
}
