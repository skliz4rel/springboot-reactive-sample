package com.example.takehome.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;

@Configuration
public class SwaggerConfiguration {

  @Bean
  public OpenAPI springOpenAPI() {
    return new OpenAPI()
            .info(new Info().title("takehome-api").description("takehome-api").version("1.0")
                    .license(new License().name("SAMPLE").url("https://mytakeapi.com/")));
  }

  @Bean
  public LinkRelationProvider linkRelationProvider() {
    return new DefaultLinkRelationProvider();
  }
}
