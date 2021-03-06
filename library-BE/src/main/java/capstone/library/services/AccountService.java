package capstone.library.services;

import capstone.library.dtos.request.CreateLibrarianRequest;
import capstone.library.dtos.request.CreatePatronRequest;
import capstone.library.dtos.request.UpdateLibrarianRequest;
import capstone.library.dtos.request.UpdatePatronRequest;
import capstone.library.dtos.response.LibrarianAccountResponse;
import capstone.library.dtos.response.PatronAccountResponse;
import capstone.library.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {

    Account findAccountByEmail(String email);

    Page<PatronAccountResponse> findAllPatronAccount(Pageable pageable);

    Page<PatronAccountResponse> findPatronByEmail(Pageable pageable, String email);

    String activateAccount(int accountId, int auditorId);

    String deactivateAccount(int accountId, int auditorId);

    Page<LibrarianAccountResponse> findAllLibrarianAccount(Pageable pageable);

    Page<LibrarianAccountResponse> findLibrarianByEmail(Pageable pageable, String email);

    String createPatronAccount(CreatePatronRequest request);

    String createLibrarianAccount(CreateLibrarianRequest request);

    String updatePatronAccount(UpdatePatronRequest request);

    String updateLibrarianAccount(UpdateLibrarianRequest request);

    String changePassword(int accountId, String oldPass, String newPass);

}
