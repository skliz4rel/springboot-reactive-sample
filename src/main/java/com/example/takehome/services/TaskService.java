package com.example.takehome.services;

import com.example.takehome.configuration.AppProperties;
import com.example.takehome.entity.StatusEnum;
import com.example.takehome.entity.Task;
import com.example.takehome.entity.User;
import com.example.takehome.models.APIResponse;
import com.example.takehome.models.TaskDTO;
import com.example.takehome.models.UserDTO;
import com.example.takehome.repositorys.TaskRepository;
import com.example.takehome.utils.MappingHelper;
import com.example.takehome.utils.ResponseHelper;
import com.example.takehome.utils.StatusCodesEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;

@Service
@AllArgsConstructor
@Slf4j
public class TaskService implements  ITaskService{

    private final TaskRepository taskRepository;

    private final MappingHelper mappingHelper;

    private final AppProperties appProperties;

    private final ResponseHelper responseHelper;

    public Mono<ResponseEntity> create(String transactionId, TaskDTO dto)
    {
        log.info("Transaction ID {} creating a new task {} ", transactionId, dto);

        try {
            Task task = this.mappingHelper.getTask(dto);
            return this.taskRepository.save(task)
                    .timeout(Duration.ofSeconds(this.appProperties.getDatabaseTimeout()))
                    .map(u -> {
                        TaskDTO dt = this.mappingHelper.returnTaskdto(u);

                        APIResponse apiResponse = responseHelper.returnSuccessfulResp(transactionId, dt, StatusCodesEnum.CREATED);

                        return responseHelper.returnResponse(apiResponse);
                    })
                    .onErrorResume(e -> {
                        APIResponse<?> errorResponse = responseHelper.handleDBFailedResp(transactionId,e);
                        return Mono.just(responseHelper.returnResponse(errorResponse));
                    });
        }
        catch (Exception e){
            log.error("Transaction Id {} an usual internal errors {}",transactionId, e);

            String customMsg = null; StatusCodesEnum statusCodesEnum = StatusCodesEnum.INTERNAL_ERROR;

            if(e instanceof IllegalArgumentException){
                customMsg = "Illegal Status was supplied.  valid options (TODO | IN_PROGRESS | DONE) ";
                statusCodesEnum = StatusCodesEnum.BAD_REQUEST;
            }

            if(e instanceof  DateTimeException){
                customMsg = "Wrong date format for Due date. Use this format YYYY-MM-DD ";
                statusCodesEnum = StatusCodesEnum.BAD_REQUEST;
            }

            APIResponse apiResponse = responseHelper.returnErrResponse(transactionId, statusCodesEnum, customMsg);
            return Mono.just(responseHelper.returnResponse(apiResponse));
        }
    }


    public Mono<ResponseEntity> retrieve(String transactionId, String id){

        log.info("Transaction ID {} retrieving user by id", transactionId, id);

        try{
            return this.taskRepository.findById(id)
                    .timeout(Duration.ofSeconds(this.appProperties.getDatabaseTimeout()))
                    .map(task->{
                        TaskDTO dto = this.mappingHelper.returnTaskdto(task);

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
            log.error("Transaction Id {} an usual internal errors when retrieving task details {}",transactionId, e);

            APIResponse apiResponse = responseHelper.returnErrResponse(transactionId, StatusCodesEnum.INTERNAL_ERROR, null);

            return Mono.just(responseHelper.returnResponse(apiResponse));
        }
    }


    public Mono<ResponseEntity> update(String transactionId,String id,  TaskDTO dto, String apiKey)
    {
        try{
            return this.taskRepository.findById(id)
                    .flatMap(task->{

                        task.setDueDate(LocalDate.parse(dto.getDueDate()));
                        task.setTitle(dto.getTitle());
                        task.setDescription(dto.getDescription());
                        task.setStatus(StatusEnum.pickOption(dto.getStatus()));

                        return taskRepository.save(task).map(u->{
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

            String customMsg = null; StatusCodesEnum statusCodesEnum = StatusCodesEnum.INTERNAL_ERROR;

            if(e instanceof IllegalArgumentException){
                customMsg = "Illegal Status was supplied.  valid options (TODO | IN_PROGRESS | DONE) ";
                statusCodesEnum = StatusCodesEnum.BAD_REQUEST;
            }

            if(e instanceof  DateTimeException){
                customMsg = "Wrong date format for Due date. Use this format YYYY-MM-DD ";
                statusCodesEnum = StatusCodesEnum.BAD_REQUEST;
            }

            APIResponse apiResponse = responseHelper.returnErrResponse(transactionId, statusCodesEnum, customMsg);
            return Mono.just(responseHelper.returnResponse(apiResponse));
        }
    }


    public Mono<ResponseEntity> delete(String transactionId, String id, String apiKey)
    {
        try{

            return this.taskRepository.findById(id)
                    .flatMap(user->{
                        TaskDTO taskDTO = this.mappingHelper.returnTaskdto(user);

                        return taskRepository.deleteById(id)
                                .then(Mono.just(responseHelper.returnResponse(responseHelper.returnSuccessfulResp(transactionId,taskDTO, StatusCodesEnum.SUCCESS))));

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
            log.error("Transaction Id {} an usual internal errors when updating task details {}",transactionId, e);

            APIResponse apiResponse = responseHelper.returnErrResponse(transactionId, StatusCodesEnum.INTERNAL_ERROR, null);

            return Mono.just(responseHelper.returnResponse(apiResponse));
        }
    }


    public Mono<ResponseEntity> list(String transactionId){

        log.info("Transaction ID {} retrieving all the users ", transactionId);

        try{
            return this.taskRepository.findAll()
                    .timeout(Duration.ofSeconds(this.appProperties.getDatabaseTimeout()))
                    .map(ta->{
                        TaskDTO dto= this.mappingHelper.returnTaskdto(ta);
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


}
