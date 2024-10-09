package com.example.takehome.models;

import com.example.takehome.validate.ValidDate;
import com.github.rkpunjal.sqlsafe.SQLInjectionSafe;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserDTO {

    private String id;

    @NotNull(message = "First name can not be empty")
    @NotEmpty(message = "First name can not be empty")
    @SQLInjectionSafe(message = "Invalid input: Potential SQL injection risk")
    private String firstName;

    @NotNull(message = "Last name can not be empty")
    @NotEmpty(message = "Last name can not be empty")
    @SQLInjectionSafe(message = "Invalid input: Potential SQL injection risk")
    private String lastName;

    @NotNull(message = "Last name can not be empty")
    @NotEmpty(message = "Last name can not be empty")
    @SQLInjectionSafe(message = "Invalid input: Potential SQL injection risk")
    private String email;

    @NotNull(message = "Date of Birth can not be empty")
    @NotEmpty(message = "Date of Birth can not be empty")
    @SQLInjectionSafe(message = "Invalid input: Potential SQL injection risk")
    @ValidDate(message = "Invalid date format, please use yyyy-MM-dd")
    private String dob;
}
