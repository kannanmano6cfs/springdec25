package com.example.EmployeeService.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity handleEmployeeNotFoundException(EmployeeNotFoundException e, WebRequest request){
        Map<String, Object> map=new HashMap<>();
        map.put("message", e.getMessage());
        map.put("timestamp", LocalDateTime.now().toString());
        map.put("path", request.getDescription(false).replace("uri=",""));
        map.put("status","EMPLOYEE_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request){
        Map<String, Object> map=new HashMap<>();
        map.put("error", "Input Validation Error");
        map.put("timestamp", LocalDateTime.now().toString());
        map.put("path", request.getDescription(false).replace("uri=",""));
        map.put("status","EMPLOYEE_DETAILS_INVALID");

        Map<String, Object> map1=new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error)->{
            String fname=((FieldError)error).getField();
            String mname=((FieldError)error).getDefaultMessage();
            map1.put(fname, mname);
        });
        map.put("errors", map1);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

}
