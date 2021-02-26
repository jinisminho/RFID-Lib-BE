package capstone.library.controllers.web;

import capstone.library.dtos.response.LostBookFineResponseDto;
import capstone.library.services.BookLostReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/lost")
public class BookLostReportController {

    @Autowired
    BookLostReportService bookLostReportService;

    @GetMapping("/getLostBookFine/{bookBorrowingId}")
    public LostBookFineResponseDto getLostBookFine(@PathVariable @NotNull int bookBorrowingId) {
        return bookLostReportService.getLostBookFine(bookBorrowingId);
    }
}
