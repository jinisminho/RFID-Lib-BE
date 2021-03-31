package capstone.library.demo.services;

import capstone.library.demo.dtos.response.ScannedBookResponse;
import capstone.library.demo.dtos.response.ScannedReturnBookResponse;

public interface BookCopyService {

        ScannedBookResponse searchBookByRfid (String rfid);

        ScannedReturnBookResponse searchReturnBook(String rfid);

}
