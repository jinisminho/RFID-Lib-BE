package capstone.library.controllers.web;

import capstone.library.dtos.request.CreateCopiesRequestDto;
import capstone.library.dtos.request.TagCopyRequestDto;
import capstone.library.dtos.request.UpdateCopyRequest;
import capstone.library.dtos.response.BookCopyResDto;
import capstone.library.dtos.response.CheckCopyPolicyResponseDto;
import capstone.library.dtos.response.CopyResponseDto;
import capstone.library.services.BookCopyService;
import capstone.library.util.ApiPageable;
import capstone.library.util.tools.BarcodePrinter;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/copy")
public class BookCopyController {
    @Autowired
    BookCopyService bookCopyService;

    @PostMapping("/add")
    public String addCopies(@RequestBody @Valid CreateCopiesRequestDto request) {
        return bookCopyService.createCopies(request);
    }

    @GetMapping("/list")
    public Page<CopyResponseDto> getCopiesList(Pageable pageable) {
        return bookCopyService.getCopiesList(pageable);
    }

    @PostMapping("/tag")
    public String tagCopy(@RequestBody @Valid @NotNull TagCopyRequestDto request) {
        return bookCopyService.tagCopy(request);
    }

    @GetMapping("/validate/{rfid}")
    public CheckCopyPolicyResponseDto checkCopyPolicy(@PathVariable @NotEmpty String rfid,
                                                      @RequestParam @NotEmpty int patronId) {
        return bookCopyService.validateCopyByRFID(rfid, patronId);
    }

    @GetMapping("/get/barcode/{barcode}")
    public CopyResponseDto getCopyByBarcode(@PathVariable @NotEmpty String barcode) {
        return bookCopyService.getCopyByBarcode(barcode);
    }

    @GetMapping("/get/rfid/{rfid}")
    public CopyResponseDto getCopyByRfid(@PathVariable @NotEmpty String rfid) {
        return bookCopyService.getCopyByRfid(rfid);
    }

    @PostMapping("/update")
    @ApiOperation("Update a book copy by id")
    public String updateCopy(@RequestBody @Valid @NotNull UpdateCopyRequest request) {
        return bookCopyService.updateCopy(request);
    }

    @ApiOperation(value = "This API use to search book copy by like title-subtitle and exact ISBN, barcode, RFID. Filter by status. e.g. [http://localhost:8091/copy/search?page=0&searchValue=hobit&size=5&status=IN_PROCESS,AVAILABLE]")
    @ApiPageable
    @GetMapping("/search")
    public Page<BookCopyResDto> findBookCopies(@RequestParam(required = false, value = "searchValue") String searchValue, @RequestParam(required = false, value = "status") List<String> status, @ApiIgnore("Ignored because swagger ui shows the wrong params") Pageable pageable) {
        return bookCopyService.findBookCopies(searchValue, status, pageable);
    }

    @GetMapping("/get/id/{id}")
    public CopyResponseDto getCopyById(@PathVariable @NotEmpty Integer id) {
        return bookCopyService.getCopyById(id);
    }

    @GetMapping(value = "/barbecue/ean13/{barcode}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<String> barbecueEAN13Barcode(@PathVariable("barcode") String barcode)
            throws Exception {
        BarcodePrinter.createImage(barcode);
        return new ResponseEntity<>( "OK",HttpStatus.OK);
    }
}
