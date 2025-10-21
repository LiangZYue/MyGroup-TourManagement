package com.ruoyi.system.service;

/**
 * AI API服务接口
 */
public interface AiApiService {
    /**
     * 获取AI回复
     * 
     * @param userInput 用户输入
     * @return AI回复内容
     * @throws Exception 调用异常
     */
    String getAiResponse(String userInput) throws Exception;
}