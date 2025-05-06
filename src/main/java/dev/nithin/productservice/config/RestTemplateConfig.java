package dev.nithin.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    // This class is used to configure RestTemplate beans if needed in the future.
    // Currently, we are using the default RestTemplate provided by Spring Boot.
    // You can add custom configurations here if required.
    // For example, you can add interceptors, error handlers, etc.
    // Uncomment the following method to create a RestTemplate bean with custom configurations.

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
