package com.example.takehome.controllers;

import com.example.takehome.models.APIResponse;
import com.example.takehome.models.TaskDTO;
import com.example.takehome.models.UserDTO;
import com.example.takehome.services.TaskService;
import com.example.takehome.utils.ApiPath;
import com.example.takehome.utils.HelperUtility;
import io.micrometer.common.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/******
 * Please note that TransactionId is used for logging purpose.
 * You can see it has a traceId. So every request can be traced from entry to the final response.
 * This would help in a microservice environment were several system make hand shakes.
 */

@RequestMapping(ApiPath.TaskPath)
@Slf4j
@AllArgsConstructor
@RestController
public class TaskController {

    private final TaskService taskService;
    private final HelperUtility helperUtility;



    @Operation(summary = "Creating a new task")
    @ApiOperation(value = "Create task", response = APIResponse.class)
    @PostMapping(
            value = "/create",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity> create(
            @RequestHeader(value = "transactionId", required = false) String transactionId,
            @RequestHeader(value="X-API-KEY", required = true) String apikey,
            @Valid  @RequestBody  TaskDTO dto){

        if(StringUtils.isEmpty(transactionId))
            transactionId = helperUtility.generateTransactionId();

        log.info("TransactionId {} entry into the create task controller {}", transactionId, dto);

        return this.taskService.create(transactionId, dto);
    }


    @Operation(summary = "Retrieving a task by id")
    @ApiOperation(value = "Retrieve task", response = APIResponse.class)
    @GetMapping(
            value = "/retrieve/{id}",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity> retrieve(
            @RequestHeader(value = "transactionId", required = false) String transactionId,
            @NotNull @PathVariable String id){

        if(StringUtils.isEmpty(transactionId))
            transactionId = helperUtility.generateTransactionId();

        log.info("TransactionId {} inside the controller to retrieve task details {}", transactionId);

        return this.taskService.retrieve(transactionId, id);
    }


    @Operation(summary = "Updating task details")
    @ApiOperation(value = "Update task details", response = APIResponse.class)
    @PutMapping(
            value = "/update/{id}",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity> update(
            @RequestHeader(value = "transactionId", required = false) String transactionId,
            @RequestHeader(value="X-API-KEY", required = true) String apikey,
            @NotNull @PathVariable String id,
            @RequestBody @Valid TaskDTO dto
    ){
        if(StringUtils.isEmpty(transactionId))
            transactionId = helperUtility.generateTransactionId();

        log.info("TransactionId {} inside the controller to retrieve task details {}", transactionId);

        return this.taskService.update(transactionId, id, dto, apikey);
    }


    @Operation(summary = "Deleting task details by id")
    @ApiOperation(value = "Delete task details", response = APIResponse.class)
    @DeleteMapping(
            value = "/delete/{id}",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity> delete(
            @RequestHeader(value = "transactionId", required = false) String transactionId,
            @RequestHeader(value="X-API-KEY", required = true) String apikey,
            @NotNull @PathVariable String id
    ){
        if(StringUtils.isEmpty(transactionId))
            transactionId = helperUtility.generateTransactionId();

        log.info("TransactionId {} inside the controller to delete task details {}", transactionId);

        return this.taskService.delete(transactionId, id, apikey);
    }


    @Operation(summary = "This would list all the users")
    @ApiOperation(value = "All users", response = APIResponse.class)
    @GetMapping(
            value = "/list",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity> retrieve(
            @RequestHeader(value = "transactionId", required = false) String transactionId
    )
    {
        if(StringUtils.isEmpty(transactionId))
            transactionId = helperUtility.generateTransactionId();

        log.info("TransactionId {} inside the controller to retrieve task details {}", transactionId);

        return this.taskService.list(transactionId);
    }

}
