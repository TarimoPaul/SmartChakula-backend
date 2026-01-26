package com.SmartChakula.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private ResponseStatus status = ResponseStatus.Success;
    private T data;
    private String message;

    public Response(T data){

        this.data = data;
        this.status = ResponseStatus.Success;
        this.message = "Success";
    }

    public Response(String message, ResponseStatus status){
        this.message = message;
        this.status = status;
        this.data = null;
    }


    public Response(String message, HttpStatus httpStatus) {
        this.message = message;
        this.status = httpStatus.is2xxSuccessful() ? ResponseStatus.Success : ResponseStatus.Error;
        this.data = null;
    }

}
