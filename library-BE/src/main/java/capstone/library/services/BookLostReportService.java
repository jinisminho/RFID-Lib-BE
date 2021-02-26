package capstone.library.services;

import capstone.library.dtos.response.BookLostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface BookLostReportService {

    Page<BookLostResponse> findBookLostInPeriod (LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}
