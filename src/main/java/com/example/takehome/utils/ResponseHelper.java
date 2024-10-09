package com.example.takehome.utils;


import com.example.takehome.models.APIError;
import com.example.takehome.models.APIResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
@Slf4j
public class ResponseHelper implements IResponseHelper {

    public APIResponse unauthorizedError(String transactionId){

        log.info("TransactionId {} there was an authorized request because the user did not provide correct api key");

        APIResponse apiResponse = new APIResponse();
        apiResponse.setStatusCode(StatusCodesEnum.UNAUTHORIZED.getStatusCode());

        apiResponse.setStatus(StatusCodesEnum.UNAUTHORIZED.getHttpStatus());

        APIError error = new APIError();
        error.setCause(StatusCodesEnum.UNAUTHORIZED.getCause());
        error.setDescription(StatusCodesEnum.UNAUTHORIZED.getDescription());

        apiResponse.setError(error);

        return apiResponse;
    }

    public APIResponse notFoundResp(String transactionId, String id){

        if(id != null)
        log.info("TransactionID {} data was not found in the database, ensure that a value with this id=[ {} ] exits", transactionId, id);

        APIResponse apiResponse = new APIResponse();
        apiResponse.setStatusCode(StatusCodesEnum.NOT_FOUND.getStatusCode());

        apiResponse.setStatus(StatusCodesEnum.NOT_FOUND.getHttpStatus());

        APIError error = new APIError();
        error.setCause(StatusCodesEnum.NOT_FOUND.getCause());
        error.setDescription(StatusCodesEnum.NOT_FOUND.getDescription());

        apiResponse.setError(error);

        return apiResponse;
    }

    public APIResponse handleDBFailedResp(String transactionId, Throwable e){

        if( e instanceof TimeoutException)
        {
            log.error("TransactionID {} database timedout {}",transactionId, e);
        }
        else {
            Exception exception = (Exception) e;

            log.error("Transaction ID {} some usual error occurred when interacting with the database {}", transactionId, exception);
        }

        APIResponse apiResponse = new APIResponse();
        apiResponse.setStatusCode(StatusCodesEnum.INTERNAL_ERROR.getStatusCode());
        apiResponse.setStatus(StatusCodesEnum.INTERNAL_ERROR.getHttpStatus());

        APIError error = new APIError();
        error.setCause(StatusCodesEnum.INTERNAL_ERROR.getCause());
        error.setDescription(StatusCodesEnum.INTERNAL_ERROR.getDescription());

        apiResponse.setError(error);

        return apiResponse;
    }

    public APIResponse returnSuccessfulResp(String transactionId, Object data, StatusCodesEnum statusCodesEnum)
    {
        log.info("TransactionId {} the entity was successful processed {}", transactionId, data);

        APIResponse apiResponse = new APIResponse();
        apiResponse.setStatusCode(statusCodesEnum.getStatusCode());
        apiResponse.setStatus(statusCodesEnum.getHttpStatus());
        apiResponse.setTransactionId(transactionId);
        apiResponse.setData(data);

        return apiResponse;
    }

    public APIResponse returnErrResponse(String transactionId, StatusCodesEnum statusCodesEnum, String customMessage)
    {
        log.error("TransactionId {} an internal error occurred returnErrResponse() was triggered ", transactionId);

        APIResponse apiResponse = new APIResponse();
        apiResponse.setStatusCode(statusCodesEnum.getStatusCode());
        apiResponse.setStatus(statusCodesEnum.getHttpStatus());

        APIError error = new APIError();
        error.setCause(statusCodesEnum.getCause());

        if(customMessage == null)
            error.setDescription(statusCodesEnum.getDescription());
        else
            error.setDescription(customMessage);

        apiResponse.setError(error);

        return apiResponse;
    }

    public ResponseEntity returnResponse(APIResponse response){

            return (ResponseEntity.status(response.getStatus())
                    .body(response));
    }

}
