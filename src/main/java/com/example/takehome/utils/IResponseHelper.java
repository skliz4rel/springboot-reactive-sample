package com.example.takehome.utils;

import com.example.takehome.models.APIResponse;
import org.springframework.http.ResponseEntity;

public interface IResponseHelper {

    public APIResponse unauthorizedError(String transactionId);

    public APIResponse notFoundResp(String transactionId, String id);

    public APIResponse handleDBFailedResp(String transactionId, Throwable e);

    public APIResponse returnSuccessfulResp(String transactionId, Object data, StatusCodesEnum statusCodesEnum);

    public APIResponse returnErrResponse(String transactionId, StatusCodesEnum statusCodesEnum, String customMessage);

    public ResponseEntity returnResponse(APIResponse response);
}
