package capstone.library.demo.exceptions.handler;

import capstone.library.demo.dtos.response.ErrorDto;
import capstone.library.demo.exceptions.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;


/**
 * This class will catch all runtime exception of the project
 * then send response to client with errorDto/list<errorDto></>, HttpStatus
 */

@RestControllerAdvice
public class ApplicationExceptionHandler {

    private static final Logger logger = LogManager.getLogger(ApplicationExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity handleException(ResourceNotFoundException exception) {
        logger.error(exception.getMessage());
        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ResponseBody
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity handleException(RuntimeException exception) {
        logger.error(exception.getMessage());
        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "server error",
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ResponseBody
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity handleException(DataIntegrityViolationException exception) {
        logger.error(exception.getMessage());
        ErrorDto errorDto = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.CONFLICT.value(),
                "Resource Conflict",
                exception.getMessage());
        if (exception.getRootCause() != null && exception.getRootCause().getMessage() != null
                && exception.getRootCause().getMessage().contains("FK_copy_book")) {
            errorDto.setMessage("Cannot delete this book because there are existing copies of this book");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDto);
        }
        if (exception.getRootCause() != null && exception.getRootCause().getMessage() != null
                && exception.getRootCause().getMessage().contains("UK_role_name")) {
            errorDto.setMessage("Role's name must be unique");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDto);
        }
        if (exception.getRootCause() != null && exception.getRootCause().getMessage() != null
                && exception.getRootCause().getMessage().contains("UK_account_email")) {
            errorDto.setMessage("Account's email must be unique");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDto);
        }
        if (exception.getRootCause() != null && exception.getRootCause().getMessage() != null
                && exception.getRootCause().getMessage().contains("UK_account_rfid")) {
            errorDto.setMessage("Account's rfid must be unique");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDto);
        }
        if (exception.getRootCause() != null && exception.getRootCause().getMessage() != null
                && exception.getRootCause().getMessage().contains("UK_bookCopy_barcode")) {
            errorDto.setMessage("Book copy's barcode must be unique");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDto);
        }
        if (exception.getRootCause() != null && exception.getRootCause().getMessage() != null
                && exception.getRootCause().getMessage().contains("UK_bookCopy_rfid")) {
            errorDto.setMessage("Book copy's rfid must be unique");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDto);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }


    /**
     *  When user request date with wrong format yyyy-mm-dd
     */

    @ResponseBody
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity handleException(HttpMessageNotReadableException exception) {
        logger.error(exception.getMessage());
        ErrorDto errorDto = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                exception.getMessage());
        if (exception.getMessage() != null
                && exception.getMessage().contains("java.time.LocalDate")) {
            errorDto.setMessage("Invalid date must has format: yyyy-mm-dd");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity handleException(MethodArgumentNotValidException exception) {
        StringBuilder msg = new StringBuilder();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            msg.append(fieldError.getObjectName()).append(":").append(fieldError.getDefaultMessage()).append(";");
        }
        logger.error(msg);
        ErrorDto errorDto = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                msg.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity handleException(ConstraintViolationException exception) {
        logger.error(exception.getMessage());
        ErrorDto errorDto = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

}
