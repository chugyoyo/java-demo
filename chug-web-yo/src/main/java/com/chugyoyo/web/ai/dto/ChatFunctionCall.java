package com.chugyoyo.web.ai.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ChatFunctionCall {

    String name;
    JsonNode arguments;
}
