package capstone.library.demo.exceptions.handler;

import capstone.library.demo.dtos.response.ErrorDto;
import capstone.library.demo.enums.ErrorStatus;
import capstone.library.demo.exceptions.InvalidPolicyException;
import capstone.library.demo.exceptions.MissingInputException;
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
    public ResponseEntity<ErrorDto> handleException(ResourceNotFoundException exception) {
        logger.error(exception.getMessage());
        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                ErrorStatus.RESOURCE_NOT_FOUND.getCode(),
                ErrorStatus.RESOURCE_NOT_FOUND.getReason(),
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ResponseBody
    @ExceptionHandler(value = InvalidPolicyException.class)
    public ResponseEntity<ErrorDto> handleException(InvalidPolicyException exception) {
        logger.error(exception.getMessage());
        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                ErrorStatus.BORROWING_POLICY_VIOLATION.getCode(),
                ErrorStatus.BORROWING_POLICY_VIOLATION.getReason(),
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ResponseBody
    @ExceptionHandler(value = MissingInputException.class)
    public ResponseEntity<ErrorDto> handleException(MissingInputException exception) {
        logger.error(exception.getMessage());
        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                ErrorStatus.MISSING_INPUT.getCode(),
                ErrorStatus.MISSING_INPUT.getReason(),
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ResponseBody
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorDto> handleException(RuntimeException exception) {
        logger.error(exception.getMessage());
        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                ErrorStatus.SYSTEM_ERROR.getCode(),
                ErrorStatus.SYSTEM_ERROR.getReason(),
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ResponseBody
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleException(DataIntegrityViolationException exception) {
        logger.error(exception.getMessage());
        ErrorDto errorDto = new ErrorDto(LocalDateTime.now().toString(),
                ErrorStatus.DATABASE_INTEGRITY_VIOLATION.getCode(),
                ErrorStatus.DATABASE_INTEGRITY_VIOLATION.getReason(),
                exception.getMessage());
        if (exception.getRootCause() != null && exception.getRootCause().getMessage() != null
                && exception.getRootCause().getMessage().contains("FK_copy_book")) {
            errorDto.setMessage("Cannot delete this book because there are existing copies of this book");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
        }
        if (exception.getRootCause() != null && exception.getRootCause().getMessage() != null
                && exception.getRootCause().getMessage().contains("UK_role_name")) {
            errorDto.setMessage("Role's name must be unique");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
        }
        if (exception.getRootCause() != null && exception.getRootCause().getMessage() != null
                && exception.getRootCause().getMessage().contains("UK_account_email")) {
            errorDto.setMessage("Account's email must be unique");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
        }
        if (exception.getRootCause() != null && exception.getRootCause().getMessage() != null
                && exception.getRootCause().getMessage().contains("UK_account_rfid")) {
            errorDto.setMessage("Account's rfid must be unique");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
        }
        if (exception.getRootCause() != null && exception.getRootCause().getMessage() != null
                && exception.getRootCause().getMessage().contains("UK_bookCopy_barcode")) {
            errorDto.setMessage("Book copy's barcode must be unique");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
        }
        if (exception.getRootCause() != null && exception.getRootCause().getMessage() != null
                && exception.getRootCause().getMessage().contains("UK_bookCopy_rfid")) {
            errorDto.setMessage("Book copy's rfid must be unique");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
        }
        if (exception.getRootCause() != null && exception.getRootCause().getMessage() != null
                && exception.getRootCause().getMessage().contains("UK_SDCopy_rfid")) {
            errorDto.setMessage("The rfid in security gate table is duplicated");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorDto(LocalDateTime.now().toString(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            exception.getMessage())
            );
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }


    /**
     *  When user request date with wrong format yyyy-mm-dd
     */

    @ResponseBody
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleException(HttpMessageNotReadableException exception) {
        logger.error(exception.getMessage());
        ErrorDto errorDto = new ErrorDto(LocalDateTime.now().toString(),
                ErrorStatus.INVALID_DATA_FIELD.getCode(),
                ErrorStatus.INVALID_DATA_FIELD.getReason(),
                exception.getMessage());
        if (exception.getMessage() != null
                && exception.getMessage().contains("java.time.LocalDate")) {
            errorDto.setMessage("Invalid date must has format: yyyy-mm-dd");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleException(MethodArgumentNotValidException exception) {
        StringBuilder msg = new StringBuilder();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            msg.append(fieldError.getObjectName()).append(":").append(fieldError.getDefaultMessage()).append(";");
        }
        logger.error(msg);
        ErrorDto errorDto = new ErrorDto(LocalDateTime.now().toString(),
                ErrorStatus.INVALID_DATA_FIELD.getCode(),
                ErrorStatus.INVALID_DATA_FIELD.getReason(),
                msg.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleException(ConstraintViolationException exception) {
        logger.error(exception.getMessage());
        ErrorDto errorDto = new ErrorDto(LocalDateTime.now().toString(),
                ErrorStatus.INVALID_DATA_FIELD.getCode(),
                ErrorStatus.INVALID_DATA_FIELD.getReason(),
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

}
