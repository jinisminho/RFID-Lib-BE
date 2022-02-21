package capstone.library.services;


import capstone.library.dtos.request.CreateCopyPostionReqDto;
import capstone.library.dtos.request.SaveSamplePositionRequestDto;
import capstone.library.dtos.response.BookCopyPositionResponse;
import capstone.library.dtos.response.CopyResponseDto;
import capstone.library.entities.BookCopyPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface BookCopyPositionService {

    BookCopyPositionResponse findPositionForBookCopy(int bookCopyId);

    List<BookCopyPositionResponse> findPositionForBook(int bookId);

    Set<String> getAllShelves();

    List<BookCopyPosition> getRowByShelf(String shelf);

    String saveSampledPosition(SaveSamplePositionRequestDto request);

    List<CopyResponseDto> getBooksOnARow(int positionId);

    List<CopyResponseDto> getBooksOnARowByRFID(String rfid);

    BookCopyPositionResponse getPositionByRFID(String rfid);

    boolean addPos(CreateCopyPostionReqDto reqDto);

    boolean updatePos(int id, CreateCopyPostionReqDto reqDto);

    boolean deletePos(int id);

    Page<BookCopyPositionResponse> getAll(String shelf, Integer line, Pageable pageable);

}
