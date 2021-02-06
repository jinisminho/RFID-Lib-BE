package capstone.library.services;

import capstone.library.dtos.common.FeePolicyDto;
import capstone.library.dtos.request.AddFeePolicyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface FeePolicyService {

    Page<FeePolicyDto> findAllFeePolicies(Pageable pageable, LocalDateTime startDate, LocalDateTime endDate);

    FeePolicyDto findTheLatestFeePolicy();

    String addFeePolicy(AddFeePolicyRequest request);
}
