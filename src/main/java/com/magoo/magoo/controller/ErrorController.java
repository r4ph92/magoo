package com.magoo.magoo.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public String handle404(HttpServletResponse response) {
        response.setStatus(404);
        return "404";
    }
}
