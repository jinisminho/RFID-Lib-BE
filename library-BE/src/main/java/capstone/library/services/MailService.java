package capstone.library.services;

import capstone.library.dtos.email.EmailCheckOutBookDto;
import capstone.library.dtos.email.EmailReturnBookDto;

import java.util.List;

public interface MailService {

    void sendCheckoutMail (String patronEmail, List<EmailCheckOutBookDto> books);

    void sendReturnMail (List<EmailReturnBookDto> books);

    void sendRemindOverdueBook();

    void sendNotifyWishlistAvailable();
}
