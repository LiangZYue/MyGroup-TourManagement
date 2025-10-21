package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.AiChat;
import com.ruoyi.system.service.IAiChatService;

/**
 * AI对话Controller
 */
@RestController
@RequestMapping("/system/ai-chat")
public class AiChatController extends BaseController {
    @Autowired
    private IAiChatService aiChatService;

    /**
     * 获取对话记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:ai-chat:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiChat aiChat) {
        startPage();
        List<AiChat> list = aiChatService.selectAiChatList(aiChat);
        return getDataTable(list);
    }

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public AjaxResult sendMessage(@RequestBody String message) {
        try {
            Long userId = SecurityUtils.getUserId();
            String response = aiChatService.sendMessage(message, userId);
            return AjaxResult.success("发送成功", response);
        } catch (Exception e) {
            return AjaxResult.error("发送失败：" + e.getMessage());
        }
    }

    /**
     * 获取对话历史
     */
    @GetMapping("/history")
    public AjaxResult getHistory() {
        Long userId = SecurityUtils.getUserId();
        List<AiChat> history = aiChatService.getAiChatHistory(userId);
        return AjaxResult.success(history);
    }

    /**
     * 删除对话记录
     */
    @PreAuthorize("@ss.hasPermi('system:ai-chat:remove')")
    @Log(title = "AI对话", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(aiChatService.deleteAiChatByIds(ids));
    }
}