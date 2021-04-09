package capstone.library.services;

import capstone.library.dtos.request.CreateBorrowPolicyRequest;
import capstone.library.dtos.request.UpdateBorrowPolicyRequest;
import capstone.library.dtos.response.BorrowPolicyResponse;
import capstone.library.dtos.response.BorrowPolicyTypesResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowPolicyService {

    Page<BorrowPolicyResponse> getBorrowPolicies(Pageable pageable, Integer patronTypeId, Integer bookCopyTypeId);

    BorrowPolicyResponse addBorrowPolicy(CreateBorrowPolicyRequest request);

    BorrowPolicyResponse updateBorrowPolicy(UpdateBorrowPolicyRequest request);

    String deleteBorrowPolicy(int id);

    BorrowPolicyTypesResDto getAvailableTypes();
}
