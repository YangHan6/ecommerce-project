package org.example.ec.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiService {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.url}")
    private String apiUrl;

    @Value("${deepseek.model}")
    private String model;

    public String chat(String message) {
        // 1. 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 2. 创建请求体
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("stream", false);

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一个电商网站的智能助手");
        messages.add(systemMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", message);
        messages.add(userMsg);

        body.put("messages", messages);

        // 3. 封装请求
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 4. 发送请求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        // 5. 解析返回结果
        Map responseBody = response.getBody();
        if (responseBody == null) {
            return "AI接口没有返回数据";
        }

        List choices = (List) responseBody.get("choices");
        if (choices == null || choices.isEmpty()) {
            return "AI接口返回结果为空";
        }

        Map firstChoice = (Map) choices.get(0);
        Map messageMap = (Map) firstChoice.get("message");

        if (messageMap == null) {
            return "AI返回格式异常";
        }

        Object content = messageMap.get("content");
        return content == null ? "AI没有回复内容" : content.toString();
    }
}