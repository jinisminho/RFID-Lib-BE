package capstone.library.demo.controllers;

import capstone.library.demo.dtos.request.BookCheckOutRequest;
import capstone.library.demo.dtos.response.BookCheckOutResponse;
import capstone.library.demo.dtos.response.BookReturnResponse;
import capstone.library.demo.services.BookBorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("BookBorrowing")
public class BookBorrowingController {

    @Autowired
    BookBorrowingService bookBorrowingService;

    @PostMapping("/checkout")
    public List<BookCheckOutResponse> checkout(@RequestBody @Valid BookCheckOutRequest request){
        return bookBorrowingService.checkout(request.getPatronId(), request.getBookCodeList());
    }

    @PostMapping("/return")
    public List<BookReturnResponse> returnBook(@RequestBody List<String> bookCodeList){
        return bookBorrowingService.returnBook(bookCodeList);
    }
}
