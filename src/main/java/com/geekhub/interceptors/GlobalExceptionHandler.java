package com.geekhub.interceptors;

import com.geekhub.exceptions.FileAccessException;
import com.geekhub.exceptions.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        String message = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler({ResourceNotFoundException.class, FileAccessException.class})
    public ModelAndView handleResourceNotFoundException() {
        return new ModelAndView("../errorPages/error404");
    }
}
