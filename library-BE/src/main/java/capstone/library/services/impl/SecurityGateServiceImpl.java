package capstone.library.services.impl;

import capstone.library.dtos.response.AlarmLogResponseDto;
import capstone.library.entities.BookCopy;
import capstone.library.entities.SecurityDeactivatedCopy;
import capstone.library.entities.SecurityGateLog;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.SecurityDeactivatedCopyRepository;
import capstone.library.repositories.SecurityGateLogRepository;
import capstone.library.services.SecurityGateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SecurityGateServiceImpl implements SecurityGateService {
    @Autowired
    SecurityDeactivatedCopyRepository securityDeactivatedCopyRepository;
    @Autowired
    SecurityGateLogRepository securityGateLogRepository;
    @Autowired
    BookCopyRepository bookCopyRepository;
    @Autowired
    ObjectMapper objectMapper;

    private static final String BOOK_COPY_NOT_FOUND_ERROR = "Cannot find this book copy in the system";
    private static final String BOOK_COPY = "Book copy";
    private static final int TIME_INTERVAL_SECONDS = 60;

    @Override
    public void deleteByRfid(String rfid) {
        securityDeactivatedCopyRepository.deleteByRfid(rfid);
    }

    @Override
    public void add(SecurityDeactivatedCopy securityDeactivatedCopy) {
        securityDeactivatedCopyRepository.save(securityDeactivatedCopy);
    }

    @Override
    public Page<AlarmLogResponseDto> getAlarmLog(Pageable pageable, LocalDate date) {
        //Get all logs from 00:00:00 of requested date to 23:59:59 of requested date
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusSeconds(1);
        return securityGateLogRepository.findAllByLoggedAtBetween(startOfDay, endOfDay, TIME_INTERVAL_SECONDS, pageable).
                map(securityGateLog -> objectMapper.convertValue(securityGateLog, AlarmLogResponseDto.class));
    }

    @Override
    public String insertAlarmLog(int bookCopyId) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId).
                orElseThrow(() -> new ResourceNotFoundException(BOOK_COPY, BOOK_COPY_NOT_FOUND_ERROR));
        SecurityGateLog log = new SecurityGateLog();
        log.setLoggedAt(LocalDateTime.now());
        log.setBookCopy(bookCopy);
        securityGateLogRepository.save(log);
        return "Success";
    }
}
