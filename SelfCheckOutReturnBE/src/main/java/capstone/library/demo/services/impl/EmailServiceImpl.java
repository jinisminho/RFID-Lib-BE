package capstone.library.demo.services.impl;

import capstone.library.demo.dtos.response.BookCheckOutResponse;
import capstone.library.demo.dtos.response.BookReturnResponse;
import capstone.library.demo.entities.Account;
import capstone.library.demo.enums.BookReturnStatus;
import capstone.library.demo.exceptions.MissingInputException;
import capstone.library.demo.repositories.AccountRepository;
import capstone.library.demo.services.EmailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl  implements EmailService {

    private static final Logger logger = LogManager.getLogger(EmailServiceImpl.class);

    private static final String NOREPLY_ADDRESS = "noreply@rfidlib.com";

    private  static  final  String CHECKOUT_EMAIL_SUBJECT = "[no-reply] BOOK CHECK OUT RECEIPT";

    private  static  final  String RETURN_EMAIL_SUBJECT = "[no-reply] BOOK RETURN RECEIPT";

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    @Autowired
    private AccountRepository accountRepo;

    @Override
    public void sendEmailAfterCheckOut(List<BookCheckOutResponse> books, int patronId) {
        if(books == null){
            throw new MissingInputException("books is missing");
        }
        List<BookCheckOutResponse> successCheckOutBook = books.stream().
                filter(BookCheckOutResponse::isAbleToBorrow).collect(Collectors.toList());
        if(!successCheckOutBook.isEmpty()){
            Optional<Account> receiverOpt = accountRepo.findById(patronId);
            if(receiverOpt.isPresent()){
                Account receiver = receiverOpt.get();
                Map<String, Object> templateModel = new HashMap<>();
                templateModel.put("patron", receiver.getProfile().getFullName());
                templateModel.put("books", successCheckOutBook);
                Context thymeleafContext = new Context();
                thymeleafContext.setVariables(templateModel);
                String htmlBody = thymeleafTemplateEngine.process("checkoutEmailTemplate.html", thymeleafContext);
                try {
                    sendHtmlMessage(receiver.getEmail(), CHECKOUT_EMAIL_SUBJECT, htmlBody);
                }catch(MessagingException e){
                    logger.error("Cannot send chek-out email for patron: " + receiver.getEmail());
                }
            }else{
                logger.error("Cannot send chek-out email for patron");
            }
        }


    }

    @Override
    public void sendEmailAfterReturn(List<BookReturnResponse> books) {
        if(books == null){
            throw  new MissingInputException("books is missing");
        }
        List<BookReturnResponse> successfulReturns = books
                .stream()
                .filter(s -> s.getStatus() == BookReturnStatus.RETURNED)
                .collect(Collectors.toList());
        Map<String, List<BookReturnResponse>> patronMap = new HashMap<>();
        for(BookReturnResponse book : successfulReturns){
            String patron = book.getPatron();
            List<BookReturnResponse> tmp = new ArrayList<>();
            if(patronMap.containsKey(patron)){
                tmp.addAll(patronMap.get(patron));
                tmp.add(book);
            }else{
                tmp.add(book);
            }
            patronMap.put(patron, tmp);
        }

        patronMap.forEach(
                (k, v) ->{
                    Account account = accountRepo.findByEmail(k).orElse(null);
                    sendReturnEmailForOnePatron(account, v);
                }
        );
    }

    private void sendReturnEmailForOnePatron(Account receiver,List<BookReturnResponse> books ){
        if(books == null){
            throw new MissingInputException("books is missing");
        }
        if(receiver != null){

            List<BookReturnResponse> successReturnBooks = books
                    .stream().filter(b -> b.getStatus() == BookReturnStatus.RETURNED)
                    .collect(Collectors.toList());
            if(!successReturnBooks.isEmpty()){
                Map<String, Object> templateModel = new HashMap<>();
                templateModel.put("patron", receiver.getProfile().getFullName());
                templateModel.put("books", books);
                Context thymeleafContext = new Context();
                thymeleafContext.setVariables(templateModel);
                String htmlBody = thymeleafTemplateEngine.process("returnEmailTemplate.html", thymeleafContext);
                try {
                    sendHtmlMessage(receiver.getEmail(), RETURN_EMAIL_SUBJECT, htmlBody);
                }catch(MessagingException e){
                    logger.error("Cannot send return email for patron: " + receiver.getEmail());
                }
            }
        }else{
            logger.error("Cannot send return email for patron");

        }
    }



    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(NOREPLY_ADDRESS);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }
}
