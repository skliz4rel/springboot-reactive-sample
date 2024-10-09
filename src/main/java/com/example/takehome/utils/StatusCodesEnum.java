package com.example.takehome.utils;

import com.example.takehome.entity.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum StatusCodesEnum {

    SUCCESS("200", "","Successful", HttpStatus.OK),
    CREATED("201", "", "Created", HttpStatus.CREATED),
    NOT_FOUND("404","Data not found","We do not have a record of this data in our database", HttpStatus.NOT_FOUND),
    BAD_REQUEST("400","Client Error","Bad Inputs passed", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("401","Invalid credentials","You do not have permission, Kindly use the id given to the user when created. Supply this id has the X-API-KEY to call this endpoint", HttpStatus.UNAUTHORIZED),
    INTERNAL_ERROR("500","Application error", "Internal errors, contact admin", HttpStatus.INTERNAL_SERVER_ERROR);


    private String statusCode;

    private String cause;

    private String description;

    private HttpStatus httpStatus;

    public StatusCodesEnum returnStatus(String code){

        for(StatusCodesEnum s : values())
        {
            if(s.getStatusCode().equalsIgnoreCase(code))
                return s;
        }

        return INTERNAL_ERROR;
    }
}
