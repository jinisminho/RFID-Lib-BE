package capstone.library.controllers.web;

import capstone.library.dtos.response.BorrowPolicyResponse;
import capstone.library.enums.RoleIdEnum;
import capstone.library.services.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static capstone.library.util.constants.SecurityConstant.ADMIN;
import static capstone.library.util.constants.SecurityConstant.LIBRARIAN;

@RestController
@RequestMapping("/policy")
public class PolicyController {


    @Autowired
    PolicyService policyService;


    //@Secured(ADMIN)
    @GetMapping("/getBorrowPolicies")
    public Page<BorrowPolicyResponse> getBorrowPolicies(Pageable pageable,
                                                        @RequestParam(required = false, name = "patronTypeId") Integer patronTypeId,
                                                        @RequestParam(required = false, name = "bookCopyTypeId") Integer bookCopyTypeId){
        return policyService.getBorrowPolicies(pageable, patronTypeId, bookCopyTypeId);
    }
}
