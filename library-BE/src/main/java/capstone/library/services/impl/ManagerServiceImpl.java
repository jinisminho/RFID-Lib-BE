package capstone.library.services.impl;

import capstone.library.dtos.response.AccountBasicInfoResponseDto;
import capstone.library.entities.Account;
import capstone.library.enums.AccountStatusEnum;
import capstone.library.enums.RoleIdEnum;
import capstone.library.exceptions.InvalidRequestException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.exceptions.UnauthorizedException;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.ProfileRepository;
import capstone.library.services.ManagerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManagerServiceImpl implements ManagerService
{
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Page<AccountBasicInfoResponseDto> getAccountsByRoleId(Pageable pageable, RoleIdEnum roleIdEnum)
    {
        return accountRepository.findAccountsByRoleId(roleIdEnum.getRoleId(), pageable).map(account -> objectMapper.convertValue(account, AccountBasicInfoResponseDto.class));
    }

    @Override
    public Page<AccountBasicInfoResponseDto> getLibrarians(Pageable pageable)
    {
        return getAccountsByRoleId(pageable, RoleIdEnum.LIBRARIAN);
    }

    private String changeLibrarianStatus(int id, int updaterId, boolean status)
    {
        Optional<Account> updaterAccountOptional = accountRepository.findById(updaterId);
        if (!updaterAccountOptional.isPresent())
        {
            ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException();
            resourceNotFoundException.setResourceName("Manager id: " + updaterId);
            resourceNotFoundException.setMessage("Manager id: '" + updaterId + "' does not exist");
            throw resourceNotFoundException;
        } else if (!updaterAccountOptional.get().getRole().getId().equals(RoleIdEnum.ADMIN.getRoleId()))
        {
            throw new UnauthorizedException("Only Manager can deactivate Librarian");
        }
        Optional<Account> accountOptional = accountRepository.findByIdAndRoleId(id, RoleIdEnum.LIBRARIAN.getRoleId());
        if (accountOptional.isPresent())
        {
            Account librarian = accountOptional.get();
            if (!librarian.isActive() && !status)
            {
                throw new InvalidRequestException("This librarian is already inactive");
            }
            if (librarian.isActive() && status)
            {
                throw new InvalidRequestException("This librarian is already active");
            }
            librarian.setActive(status);
            accountRepository.save(librarian);
        } else
        {
            ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException();
            resourceNotFoundException.setResourceName("Librarian id: " + id);
            resourceNotFoundException.setMessage("Librarian id: '" + id + "' does not exist");
            throw resourceNotFoundException;
        }
        return "Success";
    }

    @Override
    public String deactivateLibrarian(int id, int updaterId)
    {
        return changeLibrarianStatus(id, updaterId, AccountStatusEnum.STATUS_INACTIVE.getStatus());
    }

    @Override
    public String activateLibrarian(int id, int updaterId)
    {
        return changeLibrarianStatus(id, updaterId, AccountStatusEnum.STATUS_ACTIVE.getStatus());
    }

    @Override
    public Page<AccountBasicInfoResponseDto> searchLibrarian(Pageable pageable, String searchString)
    {
        if (searchString.isBlank())
        {
            throw new InvalidRequestException("Search query must no be blank");
        } else
        {
            return accountRepository.findByEmailContainsOrProfileFullNameContains(searchString, searchString, pageable).map(account -> objectMapper.convertValue(account, AccountBasicInfoResponseDto.class));
        }
    }
}
