package capstone.library.controllers.web;

import capstone.library.dtos.response.BookCopyTypeResponseDto;
import capstone.library.services.BookCopyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/copyType")
public class BookCopyTypeController
{
    @Autowired
    BookCopyTypeService bookCopyTypeService;

    @GetMapping("/all")
    public List<BookCopyTypeResponseDto> getCopiesList()
    {
        return bookCopyTypeService.getAllCopyTypes();
    }
}
