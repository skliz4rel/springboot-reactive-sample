package com.example.takehome.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/****
 *
 * @param <T> This is the object to be wrapped in the payload.
 * @transactionId This would help the client to be able to tie  a response to a request.
 * @error This would contain the api error.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class APIResponse <T> implements Serializable {

    private String transactionId;

    private  String statusCode;

    private T data;

    private APIError error;

    private transient HttpStatus status;
}