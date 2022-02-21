package capstone.library.demo.controllers;

import capstone.library.demo.dtos.request.BookCheckOutRequest;
import capstone.library.demo.dtos.request.ReturnOneRequest;
import capstone.library.demo.dtos.response.BookCheckOutResponse;
import capstone.library.demo.dtos.response.BookReturnResponse;
import capstone.library.demo.services.BookBorrowingService;
import capstone.library.demo.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/BookBorrowing")
public class BookBorrowingController {

    @Autowired
    BookBorrowingService bookBorrowingService;

    @Autowired
    EmailService emailService;

    @PostMapping("/checkout")
    public List<BookCheckOutResponse> checkout(@RequestBody @Valid BookCheckOutRequest request){
       return bookBorrowingService.checkout(request.getPatronId(), request.getBookCodeList());
    }

    @PostMapping("/returnBatch")
    public List<BookReturnResponse> returnBookByBatch(@RequestBody @NotNull List<String> bookCodeList){
        return bookBorrowingService.returnBookByBatch(bookCodeList);
    }

    @PostMapping("/returnOne")
    public BookReturnResponse returnBookOneByOne(@RequestBody @Valid ReturnOneRequest request){
        return bookBorrowingService.returnBookOneByOne(request.getBookRfid(), LocalDateTime.now());
    }
}
