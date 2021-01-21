package capstone.library.controllers.web;

import capstone.library.dtos.response.AccountBasicInfoResponseDto;
import capstone.library.enums.RoleIdEnum;
import capstone.library.services.ManagerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager")
public class ManagerController
{
    @Autowired
    private ManagerService managerService;


    @GetMapping("/getLibrarians")
    @ApiOperation(value = "Get a list of librarians")
    public Page<AccountBasicInfoResponseDto> getLibrarians(Pageable pageable){
        return managerService.getLibrarians(pageable);
    }

    @GetMapping("/getAccountsByRole")
    @ApiOperation(value = "Get a list of users by their role")
    public Page<AccountBasicInfoResponseDto> getAccountsByRole(Pageable pageable, @RequestParam(value = "Role name") @ApiParam(example = "ROLE_LIBRARIAN", required = true, value = "Role name") RoleIdEnum roleIdEnum){
        return managerService.getAccountsByRoleId(pageable, roleIdEnum);
    }

}
