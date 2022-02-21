package capstone.library.demo.controllers;

import capstone.library.demo.dtos.response.SecurityDeactivatedBooksResponse;
import capstone.library.demo.services.SecurityGateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Security")
public class SecurityGateController {

    @Autowired
    SecurityGateService securityGateService;

    @GetMapping("/getAllDeactivatedBooks")
    public SecurityDeactivatedBooksResponse getAllDeactivatedBooks() {
        return securityGateService.getAllDeactivatedBooks();
    }


    @GetMapping("/log/{rfid}")
    public String logSecurityAlarm(@PathVariable(name = "rfid") String rifd) {
        return securityGateService.logSecurity(rifd);
    }

}
