package capstone.library.controllers.web;

import capstone.library.dtos.common.PatronTypeDto;
import capstone.library.dtos.request.UpdatePatronTypePolicyRequest;
import capstone.library.services.PatronTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static capstone.library.util.constants.SecurityConstant.*;

@RestController
@RequestMapping("/patronType")
public class PatronTypeController {

    @Autowired
    PatronTypeService patronTypeService;

    @Secured({LIBRARIAN,ADMIN})
    @GetMapping("/getAll")
    public List<PatronTypeDto> findAllPatronType(){
        return patronTypeService.getAllPatronType();
    }

    @Secured({LIBRARIAN, ADMIN})
    @GetMapping("/find")
    public Page<PatronTypeDto> findPatronType(Pageable pageable,
                                              @RequestParam(required = false, name = "name")String name){
        return patronTypeService.getPatronType(pageable, name);
    }

    @Secured({ADMIN})
    @PostMapping("/updatePolicy")
    public String updatePatronTypePolicy(@RequestBody @Valid UpdatePatronTypePolicyRequest request){
        return patronTypeService.updatePatronTypePolicy(request);
    }


}
