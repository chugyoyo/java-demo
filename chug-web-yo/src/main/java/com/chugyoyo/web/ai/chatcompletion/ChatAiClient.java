package com.chugyoyo.web.ai.chatcompletion;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class ChatAiClient {

    @Value("${chat-ai.api-key}")
    private String apiKey;

    @Value("${chat-ai.base-url}")
    private String baseUrl;

    @Value("${chat-ai.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(Arrays.asList(
                        ChatMessage.builder().role("user").content(prompt).build()
                ))
                .build();

        HttpEntity<ChatCompletionRequest> request = new HttpEntity<>(chatCompletionRequest, headers);
        // 安全起见，不打印 headers 权限信息
        log.info("chat baseUrl [{}], request: {}", baseUrl, JSON.toJSONString(chatCompletionRequest));
        String postResult = restTemplate.postForObject(
                baseUrl,
                request,
                String.class
        );
        log.info("chat response {}", postResult);
        return Optional.ofNullable(postResult)
                .map(s -> JSON.parseObject(s, ChatCompletionResult.class))
                .map(result -> {
                    if (result.getChoices() != null && !result.getChoices().isEmpty()) {
                        return result.getChoices().get(0);
                    }
                    return null;
                })
                .map(ChatCompletionChoice::getMessage)
                .map(ChatMessage::getContent)
                .orElse(null);
    }
}

