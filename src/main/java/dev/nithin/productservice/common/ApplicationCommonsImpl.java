package dev.nithin.productservice.common;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ApplicationCommonsImpl implements ApplicationCommons {

    private final RestTemplate restTemplate;

    public ApplicationCommonsImpl(@Qualifier("getLoadBalancedRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void validateToken(String token) {

        if(token == null || token.isEmpty()) {
            throw new RuntimeException("Invalid token");
        }

        String url = "http://UserService/validate";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, token);

        HttpEntity<Void> responseEntity = new HttpEntity<Void>(headers);

        boolean isValid = restTemplate.postForObject(url, responseEntity, Boolean.class);

        if(Boolean.FALSE.equals(isValid)) {
            throw new RuntimeException("Invalid token");
        }

    }
}
