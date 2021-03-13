package capstone.library.demo.controllers;

import capstone.library.demo.dtos.response.LoginResponse;
import capstone.library.demo.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping("/login/{rfid}")
    public LoginResponse checkLogin(@PathVariable("rfid") String rfid){
        return accountService.checkLogin(rfid);
    }

}
