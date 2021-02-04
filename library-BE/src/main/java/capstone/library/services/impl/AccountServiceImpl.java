package capstone.library.services.impl;

import capstone.library.entities.Account;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.AccountRepository;
import capstone.library.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl  implements AccountService {

    @Autowired
    private AccountRepository accountRepo;

    @Override
    public Account findAccountByEmail(String email) {
        return accountRepo.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account","cannot find account with email: " +email));
    }
}
