package com.chugyoyo.web.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
public class ChatCompletionResult {

    private String id;
    private List<ChatCompletionChoice> choices;
    private Usage usage;
    private String object;
    private long created;
    private String model;

    @Data
    public static class Usage {
        @JsonProperty("prompt_tokens")
        long promptTokens;
        @JsonProperty("completion_tokens")
        long completionTokens;
        @JsonProperty("total_tokens")
        long totalTokens;
    }
}
