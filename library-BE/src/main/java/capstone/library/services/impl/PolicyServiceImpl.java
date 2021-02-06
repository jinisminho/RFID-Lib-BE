package capstone.library.services.impl;

import capstone.library.dtos.response.BorrowPolicyResponse;
import capstone.library.entities.BorrowPolicy;
import capstone.library.repositories.BorrowPolicyRepository;
import capstone.library.services.PolicyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PolicyServiceImpl implements PolicyService {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    BorrowPolicyRepository borrowPolicyRepo;

    @Override
    public Page<BorrowPolicyResponse> getBorrowPolicies(Pageable pageable,
                                                        Integer patronTypeId,
                                                        Integer bookCopyTypeId) {
        Page<BorrowPolicy> rs;
        if(patronTypeId == null && bookCopyTypeId == null){
           rs = borrowPolicyRepo.findAll(pageable);
        }else if (patronTypeId != null && bookCopyTypeId == null){
            rs = borrowPolicyRepo.findByPatronTypeId(pageable, patronTypeId);
        }else if(patronTypeId == null){
            rs = borrowPolicyRepo.findByBookCopyTypeId(pageable, bookCopyTypeId);
        }else{
            rs = borrowPolicyRepo.findByPatronTypeIdAndBookCopyTypeId(pageable, patronTypeId, bookCopyTypeId);
        }
        return rs.map(b -> mapper.convertValue(b, BorrowPolicyResponse.class));
    }
}
