package com.example.takehome.repositorys;

import com.example.takehome.entity.Task;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends ReactiveCrudRepository<Task, String> {



}
