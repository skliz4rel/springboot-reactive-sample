package com.example.takehome.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "user")
@Data
public class User {

    @Id
    private String id;

    private String firstName;

    private String lastName;

    @Indexed(unique = true)
    public String email;  //prevent multiple users with same email

    private LocalDate dob;
}
