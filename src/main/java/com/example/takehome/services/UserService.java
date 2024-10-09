package com.example.takehome.services;

import com.example.takehome.configuration.AppProperties;
import com.example.takehome.entity.User;
import com.example.takehome.models.APIResponse;
import com.example.takehome.models.UserDTO;
import com.example.takehome.repositorys.UserRepository;
import com.example.takehome.utils.ResponseHelper;
import com.example.takehome.utils.StatusCodesEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.example.takehome.utils.MappingHelper;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@AllArgsConstructor
public class UserService implements  IUserService{

    private final UserRepository userRepository;

    private final MappingHelper mappingHelper;

    private final AppProperties appProperties;

    private final ResponseHelper responseHelper;

    public Mono<ResponseEntity> create(String transactionId, UserDTO dto)
    {
        log.info("Transaction ID {} creating a new user {} ", transactionId, dto);

        try {

            User user = this.mappingHelper.getUser(dto);
            return this.userRepository.save(user)
                    .timeout(Duration.ofSeconds(this.appProperties.getDatabaseTimeout()))
                    .map(u -> {
                        UserDTO dt = this.mappingHelper.returnUserdto(u);

                        APIResponse apiResponse = responseHelper.returnSuccessfulResp(transactionId, dt, StatusCodesEnum.CREATED);

                        return responseHelper.returnResponse(apiResponse);
                    })
                    .onErrorResume(e -> {
                        log.info("A possible date exception here");

                        String customMsg = null; StatusCodesEnum statusCodesEnum = StatusCodesEnum.INTERNAL_ERROR;

                        customMsg = "Please note multiple accounts can not have the same email address.";

                        APIResponse apiResponse = responseHelper.returnErrResponse(transactionId,statusCodesEnum , customMsg);

                        return Mono.just(responseHelper.returnResponse(apiResponse));
                    });
        }
        catch (Exception e){
            log.error("Transaction Id {} an usual internal errors {}",transactionId, e);

            String customMsg = null; StatusCodesEnum statusCodesEnum = StatusCodesEnum.INTERNAL_ERROR;

            if(e instanceof DateTimeParseException){
                customMsg = "Illegal date was supplied. Enter this format YYYY-MM-DD";
                statusCodesEnum = StatusCodesEnum.BAD_REQUEST;
            }

            APIResponse apiResponse = responseHelper.returnErrResponse(transactionId,statusCodesEnum , customMsg);

            return Mono.just(responseHelper.returnResponse(apiResponse));
        }
    }

    public Mono<ResponseEntity> search(String transactionId, String firstName, String lastName){

        log.info("Transaction ID {} retrieving all the users by firstname {} and lastname {}", transactionId, firstName, lastName);

        try{
           return this.userRepository.findByFirstNameOrLastName(firstName, lastName)
                    .timeout(Duration.ofSeconds(this.appProperties.getDatabaseTimeout()))
                    .map(user->{
                      UserDTO dto= this.mappingHelper.returnUserdto(user);
                      return dto;
                    }).collectList()
                    .map(
                        list->{
                            APIResponse apiResponse = responseHelper.returnSuccessfulResp(transactionId, list, StatusCodesEnum.SUCCESS);

                            return responseHelper.returnResponse(apiResponse);
                        }
                    )
                    .switchIfEmpty(Mono.fromCallable(() -> {
                        APIResponse<?> errorResponse = responseHelper.notFoundResp(transactionId, null);
                        return responseHelper.returnResponse(errorResponse);
                    }))
                    .onErrorResume(e -> {
                        APIResponse<?> errorResponse = responseHelper.handleDBFailedResp(transactionId,e);
                        return Mono.just(responseHelper.returnResponse(errorResponse));
                    });
        }
        catch (Exception e){
            log.error("Transaction Id {} an usual internal errors when searching with firstName and Last Name {}",transactionId, e);

            APIResponse apiResponse = responseHelper.returnErrResponse(transactionId, StatusCodesEnum.INTERNAL_ERROR, null);

            return Mono.just(responseHelper.returnResponse(apiResponse));
        }
    }

    public Mono<ResponseEntity> list(String transactionId){

        log.info("Transaction ID {} retrieving all the users ", transactionId);

        try{
            return this.userRepository.findAll()
                    .timeout(Duration.ofSeconds(this.appProperties.getDatabaseTimeout()))
                    .map(user->{
                        UserDTO dto= this.mappingHelper.returnUserdto(user);
                        return dto;
                    }).collectList()
                    .map(
                            list->{
                                APIResponse apiResponse = responseHelper.returnSuccessfulResp(transactionId, list, StatusCodesEnum.SUCCESS);

                                return responseHelper.returnResponse(apiResponse);
                            }
                    )
                    .switchIfEmpty(Mono.fromCallable(() -> {
                        APIResponse<?> errorResponse = responseHelper.notFoundResp(transactionId, null);
                        return responseHelper.returnResponse(errorResponse);
                    }))
                    .onErrorResume(e -> {
                        APIResponse<?> errorResponse = responseHelper.handleDBFailedResp(transactionId,e);
                        return Mono.just(responseHelper.returnResponse(errorResponse));
                    });
        }
        catch (Exception e){
            log.error("Transaction Id {} an usual internal errors when retrieving all users {}",transactionId, e);

            APIResponse apiResponse = responseHelper.returnErrResponse(transactionId, StatusCodesEnum.INTERNAL_ERROR, null);

            return Mono.just(responseHelper.returnResponse(apiResponse));
        }
    }


    public Mono<ResponseEntity> retrieve(String transactionId, String id){

        log.info("Transaction ID {} retrieving user by id", transactionId, id);

        try{
        return this.userRepository.findById(id)
                .timeout(Duration.ofSeconds(this.appProperties.getDatabaseTimeout()))
                .map(user->{
                    UserDTO dto = this.mappingHelper.returnUserdto(user);

                    APIResponse apiResponse = responseHelper.returnSuccessfulResp(transactionId, dto, StatusCodesEnum.SUCCESS);

                    return responseHelper.returnResponse(apiResponse);
                })
                .switchIfEmpty(Mono.fromCallable(() -> {
                    APIResponse<?> errorResponse = responseHelper.notFoundResp(transactionId, id);
                    return responseHelper.returnResponse(errorResponse);
                }))
                .onErrorResume(e -> {
                    APIResponse<?> errorResponse = responseHelper.handleDBFailedResp(transactionId,e);
                    return Mono.just(responseHelper.returnResponse(errorResponse));
                });
        }
        catch (Exception e){
            log.error("Transaction Id {} an usual internal errors when retrieving user details {}",transactionId, e);

            APIResponse apiResponse = responseHelper.returnErrResponse(transactionId, StatusCodesEnum.INTERNAL_ERROR, null);

            return Mono.just(responseHelper.returnResponse(apiResponse));
        }
    }

    public Mono<ResponseEntity> update(String transactionId,String id,  UserDTO dto, String apiKey)
    {
        try{
           return this.userRepository.findById(id)
                    .flatMap(user->{

                        if(!apiKey.equalsIgnoreCase(id)){
                            log.info("transaction Id {}, You can  not update another users profile");
                            APIResponse apiResponse = responseHelper.returnErrResponse(transactionId, StatusCodesEnum.UNAUTHORIZED, null);

                            return Mono.just(responseHelper.returnResponse(apiResponse));
                        }

                        user.setLastName(dto.getLastName());
                        user.setFirstName(dto.getFirstName());
                        user.setEmail(dto.getEmail());
                        user.setDob(LocalDate.parse(dto.getDob()));

                        return userRepository.save(user).map(u->{

                                    dto.setId(u.getId());
                                    APIResponse apiResponse = responseHelper.returnSuccessfulResp(transactionId, dto, StatusCodesEnum.SUCCESS);

                                    return responseHelper.returnResponse(apiResponse);
                                });
                    })
                   .switchIfEmpty(Mono.fromCallable(() -> {
                       APIResponse<?> errorResponse = responseHelper.notFoundResp(transactionId,id);
                       return responseHelper.returnResponse(errorResponse);
                   }))
                   .onErrorResume(e -> {
                       APIResponse<?> errorResponse = responseHelper.handleDBFailedResp(transactionId,e);
                       return Mono.just(responseHelper.returnResponse(errorResponse));
                   });
        }
        catch (Exception e){
            log.error("Transaction Id {} an usual internal errors when updating user details {}",transactionId, e);

            APIResponse apiResponse = responseHelper.returnErrResponse(transactionId, StatusCodesEnum.INTERNAL_ERROR, null);

            return Mono.just(responseHelper.returnResponse(apiResponse));
        }
    }

    public Mono<ResponseEntity> delete(String transactionId, String id, String apiKey)
    {
        try{

            return this.userRepository.findById(id)
                    .flatMap(user->{
                        UserDTO userDTO = this.mappingHelper.returnUserdto(user);

                        if(!apiKey.equalsIgnoreCase(id)){
                            log.info("transaction Id {}, You can  not delete another users profile");
                            APIResponse apiResponse = responseHelper.returnErrResponse(transactionId, StatusCodesEnum.UNAUTHORIZED, null);

                            return Mono.just(responseHelper.returnResponse(apiResponse));
                        }

                        return userRepository.deleteById(id)
                                .then(Mono.just(responseHelper.returnResponse(responseHelper.returnSuccessfulResp(transactionId, null, StatusCodesEnum.SUCCESS))));  // Ensure this returns Mono<APIResponse>
                    })
                      .switchIfEmpty(Mono.fromCallable(() -> {
                       log.info("Transaction Id {} getting here cos no record found ", transactionId);

                       APIResponse<?> errorResponse = responseHelper.notFoundResp(transactionId,id);
                       return responseHelper.returnResponse(errorResponse);
                   }))
                    .onErrorResume(e -> {
                        APIResponse<?> errorResponse = responseHelper.handleDBFailedResp(transactionId,e);
                        return Mono.just(responseHelper.returnResponse(errorResponse));
                    });

        }
        catch (Exception e){
            log.error("Transaction Id {} an usual internal errors when updating user details {}",transactionId, e);

            APIResponse apiResponse = responseHelper.returnErrResponse(transactionId, StatusCodesEnum.INTERNAL_ERROR, null);

            return Mono.just(responseHelper.returnResponse(apiResponse));
        }
    }


}
