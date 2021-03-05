package capstone.library.controllers.web;

import capstone.library.dtos.common.ErrorDto;
import capstone.library.dtos.common.PatronTypeDto;
import capstone.library.dtos.request.AddPatronTypeReqDto;
import capstone.library.dtos.request.PatronTypeReqDto;
import capstone.library.dtos.request.UpdatePatronTypePolicyRequest;
import capstone.library.services.PatronTypeService;
import capstone.library.util.constants.ConstantUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/patronType")
public class PatronTypeController {

    @Autowired
    PatronTypeService patronTypeService;

    //@Secured({LIBRARIAN,ADMIN})
    @GetMapping("/getAll")
    public List<PatronTypeDto> findAllPatronType() {
        return patronTypeService.getAllPatronType();
    }

    @GetMapping("/find")
    public Page<PatronTypeDto> findPatronType(Pageable pageable,
                                              @RequestParam(required = false, name = "name") String name) {
        return patronTypeService.getPatronType(pageable, name);
    }

    @PostMapping("/updatePolicy")
    public String updatePatronTypePolicy(@RequestBody @Valid UpdatePatronTypePolicyRequest request) {
        return patronTypeService.updatePatronTypePolicy(request);
    }

    @ApiOperation(value = "This API create new patron type")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/add")
    public ResponseEntity<?> addPatronType(@RequestBody @Valid AddPatronTypeReqDto request) {

        boolean bool = patronTypeService.addPatronType(request);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Failed to add patron type");

        return new ResponseEntity(bool ? ConstantUtil.CREATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "This API update patron type")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updatePatronType(@NotNull @PathVariable int id, @RequestBody @Valid PatronTypeReqDto request) {

        boolean bool = patronTypeService.updatePatronType(id, request);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Failed to update patron type");

        return new ResponseEntity(bool ? ConstantUtil.UPDATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "This API delete patron type")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deletePatronType(@NotNull @PathVariable int id) {

        boolean bool = patronTypeService.deletePatronType(id);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Failed to update patron type");

        return new ResponseEntity(bool ? ConstantUtil.DELETE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

}
