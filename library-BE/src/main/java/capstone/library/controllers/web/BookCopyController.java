package capstone.library.controllers.web;

import capstone.library.dtos.request.CreateCopiesRequestDto;
import capstone.library.services.BookCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/copy")
public class BookCopyController
{
    @Autowired
    BookCopyService bookCopyService;

    @PostMapping("/add")
    public String addCopies(@RequestBody @Valid CreateCopiesRequestDto request)
    {
        return bookCopyService.createCopies(request);
    }
}
