package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.AiChat;

/**
 * AI对话服务接口
 */
public interface IAiChatService {
    /**
     * 查询对话记录列表
     *
     * @param aiChat 对话记录
     * @return 对话记录集合
     */
    public List<AiChat> selectAiChatList(AiChat aiChat);

    /**
     * 发送消息并获取AI回复
     *
     * @param userInput 用户输入
     * @param userId 用户ID
     * @return AI回复内容
     */
    public String sendMessage(String userInput, Long userId) throws RuntimeException;

    /**
     * 获取用户对话历史
     *
     * @param userId 用户ID
     * @return 对话记录集合
     */
    public List<AiChat> getAiChatHistory(Long userId);

    /**
     * 批量删除对话记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteAiChatByIds(Long[] ids);
}
