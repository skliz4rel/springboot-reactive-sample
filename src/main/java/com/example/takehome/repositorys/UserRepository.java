package com.example.takehome.repositorys;

import com.example.takehome.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UserRepository  extends ReactiveCrudRepository<User, String> {

    Flux<User> findByFirstNameOrLastName(String firstName, String lastName);

}
