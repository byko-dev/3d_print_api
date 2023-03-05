package com.byko.api_3d_printing.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message){
        super(message);
    }

}
