package capstone.library.services;

import capstone.library.dtos.email.EmailCheckOutBookDto;
import capstone.library.dtos.email.EmailReturnBookDto;
import capstone.library.dtos.response.CheckoutResponseDto;

import java.util.List;

public interface MailService {

    void sendCheckoutMail (String patronEmail, CheckoutResponseDto request);

    void sendReturnMail (List<EmailReturnBookDto> books);

    void sendRemindOverdueBook();

    void sendNotifyWishlistAvailable();
}
