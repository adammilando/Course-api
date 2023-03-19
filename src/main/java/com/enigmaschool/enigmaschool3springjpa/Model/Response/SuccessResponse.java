package com.enigmaschool.enigmaschool3springjpa.Model.Response;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

public class SuccessResponse<T> extends CommonResponse{

    private T data;
    public SuccessResponse(String message, T data) {
        super.setCode("200");
        super.setMessage(message);
        super.setStatus(HttpStatus.OK.name());
        this.data=data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
