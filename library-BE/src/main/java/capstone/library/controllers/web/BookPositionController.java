package capstone.library.controllers.web;

import capstone.library.dtos.response.BookCopyPositionResponse;
import capstone.library.services.BookCopyPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static capstone.library.util.constants.SecurityConstant.*;

@RestController
@RequestMapping("/position")
public class BookPositionController {

    @Autowired
    BookCopyPositionService bookCopyPositionService;

    @Secured({LIBRARIAN, ADMIN, PATRON})
    @GetMapping("/copy")
    public BookCopyPositionResponse findBookCopyPosition (@RequestParam(name = "bookCopyId") int bookCopyId){
        return bookCopyPositionService.findPositionForBookCopy(bookCopyId);
    }

    @Secured({LIBRARIAN, ADMIN, PATRON})
    @GetMapping("/book")
    public List<BookCopyPositionResponse> findBookPosition (@RequestParam(name = "bookId") int bookId){
        return bookCopyPositionService.findPositionForBook(bookId);
    }


}
