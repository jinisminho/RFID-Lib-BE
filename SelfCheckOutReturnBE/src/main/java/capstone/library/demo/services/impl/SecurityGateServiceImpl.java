package capstone.library.demo.services.impl;


import capstone.library.demo.dtos.response.LoginResponse;
import capstone.library.demo.dtos.response.SecurityDeactivatedBooksResponse;
import capstone.library.demo.entities.BookCopy;
import capstone.library.demo.entities.SecurityDeactivatedCopy;
import capstone.library.demo.entities.SecurityGateLog;
import capstone.library.demo.repositories.BookCopyRepository;
import capstone.library.demo.repositories.SecurityDeactivatedCopyRepository;
import capstone.library.demo.repositories.SecurityGateLogRepository;
import capstone.library.demo.services.SecurityGateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecurityGateServiceImpl implements SecurityGateService {

    @Autowired
    SecurityDeactivatedCopyRepository securityDeactivatedCopyRepository;

    @Autowired
    SecurityGateLogRepository logRepository;

    @Autowired
    BookCopyRepository bookCopyRepository;

    @Override
    public void deleteByRfid(String rfid) {
        securityDeactivatedCopyRepository.deleteByRfid(rfid);
    }

    @Override
    public void add(SecurityDeactivatedCopy securityDeactivatedCopy) {
        securityDeactivatedCopyRepository.save(securityDeactivatedCopy);
    }

    @Override
    public SecurityDeactivatedBooksResponse getAllDeactivatedBooks() {
        List<String> rfids = securityDeactivatedCopyRepository
                .findAll()
                .stream()
                .map(SecurityDeactivatedCopy::getRfid)
                .collect(Collectors.toList());
        return new SecurityDeactivatedBooksResponse(rfids);
    }

    @Override
    public String logSecurity(String rfid) {
        BookCopy bookCopy = bookCopyRepository.findByRfid(rfid)
                .orElse(null);
        if(bookCopy != null){
            SecurityGateLog record = new SecurityGateLog();
            record.setLoggedAt(LocalDateTime.now());
            record.setBookCopy(bookCopy);
            logRepository.save(record);
            System.out.println("LOGGED: " + rfid);
            return "Logged";
        }
        System.out.println("CANNOT LOG: " + rfid);
        return "Cannot find the rfid";
    }

}
