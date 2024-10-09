package com.example.takehome.controllerAdvice;

import com.example.takehome.models.APIError;
import com.example.takehome.models.APIResponse;
import com.example.takehome.utils.StatusCodesEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//This would help all controller to validate their request payload
@Slf4j
@RestControllerAdvice
public class BadRequestAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        log.error("Request was handled by controller advice because of wrong input parameters");

        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            errors.put(((FieldError) error).getField(), error.getDefaultMessage());
        });

        APIResponse apiResponse = new APIResponse();
        apiResponse.setStatusCode(StatusCodesEnum.BAD_REQUEST.getStatusCode());

        APIError error = new APIError();
        error.setCause(StatusCodesEnum.BAD_REQUEST.getDescription());
        error.setDescription("Read the message for fields that are omitted");

        apiResponse.setError(error);

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

}
