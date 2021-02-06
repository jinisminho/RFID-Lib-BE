package capstone.library.controllers.web;

import capstone.library.dtos.request.CreateCopiesRequestDto;
import capstone.library.dtos.request.TagCopyRequestDto;
import capstone.library.dtos.request.UpdateCopyRequest;
import capstone.library.dtos.response.CheckCopyPolicyResponseDto;
import capstone.library.dtos.response.CopyResponseDto;
import capstone.library.services.BookCopyService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
    public Page<CopyResponseDto> getCopiesList(Pageable pageable)
    {
        return bookCopyService.getCopiesList(pageable);
    }

    @PostMapping("/tag")
    public String tagCopy(@RequestBody @Valid @NotNull TagCopyRequestDto request)
    {
        return bookCopyService.tagCopy(request);
    }

    @GetMapping("/validate/{rfid}")
    public CheckCopyPolicyResponseDto checkCopyPolicy(@PathVariable @NotEmpty String rfid,
                                                      @RequestParam @NotEmpty int patronId)
    {
        return bookCopyService.validateCopyByRFID(rfid, patronId);
    }

    @GetMapping("/get/barcode/{barcode}")
    public CopyResponseDto getCopyByBarcode(@PathVariable @NotEmpty String barcode)
    {
        return bookCopyService.getCopyByBarcode(barcode);
    }

    @GetMapping("/get/rfid/{rfid}")
    public CopyResponseDto getCopyByRfid(@PathVariable @NotEmpty String rfid)
    {
        return bookCopyService.getCopyByRfid(rfid);
    }

    @PostMapping("/update")
    @ApiOperation("Update a book copy by id")
    public String updateCopy(@RequestBody @Valid @NotNull UpdateCopyRequest request)
    {
        return bookCopyService.updateCopy(request);
    }

}
