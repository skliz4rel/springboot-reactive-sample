package com.example.takehome.controllers;

import com.example.takehome.TakehomeApplication;
import com.example.takehome.entity.User;
import com.example.takehome.models.APIResponse;
import com.example.takehome.models.UserDTO;
import com.example.takehome.repositorys.UserRepository;
import com.example.takehome.utils.MappingHelper;
import com.example.takehome.utils.StatusCodesEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import java.util.*;
import java.util.UUID;
import com.example.takehome.utils.ApiPath;

import static org.junit.Assert.fail;

@Slf4j
@ExtendWith(SpringExtension.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = TakehomeApplication.class)
@AutoConfigureWebTestClient(timeout = "36000")
@ActiveProfiles("test")
@PropertySource("classpath:test.properties")
public class UserControllerTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MappingHelper mappingHelper;

    @Autowired
    private WebTestClient webTestClient;


    @Test
    void registerUserTest_Created_success() throws Exception {
        try {

            UserDTO dto = UserDTO.builder()
                    .email("skliz4rel@gmail.com")
                    .dob("2012-01-01")
                    .lastName("Akindejoye")
                    .firstName("Olajide")
                        .build();


            User user = this.mappingHelper.getUser(dto);

            Mockito.when(userRepository.save(user)).thenReturn(Mono.just(user));

            String transactionId = UUID.randomUUID().toString();
            Map<String, String> headers = new HashMap<>();
            headers.put("transactionId", transactionId);  //transactionId is used for tracing logs.

            this.webTestClient.post()
                    .uri(ApiPath.UserPath + "/create")
                    .body(Mono.just(dto), UserDTO.class)
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
    void registerUserTest_WrongDate_BadRequest() throws Exception {
        try {

            UserDTO dto = UserDTO.builder()
                    .email("skliz4rel@gmail.com")
                    .dob("2010-01-01")
                    .lastName("")
                    .firstName("")
                    .build();


            User user = this.mappingHelper.getUser(dto);

            Mockito.when(userRepository.save(user)).thenReturn(Mono.just(user));

            String transactionId = UUID.randomUUID().toString();
            Map<String, String> headers = new HashMap<>();
            headers.put("transactionId", transactionId);  //transactionId is used for tracing logs.

            this.webTestClient.post()
                    .uri(ApiPath.UserPath + "/create")
                    .body(Mono.just(dto), UserDTO.class)
                    .headers(httpHeaders -> {
                        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

                        if (null != headers) {
                            headers.keySet().forEach(key -> httpHeaders.add(key, headers.get(key)));
                        }
                    })
                    .exchange()
                    .expectStatus()
                    .isBadRequest()
                    .expectBody(APIResponse.class)
                    .value(response ->
                            Assert.assertEquals(response.getStatusCode(),
                                    StatusCodesEnum.BAD_REQUEST.getStatusCode())
                    );


        } catch (Exception e) {
            fail();
        }
    }


    @Test
    public void testGetUserbyId_Success() {

        String id  = "111111111111111111111111111";

        UserDTO dto = UserDTO.builder()
                .email("skliz4rel@gmail.com")
                .dob("2010-01-01")
                .lastName("akin ")
                .firstName("jide")
                .build();


        User user = this.mappingHelper.getUser(dto);

        Mockito.when(userRepository.findById(id)).thenReturn(
                Mono.just(user));

        try {

            webTestClient.get()
                    .uri(String.format(ApiPath.UserPath + "/retrieve/%s", id))
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
    public void testGetUserbyId_NotFound() {

        String id  = "111111111111111111111111111";

        UserDTO dto = UserDTO.builder()
                .email("skliz4rel@gmail.com")
                .dob("2010-01-01")
                .lastName("akin ")
                .firstName("jide")
                .build();


        User user = this.mappingHelper.getUser(dto);

        Mockito.when(userRepository.findById(id)).thenReturn(
                Mono.empty());

        try {

            webTestClient.get()
                    .uri(String.format(ApiPath.UserPath + "/retrieve/%s", id))
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