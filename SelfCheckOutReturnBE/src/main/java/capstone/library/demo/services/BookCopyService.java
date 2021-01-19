package capstone.library.demo.services;

import capstone.library.demo.dtos.response.ScannedBookResponse;

public interface BookCopyService {

        ScannedBookResponse searchBookByRfid (String rfid);
}
