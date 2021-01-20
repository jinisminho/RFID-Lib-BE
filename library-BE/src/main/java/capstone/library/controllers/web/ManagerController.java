package capstone.library.controllers.web;

import capstone.library.dtos.common.ErrorDto;
import capstone.library.dtos.request.CreateLibrarianRequestDto;
import capstone.library.services.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/manager")
public class ManagerController
{
    @Autowired
    private ManagerService managerService;

    @PostMapping("/createLibrarian")
    public String createLibrarian(@RequestBody @Valid CreateLibrarianRequestDto newLibrarian) {
        return managerService.createLibrarian(newLibrarian);
    }

}
