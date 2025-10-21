import request from '@/utils/request'

// 发送AI问答请求
export function sendAiQuestion(data) {
  return request({
    url: '/system/ai-chat/send',
    method: 'post',
    data: data
  })
}

// 获取聊天历史记录
export function getAiChatHistory() {
  return request({
    url: '/system/ai-chat/history',
    method: 'get'
  })
}