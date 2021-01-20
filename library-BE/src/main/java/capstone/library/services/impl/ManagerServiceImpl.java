package capstone.library.services.impl;

import capstone.library.dtos.request.CreateLibrarianRequestDto;
import capstone.library.entities.Account;
import capstone.library.entities.Profile;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.ProfileRepository;
import capstone.library.services.ManagerService;
import capstone.library.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerServiceImpl implements ManagerService
{
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Override
    public String createLibrarian(CreateLibrarianRequestDto newLibrarian)
    {
        /*Create new profile from request
        * Then add profile to new Account*/
        Profile profile = new Profile();
        profile.setFullName(newLibrarian.getFullName());
        profile.setGender(newLibrarian.getGender());
        /*============================*/
        Account account = new Account();

        return ConstantUtil.CREATE_SUCCESS;
    }
}
