package com.example.takehome.controllers;

import com.example.takehome.TakehomeApplication;
import com.example.takehome.entity.Task;
import com.example.takehome.entity.User;
import com.example.takehome.models.APIResponse;
import com.example.takehome.models.TaskDTO;
import com.example.takehome.models.UserDTO;
import com.example.takehome.repositorys.TaskRepository;
import com.example.takehome.repositorys.UserRepository;
import com.example.takehome.utils.ApiPath;
import com.example.takehome.utils.MappingHelper;
import com.example.takehome.utils.StatusCodesEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.fail;

@Slf4j
@ExtendWith(SpringExtension.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = TakehomeApplication.class)
@AutoConfigureWebTestClient(timeout = "36000")
@ActiveProfiles("test")
@PropertySource("classpath:test.properties")
public class TaskControllerTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TaskRepository taskRepository;

    @Autowired
    private MappingHelper mappingHelper;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void registerTaskTest_Created_success() throws Exception {
        try {

            TaskDTO dto = TaskDTO.builder()
                    .status("DONE")
                    .title("new book")
                    .dueDate("2012-01-01")
                    .description("Adding a new book")
                    .build();


            Task task = this.mappingHelper.getTask(dto);

            String apiKey = "11111111111111111111111";

            Mockito.when(userRepository.existsById(apiKey)).thenReturn(Mono.just(true));
            Mockito.when(taskRepository.save(task)).thenReturn(Mono.just(task));

            String transactionId = UUID.randomUUID().toString();
            Map<String, String> headers = new HashMap<>();
            headers.put("transactionId", transactionId);  //transactionId is used for tracing logs.
            headers.put("X-API-KEY",apiKey);

            this.webTestClient.post()
                    .uri(ApiPath.TaskPath + "/create")
                    .body(Mono.just(dto), TaskDTO.class)
                    .headers(httpHeaders -> {
                        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

                        if (null != headers) {
                            headers.keySet().forEach(key -> httpHeaders.add(key, headers.get(key)));
                        }
                    })
                    .exchange()
                    .expectStatus()
                    .isCreated()
                    .expectBody(APIResponse.class)
                    .value(response ->
                            Assert.assertEquals(response.getStatusCode(),
                                    StatusCodesEnum.CREATED.getStatusCode())
                    );

        } catch (Exception e) {
            fail();
        }
    }


    @Test
    void registerTaskTest_UNAuthorized() throws Exception {
        try {

            TaskDTO dto = TaskDTO.builder()
                    .status("DONE")
                    .title("new book")
                    .dueDate("2012-01-01")
                    .description("Adding a new book")
                    .build();


            Task task = this.mappingHelper.getTask(dto);

            String apiKey = "11111111111111111111111";

            Mockito.when(userRepository.existsById(apiKey)).thenReturn(Mono.just(false));
            Mockito.when(taskRepository.save(task)).thenReturn(Mono.just(task));

            String transactionId = UUID.randomUUID().toString();
            Map<String, String> headers = new HashMap<>();
            headers.put("transactionId", transactionId);  //transactionId is used for tracing logs.
            headers.put("X-API-KEY",apiKey);

            this.webTestClient.post()
                    .uri(ApiPath.TaskPath + "/create")
                    .body(Mono.just(dto), TaskDTO.class)
                    .headers(httpHeaders -> {
                        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

                        if (null != headers) {
                            headers.keySet().forEach(key -> httpHeaders.add(key, headers.get(key)));
                        }
                    })
                    .exchange()
                    .expectStatus()
                    .isUnauthorized()
                    .expectBody(APIResponse.class)
                    .value(response ->
                            Assert.assertEquals(response.getStatusCode(),
                                    StatusCodesEnum.UNAUTHORIZED.getStatusCode())
                    );

        } catch (Exception e) {
            fail();
        }
    }



    @Test
    public void testGetTaskById_Success() {

        String id  = "111111111111111111111111111";

        TaskDTO dto = TaskDTO.builder()
                .status("Done")
                .dueDate("2012-01-01")
                .description("creeating the task")
                .title("create")
                .build();


        Task task = this.mappingHelper.getTask(dto);

        Mockito.when(taskRepository.findById(id)).thenReturn(
                Mono.just(task));

        try {

            webTestClient.get()
                    .uri(String.format(ApiPath.TaskPath + "/retrieve/%s", id))
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("transactionId", UUID.randomUUID().toString())
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(APIResponse.class)
                    .value(response ->
                            Assert.assertEquals(response.getStatusCode(),
                                    StatusCodesEnum.SUCCESS.getStatusCode())
                    );
        } catch (Exception e) {
            log.error("Error while trying to get the user {}", e);
        }
    }


    @Test
    public void testGetTaskById_NotFound() {

        String id  = "111111111111111111111111111";

        TaskDTO dto = TaskDTO.builder()
                .status("Done")
                .dueDate("2012-01-01")
                .description("creeating the task")
                .title("create")
                .build();


        Task task = this.mappingHelper.getTask(dto);

        Mockito.when(taskRepository.findById(id)).thenReturn(
                Mono.empty());

        try {

            webTestClient.get()
                    .uri(String.format(ApiPath.TaskPath + "/retrieve/%s", id))
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("transactionId", UUID.randomUUID().toString())
                    .exchange()
                    .expectStatus()
                    .isNotFound()
                    .expectBody(APIResponse.class)
                    .value(response ->
                            Assert.assertEquals(response.getStatusCode(),
                                    StatusCodesEnum.NOT_FOUND.getStatusCode())
                    );
        } catch (Exception e) {
            log.error("Error while trying to get the user {}", e);
        }
    }

}
