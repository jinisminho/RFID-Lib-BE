package capstone.library.services.impl;

import capstone.library.dtos.common.FeePolicyDto;
import capstone.library.dtos.request.AddFeePolicyRequest;
import capstone.library.entities.FeePolicy;
import capstone.library.exceptions.CustomException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.FeePolicyRepository;
import capstone.library.services.FeePolicyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static capstone.library.util.ConstantUtil.CREATE_SUCCESS;

@Service
public class FeePolicyServiceImpl implements FeePolicyService {

    @Autowired
    FeePolicyRepository feePolicyRepo;

    @Autowired
    ObjectMapper mapper;

    @Override
    public Page<FeePolicyDto> findAllFeePolicies(Pageable pageable, LocalDateTime startDate, LocalDateTime endDate) {
        Page<FeePolicy> rs;
        if(startDate != null && endDate != null){
            if(startDate.isAfter(endDate)){
                throw new CustomException(
                        HttpStatus.BAD_REQUEST,
                        "Request Conflict",
                        "Start date must before end date"
                );
            }
            rs =  feePolicyRepo.findByCreatedAtBetweenOrderByCreatedAtDesc(pageable, startDate, endDate);
        }else if ((startDate == null && endDate != null) || (startDate != null && endDate == null)){
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Request Conflict",
                    "Start date and end date must go together"
            );
        }else{
            rs = feePolicyRepo.findAllByOrderByCreatedAtDesc(pageable);
        }
        return rs.map(p -> mapper.convertValue(p, FeePolicyDto.class));
    }

    @Override
    public FeePolicyDto findTheLatestFeePolicy() {
        FeePolicy feePolicy = feePolicyRepo
                .findAllByOrderByCreatedAtDesc()
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fee Policy",
                        "Cannot find the latest fee policy"
                ));
        return mapper.convertValue(feePolicy, FeePolicyDto.class);
    }

    @Override
    public String addFeePolicy(AddFeePolicyRequest request) {
        FeePolicy feePolicy = mapper.convertValue(request, FeePolicy.class);
        feePolicy.setCreatedAt(LocalDateTime.now());
        feePolicyRepo.save(feePolicy);
        return CREATE_SUCCESS;
    }

    private FeePolicy findFeePolicyById(int id){
        return feePolicyRepo
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee Policy",
                        "Cannot find fee policy with id: " + id));
    }
}
