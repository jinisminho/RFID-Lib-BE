package capstone.library.services.impl;

import capstone.library.dtos.response.AccountBasicInfoResponseDto;
import capstone.library.enums.RoleIdEnum;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.ProfileRepository;
import capstone.library.services.ManagerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ManagerServiceImpl implements ManagerService
{
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    ObjectMapper objectMapper;

//    @Override
//    public String createLibrarian(CreateLibrarianRequestDto newLibrarian)
//    {
//        /*Create new profile from request
//        * Then add profile to new Account*/
//        Profile profile = new Profile();
//        profile.setFullName(newLibrarian.getFullName());
//        profile.setGender(newLibrarian.getGender());
//        /*============================*/
//        Account account = new Account();
//
//        return ConstantUtil.CREATE_SUCCESS;
//    }

    @Override
    public Page<AccountBasicInfoResponseDto> getAccountsByRoleId(Pageable pageable, RoleIdEnum roleIdEnum)
    {
        return accountRepository.findAccountsByRoleId(roleIdEnum.getRoleId(), pageable).map(account -> objectMapper.convertValue(account, AccountBasicInfoResponseDto.class));
    }

    @Override
    public Page<AccountBasicInfoResponseDto> getLibrarians(Pageable pageable)
    {
        return getAccountsByRoleId(pageable, RoleIdEnum.ROLE_LIBRARIAN);
    }
}
