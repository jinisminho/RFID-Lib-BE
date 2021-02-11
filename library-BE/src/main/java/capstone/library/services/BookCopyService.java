package capstone.library.services;

import capstone.library.dtos.request.CreateCopiesRequestDto;
import capstone.library.dtos.request.TagCopyRequestDto;
import capstone.library.dtos.request.UpdateCopyRequest;
import capstone.library.dtos.response.BookCopyResDto;
import capstone.library.dtos.response.CheckCopyPolicyResponseDto;
import capstone.library.dtos.response.CopyResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookCopyService {
    String createCopies(CreateCopiesRequestDto request);

    Page<CopyResponseDto> getCopiesList(Pageable pageable);

    String tagCopy(TagCopyRequestDto request);

    CheckCopyPolicyResponseDto validateCopyByRFID(String rfid, int patronId);

    CopyResponseDto getCopyByBarcode(String barcode);

    CopyResponseDto getCopyByRfid(String rfid);

    String updateCopy(UpdateCopyRequest request);

    Page<BookCopyResDto> findBookCopies(String searchValue, List<String> status, Pageable pageable);
}
