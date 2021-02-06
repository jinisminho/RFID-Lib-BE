package capstone.library.services;

import capstone.library.dtos.response.BorrowPolicyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PolicyService {

    Page<BorrowPolicyResponse> getBorrowPolicies (Pageable pageable, Integer patronTypeId, Integer bookCopyTypeId);


}
