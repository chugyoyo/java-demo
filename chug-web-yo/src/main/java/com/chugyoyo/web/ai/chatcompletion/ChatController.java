package com.chugyoyo.web.ai.chatcompletion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/chat")
public class ChatController {

    @Autowired
    private ChatAiClient chatAiClient;

    @PostMapping("chat")
    public String chat(@RequestBody String prompt) {
        return chatAiClient.chat(prompt);
    }
}
