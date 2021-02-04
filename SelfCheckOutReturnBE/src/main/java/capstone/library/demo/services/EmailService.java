package capstone.library.demo.services;

import capstone.library.demo.dtos.response.BookCheckOutResponse;
import capstone.library.demo.dtos.response.BookReturnResponse;

import java.util.List;
import java.util.Map;

public interface EmailService {

    void sendEmailAfterCheckOut (List<BookCheckOutResponse> books, int patronId);

    void sendEmailAfterReturn (List<BookReturnResponse> books);

}
