package com.example.takehome.services;

import com.example.takehome.models.TaskDTO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface ITaskService {

    Mono<ResponseEntity> create(String transactionId, TaskDTO dto);

    Mono<ResponseEntity> retrieve(String transactionId, String id);

    Mono<ResponseEntity> update(String transactionId,String id,  TaskDTO dto, String apiKey);

    Mono<ResponseEntity> delete(String transactionId, String id, String apiKey);

    Mono<ResponseEntity> list(String transactionId);
}
