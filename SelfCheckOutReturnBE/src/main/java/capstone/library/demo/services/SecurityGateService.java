package capstone.library.demo.services;


import capstone.library.demo.entities.SecurityDeactivatedCopy;

public interface SecurityGateService {

    void deleteByRfid(String rfid);

    void add(SecurityDeactivatedCopy securityDeactivatedCopy);
}
