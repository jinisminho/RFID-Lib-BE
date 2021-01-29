package capstone.library.controllers.web;

import capstone.library.dtos.request.CreateCopiesRequestDto;
import capstone.library.dtos.response.CopyResponseDto;
import capstone.library.services.BookCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping("/list")
    public List<CopyResponseDto> getCopiesList(Pageable pageable)
    {
        return bookCopyService.getCopiesList(pageable);
    }

    @PostMapping("/tag")
    public String tagCopy(String barcode, String rfid)
    {
        return bookCopyService.tagCopy(barcode, rfid);
    }

}