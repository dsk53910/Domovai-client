package com.domovai.core;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DomovaiClient {

    private final RestClient restClient;

    public DomovaiClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public AiExchange.Response scanCode(String code, String language) {
        var request = new AiExchange.Request(code, language); // 'var' - вывод типов

        // Использование Fluent API RestClient-а
        return restClient.post()
                .uri("/review")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AiExchange.Response.class);
    }
}
