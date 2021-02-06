package capstone.library.controllers.web;

import capstone.library.dtos.request.CreateBorrowPolicyRequest;
import capstone.library.dtos.request.UpdateBorrowPolicyRequest;
import capstone.library.dtos.response.BorrowPolicyResponse;
import capstone.library.services.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PostMapping("/addBorrowPolicy")
    public BorrowPolicyResponse addBorrowPolicy (@RequestBody @Valid CreateBorrowPolicyRequest request){
        return policyService.addBorrowPolicy(request);
    }

    @PostMapping("/updateBorrowPolicy")
    public BorrowPolicyResponse updateBorrowPolicy (@RequestBody @Valid UpdateBorrowPolicyRequest request){
        return policyService.updateBorrowPolicy(request);
    }

    @PostMapping("/deleteBorrowPolicy")
    public String deleteBorrowPolicy(@RequestParam(name = "id") int id){
        return policyService.deleteBorrowPolicy(id);
    }
}
