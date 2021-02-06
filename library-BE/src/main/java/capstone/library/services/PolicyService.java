package capstone.library.services;

import capstone.library.dtos.request.CreateBorrowPolicyRequest;
import capstone.library.dtos.request.UpdateBorrowPolicyRequest;
import capstone.library.dtos.response.BorrowPolicyResponse;
import capstone.library.entities.BorrowPolicy;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PolicyService {

    Page<BorrowPolicyResponse> getBorrowPolicies (Pageable pageable, Integer patronTypeId, Integer bookCopyTypeId);

    BorrowPolicyResponse addBorrowPolicy(CreateBorrowPolicyRequest request);

    BorrowPolicyResponse updateBorrowPolicy(UpdateBorrowPolicyRequest request);

    String deleteBorrowPolicy(int id);


}
