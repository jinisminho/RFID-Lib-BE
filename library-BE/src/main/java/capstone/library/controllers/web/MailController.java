package capstone.library.controllers.web;

import capstone.library.dtos.email.EmailCheckOutBookDto;
import capstone.library.dtos.email.EmailReturnBookDto;
import capstone.library.dtos.response.CheckoutResponseDto;
import capstone.library.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    MailService mailService;

    @PostMapping("/checkout")
    void sendCheckoutEmail (@RequestParam(name = "patronEmail")String patronEmail,
                            @RequestBody @Valid CheckoutResponseDto request){
        mailService.sendCheckoutMail(patronEmail, request);
    }

    @PostMapping("/return")
    void sendReturnEmail (@RequestBody @Valid List<EmailReturnBookDto> books){
        mailService.sendReturnMail(books);
    }

    @GetMapping("/wishlist")
    void sendWishlistAvailableEmail(){
        mailService.sendNotifyWishlistAvailable();
    }

    @GetMapping("/remindDue")
    void sendRemindDueDateEmail(){
        mailService.sendRemindOverdueBook();
    }
}
