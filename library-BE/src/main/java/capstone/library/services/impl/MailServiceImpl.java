package capstone.library.services.impl;

import capstone.library.dtos.common.CheckoutCopyDto;
import capstone.library.dtos.response.CheckoutResponseDto;
import capstone.library.dtos.response.ImportPatronResponse;
import capstone.library.dtos.response.ReturnBookResponseDto;
import capstone.library.entities.*;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.WishListStatus;
import capstone.library.exceptions.EmailException;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.*;
import capstone.library.services.MailService;
import capstone.library.util.tools.DateTimeUtils;
import capstone.library.util.tools.DoubleFormatter;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger logger = LogManager.getLogger(MailServiceImpl.class);

    private static final String NOREPLY_ADDRESS = "noreply@rfidlib.com";

    private static final String CHECKOUT_EMAIL_SUBJECT = "[no-reply] BOOK CHECK OUT RECEIPT";

    private static final String RETURN_EMAIL_SUBJECT = "[no-reply] BOOK RETURN RECEIPT";

    private static final String WISHLIST_EMAIL_SUBJECT = "[no-reply] WISHLIST BOOK IS AVAILABLE";

    private static final String DUE_DATE_EMAIL_SUBJECT = "[no-reply] REMIND BORROWING BOOK WILL BE DUE ON";

    private static final String CREATE_ACCOUNT_EMAIL_SUBJECT = "[no-reply] CREATE SMART LIBRARY ACCOUNT";

    private static final String BOOK_LOST_EMAIL_SUBJECT = "[no-reply] LOST BOOK BILLING";

    private static final String RENEW_EMAIL_SUBJECT = "[no-reply] RENEW RECEIPT";

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

    @Autowired
    BorrowPolicyRepository borrowPolicyRepo;

    @Override
    public void sendCheckoutMail(String patronEmail, CheckoutResponseDto request) {
        System.out.println(("---------------------" + request.getCheckoutCopyDto()));
        if (request == null || request.getCheckoutCopyDto() == null) {
            throw new MissingInputException("books is missing");
        }
        List<CheckoutCopyDto> books = request.getCheckoutCopyDto()
                .stream()
                .filter(CheckoutCopyDto::isAbleToBorrow).collect(Collectors.toList());
        if (!books.isEmpty()) {
            FeePolicy feePolicy = request.getFeePolicy();
            Optional<Account> receiverOpt = accountRepo.findByEmail(patronEmail);
            if (receiverOpt.isPresent()) {
                Account receiver = receiverOpt.get();
                Map<String, Object> templateModel = new HashMap<>();
                templateModel.put("patron", receiver.getProfile().getFullName());
                templateModel.put("books", books);
                templateModel.put("feePolicy", feePolicy);
                Context thymeleafContext = new Context();
                thymeleafContext.setVariables(templateModel);
                String htmlBody = thymeleafTemplateEngine.process("checkoutEmailTemplate.html", thymeleafContext);
                try {
                    sendHtmlMessage(receiver.getEmail(), CHECKOUT_EMAIL_SUBJECT, htmlBody);
                } catch (MessagingException e) {
                    logger.error("Cannot send chek-out email for patron: " + receiver.getEmail());
                }
            } else {
                logger.error("Cannot send chek-out email for patron");
            }
        }

    }

    @Override
    public void sendReturnMail(List<ReturnBookResponseDto> books) {

        if (books == null) {
            throw new MissingInputException("books is missing");
        }
        Map<String, List<ReturnBookResponseDto>> patronMap = new HashMap<>();
        for (ReturnBookResponseDto book : books) {
            String patronEmail = book.getBorrower().getEmail();
            List<ReturnBookResponseDto> tmp = new ArrayList<>();
            if (patronMap.containsKey(patronEmail)) {
                tmp.addAll(patronMap.get(patronEmail));
                tmp.add(book);
            } else {
                tmp.add(book);
            }
            patronMap.put(patronEmail, tmp);
        }
        patronMap.forEach(
                this::sendReturnEmailForOnePatron
        );

    }

    /*Function to send remind overdue email*/
    @Override
    public void sendRemindOverdueBook() {
        LocalDate dueDate = LocalDate.now().plusDays(DAY_NUMBER_REMIND_BEFORE_DUE);
        List<BookBorrowing> bookBorrowings = borrowingRepo.findByDueAtAndReturnedAtIsNullAndLostAtIsNull(dueDate);
        for (BookBorrowing borrowing : bookBorrowings) {
            sendOneDueDayReminderEmail(borrowing);
        }
    }

    /*Function to notify wishlisted book is available*/
    @Override
    public void sendNotifyWishlistAvailable() {
        List<WishlistBook> curWishlistBooks = wishlistRepo.findByStatus(WishListStatus.NOT_EMAIL_YET);
        if (!curWishlistBooks.isEmpty()) {
            Set<BookCopyStatus> availableCopyStatus = new HashSet<>();
            availableCopyStatus.add(BookCopyStatus.AVAILABLE);
            availableCopyStatus.add(BookCopyStatus.LIB_USE_ONLY);
            for (WishlistBook wish : curWishlistBooks) {
                List<BookCopy> copies = copyRepo.findByBookIdAndStatusIn(wish.getBook().getId(), availableCopyStatus);
                //if book is available / lib_use_only
                if (!copies.isEmpty()) {
                    if (sendOneWishList(wish.getBorrower(), wish.getBook())) {
                        wish.setStatus(WishListStatus.EMAILED);
                        wishlistRepo.save(wish);
                    }
                }
            }
        }
    }

    @Override
    public void sendAccountPassword(String email, String password) {
        Account account = accountRepo
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot find account with email: " + email));

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("patron", account.getProfile().getFullName());
        templateModel.put("username", email);
        templateModel.put("password", password);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process("createAccount.html", thymeleafContext);
        try {
            sendHtmlMessage(email, CREATE_ACCOUNT_EMAIL_SUBJECT, htmlBody);
        } catch (MessagingException e) {
            throw new EmailException(e.getMessage());
        }

    }

    @Override
    public void sendLostBookFine(BookLostReport bookLostReport) {
        DateTimeUtils dateTimeUtils = new DateTimeUtils();
        String email = bookLostReport.getBookBorrowing().getBorrowing().getBorrower().getEmail();
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("patron", bookLostReport.getBookBorrowing().getBorrowing().getBorrower().getProfile().getFullName());
        templateModel.put("book", bookLostReport.getBookBorrowing().getBookCopy().getBook());
        templateModel.put("reportedAt", dateTimeUtils.convertDateTimeToString(bookLostReport.getLostAt()));
        templateModel.put("fine", DoubleFormatter.formatToDecimal(bookLostReport.getFine()));
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process("bookLostFineTemplate.html", thymeleafContext);
        try {
            sendHtmlMessage(email, BOOK_LOST_EMAIL_SUBJECT, htmlBody);
        } catch (MessagingException e) {
            throw new EmailException(e.getMessage());
        }
    }

    @Override
    public void sendAccountBatch(ImportPatronResponse request) {
        request.getImportPatronList()
                .forEach(account -> sendAccountPassword(account.getEmail(), account.getRawPassword()));
    }

    @Override
    public void sendRenewMail(BorrowPolicy borrowPolicy, ExtendHistory oldBorrowing, BookBorrowing newBorrowing) {
        if(borrowPolicy == null || oldBorrowing == null || newBorrowing == null){
            throw new MissingInputException("missing input");
        }
        DateTimeUtils dateTimeUtils = new DateTimeUtils();
        String email =  newBorrowing.getBorrowing().getBorrower().getEmail();
        String patron = newBorrowing.getBorrowing().getBorrower().getProfile().getFullName();
        Book book = newBorrowing.getBookCopy().getBook();
        String bookTitle = book.getTitle() + " - " + book.getEdition() + " edt";
        LocalDate oldDue = oldBorrowing.getDueAt();
        LocalDate newDue = newBorrowing.getDueAt();
        String renewAt = dateTimeUtils.convertDateTimeToString(newBorrowing.getExtendedAt());
        FeePolicy feePolicy = newBorrowing.getFeePolicy();
        String patronType = borrowPolicy.getPatronType().getName();
        String copyType = borrowPolicy.getBookCopyType().getName();
        int maxExtendTime = borrowPolicy.getMaxExtendTime();
        int maxExtendDuration = borrowPolicy.getExtendDueDuration();
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("patron", patron);
        templateModel.put("book", bookTitle);
        templateModel.put("oldDue", oldDue);
        templateModel.put("newDue", newDue);
        templateModel.put("renewAt", renewAt);
        templateModel.put("renewTime", newBorrowing.getExtendIndex());
        templateModel.put("feePolicy", feePolicy);
        templateModel.put("copyType", copyType);
        templateModel.put("patronType", patronType);
        templateModel.put("maxExtendTime", maxExtendTime);
        templateModel.put("maxExtendDuration", maxExtendDuration);
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process("renewTemplate.html", thymeleafContext);
        try {
            sendHtmlMessage(email, RENEW_EMAIL_SUBJECT, htmlBody);
        } catch (MessagingException e) {
            throw new EmailException(e.getMessage());
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

    private void sendReturnEmailForOnePatron(String patronEmail, List<ReturnBookResponseDto> books) {
        if (books == null) {
            throw new MissingInputException("books is missing");
        }
        Optional<Account> accountOpt = accountRepo.findByEmail(patronEmail);
        if (accountOpt.isPresent()) {
            Account receiver = accountOpt.get();
            if (!books.isEmpty()) {
                Map<String, Object> templateModel = new HashMap<>();
                templateModel.put("patron", receiver.getProfile().getFullName());
                templateModel.put("books", books);
                Context thymeleafContext = new Context();
                thymeleafContext.setVariables(templateModel);
                String htmlBody = thymeleafTemplateEngine.process("returnEmailTemplate.html", thymeleafContext);
                try {
                    sendHtmlMessage(receiver.getEmail(), RETURN_EMAIL_SUBJECT, htmlBody);
                } catch (MessagingException e) {
                    logger.error("Cannot send return email for patron: " + receiver.getEmail());
                }
            }
        } else {
            logger.error("Cannot send return email for patron with email:" + patronEmail);

        }
    }

    private boolean sendOneWishList(Account patron, Book book) {
        if (patron == null || book == null) {
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
        } catch (Exception e) {
            logger.error("Cannot send return email for patron: " + patron.getEmail());
            return false;
        }
    }

    private void sendOneDueDayReminderEmail(BookBorrowing borrowing) {
        if (borrowing == null) {
            throw new MissingInputException("cannot find borrowing transaction");
        }
        Account patron = borrowing.getBorrowing().getBorrower();
        double fineRate = borrowing.getFeePolicy().getOverdueFinePerDay();
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("patron", patron.getProfile().getFullName());
        templateModel.put("book", borrowing.getBookCopy().getBook());
        templateModel.put("dueDate", borrowing.getDueAt());
        templateModel.put("fineRate",DoubleFormatter.formatToDecimal( fineRate));
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process("dueRemind.html", thymeleafContext);
        try {
            sendHtmlMessage(patron.getEmail(), DUE_DATE_EMAIL_SUBJECT + " " + borrowing.getDueAt(), htmlBody);
        } catch (MessagingException e) {
            logger.error("Cannot send return email for patron: " + patron.getEmail());
        }
    }
}
