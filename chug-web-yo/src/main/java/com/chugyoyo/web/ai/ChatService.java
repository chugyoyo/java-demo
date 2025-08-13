package com.chugyoyo.web.ai;

import cn.hutool.core.collection.CollUtil;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;


@Service
public class ChatService {

    private final OpenAiService openAiService;

    public ChatService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public String chat(String prompt) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4o-mini") // 选你要用的模型
                .messages(CollUtil.newArrayList(
                        new ChatMessage("system", "你是一个Spring Boot专家"),
                        new ChatMessage("user", prompt)
                ))
                .maxTokens(500)
                .temperature(0.7)
                .build();

        ChatCompletionResult result = openAiService.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent();
    }
}
