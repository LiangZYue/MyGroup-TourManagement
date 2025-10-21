package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.AiChat;
import com.ruoyi.system.mapper.AiChatMapper;
import com.ruoyi.system.service.IAiChatService;
import com.ruoyi.system.service.AiApiService;

/**
 * AI对话Service业务层处理
 */
@Service
public class AiChatServiceImpl implements IAiChatService {
    @Autowired
    private AiChatMapper aiChatMapper;

    @Autowired
    private AiApiService aiApiService;

    /**
     * 查询对话记录列表
     *
     * @param aiChat 对话记录
     * @return 对话记录
     */
    @Override
    public List<AiChat> selectAiChatList(AiChat aiChat) {
        return aiChatMapper.selectAiChatList(aiChat);
    }

    /**
     * 发送消息并获取AI回复
     *
     * @param userInput 用户输入
     * @param userId 用户ID
     * @return AI回复内容
     */
    @Override
    public String sendMessage(String userInput, Long userId) throws RuntimeException {
        // 创建对话记录
        AiChat aiChat = new AiChat();
        aiChat.setUserId(userId);
        aiChat.setUserInput(userInput);
        aiChat.setTimestamp(System.currentTimeMillis());

        try {
            // 调用AI API获取回复
            String aiResponse = aiApiService.getAiResponse(userInput);
            
            // 保存对话记录
            aiChat.setAiResponse(aiResponse);
            aiChat.setStatus("0"); // 成功
            aiChatMapper.insertAiChat(aiChat);
            
            return aiResponse;
        } catch (Exception e) {
            aiChat.setStatus("1"); // 失败
            aiChatMapper.insertAiChat(aiChat);
            throw new RuntimeException("调用AI接口失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取用户对话历史
     *
     * @param userId 用户ID
     * @return 对话记录集合
     */
    @Override
    public List<AiChat> getAiChatHistory(Long userId) {
        return aiChatMapper.selectAiChatHistoryByUserId(userId);
    }

    /**
     * 批量删除对话记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteAiChatByIds(Long[] ids) {
        return aiChatMapper.deleteAiChatByIds(ids);
    }
}
