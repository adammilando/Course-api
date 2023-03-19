package com.enigmaschool.enigmaschool3springjpa.Controller;

import com.enigmaschool.enigmaschool3springjpa.Exception.DuplicateDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.MaxDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.NotFoundException;
import com.enigmaschool.enigmaschool3springjpa.Model.Response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
@RestControllerAdvice
public class ErrorController {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("01",e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDataNotFound(NotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("02", e.getMessage()));
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateData(DuplicateDataException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("03",e.getMessage()));
    }

    @ExceptionHandler(MaxDataException.class)
    public ResponseEntity<ErrorResponse> handleMaxLimit(MaxDataException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("04", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumenNotValid(MethodArgumentNotValidException e){
        List<FieldError> fieldErrorList = e.getBindingResult().getFieldErrors();
        List<String> errors = new ArrayList<>();
        for (FieldError error : fieldErrorList){
            errors.add(error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("06", errors.toString()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleContraintViolationException(ConstraintViolationException e){
        List<String> errors = new ArrayList<>();
        e.getConstraintViolations().forEach(constraintViolation -> {
            errors.add(constraintViolation.getMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("07", errors.toString()));
    }
}
