package dev.nithin.productservice.exception;

import dev.nithin.productservice.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NullPointerException.class)
    public ErrorDto handleNullPointerException() {
        // Handle the exception and return an appropriate response
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage("NPE Occurred");
        errorDto.setStatus("Failure");
        return errorDto;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProductNotFoundException(Exception e) {
        // Handle the exception and return an appropriate response
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(e.getMessage());
        errorDto.setStatus("Failure");
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
