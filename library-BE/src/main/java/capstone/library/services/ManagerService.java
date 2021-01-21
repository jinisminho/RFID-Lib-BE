package capstone.library.services;

import capstone.library.dtos.response.AccountBasicInfoResponseDto;
import capstone.library.enums.RoleIdEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManagerService
{
    Page<AccountBasicInfoResponseDto> getAccountsByRoleId(Pageable pageable, RoleIdEnum roleIdEnum);

    Page<AccountBasicInfoResponseDto> getLibrarians(Pageable pageable);
}
