package capstone.library.services;

import capstone.library.dtos.request.ConfirmLostBookRequest;
import capstone.library.dtos.response.LostBookFineResponseDto;
import capstone.library.dtos.response.BookLostResponse;
import capstone.library.enums.LostBookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface BookLostReportService {
    Page<BookLostResponse> findBookLostInPeriod (LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    LostBookFineResponseDto getLostBookFine(int bookLostReportId);

    String confirmBookLost(ConfirmLostBookRequest lostBook);

    String reportLostByPatron(int BookBorrowingId);

    Page<BookLostResponse> findBookLostByStatus(LostBookStatus status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}