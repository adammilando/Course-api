package com.enigmaschool.enigmaschool3springjpa.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MaxDataException extends RuntimeException{
    public MaxDataException(String model, int max) {
        super(model+" data exceeds the maximum limit of "+ max);
    }
}