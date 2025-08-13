package com.chugyoyo.web.ai.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatCompletionRequest {

    String model;
    List<ChatMessage> messages;
}
