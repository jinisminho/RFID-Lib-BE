package capstone.library.controllers.web;

import capstone.library.dtos.response.AccountBasicInfoResponseDto;
import capstone.library.enums.RoleIdEnum;
import capstone.library.services.ManagerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static capstone.library.util.constants.SecurityConstant.ADMIN;

@RestController
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private ManagerService managerService;


    @GetMapping("/getLibrarians")
    @ApiOperation(value = "Get a list of librarians")
    @Secured({ADMIN})
    public Page<AccountBasicInfoResponseDto> getLibrarians(Pageable pageable) {
        return managerService.getLibrarians(pageable);
    }

    @GetMapping("/getAccountsByRole")
    @ApiOperation(value = "Get a list of users by their role")
    @Secured({ADMIN})
    public Page<AccountBasicInfoResponseDto> getAccountsByRole(Pageable pageable,
                                                               @RequestParam(value = "Role name")
                                                               @ApiParam(example = "ROLE_LIBRARIAN", required = true, value = "Role name")
                                                                       RoleIdEnum roleIdEnum) {
        return managerService.getAccountsByRoleId(pageable, roleIdEnum);
    }

    @GetMapping("/deactivate/librarian/{id}")
    @ApiOperation(value = "Deactivate a librarian by their ID")
    @Secured({ADMIN})
    public String deactivateLibrarian(@PathVariable @ApiParam(required = true) int id, @RequestParam(value = "updaterId") int updaterId) {
        return managerService.deactivateLibrarian(id, updaterId);
    }

    @GetMapping("/activate/librarian/{id}")
    @ApiOperation(value = "Activate a librarian by their ID")
    @Secured({ADMIN})
    public String activateLibrarian(@PathVariable @ApiParam(required = true) int id, @RequestParam(value = "updaterId") int updaterId) {
        return managerService.activateLibrarian(id, updaterId);
    }

    /*Search Librarian by name or username*/
    @GetMapping("/result")
    @ApiOperation(value = "Search librarian by name or email")
    @Secured({ADMIN})
    public Page<AccountBasicInfoResponseDto> activateLibrarian(Pageable pageable, @RequestParam(value = "search_query") String searchString) {
        return managerService.searchLibrarian(pageable, searchString);
    }
}
