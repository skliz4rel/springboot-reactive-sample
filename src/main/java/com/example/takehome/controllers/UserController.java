package com.example.takehome.controllers;

import com.ctc.wstx.util.StringUtil;
import com.example.takehome.models.APIResponse;
import com.example.takehome.models.SearchDTO;
import com.example.takehome.models.UserDTO;
import com.example.takehome.services.UserService;
import com.example.takehome.utils.ApiPath;
import com.example.takehome.utils.HelperUtility;
import io.micrometer.common.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import jakarta.validation.Valid;

/******
 * Please note that TransactionId is used for logging purpose.
 * You can see it has a traceId. So every request can be traced from entry to the final response.
 * This would help in a microservice environment were several system make hand shakes.
 */

@RequestMapping(ApiPath.UserPath)
@Slf4j
@AllArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final HelperUtility helperUtility;


    @Operation(summary = "Creating a new user")
    @ApiOperation(value = "Create user", response = APIResponse.class)
    @PostMapping(
            value = "/create",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity> create(
            @RequestHeader(value = "transactionId", required = false) String transactionId,
            @Valid  @RequestBody  UserDTO dto){

            if(StringUtils.isEmpty(transactionId))
                transactionId = helperUtility.generateTransactionId();

            log.info("TransactionId {} entry into the create user controller {}", transactionId, dto);

            return this.userService.create(transactionId, dto);
    }

    @Operation(summary = "Search by first name or last name")
    @ApiOperation(value = "Search by name", response = APIResponse.class)
    @PostMapping(
            value = "/search",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity> search(
            @RequestHeader(value = "transactionId", required = false) String transactionId,
                                            @Valid   @RequestBody  SearchDTO dto){
        if(StringUtils.isEmpty(transactionId))
            transactionId = helperUtility.generateTransactionId();

       return this.userService.search(transactionId, dto.getFirstName(),  dto.getLastName());
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

        log.info("TransactionId {} inside the controller to retrieve user details {}", transactionId);

        return this.userService.list(transactionId);
    }



    @Operation(summary = "Retrieving a user by id")
    @ApiOperation(value = "Retrieve user", response = APIResponse.class)
    @GetMapping(
            value = "/retrieve/{id}",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity> retrieve(
            @RequestHeader(value = "transactionId", required = false) String transactionId,
            @NotNull @PathVariable String id){

        if(StringUtils.isEmpty(transactionId))
            transactionId = helperUtility.generateTransactionId();

        log.info("TransactionId {} inside the controller to retrieve user details {}", transactionId);

        return this.userService.retrieve(transactionId, id);
    }

    @Operation(summary = "Updating user details")
    @ApiOperation(value = "Update user details", response = APIResponse.class)
    @PutMapping(
            value = "/update/{id}",
            produces = APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity> update(
            @RequestHeader(value = "transactionId", required = false) String transactionId,
            @RequestHeader(value="X-API-KEY", required = true) String apikey,
            @NotNull @PathVariable String id,
            @RequestBody @Valid UserDTO dto
    ){
        if(StringUtils.isEmpty(transactionId))
            transactionId = helperUtility.generateTransactionId();

        log.info("TransactionId {} inside the controller to retrieve user details {}", transactionId);

        return this.userService.update(transactionId, id, dto, apikey);
    }


    @Operation(summary = "Deleting user details by id")
    @ApiOperation(value = "Delete user details", response = APIResponse.class)
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

        log.info("TransactionId {} inside the controller to delete user details {}", transactionId);

        return this.userService.delete(transactionId, id, apikey);
    }


}
