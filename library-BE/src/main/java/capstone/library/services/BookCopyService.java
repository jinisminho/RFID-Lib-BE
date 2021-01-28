package capstone.library.services;

import capstone.library.dtos.request.CreateCopiesRequestDto;

public interface BookCopyService
{
    String createCopies(CreateCopiesRequestDto request);
}
