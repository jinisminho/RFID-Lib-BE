package capstone.library.controllers.web;

import capstone.library.dtos.request.*;
import capstone.library.dtos.response.LibrarianAccountResponse;
import capstone.library.dtos.response.PatronAccountResponse;
import capstone.library.entities.Account;
import capstone.library.security.JwtTokenProvider;
import capstone.library.services.AccountService;
import capstone.library.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.ManyToOne;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static capstone.library.util.constants.ConstantUtil.CREATE_SUCCESS;
import static capstone.library.util.constants.SecurityConstant.EXPIRATION_TIME;
import static capstone.library.util.constants.SecurityConstant.HEADER_STRING;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    AccountService accountService;



    @PostMapping("/deactivate")
    public String deactivateAccount(@RequestParam(name = "id") int id,
                                    @RequestParam(name = "auditorId") int auditorId) {
        return accountService.deactivateAccount(id, auditorId);
    }

    @PostMapping("/activate")
    public String activateAccount(@RequestParam(name = "id") int id,
                                  @RequestParam(name = "auditorId") int auditorId) {
        return accountService.activateAccount(id, auditorId);
    }

    @GetMapping("/patron/getAll")
    public Page<PatronAccountResponse> getAllPatronAccount(Pageable pageable) {
        return accountService.findAllPatronAccount(pageable);
    }

    @GetMapping("/patron/find")
    public Page<PatronAccountResponse> findPatronAccount(Pageable pageable,
                                                         @RequestParam(name = "email") String email) {
        return accountService.findPatronByEmail(pageable, email);
    }


    @GetMapping("/librarian/getAll")
    public Page<LibrarianAccountResponse> getAllLibrarianAccount(Pageable pageable) {
        return accountService.findAllLibrarianAccount(pageable);
    }

    @GetMapping("/librarian/find")
    public Page<LibrarianAccountResponse> findLibrarianAccount(Pageable pageable,
                                                               @RequestParam(name = "email") String email) {
        return accountService.findLibrarianByEmail(pageable, email);
    }

    @PostMapping("/patron/create")
    public String createPatronAccount(@RequestBody @Valid CreatePatronRequest request) {
        return accountService.createPatronAccount(request);
    }

    @PostMapping("/librarian/create")
    public String createPatronAccount(@RequestBody @Valid CreateLibrarianRequest request) {
        return accountService.createLibrarianAccount(request);
    }

    @PostMapping("/patron/update")
    public String updatePatronAccount(@RequestBody @Valid UpdatePatronRequest request) {
        return accountService.updatePatronAccount(request);
    }

    @PostMapping("/librarian/update")
    public String updateLibrarianAccount(@RequestBody @Valid UpdateLibrarianRequest request) {
        return accountService.updateLibrarianAccount(request);
    }

    @PostMapping("/password/change")
    public String changePassword(@RequestBody @Valid @NotNull ChangePasswordRequest request,
                                 HttpServletResponse response) {
        String newJWT = accountService.changePassword(request.getAccountId(),
                request.getCurrentPassword(),
                request.getNewPassword());

        Cookie cookie = new Cookie(HEADER_STRING, newJWT);
        cookie.setPath("/");
        cookie.setMaxAge(EXPIRATION_TIME/1000);
        response.addCookie(cookie);
        return newJWT;
    }
}
