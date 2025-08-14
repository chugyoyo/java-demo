package com.chugyoyo.web.ai.chatcompletion;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ChatFunctionCall {

    String name;
    JsonNode arguments;
}
