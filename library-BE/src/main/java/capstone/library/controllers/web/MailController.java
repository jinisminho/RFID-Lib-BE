package capstone.library.controllers.web;


import capstone.library.dtos.common.ErrorDto;
import capstone.library.services.EmailService;
import capstone.library.util.ConstantUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.mapstruct.ap.shaded.freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/mail")
public class MailController {
    @Autowired
    public EmailService emailService;

    @ApiOperation(value = "This API test send mail")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/sendHtml")
    public ResponseEntity<?> sendEmail(@RequestParam(required = true, value = "to") String toMail,@RequestParam(required = true, value = "subject") String subject) throws IOException, MessagingException, TemplateException {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("recipientName", "KhangNDN");
        templateModel.put("text", "Test email");
        emailService.sendMessageUsingThymeleafTemplate(
                toMail,
                subject,
                templateModel);
        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL SERVER ERROR",
                "Failed to send email");

        return new ResponseEntity("ok",
                 HttpStatus.OK );
    }
}
