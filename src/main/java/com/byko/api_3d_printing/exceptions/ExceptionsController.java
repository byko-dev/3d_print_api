package com.byko.api_3d_printing.exceptions;

import com.byko.api_3d_printing.model.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class ExceptionsController {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Status handleResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request){
        log.error(String.format("ResourceNotFoundException => %s, request path => %s", exception.getMessage(), request.getServletPath()));
        return new Status(exception.getMessage(), request.getServletPath());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Status handleUnauthorizedException(UnauthorizedException exception, HttpServletRequest request){
        log.error(String.format("UnauthorizedException => %s, request path => %s", exception.getMessage(), request.getServletPath()));
        return new Status(exception.getMessage(), request.getServletPath());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Status handleBadRequestException(BadRequestException exception, HttpServletRequest request){
        log.error(String.format("BadRequestException => %s, request path => %s", exception.getMessage(), request.getServletPath()));
        return new Status(exception.getMessage(), request.getServletPath());
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    public Status handleMultipartException(MultipartException ex, HttpServletRequest request) {
        log.error(String.format("MultipartException => %s, caused => %s, request path => %s",
                ex.getMessage(), ex.getCause().getMessage(), request.getServletPath()));

        return new Status("Requested file is too large", request.getServletPath());
    }



}
