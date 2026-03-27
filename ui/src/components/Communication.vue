<template>
  <div class="comm-page">
    <div class="bg-grid"></div>
    <div class="bg-orb orb1"></div>
    <div class="bg-orb orb2"></div>

    <div class="comm-layout">
      <aside class="sidebar">
        <div class="sidebar-header">
          <div class="server-icon">🛒</div>
          <div>
            <div class="server-name">电商社区</div>
            <div class="server-sub">{{ onlineCount }} 人在线</div>
          </div>
        </div>

        <div class="channel-section">
          <div class="section-label">文字频道</div>
          <div
            v-for="ch in channels"
            :key="ch.id"
            class="channel-item"
            :class="{ active: activeChannel.id === ch.id }"
            @click="switchChannel(ch)"
          >
            <span class="ch-hash">{{ ch.icon }}</span>
            <span class="ch-name">{{ ch.name }}</span>
            <span v-if="ch.unread" class="ch-badge">{{ ch.unread }}</span>
          </div>
        </div>

        <div class="channel-section">
          <div class="section-label">在线用户</div>
          <div class="user-item" v-for="u in onlineUsers" :key="u.name">
            <div class="user-dot" :class="u.status"></div>
            <span class="user-name">{{ u.name }}</span>
          </div>
        </div>
      </aside>

      <main class="chat-main">
        <div class="chat-topbar">
          <div class="topbar-left">
            <span class="topbar-hash">{{ activeChannel.readonly ? '📢' : '#' }}</span>
            <span class="topbar-name">{{ activeChannel.name }}</span>
            <span class="topbar-divider">|</span>
            <span class="topbar-desc">{{ activeChannel.desc }}</span>
          </div>
          <div class="topbar-right">
            <span v-if="activeChannel.readonly" class="readonly-badge">只读频道</span>
            <button class="icon-btn">🔍</button>
            <button class="icon-btn">👥</button>
          </div>
        </div>

        <div class="messages-wrap" ref="messagesRef">
          <div class="channel-welcome">
            <div class="welcome-icon">{{ activeChannel.icon }}</div>
            <h3>欢迎来到 {{ activeChannel.name }}</h3>
            <p>{{ activeChannel.desc }}</p>
            <div v-if="activeChannel.readonly" class="readonly-tip">
              📌 这是公告频道，只有管理员可以发送消息
            </div>
          </div>

          <!-- ✅ 消息列表 -->
          <div
              v-for="(msg, i) in activeMessages"
              :key="msg.id"
              class="msg-row"
              :class="{ 'msg-self': msg.isSelf }"
            >
              <template v-if="!msg.isSelf">
                <div class="msg-avatar" :style="{ background: msg.color }">{{ msg.author[0] }}</div>
                <div class="msg-body">
                  <div class="msg-meta">
                    <span class="msg-author" :style="{ color: msg.color }">{{ msg.author }}</span>
                    <span class="msg-time">{{ msg.time }}</span>
                  </div>
                  <div class="msg-bubble other" :style="{ background: msg.color + '22', borderColor: msg.color + '44' }" v-html="formatMsg(msg.text)"></div>
                </div>
              </template>

              <template v-else>
                <div class="msg-body msg-body-self">
                  <div class="msg-meta msg-meta-self">
                    <span class="msg-time">{{ msg.time }}</span>
                    <span class="msg-author self-name">{{ msg.author }}</span>
                  </div>
                  <div class="msg-bubble self" :style="{ background: currentColor }" v-html="formatMsg(msg.text)"></div>
                </div>
                <div class="msg-avatar" :style="{ background: msg.color }">{{ msg.author[0] }}</div>
              </template> 
            </div>

          <div v-if="isTyping" class="typing-indicator">
            <span></span><span></span><span></span>
            <em>有人正在输入...</em>
          </div>
        </div>

        <div v-if="activeChannel.readonly" class="readonly-bar">
          <span>📢 这是公告频道，无法发送消息</span>
        </div>

        <div v-else class="input-wrap">
          <div class="input-box">
            <button v-if="activeChannel.id !== 2" class="attach-btn">＋</button>
            <textarea
              v-model="inputText"
              :placeholder="`在 #${activeChannel.name} 发送消息`"
              @keydown.enter.exact.prevent="sendMessage"
              @input="onInput"
              rows="1"
              ref="inputRef"
            ></textarea>
            <div class="input-actions">
              <div class="emoji-picker-wrap">
                <button class="emoji-btn" @click.stop="toggleEmojiPicker">😊</button>
                <transition name="emoji-pop">
                  <div v-if="showEmojiPicker" class="emoji-picker" @click.stop>
                    <div class="emoji-grid">
                      <button v-for="e in emojiList" :key="e" class="emoji-option" @click="insertEmoji(e)">{{ e }}</button>
                    </div>
                  </div>
                </transition>
              </div>
              <button class="send-btn" @click="sendMessage" :disabled="!inputText.trim()">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M2 21L23 12 2 3v7l15 2-15 2z"/>
                </svg>
              </button>
            </div>
          </div>
          <div class="input-hint">按 Enter 发送，Shift+Enter 换行</div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import axios from 'axios'

const channels = ref([
  { id: 1, name: '公告',    desc: '平台最新公告和活动信息', unread: 0, readonly: true,  icon: '📢' },
  { id: 2, name: '世界频道', desc: '随便聊点什么吧',         unread: 0, readonly: false, icon: '💬' },
  { id: 3, name: '商品推荐', desc: '分享你发现的好物',       unread: 0, readonly: false, icon: '🛍️' },
  { id: 4, name: '买家秀',  desc: '晒出你的购物战果',       unread: 0, readonly: false, icon: '📸' },
  { id: 5, name: '问题反馈', desc: '遇到问题在这里反馈',     unread: 0, readonly: false, icon: '🙋' },
])

const onlineUsers = ref([])
const activeChannel = ref(channels.value[0])
const allMessages = ref({ 1: [], 2: [], 3: [], 4: [], 5: [] })
const activeMessages = computed(() => allMessages.value[activeChannel.value.id] || [])

const currentUser = localStorage.getItem('username') || '我'

// ✅ 颜色跟账号绑定，用用户名作为 key
const currentColor = ref(localStorage.getItem(`avatarColor_${currentUser}`) || '#00AECB')

const messagesRef = ref(null)
const scrollToBottom = () => {
  if (messagesRef.value) messagesRef.value.scrollTop = messagesRef.value.scrollHeight
}

const loadHistory = async (channelId) => {
  try {
    const res = await axios.get(`/api/messages/${channelId}`)
    allMessages.value[channelId] = (res.data.data || []).map(m => ({
      id: m.id,
      author: m.author,
      color: m.color || '#00AECB',
      text: m.content,
      time: m.createTime?.substring(5, 16).replace('T', ' ') || '',
      isSelf: m.author === currentUser
    }))
  } catch (e) {
    console.error('加载历史消息失败', e)
  }
}

let ws = null
const connectWS = () => {
  const token = localStorage.getItem('token')
  const wsHost = window.location.hostname
  ws = new WebSocket(`ws://localhost:8080/ws/chat?token=${token}`)
  ws.onopen = () => console.log('WebSocket 连接成功')
  ws.onmessage = (event) => {
    const msg = JSON.parse(event.data)
    // ✅ 在线人数更新
    if (msg.type === 'onlineCount') {
        onlineCount.value = msg.count
        return
    }
    const msgs = allMessages.value[msg.channelId]
    if (msgs) {
      msgs.push({
        id: Date.now(),
        author: msg.author,
        color: msg.color,
        text: msg.content,
        time: msg.time,
        isSelf: msg.author === currentUser
      })
      if (activeChannel.value.id === msg.channelId) {
        nextTick(scrollToBottom)
      } else {
        const ch = channels.value.find(c => c.id === msg.channelId)
        if (ch) ch.unread++
      }
    }
  }
  ws.onerror = (err) => console.error('WebSocket 错误:', err)
  ws.onclose = () => { console.log('断开，3秒后重连...'); setTimeout(connectWS, 3000) }
}

const showEmojiPicker = ref(false)
const closeEmojiPicker = () => { showEmojiPicker.value = false }
const toggleEmojiPicker = () => { showEmojiPicker.value = !showEmojiPicker.value }

const emojiList = [
  '😊','😂','🥰','😍','🤩','😎','🥳',
  '👍','👏','🙌','❤️','🔥','😱','😜',
  '😭','😅','🤔','😴','🤣','😡','🥺',
  '🛒','💻','📱','🎮','📦','🎉','✨',
  '🌟','💎','🎁','🏆','🌈','💰','💯',
]

const inputRef = ref(null)
const inputText = ref('')
const isTyping = ref(false)
let typingTimer = null

const insertEmoji = (emoji) => {
  inputText.value += emoji
  showEmojiPicker.value = false
  inputRef.value?.focus()
}

onMounted(async () => {
  document.addEventListener('click', closeEmojiPicker)
  for (const ch of channels.value) {
    await loadHistory(ch.id)
  }
  nextTick(scrollToBottom)
  connectWS()
})

onUnmounted(() => {
  document.removeEventListener('click', closeEmojiPicker)
  ws?.close()
})

const switchChannel = async (ch) => {
  activeChannel.value = ch
  ch.unread = 0
  showEmojiPicker.value = false
  await loadHistory(ch.id)
  nextTick(scrollToBottom)
}

const formatMsg = (text) => {
  if (!text) return ''
  return text
    .replace(/#(\S+)/g, '<span class="mention-tag">#$1</span>')
    .replace(/@(\S+)/g, '<span class="mention-user">@$1</span>')
}

const onInput = () => {
  isTyping.value = true
  clearTimeout(typingTimer)
  typingTimer = setTimeout(() => { isTyping.value = false }, 2000)
}

const sendMessage = () => {
  const text = inputText.value.trim()
  if (!text || activeChannel.value.readonly) return
  if (!ws || ws.readyState !== WebSocket.OPEN) { console.warn('WebSocket 未连接'); return }
  ws.send(JSON.stringify({
    channelId: activeChannel.value.id,
    author: currentUser,
    content: text,
    color: currentColor.value
  }))
  inputText.value = ''
  isTyping.value = false
  clearTimeout(typingTimer)
  inputRef.value?.focus()
}

const onlineCount = ref(0)
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.comm-page {
  position: relative;
  min-height: 100vh;
  font-family: 'PingFang SC', 'Microsoft YaHei', 'Hiragino Sans GB', sans-serif;
  overflow: hidden;
  transition: background 0.3s ease;
  --comm-bg: #f0f2f5;
  --comm-sidebar: #f7f8fc;
  --comm-main: #ffffff;
  --comm-topbar: #ffffff;
  --comm-input-bg: #f0f2f5;
  --comm-msg-hover: rgba(0,0,0,0.02);
  --comm-border: rgba(0,0,0,0.06);
  --comm-text: #2d3142;
  --comm-text-muted: #8b92b4;
  --comm-active: rgba(0,174,203,0.1);
  --comm-active-text: #0097b2;
  --comm-section: #b0b8d4;
  --comm-picker-bg: #ffffff;
  --comm-picker-shadow: 0 8px 32px rgba(0,0,0,0.12);
  --bubble-other-bg: #f0f2f5;
  --bubble-other-text: #2d3142;
  background: var(--comm-bg);
}

[data-theme="dark"] .comm-page {
  --comm-bg: #0e1117;
  --comm-sidebar: #13161f;
  --comm-main: #181c27;
  --comm-topbar: #13161f;
  --comm-input-bg: #1e2235;
  --comm-msg-hover: rgba(255,255,255,0.03);
  --comm-border: rgba(255,255,255,0.06);
  --comm-text: #d4d8e8;
  --comm-text-muted: #6b7394;
  --comm-active: rgba(0,174,203,0.15);
  --comm-active-text: #00AECB;
  --comm-section: #3a4060;
  --comm-picker-bg: #1e2235;
  --comm-picker-shadow: 0 8px 32px rgba(0,0,0,0.4);
  --bubble-other-bg: #252a3a;
  --bubble-other-text: #d4d8e8;
  background: var(--comm-bg);
}

.bg-grid {
  position: absolute; inset: 0;
  background-image: linear-gradient(var(--comm-border) 1px, transparent 1px),
                    linear-gradient(90deg, var(--comm-border) 1px, transparent 1px);
  background-size: 32px 32px;
  pointer-events: none; z-index: 0;
}

.bg-orb { position: absolute; border-radius: 50%; filter: blur(80px); pointer-events: none; z-index: 0; }
.orb1 { width: 400px; height: 400px; top: -100px; right: -100px; background: rgba(0,174,203,0.08); }
.orb2 { width: 300px; height: 300px; bottom: -80px; left: 200px; background: rgba(164,120,250,0.06); }

.comm-layout { position: relative; z-index: 1; display: flex; height: 100vh; }

.sidebar {
  width: 240px; min-width: 240px;
  background: var(--comm-sidebar);
  border-right: 1px solid var(--comm-border);
  display: flex; flex-direction: column; overflow-y: auto;
}

.sidebar-header { display: flex; align-items: center; gap: 12px; padding: 20px 16px; border-bottom: 1px solid var(--comm-border); }
.server-icon { width: 44px; height: 44px; background: linear-gradient(135deg, #00AECB, #6366f1); border-radius: 14px; display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0; }
.server-name { font-weight: 600; font-size: 15px; color: var(--comm-text); }
.server-sub { font-size: 12px; color: var(--comm-text-muted); margin-top: 2px; }
.channel-section { padding: 16px 8px 8px; }
.section-label { font-size: 11px; font-weight: 600; letter-spacing: 0.8px; text-transform: uppercase; color: var(--comm-section); padding: 0 8px; margin-bottom: 4px; }
.channel-item { display: flex; align-items: center; gap: 8px; padding: 7px 10px; border-radius: 8px; cursor: pointer; transition: all 0.15s; color: var(--comm-text-muted); font-size: 14px; margin-bottom: 1px; }
.channel-item:hover { background: var(--comm-msg-hover); color: var(--comm-text); }
.channel-item.active { background: var(--comm-active); color: var(--comm-active-text); font-weight: 500; }
.ch-hash { font-size: 14px; opacity: 0.7; font-weight: 600; }
.ch-name { flex: 1; }
.ch-badge { background: #e74c3c; color: #fff; font-size: 10px; font-weight: 700; padding: 1px 6px; border-radius: 10px; }
.user-item { display: flex; align-items: center; gap: 10px; padding: 6px 10px; border-radius: 8px; font-size: 13px; color: var(--comm-text-muted); margin-bottom: 1px; }
.user-dot { width: 9px; height: 9px; border-radius: 50%; flex-shrink: 0; }
.user-dot.online { background: #23d18b; }
.user-dot.idle { background: #f0a500; }
.user-dot.dnd { background: #e74c3c; }

.chat-main { flex: 1; display: flex; flex-direction: column; background: var(--comm-main); overflow: hidden; }

.chat-topbar { display: flex; align-items: center; justify-content: space-between; padding: 0 20px; height: 56px; border-bottom: 1px solid var(--comm-border); background: var(--comm-topbar); flex-shrink: 0; }
.topbar-left { display: flex; align-items: center; gap: 10px; }
.topbar-hash { font-size: 18px; font-weight: 700; color: var(--comm-text-muted); }
.topbar-name { font-weight: 600; font-size: 15px; color: var(--comm-text); }
.topbar-divider { color: var(--comm-border); font-size: 18px; }
.topbar-desc { font-size: 13px; color: var(--comm-text-muted); }
.topbar-right { display: flex; align-items: center; gap: 8px; }
.readonly-badge { font-size: 11px; padding: 3px 10px; background: rgba(240,165,0,0.12); color: #f0a500; border-radius: 20px; font-weight: 500; border: 1px solid rgba(240,165,0,0.2); }
.icon-btn { width: 34px; height: 34px; border: none; background: transparent; border-radius: 8px; cursor: pointer; font-size: 16px; transition: background 0.15s; color: var(--comm-text-muted); }
.icon-btn:hover { background: var(--comm-msg-hover); }

.messages-wrap { flex: 1; overflow-y: auto; padding: 20px 16px; scroll-behavior: smooth; }
.messages-wrap::-webkit-scrollbar { width: 6px; }
.messages-wrap::-webkit-scrollbar-thumb { background: var(--comm-border); border-radius: 3px; }

.channel-welcome { padding: 32px 16px 24px; margin-bottom: 16px; border-bottom: 1px solid var(--comm-border); }
.welcome-icon { width: 64px; height: 64px; background: linear-gradient(135deg, rgba(0,174,203,0.15), rgba(99,102,241,0.15)); border: 2px solid var(--comm-border); border-radius: 16px; display: flex; align-items: center; justify-content: center; font-size: 28px; font-weight: 700; color: var(--comm-active-text); margin-bottom: 16px; }
.channel-welcome h3 { font-size: 22px; font-weight: 700; color: var(--comm-text); margin-bottom: 6px; }
.channel-welcome p { font-size: 14px; color: var(--comm-text-muted); }
.readonly-tip { margin-top: 14px; padding: 10px 14px; background: rgba(240,165,0,0.08); border: 1px solid rgba(240,165,0,0.2); border-radius: 10px; font-size: 13px; color: #f0a500; display: inline-block; }

/* ✅ 消息行 */
.msg-row {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  margin-bottom: 12px;
  padding: 0 4px;
}

/* ✅ 自己的消息靠右 */
.msg-row.msg-self {
  justify-content: flex-end;
}

.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  color: #fff;
  flex-shrink: 0;
}

.msg-body {
  display: flex;
  flex-direction: column;
  max-width: 65%;
  gap: 4px;
}

.msg-body-self {
  align-items: flex-end;
}

.msg-meta {
  display: flex;
  align-items: baseline;
  gap: 6px;
  padding: 0 4px;
}

.msg-meta-self {
  justify-content: flex-end;
}

.msg-author { font-weight: 600; font-size: 13px; }
.self-name { color: #00AECB; }
.msg-time { font-size: 11px; color: var(--comm-text-muted); }

/* ✅ 消息气泡 */
.msg-bubble {
  padding: 10px 14px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
}

/* 他人气泡：左下角直角 */
.msg-bubble.other {
  color: var(--bubble-other-text);
  border-bottom-left-radius: 4px;
}

/* 自己气泡：右下角直角，青色 */
.msg-bubble.self {
  color: #fff;
  border-bottom-right-radius: 4px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .comm-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    min-width: unset;
    max-height: 200px;
    border-right: none;
    border-bottom: 1px solid var(--comm-border);
    overflow-y: auto;
  }

  .channel-section {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    padding: 8px;
  }

  .section-label {
    width: 100%;
  }

  .channel-item {
    padding: 5px 10px;
    font-size: 13px;
  }

  .user-item {
    display: none;
  }

  .sidebar-header {
    padding: 12px 16px;
  }

  .chat-main {
    flex: 1;
    min-height: 0;
  }

  .topbar-desc {
    display: none;
  }

  .topbar-divider {
    display: none;
  }

  .msg-body {
    max-width: 80%;
  }

  .messages-wrap {
    padding: 12px 8px;
  }

  .input-wrap {
    padding: 8px 10px 12px;
  }

  .emoji-picker {
    right: -60px;
    width: 240px;
  }

  .emoji-grid {
    grid-template-columns: repeat(6, 1fr);
  }
}

:deep(.mention-tag) { background: rgba(0,174,203,0.15); color: #00AECB; padding: 1px 6px; border-radius: 4px; font-size: 13px; font-weight: 500; }
:deep(.mention-user) { background: rgba(99,102,241,0.15); color: #818cf8; padding: 1px 6px; border-radius: 4px; font-size: 13px; font-weight: 500; }

.typing-indicator { display: flex; align-items: center; gap: 6px; padding: 8px 16px; font-size: 13px; color: var(--comm-text-muted); }
.typing-indicator span { width: 6px; height: 6px; background: var(--comm-text-muted); border-radius: 50%; animation: bounce 1.2s ease-in-out infinite; }
.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }
@keyframes bounce { 0%, 60%, 100% { transform: translateY(0); } 30% { transform: translateY(-5px); } }

.readonly-bar { padding: 16px 20px; background: rgba(240,165,0,0.06); border-top: 1px solid rgba(240,165,0,0.15); font-size: 14px; color: #f0a500; text-align: center; flex-shrink: 0; }

.input-wrap { padding: 12px 16px 16px; flex-shrink: 0; }
.input-box { display: flex; align-items: center; gap: 8px; background: var(--comm-input-bg); border: 1px solid var(--comm-border); border-radius: 14px; padding: 10px 12px; transition: border-color 0.2s; }
.input-box:focus-within { border-color: #00AECB; }
.attach-btn { width: 30px; height: 30px; border: none; background: transparent; font-size: 20px; font-weight: 300; color: var(--comm-text-muted); cursor: pointer; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; transition: color 0.15s; padding-bottom: 2px; }
.attach-btn:hover { color: #00AECB; }
.input-box textarea { flex: 1; border: none; background: transparent; resize: none; font-size: 14px; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; color: var(--comm-text); outline: none; line-height: 1.5; max-height: 120px; overflow-y: auto; align-self: center; }
.input-box textarea::placeholder { color: var(--comm-text-muted); }
.input-actions { display: flex; gap: 4px; align-items: center; flex-shrink: 0; }
.emoji-picker-wrap { position: relative; }
.emoji-btn { width: 30px; height: 30px; border: none; background: transparent; font-size: 18px; cursor: pointer; border-radius: 8px; opacity: 0.7; transition: opacity 0.15s, transform 0.15s; display: flex; align-items: center; justify-content: center; }
.emoji-btn:hover { opacity: 1; transform: scale(1.15); }
.emoji-picker { position: absolute; bottom: 42px; right: 0; width: 280px; background: var(--comm-picker-bg); border: 1px solid var(--comm-border); border-radius: 14px; padding: 12px; box-shadow: var(--comm-picker-shadow); z-index: 100; }
.emoji-grid { display: grid; grid-template-columns: repeat(7, 1fr); gap: 4px; }
.emoji-option { width: 32px; height: 32px; border: none; background: transparent; font-size: 18px; cursor: pointer; border-radius: 8px; display: flex; align-items: center; justify-content: center; transition: background 0.1s, transform 0.1s; }
.emoji-option:hover { background: var(--comm-active); transform: scale(1.2); }
.emoji-pop-enter-active, .emoji-pop-leave-active { transition: all 0.15s ease; }
.emoji-pop-enter-from { opacity: 0; transform: scale(0.9) translateY(8px); }
.emoji-pop-leave-to { opacity: 0; transform: scale(0.9) translateY(8px); }
.send-btn { width: 32px; height: 32px; border: none; background: #00AECB; color: #fff; border-radius: 9px; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.15s; }
.send-btn:hover:not(:disabled) { background: #0097b2; transform: translateY(-1px); }
.send-btn:disabled { background: var(--comm-border); cursor: not-allowed; }
.input-hint { font-size: 11px; color: var(--comm-text-muted); margin-top: 6px; padding-left: 4px; }
</style>