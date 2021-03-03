package capstone.library.demo.services.impl;


import capstone.library.demo.entities.SecurityDeactivatedCopy;
import capstone.library.demo.repositories.SecurityDeactivatedCopyRepository;
import capstone.library.demo.services.SecurityGateService;
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
