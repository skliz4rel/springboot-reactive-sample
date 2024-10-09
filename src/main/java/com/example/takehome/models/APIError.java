package com.example.takehome.models;

import lombok.Data;

@Data
public class APIError {

    private String cause;

    private String description;
}
