package capstone.library.services;

import capstone.library.dtos.email.EmailCheckOutBookDto;
import capstone.library.dtos.email.EmailReturnBookDto;
import capstone.library.dtos.response.CheckoutResponseDto;
import capstone.library.dtos.response.ImportPatronResponse;
import capstone.library.dtos.response.ReturnBookResponseDto;
import capstone.library.entities.*;

import java.util.List;

public interface MailService {

    void sendCheckoutMail (String patronEmail, CheckoutResponseDto request);

    void sendReturnMail (List<ReturnBookResponseDto> books);

    void sendRemindOverdueBook();

    void sendNotifyWishlistAvailable();

    void sendAccountPassword(String email, String password);

    void sendLostBookFine(BookLostReport bookLostReport);

    void sendAccountBatch(ImportPatronResponse request);

    void sendRenewMail(ExtendHistory extendHistory, BookBorrowing newBorrowing);
}
