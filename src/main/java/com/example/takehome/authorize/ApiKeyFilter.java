package com.example.takehome.authorize;

import com.example.takehome.configuration.AppProperties;
import com.example.takehome.models.APIResponse;
import com.example.takehome.repositorys.UserRepository;
import com.example.takehome.utils.ResponseHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.example.takehome.utils.ApiPath;

//@WebFilter(urlPatterns = ApiPath.UserPath+"/update/*") // Specify the paths you want to secure
@Slf4j
@AllArgsConstructor
public class ApiKeyFilter extends HttpFilter {

    private UserRepository userRepository;

    private ResponseHelper responseHelper;

    private  ObjectMapper objectMapper;

   /* @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String apiKey = request.getHeader(API_KEY_HEADER);

        if (validApiKey.equals(apiKey)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid API Key");
        }
    }*/

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

    //    if (request instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {
            String apiKey = request.getHeader("X-API-KEY");
            String transactionId = request.getHeader("transactionId");

            log.info("check the value here {} {}", apiKey, request);

            log.info("check the headers that are passed {} {}",apiKey, transactionId );

            boolean check =false;

           check =  this.userRepository.existsById(apiKey).block();

           log.info("feedback after checking the id {}", check);

           if(!check){
               log.info("got in here ");
               // Set the content type to application/json
               response.setContentType("application/json");
               response.setCharacterEncoding("UTF-8");

              response.setStatus(401);
              // response.getWriter().write("Invalid API Key");

               APIResponse apiResponse = this.responseHelper.unauthorizedError(transactionId);

                String responseStr = objectMapper.writeValueAsString(apiResponse);

               // Write the response body back to the client
               response.getOutputStream().write(responseStr.getBytes());

               response.getOutputStream().flush();

               // Important: Don't call chain.doFilter() to stop further processing of the request
               return;
           }

     //   }

        chain.doFilter(request, response);
    }

}
