package capstone.library.demo.services;


import capstone.library.demo.dtos.response.SecurityDeactivatedBooksResponse;
import capstone.library.demo.entities.SecurityDeactivatedCopy;

public interface SecurityGateService {

    void deleteByRfid(String rfid);

    void add(SecurityDeactivatedCopy securityDeactivatedCopy);

    SecurityDeactivatedBooksResponse getAllDeactivatedBooks();

    String logSecurity(String rfid);
}
