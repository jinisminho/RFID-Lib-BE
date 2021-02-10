package capstone.library.services;

import capstone.library.entities.Account;

public interface AccountService {

    Account findAccountByEmail(String email);


}
