package capstone.library.controllers.web;


import capstone.library.dtos.common.FeePolicyDto;
import capstone.library.dtos.request.AddFeePolicyRequest;
import capstone.library.services.FeePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static capstone.library.util.ConstantUtil.DATE_TIME_PATTERN;

@RestController
@RequestMapping("/feePolicy")
public class FeePolicyController {

    @Autowired
    FeePolicyService feePolicyService;

    @GetMapping("/find")
    public Page<FeePolicyDto> findFeePolicies(Pageable pageable,
                                              @RequestParam(required = false, name = "startDate")
                                              @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                      LocalDateTime startDate,
                                              @RequestParam(required = false, name = "endDate")
                                              @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                      LocalDateTime endDate) {
        return feePolicyService.findAllFeePolicies(pageable, startDate, endDate);
    }

    @GetMapping("/getLatest")
    public FeePolicyDto findLatestFeePolicy() {
        return feePolicyService.findTheLatestFeePolicy();
    }

    @PostMapping("/add")
    public String addFeePolicy(@RequestBody @Valid AddFeePolicyRequest request) {
        return feePolicyService.addFeePolicy(request);
    }
}
