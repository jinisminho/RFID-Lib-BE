package capstone.library.controllers.web;

import capstone.library.dtos.request.CreateLibrarianRequest;
import capstone.library.dtos.request.CreatePatronRequest;
import capstone.library.dtos.request.UpdateLibrarianRequest;
import capstone.library.dtos.request.UpdatePatronRequest;
import capstone.library.dtos.response.LibrarianAccountResponse;
import capstone.library.dtos.response.PatronAccountResponse;
import capstone.library.services.AccountService;
import capstone.library.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.persistence.ManyToOne;
import javax.validation.Valid;

import static capstone.library.util.constants.ConstantUtil.CREATE_SUCCESS;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    AccountService accountService;


    @PostMapping("/deactivate")
    public String deactivateAccount(@RequestParam(name = "id")int id,
                                    @RequestParam(name = "auditorId")int auditorId){
        return accountService.deactivateAccount(id, auditorId);
    }

    @PostMapping("/activate")
    public String activateAccount(@RequestParam(name = "id")int id,
                                  @RequestParam(name = "auditorId")int auditorId ){
        return accountService.activateAccount(id, auditorId);
    }

    @GetMapping("/patron/getAll")
    public Page<PatronAccountResponse> getAllPatronAccount(Pageable pageable){
        return accountService.findAllPatronAccount(pageable);
    }

    @GetMapping("/patron/find")
    public Page<PatronAccountResponse> findPatronAccount(Pageable pageable,
                                                         @RequestParam(name = "email")String email){
        return accountService.findPatronByEmail(pageable, email);
    }


    @GetMapping("/librarian/getAll")
    public Page<LibrarianAccountResponse> getAllLibrarianAccount(Pageable pageable){
        return accountService.findAllLibrarianAccount(pageable);
    }

    @GetMapping("/librarian/find")
    public Page<LibrarianAccountResponse> findLibrarianAccount(Pageable pageable,
                                                         @RequestParam(name = "email")String email){
        return accountService.findLibrarianByEmail(pageable, email);
    }

    @PostMapping("/patron/create")
    public String createPatronAccount(@RequestBody @Valid CreatePatronRequest request){
        return accountService.createPatronAccount(request);
    }

    @PostMapping("/librarian/create")
    public String createPatronAccount(@RequestBody @Valid CreateLibrarianRequest request){
        return accountService.createLibrarianAccount(request);
    }

    @PostMapping("/patron/update")
    public String updatePatronAccount(@RequestBody @Valid UpdatePatronRequest request){
        return accountService.updatePatronAccount(request);
    }

    @PostMapping("/librarian/update")
    public String updateLibrarianAccount(@RequestBody @Valid UpdateLibrarianRequest request){
       return accountService.updateLibrarianAccount(request);
    }
}
