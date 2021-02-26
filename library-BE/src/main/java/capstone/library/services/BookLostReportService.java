package capstone.library.services;

import capstone.library.dtos.response.LostBookFineResponseDto;

public interface BookLostReportService {

    LostBookFineResponseDto getLostBookFine(int bookBorrowingId);
}
