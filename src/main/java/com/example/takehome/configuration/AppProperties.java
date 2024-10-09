package com.example.takehome.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties("app")
@Getter
@Setter
public class AppProperties {

    private long databaseTimeout;

    private String xapiKey;
}
