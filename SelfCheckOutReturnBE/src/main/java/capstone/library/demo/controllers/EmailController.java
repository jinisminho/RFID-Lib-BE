package capstone.library.demo.controllers;

import capstone.library.demo.dtos.request.EmailCheckOutRequest;
import capstone.library.demo.dtos.response.BookReturnResponse;
import capstone.library.demo.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/Email")
public class EmailController {

    @Autowired
    EmailService emailService;

    @PostMapping("/checkout")
    public void emailBookCheckOut (@RequestBody @Valid EmailCheckOutRequest request){
        emailService.sendEmailAfterCheckOut(request.getBooks(), request.getPatronId());
    }

    @PostMapping("/return")
    public void emailBookReturn (@RequestBody @Valid List<BookReturnResponse> request){
        emailService.sendEmailAfterReturn(request);
    }
}
