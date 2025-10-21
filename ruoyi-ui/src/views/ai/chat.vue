<template>
  <div class="chat-page"> <!-- 新增外层容器 -->
    <div class="chat-container">
      <!-- 聊天窗口头部 -->
      <div class="chat-header">
        <el-row>
          <el-col :span="20">
            <h3>AI 问答助手</h3>
          </el-col>
          <el-col :span="4" class="text-right">
            <el-button 
              icon="el-icon-refresh" 
              size="mini" 
              @click="handleClear"
            ></el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 聊天消息区域 -->
      <div class="chat-messages" ref="messageContainer">
        <!-- 系统提示 -->
        <div class="system-message" v-if="messages.length === 0">
          请输入问题，AI 将为您解答...
        </div>

        <!-- 消息列表 -->
        <div 
          class="message-item" 
          v-for="(msg, index) in messages" 
          :key="index"
          :class="{ 'user-message': msg.isUser, 'ai-message': !msg.isUser }"
        >
          <div class="avatar">
            <img 
              :src="msg.isUser ? userAvatar : aiAvatar" 
              alt="头像"
            >
          </div>
          <div class="message-content">
            <div class="sender">{{ msg.isUser ? '我' : 'AI 助手' }}</div>
            <div class="content" v-html="msg.content"></div>
            <div class="time">{{ parseTime(msg.timestamp) }}</div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <el-input
          v-model="inputContent"
          type="textarea"
          :rows="3"
          placeholder="请输入消息..."
          @keyup.enter.native="handleSend"
        ></el-input>
        <div class="send-btn">
          <el-button 
            type="primary" 
            @click="handleSend"
            :disabled="!inputContent.trim()"
          >
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'
import { sendAiQuestion, getAiChatHistory } from '@/api/ai/chat'

export default {
  name: 'Chat',
  data() {
    return {
      messages: [],
      inputContent: '',
      userAvatar: require('@/assets/images/user.png'),
      aiAvatar: require('@/assets/images/ai.png'),
      loading: false,
      userId: undefined // 添加用户ID字段
    }
  },
  methods: {
    parseTime,
    async handleSend() {
      const content = this.inputContent.trim()
      if (!content) return

      this.messages.push({
        isUser: true,
        content,
        timestamp: new Date()
      })
      this.inputContent = ''
      this.scrollToBottom()

      this.loading = true
      try {
        const response = await sendAiQuestion(content)
        if (response.code === 200) {
          this.messages.push({
            isUser: false,
            content: response.data,
            timestamp: new Date()
          })
        } else {
          throw new Error(response.msg)
        }
        this.scrollToBottom()
      } catch (error) {
        this.$message.error(error.message || '请求失败，请重试')
      } finally {
        this.loading = false
      }
    },
    handleClear() {
      this.$confirm('确定要清空聊天记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.messages = []
      })
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messageContainer
        container.scrollTop = container.scrollHeight
      })
    },
    async loadHistory() {
      try {
        const res = await getAiChatHistory()
        if (res.code === 200) {
          this.messages = res.data.map(item => ({
            isUser: item.userId === this.userId,
            content: item.isUser ? item.userInput : item.aiResponse,
            timestamp: new Date(item.createTime)
          }))
        }
      } catch (error) {
        console.error('加载历史记录失败:', error)
      }
    }
  },
  async mounted() {
    await this.loadHistory()
    this.scrollToBottom()
  }
}
</script>

<style scoped>
/* 关键：设置外层容器占满浏览器窗口 */
.chat-page {
  position: relative; /* 固定定位，脱离文档流 */
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 0;
  margin: 0;
  overflow: hidden; /* 隐藏窗口外内容 */
}

.chat-container {
  width: 100%;
  height: calc(100vh - 100px); /* 高度占满视口 */
  margin-left: 0; 
  display: flex;
  flex-direction: column;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  overflow: hidden;
  box-sizing: border-box; /* 避免边框导致溢出 */
}

.chat-header {
  padding: 12px 20px;
  border-bottom: 1px solid #e6e6e6;
  background-color: #f5f7fa;
  /* 固定头部高度，不随内容拉伸 */
  height: 60px;
  box-sizing: border-box;
}

.chat-messages {
  flex: 1; /* 占满剩余空间 */
  padding: 20px;
  overflow-y: auto;
  background-color: #fff;
  box-sizing: border-box;
}

.system-message {
  text-align: center;
  color: #999;
  padding: 20px 0;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
}

.user-message {
  flex-direction: row-reverse;
}

.avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.user-message .avatar img {
  margin-right: 0;
  margin-left: 10px;
}

.message-content {
  max-width: 70%;
}

.user-message .message-content {
  text-align: right;
}

.sender {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.content {
  padding: 10px 15px;
  border-radius: 6px;
  line-height: 1.5;
}

.user-message .content {
  background-color: #409eff;
  color: #fff;
}

.ai-message .content {
  background-color: #f5f5f5;
  color: #333;
}

.time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.chat-input {
  padding: 15px;
  border-top: 1px solid #e6e6e6;
  background-color: #f5f7fa;
  /* 固定输入区高度，不随内容拉伸 */
  height: auto;
  min-height: 120px;
  box-sizing: border-box;
}

.send-btn {
  margin-top: 10px;
  text-align: right;
}
</style>