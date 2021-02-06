package capstone.library.controllers.web;

import capstone.library.dtos.request.CreateBorrowPolicyRequest;
import capstone.library.dtos.request.UpdateBorrowPolicyRequest;
import capstone.library.dtos.response.BorrowPolicyResponse;
import capstone.library.services.BorrowPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/borrowPolicy")
public class BorrowPolicyController {


    @Autowired
    BorrowPolicyService borrowPolicyService;


    //@Secured(ADMIN)
    @GetMapping("/get")
    public Page<BorrowPolicyResponse> getBorrowPolicies(Pageable pageable,
                                                        @RequestParam(required = false, name = "patronTypeId") Integer patronTypeId,
                                                        @RequestParam(required = false, name = "bookCopyTypeId") Integer bookCopyTypeId){
        return borrowPolicyService.getBorrowPolicies(pageable, patronTypeId, bookCopyTypeId);
    }

    //@Secured(ADMIN)
    @PostMapping("/add")
    public BorrowPolicyResponse addBorrowPolicy (@RequestBody @Valid CreateBorrowPolicyRequest request){
        return borrowPolicyService.addBorrowPolicy(request);
    }

    //@Secured(ADMIN)
    @PostMapping("/update")
    public BorrowPolicyResponse updateBorrowPolicy (@RequestBody @Valid UpdateBorrowPolicyRequest request){
        return borrowPolicyService.updateBorrowPolicy(request);
    }

    //@Secured(ADMIN)
    @PostMapping("/delete")
    public String deleteBorrowPolicy(@RequestParam(name = "id") int id){
        return borrowPolicyService.deleteBorrowPolicy(id);
    }
}
