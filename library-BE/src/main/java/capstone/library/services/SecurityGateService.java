package capstone.library.services;

import capstone.library.entities.SecurityDeactivatedCopy;

public interface SecurityGateService {
    void deleteByRfid(String rfid);

    void add(SecurityDeactivatedCopy securityDeactivatedCopy);
}
