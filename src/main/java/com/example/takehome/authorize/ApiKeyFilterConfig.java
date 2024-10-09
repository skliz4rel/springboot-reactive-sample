package com.example.takehome.authorize;

import com.example.takehome.configuration.AppProperties;
import com.example.takehome.repositorys.UserRepository;
import com.example.takehome.utils.ApiPath;
import com.example.takehome.utils.ResponseHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class ApiKeyFilterConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResponseHelper responseHelper;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilter() {
        FilterRegistrationBean<ApiKeyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiKeyFilter(userRepository, responseHelper, objectMapper));

        //Registering and security authorization of the User.
        registrationBean.addUrlPatterns(ApiPath.UserPath+"/update/*"); // Secure update user path
        registrationBean.addUrlPatterns(ApiPath.UserPath+"/delete/*"); // Secure delete user path

        //Securing authorization for the Task
        registrationBean.addUrlPatterns(ApiPath.TaskPath+"/create"); // Secure update user path
        registrationBean.addUrlPatterns(ApiPath.TaskPath+"/update/*"); // Secure update user path
        registrationBean.addUrlPatterns(ApiPath.TaskPath+"/delete/*"); // Secure delete user path

        return registrationBean;
    }
}
