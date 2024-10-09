package com.example.takehome.models;

import com.github.rkpunjal.sqlsafe.SQLInjectionSafe;
import lombok.Data;

@Data
public class SearchDTO {

    @SQLInjectionSafe(message = "Invalid input: Potential SQL injection risk")
    private String firstName;

    @SQLInjectionSafe(message = "Invalid input: Potential SQL injection risk")
    private String lastName;
}
