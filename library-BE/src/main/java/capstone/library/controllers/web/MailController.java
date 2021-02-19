package capstone.library.controllers.web;

import capstone.library.dtos.email.EmailCheckOutBookDto;
import capstone.library.dtos.email.EmailReturnBookDto;
import capstone.library.dtos.response.CheckoutResponseDto;
import capstone.library.dtos.response.ReturnBookResponseDto;
import capstone.library.dtos.response.ReturnBooksResponse;
import capstone.library.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static capstone.library.util.constants.SecurityConstant.ADMIN;
import static capstone.library.util.constants.SecurityConstant.LIBRARIAN;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    MailService mailService;

    //@Secured({LIBRARIAN, ADMIN})
    @PostMapping("/checkout")
    void sendCheckoutEmail (@RequestParam(name = "patronEmail")String patronEmail,
                            @RequestBody @Valid CheckoutResponseDto request){
        mailService.sendCheckoutMail(patronEmail, request);
    }

    //@Secured({LIBRARIAN, ADMIN})
    @PostMapping("/return")
    void sendReturnEmail (@RequestBody @Valid ReturnBooksResponse books){
        mailService.sendReturnMail(books.getReturnedBooks());
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
