package capstone.library.controllers.web;

import capstone.library.dtos.request.CreateCopiesRequestDto;
import capstone.library.dtos.request.TagCopyRequestDto;
import capstone.library.dtos.request.UpdateCopyRequest;
import capstone.library.dtos.response.BookCopyResDto;
import capstone.library.dtos.response.CheckCopyPolicyResponseDto;
import capstone.library.dtos.response.CopyResponseDto;
import capstone.library.services.BookCopyService;
import capstone.library.util.ApiPageable;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static capstone.library.util.constants.SecurityConstant.ADMIN;
import static capstone.library.util.constants.SecurityConstant.LIBRARIAN;

@RestController
@RequestMapping("/copy")
public class BookCopyController {
    @Autowired
    BookCopyService bookCopyService;

    @PostMapping("/add")
    public ResponseEntity<Resource> addCopies(@RequestBody @Valid CreateCopiesRequestDto request) {
        Resource resource = bookCopyService.createCopies(request);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/list")
    public Page<CopyResponseDto> getCopiesList(Pageable pageable) {
        return bookCopyService.getCopiesList(pageable);
    }

    @PostMapping("/tag")
    public String tagCopy(@RequestBody @Valid @NotNull TagCopyRequestDto request) {
        return bookCopyService.tagCopy(request);
    }

//    @GetMapping("/validate/{rfid}")
//    public CheckCopyPolicyResponseDto checkCopyPolicy(@PathVariable @NotEmpty String rfid,
//                                                      @RequestParam @NotEmpty int patronId) {
//        return bookCopyService.validateCopyByRFID(rfid, patronId);
//    }

    @GetMapping("/validate/{rfidOrBarcode}")
    public CheckCopyPolicyResponseDto checkCopyPolicy(@PathVariable("rfidOrBarcode") @NotEmpty String key,
                                                      @RequestParam @NotEmpty int patronId) {
        return bookCopyService.validateCopyByRFIDOrBarcode(key, patronId);
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
    @Secured({ADMIN, LIBRARIAN})
    public Page<BookCopyResDto> findBookCopies(@RequestParam(required = false, value = "searchValue") String searchValue, @RequestParam(required = false, value = "status") List<String> status, @ApiIgnore("Ignored because swagger ui shows the wrong params") Pageable pageable) {
        return bookCopyService.findBookCopies(searchValue, status, pageable);
    }

    @GetMapping("/get/id/{id}")
    @Secured({ADMIN, LIBRARIAN})
    public CopyResponseDto getCopyById(@PathVariable @NotEmpty Integer id) {
        return bookCopyService.getCopyById(id);
    }


}
