package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.AiChat;

/**
 * AI对话记录Mapper接口
 */
public interface AiChatMapper {
    /**
     * 查询对话记录列表
     *
     * @param aiChat 对话记录
     * @return 对话记录集合
     */
    public List<AiChat> selectAiChatList(AiChat aiChat);

    /**
     * 新增对话记录
     *
     * @param aiChat 对话记录
     * @return 结果
     */
    public int insertAiChat(AiChat aiChat);

    /**
     * 查询用户的对话历史
     *
     * @param userId 用户ID
     * @return 对话记录集合
     */
    public List<AiChat> selectAiChatHistoryByUserId(Long userId);

    /**
     * 批量删除对话记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteAiChatByIds(Long[] ids);
}
