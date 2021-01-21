package capstone.library.controllers.web;

import capstone.library.dtos.common.BookDto;
import capstone.library.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/web/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/search")
    public List<BookDto> findBooks(@RequestParam(required = false, value = "searchValue") String searchValue, Pageable pageable) {
        return bookService.findBooks(searchValue);
    }

}