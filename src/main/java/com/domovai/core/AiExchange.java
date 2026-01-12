package com.domovai.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface AiExchange {

    record Request(
        String code,
        String language
    ) {}

    public record Response(
        String review,
        @JsonProperty("bugs_found") boolean bugsFound
    ) {}

}
