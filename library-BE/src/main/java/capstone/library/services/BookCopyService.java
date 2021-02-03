package capstone.library.services;

import capstone.library.dtos.request.CreateCopiesRequestDto;
import capstone.library.dtos.response.CopyResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookCopyService
{
    String createCopies(CreateCopiesRequestDto request);

    Page<CopyResponseDto> getCopiesList(Pageable pageable);

    String tagCopy(String barcode, String rfid);
}
