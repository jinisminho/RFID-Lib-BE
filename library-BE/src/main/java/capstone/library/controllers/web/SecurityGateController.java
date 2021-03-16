package capstone.library.controllers.web;

import capstone.library.dtos.response.AlarmLogResponseDto;
import capstone.library.services.SecurityGateService;
import capstone.library.util.ApiPageable;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static capstone.library.util.constants.ConstantUtil.DATE_PATTERN;
import static capstone.library.util.constants.SecurityConstant.ADMIN;
import static capstone.library.util.constants.SecurityConstant.LIBRARIAN;

@RestController
@RequestMapping("/securityGate")
public class SecurityGateController {

    @Autowired
    SecurityGateService securityGateService;

    @ApiOperation(value = "Get security alarm log by specific date")
    @ApiPageable
    @GetMapping("/getAlarmLog/{date}")
    @Secured({ADMIN, LIBRARIAN})
    public Page<AlarmLogResponseDto> getAlarmLog(Pageable pageable,
                                                 @NotNull
                                                 @PathVariable
                                                 @DateTimeFormat(pattern = DATE_PATTERN) LocalDate date) {
        return securityGateService.getAlarmLog(pageable, date);
    }

    @ApiOperation(value = "[FOR TESTING ONLY]Insert security alarm log to the database")
    @ApiPageable
    @PostMapping("/insertAlarmLog")
    public String insertAlarmLog(@RequestBody int bookCopyId) {
        return securityGateService.insertAlarmLog(bookCopyId);
    }
}
