package com.example.takehome.services;

import com.example.takehome.models.UserDTO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IUserService {

    Mono<ResponseEntity> create(String transactionId, UserDTO dto);

    Mono<ResponseEntity> search(String transactionId, String firstName, String lastName);

    Mono<ResponseEntity> list(String transactionId);

    Mono<ResponseEntity> retrieve(String transactionId, String id);

    Mono<ResponseEntity> update(String transactionId,String id,  UserDTO dto, String apiKey);

    Mono<ResponseEntity> delete(String transactionId, String id, String apiKey);


}
