package capstone.library.exceptions.handler;

import capstone.library.dtos.common.ErrorDto;
import capstone.library.exceptions.InvalidRequestException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.exceptions.UnauthorizedException;
import capstone.library.util.ConstantUtil;
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
                HttpStatus.NOT_FOUND.value(),
                ConstantUtil.EXCEPTION_RESOURCE_NOT_FOUND,
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /*
     * Is thrown when an invalid request is received
     *   eg: request to deactivate an already inactive account
     * */
    @ResponseBody
    @ExceptionHandler(value = InvalidRequestException.class)
    public ResponseEntity<ErrorDto> handleException(InvalidRequestException exception) {
        logger.error(exception.getMessage());
        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                ConstantUtil.EXCEPTION_INVALID_REQUEST,
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /*
     * Is thrown when a user using other role's API
     * */
    @ResponseBody
    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<ErrorDto> handleException(UnauthorizedException exception) {
        logger.error(exception.getMessage());
        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.UNAUTHORIZED.value(),
                ConstantUtil.EXCEPTION_UNAUTHORIZED,
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ResponseBody
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorDto> handleException(RuntimeException exception) {
        logger.error(exception.getMessage());
        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ConstantUtil.EXCEPTION_UNEXPECTED_ERROR,
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ResponseBody
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleException(DataIntegrityViolationException exception) {
        logger.error(exception.getMessage());
        ErrorDto errorDto = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.CONFLICT.value(),
                ConstantUtil.EXCEPTION_FK_DUPLICATED,
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
                HttpStatus.BAD_REQUEST.value(),
                ConstantUtil.EXCEPTION_VALIDATION_FAILED,
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
            msg.append(fieldError.getField()).append(":").append(fieldError.getDefaultMessage()).append(";");
        }
        logger.error(msg);
        ErrorDto errorDto = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                ConstantUtil.EXCEPTION_VALIDATION_FAILED,
                msg.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ResponseBody
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleException(ConstraintViolationException exception) {
        logger.error(exception.getMessage());
        ErrorDto errorDto = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                ConstantUtil.EXCEPTION_VALIDATION_FAILED,
                exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }


}