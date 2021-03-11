package capstone.library.controllers.web;

import capstone.library.dtos.request.CreateBookRequestDto;
import capstone.library.dtos.request.UpdateBookInfoRequestDto;
import capstone.library.dtos.response.BookResDto;
import capstone.library.dtos.response.BookResponseDto;
import capstone.library.enums.BookStatus;
import capstone.library.services.BookService;
import capstone.library.util.ApiPageable;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static capstone.library.util.constants.SecurityConstant.*;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @ApiOperation(value = "This API use to search book by like title-subtitle and exact ISBN")
    @ApiPageable
    @GetMapping("/search")
    @Secured({ADMIN, LIBRARIAN, PATRON})
    public Page<BookResDto> findBooks(@RequestParam(required = false, value = "searchValue") String searchValue, @RequestParam(required = false, value = "status") List<String> status, @ApiIgnore("Ignored because swagger ui shows the wrong params") Pageable pageable) {
        return bookService.findBooks(searchValue, status, pageable);
    }

    @ApiOperation(value = "This API use to reindex all for Hibernate Search")
    @PostMapping("/reindex")
    public ResponseEntity<?> reindexAll() {
        boolean bool = bookService.reindexAll();
        return new ResponseEntity(bool ? "Reindexed" : "Failed to reindex", bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/all")
    public Page<BookResponseDto> findAllBooks(Pageable pageable) {
        return bookService.findAllBooks(pageable);
    }

    @PostMapping("/add")
    public String addBook(@RequestBody @Valid CreateBookRequestDto request) {
        return bookService.addBook(request);
    }

    @PostMapping("/update/status/{id}")
    public String updateBookStatus(@NotNull @PathVariable int id, @RequestParam(value = "status") BookStatus status) {
        return bookService.updateBookStatus(id, status);
    }

    @PostMapping("/update")
    public String updateBookInfo(@RequestBody @Valid UpdateBookInfoRequestDto request) {
        return bookService.updateBookInfo(request);
    }

    @GetMapping("/findByISBN")
    public BookResponseDto findByISBN(String isbn) {
        return bookService.findByISBN(isbn);
    }
}