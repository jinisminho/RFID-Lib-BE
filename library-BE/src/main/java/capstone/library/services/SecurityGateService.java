package capstone.library.services;

import capstone.library.dtos.response.AlarmLogResponseDto;
import capstone.library.entities.SecurityDeactivatedCopy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SecurityGateService {
    void deleteByRfid(String rfid);

    void add(SecurityDeactivatedCopy securityDeactivatedCopy);

    Page<AlarmLogResponseDto> getAlarmLog(Pageable pageable, LocalDate date);

    String insertAlarmLog(int bookCopyId);
}
