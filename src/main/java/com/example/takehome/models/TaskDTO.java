package com.example.takehome.models;

import com.example.takehome.entity.StatusEnum;
import com.github.rkpunjal.sqlsafe.SQLInjectionSafe;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TaskDTO {

    private String id;


    @NotNull(message = "Title can not be empty")
    @NotEmpty(message = "Title can not be empty")
    @SQLInjectionSafe(message = "Invalid input: Potential SQL injection risk")
    private String title;

    @NotNull(message = "Description can not be empty")
    @NotEmpty(message = "Description can not be empty")
    @SQLInjectionSafe(message = "Invalid input: Potential SQL injection risk")
    private String description;

    @NotNull(message = "Due date can not be empty")
    @NotEmpty(message = "Due date can not be empty")
    @SQLInjectionSafe(message = "Invalid input: Potential SQL injection risk")
    private String dueDate;

    @NotNull(message = "Status can not be empty")
    @NotEmpty(message = "Status can not be empty")
    @SQLInjectionSafe(message = "Invalid input: Potential SQL injection risk")
    private String status;
}
