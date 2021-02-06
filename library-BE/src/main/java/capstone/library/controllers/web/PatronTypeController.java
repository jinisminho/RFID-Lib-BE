package capstone.library.controllers.web;

import capstone.library.dtos.common.PatronTypeDto;
import capstone.library.services.PatronTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static capstone.library.util.constants.SecurityConstant.ADMIN;
import static capstone.library.util.constants.SecurityConstant.LIBRARIAN;

@RestController
@RequestMapping("/patronType")
public class PatronTypeController {

    @Autowired
    PatronTypeService patronTypeService;

    //@Secured({LIBRARIAN,ADMIN})
    @GetMapping("/getAll")
    public List<PatronTypeDto> findAllPatronType(){
        return patronTypeService.getAllPatronType();
    }
}
