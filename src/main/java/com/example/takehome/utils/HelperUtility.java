package com.example.takehome.utils;

import com.example.takehome.models.UserDTO;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.stereotype.Component;
import com.github.rkpunjal.sqlsafe.SQLInjectionSafe;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class HelperUtility {

    /**
     *
     * @return This is going to return TransactionId that would help in log tracing for every request.
     * It is standard practice to transaction every logs and tie it to a request call.
     */
    public String generateTransactionId(){

        String transactionId = "" + LocalDateTime.now();
        transactionId = transactionId.replace("/","");
        return transactionId;
    }

    public boolean validateDate(String date)
    {
        try{
            LocalDate.parse(date);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }


}
