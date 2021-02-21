package capstone.library.services;

import capstone.library.dtos.response.ValidateRenewDto;

public interface RenewService {
    ValidateRenewDto validateRenew(int bookBorrowingId);

    boolean addNewExtendHistory(Integer bookBorrowingId, Integer librarianId, Integer numberOfDayToPlus);
}
