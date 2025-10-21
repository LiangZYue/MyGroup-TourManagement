package com.ruoyi.system.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import com.ruoyi.system.service.AiApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 阿里云 DashScope API 服务实现
 */
@Service
public class AiApiServiceImpl implements AiApiService {
    private static final Logger log = LoggerFactory.getLogger(AiApiServiceImpl.class);
    
    @Value("${ai.aliyun.api-key}")
    private String apiKey;

    @Value("${ai.aliyun.endpoint}")
    private String endpoint;
    
    @Value("${ai.aliyun.model:qwen-turbo}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 调用通义千问API获取回复
     *
     * @param userInput 用户输入
     * @return AI回复文本
     */
    @Override
    public String getAiResponse(String userMessage) {
        try {
            // 构建请求体 - 使用标准DashScope格式
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            // 构建messages
            List<Map<String, String>> messages = new ArrayList<>();
            
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个专业的旅游助手，专注于旅游相关的问答。");
            messages.add(systemMessage);
            
            Map<String, String> userMessageMap = new HashMap<>();
            userMessageMap.put("role", "user");
            userMessageMap.put("content", userMessage);
            messages.add(userMessageMap);
            
            Map<String, Object> input = new HashMap<>();
            input.put("messages", messages);
            requestBody.put("input", input);
            
            // 参数配置
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("result_format", "message");
            parameters.put("max_tokens", 1500);
            parameters.put("temperature", 0.7);
            parameters.put("top_p", 0.8);
            requestBody.put("parameters", parameters);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            log.info("发送请求到: {}", endpoint);
            log.info("请求头: {}", headers);
            log.info("请求体: {}", requestBody);
            
            // 发送请求
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            log.info("响应状态: {}", response.getStatusCode());
            log.info("响应体: {}", response.getBody());
            
            // 解析响应
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> body = response.getBody();
                if (body.containsKey("output")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> output = (Map<String, Object>) body.get("output");
                    if (output != null && output.containsKey("choices")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");
                        if (choices != null && !choices.isEmpty()) {
                            Map<String, Object> choice = choices.get(0);
                            @SuppressWarnings("unchecked")
                            Map<String, String> message = (Map<String, String>) choice.get("message");
                            if (message != null && message.containsKey("content")) {
                                return message.get("content");
                            }
                        }
                    }
                }
                // 检查是否有错误
                if (body.containsKey("code")) {
                    throw new RuntimeException("AI服务返回错误: " + body.get("message"));
                }
            }
            
            return "抱歉，AI服务暂时无法响应。";
            
        } catch (Exception e) {
            log.error("AI服务调用失败", e);
            return "抱歉，AI服务调用失败: " + e.getMessage();
        }
    }
}