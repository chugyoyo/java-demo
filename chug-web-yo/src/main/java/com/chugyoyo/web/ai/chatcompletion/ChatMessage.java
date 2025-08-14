package com.chugyoyo.web.ai.chatcompletion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessage {

    private String role;
    private String content;
    private String name;
    @JsonProperty("function_call")
    private ChatFunctionCall functionCall;
}
