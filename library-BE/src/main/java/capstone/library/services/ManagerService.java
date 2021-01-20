package capstone.library.services;

import capstone.library.dtos.common.ErrorDto;
import capstone.library.dtos.request.CreateLibrarianRequestDto;
import org.springframework.stereotype.Service;

public interface ManagerService
{
    String createLibrarian(CreateLibrarianRequestDto newLibrarian);
}
