package capstone.library.controllers.web;

import capstone.library.dtos.response.BookLostResponse;
import capstone.library.services.BookLostReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static capstone.library.util.constants.ConstantUtil.DATE_TIME_PATTERN;

@RestController
@RequestMapping("/lostBook")
public class BookLostReportController {

    @Autowired
    private BookLostReportService bookLostReportService;

    @GetMapping("/find")
    public Page<BookLostResponse> findLostBooksInPeriod(Pageable pageable,
                                                        @RequestParam(required = false, name = "startDate")
                                                        @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                                LocalDateTime startDate,
                                                        @RequestParam(required = false, name = "endDate")
                                                        @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                                LocalDateTime endDate) {
        return bookLostReportService.findBookLostInPeriod(startDate, endDate, pageable);
    }
}
