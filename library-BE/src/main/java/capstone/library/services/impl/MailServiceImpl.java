package capstone.library.services.impl;

import capstone.library.dtos.email.EmailCheckOutBookDto;
import capstone.library.dtos.email.EmailReturnBookDto;
import capstone.library.entities.*;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.WishListStatus;
import capstone.library.exceptions.MissingInputException;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.BookBorrowingRepository;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.WishlistRepository;
import capstone.library.services.MailService;
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
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger logger = LogManager.getLogger(MailServiceImpl.class);

    private static final String NOREPLY_ADDRESS = "noreply@rfidlib.com";

    private  static  final  String CHECKOUT_EMAIL_SUBJECT = "[no-reply] BOOK CHECK OUT RECEIPT";

    private  static  final  String RETURN_EMAIL_SUBJECT = "[no-reply] BOOK RETURN RECEIPT";

    private  static  final  String WISHLIST_EMAIL_SUBJECT = "[no-reply] WISHLIST BOOK IS AVAILABLE";

    private  static  final  String DUE_DATE_EMAIL_SUBJECT = "[no-reply] REMIND BORROWING BOOK WILL BE DUE ON";

    private static final int DAY_NUMBER_REMIND_BEFORE_DUE = 1;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    @Autowired
    AccountRepository accountRepo;

    @Autowired
    WishlistRepository wishlistRepo;

    @Autowired
    BookCopyRepository copyRepo;

    @Autowired
    BookBorrowingRepository borrowingRepo;

    @Override
    public void sendCheckoutMail(String patronEmail, List<EmailCheckOutBookDto> books) {
        if(books == null){
            throw new MissingInputException("books is missing");
        }

        if(!books.isEmpty()){
            Optional<Account> receiverOpt = accountRepo.findByEmail(patronEmail);
            if(receiverOpt.isPresent()){
                Account receiver = receiverOpt.get();
                Map<String, Object> templateModel = new HashMap<>();
                templateModel.put("patron", receiver.getProfile().getFullName());
                templateModel.put("books", books);
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
    public void sendReturnMail(List<EmailReturnBookDto> books) {

        if(books == null){
            throw  new MissingInputException("books is missing");
        }
        Map<String, List<EmailReturnBookDto>> patronMap = new HashMap<>();
        for(EmailReturnBookDto book : books){
            String patronEmail = book.getPatronEmail();
            List<EmailReturnBookDto> tmp = new ArrayList<>();
            if(patronMap.containsKey(patronEmail)){
                tmp.addAll(patronMap.get(patronEmail));
                tmp.add(book);
            }else{
                tmp.add(book);
            }
            patronMap.put(patronEmail, tmp);
        }
        patronMap.forEach(
                this::sendReturnEmailForOnePatron
        );

    }


    @Override
    public void sendRemindOverdueBook() {
        LocalDate dueDate = LocalDate.now().plusDays(DAY_NUMBER_REMIND_BEFORE_DUE);
        List<BookBorrowing> bookBorrowings = borrowingRepo.findByDueAtAndReturnedAtIsNullAndLostAtIsNull(dueDate);
        for(BookBorrowing borrowing : bookBorrowings){
            sendOneDueDayReminderEmail(borrowing);
        }
    }

    @Override
    public void sendNotifyWishlistAvailable() {
        List<WishlistBook> curWishlistBooks = wishlistRepo.findByStatus(WishListStatus.NOT_EMAIL_YET);
        if(!curWishlistBooks.isEmpty()){
            Set<BookCopyStatus> availableCopyStatus = new HashSet<>();
            availableCopyStatus.add(BookCopyStatus.AVAILABLE);
            availableCopyStatus.add(BookCopyStatus.LIB_USE_ONLY);
            for(WishlistBook wish : curWishlistBooks){
                Book book = wish.getBook();
                List<BookCopy> copies = copyRepo.findByBookIdAndStatusIn(wish.getBook().getId(),availableCopyStatus);
                //if book is available / lib_use_only
                if(!copies.isEmpty()){
                    if(sendOneWishList(wish.getBorrower(), wish.getBook())){
                        wish.setStatus(WishListStatus.EMAILED);
                        wishlistRepo.save(wish);
                    }
                }
            }
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

    private void sendReturnEmailForOnePatron(String patronEmail,List<EmailReturnBookDto> books ){
        if(books == null){
            throw new MissingInputException("books is missing");
        }
        Optional<Account> accountOpt = accountRepo.findByEmail(patronEmail);
        if(accountOpt.isPresent()){
            Account receiver = accountOpt.get();
            if(!books.isEmpty()){
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
            logger.error("Cannot send return email for patron with email:" + patronEmail);

        }
    }

    private boolean sendOneWishList(Account patron, Book book){
        if(patron == null || book == null){
            logger.error("Cannot find patron or books in wish list");
            return false;
        }
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("patron", patron.getProfile().getFullName());
        templateModel.put("book", book);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process("wishlistEmailTemplate.html", thymeleafContext);
        try {
            sendHtmlMessage(patron.getEmail(), WISHLIST_EMAIL_SUBJECT, htmlBody);
            return true;
        } catch (MessagingException e) {
            logger.error("Cannot send return email for patron: " + patron.getEmail());
            return false;
        }
    }

    private void sendOneDueDayReminderEmail(BookBorrowing borrowing){
        if(borrowing == null){
            throw  new MissingInputException("cannot find borrowing transaction");
        }
        Account patron = borrowing.getBorrower();
        double fineRate = borrowing.getFeePolicy().getOverdueFinePerDay();
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("patron", patron.getProfile().getFullName());
        templateModel.put("book", borrowing.getBookCopy().getBook());
        templateModel.put("dueDate", borrowing.getDueAt());
        templateModel.put("fineRate", fineRate);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process("dueRemind.html", thymeleafContext);
        try {
            sendHtmlMessage(patron.getEmail(), DUE_DATE_EMAIL_SUBJECT+ " " + borrowing.getDueAt(), htmlBody);
        } catch (MessagingException e) {
            logger.error("Cannot send return email for patron: " + patron.getEmail());
        }
    }
}