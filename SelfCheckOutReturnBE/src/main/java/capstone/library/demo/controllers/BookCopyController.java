package capstone.library.demo.controllers;

import capstone.library.demo.dtos.response.ScannedBookResponse;
import capstone.library.demo.services.BookCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/BookCopy")
public class BookCopyController {

    @Autowired
    BookCopyService bookCopyService;

    @GetMapping("/{rfid}")
    public ScannedBookResponse getBookCopyByRfid(@PathVariable("rfid")String rfid){
        return bookCopyService.searchBookByRfid(rfid);
    }
}
