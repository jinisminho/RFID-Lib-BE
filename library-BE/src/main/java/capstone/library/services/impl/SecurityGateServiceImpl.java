package capstone.library.services.impl;

import capstone.library.entities.SecurityDeactivatedCopy;
import capstone.library.repositories.SecurityDeactivatedCopyRepository;
import capstone.library.services.SecurityGateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityGateServiceImpl implements SecurityGateService {
    @Autowired
    SecurityDeactivatedCopyRepository securityDeactivatedCopyRepository;

    @Override
    public void deleteByRfid(String rfid) {
        securityDeactivatedCopyRepository.deleteByRfid(rfid);
    }

    @Override
    public void add(SecurityDeactivatedCopy securityDeactivatedCopy) {
        securityDeactivatedCopyRepository.save(securityDeactivatedCopy);
    }
}
