<template>
  <div class="user-container">
    <div class="user-layout">

      <!-- 左侧导航 -->
      <aside class="user-sidebar">
        <div class="sidebar-avatar-wrap">
          <div class="sidebar-avatar-text" :style="{ background: avatarColor }">
            {{ username[0]?.toUpperCase() }}
          </div>
          <div class="sidebar-username">{{ username }}</div>
          <div class="sidebar-email">{{ email }}</div>
        </div>
        <nav class="sidebar-nav">
          <div
            v-for="item in navItems"
            :key="item.key"
            class="nav-item"
            :class="{ active: activeTab === item.key }"
            @click="activeTab = item.key"
          >
            <span class="nav-icon">{{ item.icon }}</span>
            <span>{{ item.label }}</span>
          </div>
        </nav>
      </aside>

      <!-- 右侧内容 -->
      <main class="user-main">

        <!-- 绑定信息 -->
        <div v-if="activeTab === 'bind'" class="section-card">
          <div class="section-title">绑定信息</div>
          <div class="bind-list">
            <div class="bind-item" v-for="b in bindList" :key="b.key">
              <div class="bind-left">
                <span class="bind-icon">{{ b.icon }}</span>
                <div>
                  <div class="bind-name">{{ b.name }}</div>
                  <div class="bind-status">{{ b.status }}</div>
                </div>
              </div>
              <button
                class="bind-btn"
                :class="{ bound: b.bound }"
                @click="b.bound ? null : openComingSoon(b.name)"
              >
                {{ b.bound ? '已绑定' : '去绑定' }}
              </button>
            </div>
          </div>
        </div>

        <!-- 安全与隐私 -->
        <div v-if="activeTab === 'security'" class="section-card">
          <div class="section-title">安全与隐私</div>
          <div class="form-body">
            <div class="input-eye">
              <input :type="showOld ? 'text' : 'password'" v-model="pwdForm.oldPassword" placeholder="旧密码" />
              <span class="eye" @click="showOld = !showOld">{{ showOld ? '🙈' : '👁️' }}</span>
            </div>
            <div class="input-eye">
              <input :type="showNew ? 'text' : 'password'" v-model="pwdForm.newPassword" placeholder="新密码" />
              <span class="eye" @click="showNew = !showNew">{{ showNew ? '🙈' : '👁️' }}</span>
            </div>
            <div class="input-eye">
              <input :type="showConfirm ? 'text' : 'password'" v-model="pwdForm.confirmPassword" placeholder="确认新密码" />
              <span class="eye" @click="showConfirm = !showConfirm">{{ showConfirm ? '🙈' : '👁️' }}</span>
            </div>
            <span class="error-tip" v-if="pwdError">{{ pwdError }}</span>
            <button class="action-btn" @click="updatePassword">更新密码</button>
          </div>

          <div class="divider"></div>
          <div class="section-subtitle">更新邮箱</div>
          <div class="form-body">
            <input type="email" v-model="emailForm.newEmail" placeholder="邮箱地址" />
            <div class="code-row">
              <input type="text" v-model="emailForm.code" placeholder="验证码" maxlength="6" />
              <button class="code-btn" @click="sendEmailCode" :disabled="countdown > 0">
                {{ countdown > 0 ? countdown + 's' : '发送' }}
              </button>
            </div>
            <span class="error-tip" v-if="emailError">{{ emailError }}</span>
            <button class="action-btn" @click="updateEmail">更新邮箱</button>
          </div>

          <div class="divider"></div>
          <div class="section-subtitle">隐私设置</div>
          <div class="privacy-list">
            <div class="privacy-item" v-for="p in privacyList" :key="p.key">
              <div>
                <div class="privacy-name">{{ p.name }}</div>
                <div class="privacy-desc">{{ p.desc }}</div>
              </div>
              <div class="toggle" :class="{ on: p.enabled }" @click="p.enabled = !p.enabled">
                <div class="toggle-thumb"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 编辑个人资料 -->
        <div v-if="activeTab === 'profile'" class="section-card">
          <div class="section-title">编辑个人资料</div>
          <div class="avatar-edit-wrap">
            <div class="avatar-edit-text" :style="{ background: avatarColor }">
              {{ username[0]?.toUpperCase() }}
            </div>
            <div class="avatar-edit-right">
              <div class="avatar-edit-label">头像颜色</div>
              <div class="color-picker-row">
                <div
                  v-for="c in colorOptions"
                  :key="c"
                  class="color-dot"
                  :style="{ background: c }"
                  :class="{ selected: avatarColor === c }"
                  @click="changeColor(c)"
                ></div>
              </div>
              <div class="avatar-edit-hint">选择你喜欢的颜色</div>
            </div>
          </div>

          <div class="form-body" style="margin-top: 24px;">
            <div class="form-row">
              <label>用户名</label>
              <!-- 用户名不允许修改，置灰显示 -->
              <input type="text" v-model="profileForm.username" disabled style="opacity:0.5;cursor:not-allowed" />
            </div>
            <div class="form-row">
              <label>个性签名</label>
              <input type="text" v-model="profileForm.bio" placeholder="介绍一下自己" />
            </div>
            <div class="form-row">
              <label>所在地区</label>
              <input type="text" v-model="profileForm.location" placeholder="城市" />
            </div>
            <button class="action-btn" @click="saveProfile">保存修改</button>
          </div>
        </div>

        <!-- 关于版本 -->
        <div v-if="activeTab === 'about'" class="section-card">
          <div class="section-title">关于版本</div>
          <div class="about-list">
            <div class="about-item" v-for="a in aboutList" :key="a.label">
              <span class="about-label">{{ a.label }}</span>
              <span class="about-value">{{ a.value }}</span>
            </div>
          </div>
          <div class="divider"></div>
          <div class="about-tech">
            <div class="section-subtitle">技术栈</div>
            <div class="tech-tags">
              <span class="tech-tag" v-for="t in techStack" :key="t">{{ t }}</span>
            </div>
          </div>
          <div class="divider"></div>
          <div class="about-links">
            <button class="link-btn" @click="openComingSoon('检查更新')">🔄 检查更新</button>
            <button class="link-btn" @click="openComingSoon('用户协议')">📄 用户协议</button>
            <button class="link-btn" @click="openComingSoon('隐私政策')">🔒 隐私政策</button>
          </div>
        </div>

      </main>
    </div>

    <el-dialog v-model="showComingSoon" width="360px" :title="comingSoonTitle">
      <div class="coming-soon-body">
        <div class="coming-soon-icon">🚧</div>
        <p>该功能正在开发中，敬请期待！</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="showComingSoon = false">知道了</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const username = ref(localStorage.getItem('username') || '用户')
const email = ref(localStorage.getItem('email') || '未绑定邮箱')

const colorOptions = [
  '#00AECB', '#6366f1', '#f472b6', '#34d399',
  '#fb923c', '#a78bfa', '#f87171', '#38bdf8',
  '#4ade80', '#facc15', '#e879f9', '#2dd4bf',
  '#ef4444', '#3b82f6', '#10b981', '#f59e0b',
]

const avatarColor = ref(
  localStorage.getItem(`avatarColor_${username.value}`) || '#00AECB'
)

const changeColor = (color) => {
  avatarColor.value = color
  localStorage.setItem(`avatarColor_${username.value}`, color)
  ElMessage.success('头像颜色已更新')
}

const activeTab = ref('bind')

const navItems = [
  { key: 'bind',     icon: '🔗', label: '绑定信息' },
  { key: 'security', icon: '🔒', label: '安全与隐私' },
  { key: 'profile',  icon: '✏️', label: '编辑个人资料' },
  { key: 'about',    icon: 'ℹ️', label: '关于版本' },
]

// 初始化时从 localStorage 读取绑定状态
const bindStorage = JSON.parse(localStorage.getItem('bindInfo') || '{}')

const bindList = ref([
  { key: 'phone',  icon: '📱', name: '手机号', status: bindStorage.phone  || '未绑定', bound: !!bindStorage.phone  },
  { key: 'wechat', icon: '💬', name: '微信',   status: bindStorage.wechat || '未绑定', bound: !!bindStorage.wechat },
  { key: 'alipay', icon: '💰', name: '支付宝', status: bindStorage.alipay || '未绑定', bound: !!bindStorage.alipay },
  { key: 'email',  icon: '📧', name: '邮箱',   status: bindStorage.email  || '未绑定', bound: !!bindStorage.email  },
])

// 统一绑定方法，更新 localStorage 和页面状态
const saveBind = (key, value) => {
  const storage = JSON.parse(localStorage.getItem('bindInfo') || '{}')
  storage[key] = value
  localStorage.setItem('bindInfo', JSON.stringify(storage))
  const item = bindList.value.find(b => b.key === key)
  if (item) { item.bound = true; item.status = value }
}

const privacyList = ref([
  { key: 'showOrder',  name: '公开订单记录', desc: '其他用户可以看到你的购买记录', enabled: false },
  { key: 'showReview', name: '公开评价内容', desc: '你的商品评价将对所有人可见',   enabled: true  },
  { key: 'allowMsg',   name: '允许陌生人私信', desc: '非好友可以向你发送消息',     enabled: false },
])

// 初始化时从 localStorage 读取 bio 和 location
const profileForm = ref({
  username: localStorage.getItem('username') || '',
  bio:      localStorage.getItem('bio')      || '',
  location: localStorage.getItem('location') || '',
})

const aboutList = [
  { label: '当前版本', value: 'v1.0.0' },
  { label: '前端框架', value: 'Vue 3 + Vite' },
  { label: '后端框架', value: 'Spring Boot 3' },
  { label: '数据库',   value: 'MySQL 8 + Redis' },
  { label: '构建时间', value: '2026-03-18' },
]

const techStack = ['Vue 3', 'Vite', 'Element Plus', 'Spring Boot', 'MyBatis-Plus', 'Redis', 'MySQL', 'WebSocket']

const showComingSoon = ref(false)
const comingSoonTitle = ref('')
const openComingSoon = (name) => {
  comingSoonTitle.value = name
  showComingSoon.value = true
}

const pwdError = ref('')
const showOld = ref(false)
const showNew = ref(false)
const showConfirm = ref(false)
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const updatePassword = async () => {
  pwdError.value = ''
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword || !pwdForm.value.confirmPassword) {
    pwdError.value = '请填写完整信息'; return
  }
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) {
    pwdError.value = '两次密码不一致'; return
  }
  const reg = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[^a-zA-Z0-9]).{8,}$/
  if (!reg.test(pwdForm.value.newPassword)) {
    pwdError.value = '至少8位，需包含字母、数字和特殊符号'; return
  }
  try {
    const res = await axios.post('/api/user/updatePassword', pwdForm.value, {
      headers: { Authorization: localStorage.getItem('token') }
    })
    if (res.data.code === 200) {
      ElMessage.success('密码更新成功')
      pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    } else {
      pwdError.value = res.data.msg
    }
  } catch {
    ElMessage.error('更新密码失败')
  }
}

const emailError = ref('')
const countdown = ref(0)
const emailForm = ref({ newEmail: '', code: '' })

const sendEmailCode = async () => {
  emailError.value = ''
  if (!emailForm.value.newEmail) { emailError.value = '请输入新邮箱'; return }
  try {
    const res = await axios.post('/api/user/sendBindEmailCode', { email: emailForm.value.newEmail })
    if (res.data.code === 200) {
      ElMessage.success('验证码发送成功')
      countdown.value = 60
      const timer = setInterval(() => { countdown.value--; if (countdown.value <= 0) clearInterval(timer) }, 1000)
    } else {
      emailError.value = res.data.msg
    }
  } catch {
    ElMessage.error('发送验证码失败')
  }
}

const updateEmail = async () => {
  emailError.value = ''
  if (!emailForm.value.newEmail || !emailForm.value.code) {
    emailError.value = '请填写完整信息'; return
  }
  try {
    const res = await axios.post('/api/user/updateEmail', {
      email: emailForm.value.newEmail,
      code: emailForm.value.code
    }, {
      headers: { Authorization: localStorage.getItem('token') }
    })
    if (res.data.code === 200) {
      ElMessage.success('邮箱绑定成功')
      saveBind('email', emailForm.value.newEmail)
      email.value = emailForm.value.newEmail
      localStorage.setItem('email', emailForm.value.newEmail)
      emailForm.value = { newEmail: '', code: '' }
    } else {
      emailError.value = res.data.msg
    }
  } catch {
    ElMessage.error('绑定失败')
  }
}

// 保存个人资料
const saveProfile = async () => {
  try {
    const res = await axios.post('/api/user/updateProfile', {
      bio: profileForm.value.bio,
      location: profileForm.value.location
    }, {
      headers: { Authorization: localStorage.getItem('token') }
    })
    if (res.data.code === 200) {
      ElMessage.success('保存成功')
      // 同步到 localStorage，刷新页面不丢失
      localStorage.setItem('bio', profileForm.value.bio)
      localStorage.setItem('location', profileForm.value.location)
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch {
    ElMessage.error('保存失败')
  }
}
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.user-container {
  min-height: 100vh;
  background: var(--bg-color, #f9f9f7);
  padding: 40px 20px;
  font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.user-layout {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.user-sidebar {
  width: 220px;
  flex-shrink: 0;
  background: var(--card-bg, #fff);
  border: 1px solid var(--border-color, #ebebeb);
  border-radius: 16px;
  padding: 24px 16px;
  position: sticky;
  top: 40px;
}

.sidebar-avatar-wrap {
  text-align: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-color, #f0f0f0);
}

.sidebar-avatar-text {
  width: 72px; height: 72px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 28px; font-weight: 700; color: #fff;
  margin: 0 auto 10px; transition: background 0.3s ease;
}

.sidebar-username { font-size: 15px; font-weight: 600; color: var(--text-color, #111); margin-bottom: 4px; }
.sidebar-email { font-size: 12px; color: var(--muted-color, #999); word-break: break-all; }
.sidebar-nav { display: flex; flex-direction: column; gap: 2px; }

.nav-item {
  display: flex; align-items: center; gap: 10px;
  padding: 10px 12px; border-radius: 10px; cursor: pointer;
  font-size: 14px; color: var(--muted-color, #666); transition: all 0.15s;
}
.nav-item:hover { background: var(--row-hover-bg, #f5f5f5); color: var(--text-color, #111); }
.nav-item.active { background: rgba(0,174,203,0.1); color: #0097b2; font-weight: 500; }
.nav-icon { font-size: 16px; }

.user-main { flex: 1; min-width: 0; }

.section-card {
  background: var(--card-bg, #fff);
  border: 1px solid var(--border-color, #ebebeb);
  border-radius: 16px; padding: 32px;
  animation: fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(12px); }
  to   { opacity: 1; transform: translateY(0); }
}

.section-title { font-size: 18px; font-weight: 600; color: var(--text-color, #111); margin-bottom: 24px; }
.section-subtitle { font-size: 14px; font-weight: 600; color: var(--text-color, #111); margin-bottom: 14px; }
.divider { height: 1px; background: var(--border-color, #f0f0f0); margin: 24px 0; }

.bind-list { display: flex; flex-direction: column; gap: 12px; }
.bind-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px; border: 1px solid var(--border-color, #ebebeb);
  border-radius: 12px; transition: border-color 0.2s;
}
.bind-item:hover { border-color: #00AECB; }
.bind-left { display: flex; align-items: center; gap: 14px; }
.bind-icon { font-size: 24px; }
.bind-name { font-size: 14px; font-weight: 500; color: var(--text-color, #111); margin-bottom: 2px; }
.bind-status { font-size: 12px; color: var(--muted-color, #999); }
.bind-btn {
  padding: 7px 16px; border-radius: 8px; font-size: 13px; font-family: inherit;
  cursor: pointer; border: 1px solid var(--border-color, #e8e8e8);
  background: var(--card-bg, #fff); color: var(--text-color, #111); transition: all 0.15s;
}
.bind-btn:hover { background: #111; color: #fff; border-color: #111; }
.bind-btn.bound { color: #00AECB; border-color: #00AECB; cursor: default; }
.bind-btn.bound:hover { background: var(--card-bg, #fff); color: #00AECB; border-color: #00AECB; }

.privacy-list { display: flex; flex-direction: column; gap: 16px; }
.privacy-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 0; border-bottom: 1px solid var(--border-color, #f5f5f5);
}
.privacy-name { font-size: 14px; color: var(--text-color, #111); margin-bottom: 2px; }
.privacy-desc { font-size: 12px; color: var(--muted-color, #999); }
.toggle { width: 44px; height: 24px; border-radius: 12px; background: #ddd; position: relative; cursor: pointer; transition: background 0.2s; flex-shrink: 0; }
.toggle.on { background: #00AECB; }
.toggle-thumb { width: 18px; height: 18px; background: #fff; border-radius: 50%; position: absolute; top: 3px; left: 3px; transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1); box-shadow: 0 1px 3px rgba(0,0,0,0.2); }
.toggle.on .toggle-thumb { transform: translateX(20px); }

.avatar-edit-wrap {
  display: flex; align-items: center; gap: 24px; padding: 20px;
  background: var(--row-hover-bg, #fafafa); border-radius: 12px;
  border: 1px solid var(--border-color, #ebebeb);
}
.avatar-edit-text {
  width: 80px; height: 80px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 32px; font-weight: 700; color: #fff;
  flex-shrink: 0; transition: background 0.3s ease;
}
.avatar-edit-right { flex: 1; }
.avatar-edit-label { font-size: 13px; font-weight: 600; color: var(--text-color, #111); margin-bottom: 10px; }
.color-picker-row { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 8px; }
.color-dot { width: 28px; height: 28px; border-radius: 50%; cursor: pointer; transition: transform 0.15s, box-shadow 0.15s; border: 2px solid transparent; }
.color-dot:hover { transform: scale(1.15); }
.color-dot.selected { border-color: var(--text-color, #111); box-shadow: 0 0 0 2px var(--card-bg, #fff), 0 0 0 4px var(--text-color, #111); transform: scale(1.1); }
.avatar-edit-hint { font-size: 11px; color: var(--muted-color, #999); }

.form-row { display: flex; flex-direction: column; gap: 6px; }
.form-row label { font-size: 13px; color: var(--muted-color, #666); font-weight: 500; }

.about-list { display: flex; flex-direction: column; gap: 14px; }
.about-item { display: flex; justify-content: space-between; align-items: center; font-size: 14px; }
.about-label { color: var(--muted-color, #666); }
.about-value { color: var(--text-color, #111); font-weight: 500; }
.tech-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.tech-tag { padding: 4px 12px; background: rgba(0,174,203,0.1); color: #0097b2; border-radius: 20px; font-size: 12px; font-weight: 500; }
.about-links { display: flex; gap: 10px; flex-wrap: wrap; }
.link-btn { padding: 9px 16px; border: 1px solid var(--border-color, #e8e8e8); border-radius: 10px; background: var(--card-bg, #fff); font-size: 13px; font-family: inherit; color: var(--text-color, #111); cursor: pointer; transition: all 0.15s; }
.link-btn:hover { border-color: #111; transform: translateY(-1px); }

.form-body { display: flex; flex-direction: column; gap: 10px; }

input { width: 100%; padding: 11px 14px; border: 1px solid var(--border-color, #e8e8e8); border-radius: 10px; font-size: 14px; font-family: inherit; color: var(--text-color, #111); background: var(--bg-color, #fafafa); outline: none; transition: all 0.2s; }
input:focus { border-color: #111; background: var(--card-bg, #fff); box-shadow: 0 0 0 3px rgba(0,0,0,0.05); }
input::placeholder { color: var(--muted-color, #bbb); }

.input-eye { position: relative; }
.input-eye input { padding-right: 40px; }
.eye { position: absolute; right: 12px; top: 50%; transform: translateY(-50%); cursor: pointer; font-size: 15px; user-select: none; }

.code-row { display: flex; gap: 8px; }
.code-row input { flex: 1; }
.code-btn { flex-shrink: 0; padding: 0 16px; border: 1px solid var(--border-color, #e8e8e8); border-radius: 10px; background: var(--bg-color, #fafafa); font-size: 13px; font-family: inherit; color: var(--text-color, #111); cursor: pointer; transition: all 0.2s; white-space: nowrap; }
.code-btn:hover:not(:disabled) { background: #111; color: #fff; border-color: #111; }
.code-btn:disabled { color: var(--muted-color, #bbb); cursor: not-allowed; }

.action-btn { padding: 11px; background: #111; border: none; border-radius: 10px; color: #fff; font-size: 14px; font-family: inherit; cursor: pointer; transition: all 0.2s; margin-top: 4px; }
.action-btn:hover { background: #333; transform: translateY(-1px); box-shadow: 0 4px 12px rgba(0,0,0,0.12); }

.error-tip { font-size: 12px; color: #e74c3c; padding-left: 2px; }

.coming-soon-body { text-align: center; padding: 20px 0; }
.coming-soon-icon { font-size: 48px; margin-bottom: 12px; }
.coming-soon-body p { font-size: 14px; color: var(--muted-color, #666); }
</style>