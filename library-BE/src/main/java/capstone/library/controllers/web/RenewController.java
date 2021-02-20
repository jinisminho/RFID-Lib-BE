package capstone.library.controllers.web;

import capstone.library.dtos.response.ValidateRenewDto;
import capstone.library.services.RenewService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/renew")
public class RenewController {
    @Autowired
    RenewService renewService;

    @ApiOperation(value = "Validate renew API")
    @GetMapping("/validate/{bookBorrowingId}")
    public ValidateRenewDto validateRenew(@PathVariable @NotEmpty int bookBorrowingId) {
        return renewService.validateRenew(bookBorrowingId);
    }
}
