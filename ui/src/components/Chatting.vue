<template>
  <div class="ai-page">
    <section class="chat-shell">
      <div class="chat-header">
        <div class="header-left">
          <div class="ai-avatar">🤖</div>
          <div>
            <h2>AI 助手</h2>
            <p>欢迎来到你的智能助手</p>
          </div>
        </div>
        <div class="status">{{ loading ? '思考中...' : '在线' }}</div>
      </div>

      <div class="chat-body" ref="chatBodyRef">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          class="message-row"
          :class="msg.role"
        >
          <div class="message-bubble">
            <div class="message-text">{{ msg.content }}</div>
          </div>
        </div>
      </div>

      <div class="chat-footer">
        <textarea
          v-model="inputText"
          class="chat-input"
          placeholder="输入你的问题..."
          @keydown.enter.exact.prevent="sendMessage"
        ></textarea>

        <div class="footer-actions">
          <button class="btn secondary" @click="clearChat">清空对话</button>
          <button class="btn primary" @click="sendMessage" :disabled="loading || !inputText.trim()">
            {{ loading ? '发送中...' : '发送' }}
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue'
import axios from 'axios'

const inputText = ref('')
const loading = ref(false)
const chatBodyRef = ref(null)

// 初始化时从 localStorage 读取历史记录，没有则使用默认欢迎消息
const messages = ref(
  JSON.parse(localStorage.getItem('chat_messages') || 'null') || [
    {
      role: 'assistant',
      content: '你好，我是你的 AI 助手，有什么可以帮你的吗？'
    }
  ]
)

// 封装保存函数，每次消息变化时调用
const saveMessages = () => {
  localStorage.setItem('chat_messages', JSON.stringify(messages.value))
}

const scrollToBottom = async () => {
  await nextTick()
  if (chatBodyRef.value) {
    chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
  }
}

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  // 推入用户消息并保存
  messages.value.push({
    role: 'user',
    content: text
  })
  saveMessages() // 保存用户消息

  inputText.value = ''
  await scrollToBottom()

  loading.value = true

  try {
    const res = await axios.post('/api/ai/chat', {
      message: text
    })
    const aiReply = res.data?.data || '暂时没有获取到回复'

    // 推入 AI 回复并保存
    messages.value.push({
      role: 'assistant',
      content: aiReply
    })
    saveMessages() // 保存 AI 回复
  } catch (error) {
    // 推入错误提示并保存
    messages.value.push({
      role: 'assistant',
      content: '请求失败，请检查后端接口是否启动'
    })
    saveMessages() // 保存错误提示
    console.log(error)
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const clearChat = () => {
  messages.value = [
    {
      role: 'assistant',
      content: '你好，我是你的 AI 助手，有什么可以帮你的吗？'
    }
  ]
  saveMessages() // 清空后也同步更新 localStorage
  scrollToBottom()
}
</script>

<style scoped>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.ai-page {
  min-height: calc(100vh - 200px);
  padding: 32px 20px;
  background: var(--bg-color);
  font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.chat-shell {
  width: 100%;
  max-width: 1000px;
  height: 78vh;
  margin: 0 auto;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.04);
  animation: fadeUp 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}

.chat-header {
  height: 80px;
  padding: 0 24px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--card-bg);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.ai-avatar {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  background: var(--text-color);
  color: var(--card-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.header-left h2 {
  font-size: 18px;
  color: var(--text-color);
  margin-bottom: 4px;
}

.header-left p {
  font-size: 13px;
  color: var(--muted-color);
}

.status {
  font-size: 13px;
  color: var(--muted-color);
  background: var(--table-header-bg);
  padding: 6px 12px;
  border-radius: 999px;
}

.chat-body {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: var(--bg-color);
}

.message-row {
  display: flex;
  margin-bottom: 16px;
}

.message-row.user {
  justify-content: flex-end;
}

.message-row.assistant {
  justify-content: flex-start;
}

.message-bubble {
  max-width: 72%;
  padding: 14px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.8;
  word-break: break-word;
  white-space: pre-wrap;
}

.message-row.user .message-bubble {
  background: var(--text-color);
  color: var(--card-bg);
  border-bottom-right-radius: 6px;
}

.message-row.assistant .message-bubble {
  background: var(--card-bg);
  color: var(--text-color);
  border: 1px solid var(--border-color);
  border-bottom-left-radius: 6px;
}

.chat-footer {
  border-top: 1px solid var(--border-color);
  padding: 16px 20px 20px;
  background: var(--card-bg);
}

.chat-input {
  width: 100%;
  min-height: 90px;
  max-height: 180px;
  resize: vertical;
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 14px 16px;
  font-size: 14px;
  line-height: 1.7;
  outline: none;
  transition: all 0.2s ease;
  font-family: inherit;
  background: var(--card-bg);
  color: var(--text-color);
}

.chat-input:focus {
  border-color: var(--text-color);
  box-shadow: 0 0 0 3px rgba(17, 17, 17, 0.06);
}

.footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 14px;
}

.btn {
  padding: 11px 24px;
  border-radius: 12px;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn.primary {
  background: var(--text-color);
  color: var(--card-bg);
}

.btn.primary:hover:not(:disabled) {
  opacity: 0.8;
  transform: translateY(-1px);
}

.btn.primary:disabled {
  background: var(--muted-color);
  cursor: not-allowed;
}

.btn.secondary {
  background: var(--card-bg);
  color: var(--text-color);
  border: 1px solid var(--border-color);
}

.btn.secondary:hover {
  border-color: var(--text-color);
  transform: translateY(-1px);
}

.chat-body::-webkit-scrollbar {
  width: 8px;
}

.chat-body::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 10px;
}

.chat-body::-webkit-scrollbar-track {
  background: transparent;
}
</style>