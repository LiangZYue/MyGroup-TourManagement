import request from '@/utils/request'

// 发送消息
export function sendMessage(message) {
  return request({
    url: '/system/ai-chat/send',
    method: 'post',
    data: message
  })
}

// 获取对话历史
export function getHistory() {
  return request({
    url: '/system/ai-chat/history',
    method: 'get'
  })
}

// 删除对话记录
export function deleteChat(ids) {
  return request({
    url: '/system/ai-chat/' + ids,
    method: 'delete'
  })
}