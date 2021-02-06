package capstone.library.controllers.web;

import capstone.library.entities.Account;
import capstone.library.security.JwtTokenProvider;
import capstone.library.security.payload.LoginRequest;
import capstone.library.security.payload.LoginResponse;
import capstone.library.services.AccountService;
import capstone.library.services.PatronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.Date;

import static capstone.library.util.constants.SecurityConstant.*;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AccountService accountService;

    @PostMapping(LOGIN_URL)
    public LoginResponse checkLogin(@RequestBody @Valid LoginRequest loginRequest,
                                    HttpServletResponse response){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Account account = accountService.findAccountByEmail(loginRequest.getEmail());
        String jwt = tokenProvider.generateToken(authentication);
        String role = account.getRole().getName();
        String patronGroup = account.getPatronType() == null ? "" : account.getPatronType().getName();
        String avatar = account.getAvatar();
        LoginResponse payload = new LoginResponse(
                jwt,
                tokenProvider.getUserIdFromJwt(jwt),
                tokenProvider.getExpiryDateFromJwt(jwt),
                role,
                patronGroup,
                loginRequest.getEmail(),
                avatar
                );
        //set cookies
        Cookie cookie = new Cookie(HEADER_STRING, jwt);
        cookie.setPath("/");
        cookie.setMaxAge(EXPIRATION_TIME);
        response.addCookie(cookie);

        return payload;
    }
}
