package capstone.library.demo.services.impl;

import capstone.library.demo.dtos.response.LoginResponse;
import capstone.library.demo.entities.Account;
import capstone.library.demo.entities.BookBorrowing;
import capstone.library.demo.entities.BorrowPolicy;
import capstone.library.demo.exceptions.ResourceNotFoundException;
import capstone.library.demo.repositories.AccountRepository;
import capstone.library.demo.repositories.BookBorrowingRepository;
import capstone.library.demo.repositories.BorrowPolicyRepository;
import capstone.library.demo.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepo;

    @Autowired
    BorrowPolicyRepository borrowPolicyRepo;

    @Autowired
    BookBorrowingRepository bookBorrowingRepo;


    @Override
    public LoginResponse checkLogin(String rfid) {
        Optional<Account> accOpt = accountRepo.findByRfidAndIsActive(rfid, true);
        if(!accOpt.isPresent()){
            return null;
        }else{
            Account account = accOpt.get();
            BorrowPolicy policy = borrowPolicyRepo.findById(1)
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot find the policy"));
            List<BookBorrowing> borrowings = bookBorrowingRepo.findOverDueTransactionByPatronId(account.getId());
            if(borrowings.isEmpty()){
                return new LoginResponse(account.getId(),
                        account.getEmail(),
                        account.getRole().getName(),
                        policy.getMaxNumberCopyBorrow(),
                        false);
            }else{
                return new LoginResponse(account.getId(),
                        account.getEmail(),
                        account.getRole().getName(),
                        policy.getMaxNumberCopyBorrow(),
                        true);
            }
        }
    }
}
