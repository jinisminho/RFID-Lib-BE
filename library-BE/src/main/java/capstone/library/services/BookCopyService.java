package capstone.library.services;

import capstone.library.dtos.request.CreateCopiesRequestDto;
import capstone.library.dtos.response.CopyResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookCopyService
{
    String createCopies(CreateCopiesRequestDto request);

    List<CopyResponseDto> getCopiesList(Pageable pageable);

}
