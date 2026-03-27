<template>
  <div class="register-container">
    <div class="register-card">
      <div class="card-header">
        <div class="logo">◆</div>
        <h1>注册</h1>
      </div>

      <div class="form-body">
        <!-- 用户名 -->
        <div class="form-item" :style="{ animationDelay: '0s' }">
          <input type="text" placeholder="用户名" v-model="registerForm.username" />
        </div>

        <!-- 密码 -->
        <div class="form-item input-eye" :style="{ animationDelay: '0.07s' }">
          <input :type="showPwd ? 'text' : 'password'" placeholder="密码" v-model="registerForm.password" />
          <span class="eye" @click="showPwd = !showPwd">{{ showPwd ? '🙈' : '👁️' }}</span>
        </div>

        <!-- 确认密码 -->
        <div class="form-item input-eye" :style="{ animationDelay: '0.14s' }">
          <input :type="showConfirmPwd ? 'text' : 'password'" placeholder="确认密码" v-model="registerForm.confirmPassword" />
          <span class="eye" @click="showConfirmPwd = !showConfirmPwd">{{ showConfirmPwd ? '🙈' : '👁️' }}</span>
        </div>

        <!-- 邮箱 -->
        <div class="form-item" :style="{ animationDelay: '0.21s' }">
          <input type="email" placeholder="邮箱" v-model="registerForm.email" />
        </div>

        <!-- 验证码 -->
        <div class="form-item code-row" :style="{ animationDelay: '0.28s' }">
          <input
            type="text"
            placeholder="验证码"
            v-model="registerForm.verifyCode"
            maxlength="6"
          />
          <button class="code-btn" @click="getVerifyCode" :disabled="countdown > 0">
            {{ countdown > 0 ? countdown + 's' : '发送' }}
          </button>
        </div>

        <span class="error-tip" v-if="errorMsg">{{ errorMsg }}</span>

        <button class="submit-btn" @click="handleRegister" :style="{ animationDelay: '0.35s' }">
          注册
        </button>

        <p class="login-link" :style="{ animationDelay: '0.42s' }">
          已有账号？<a href="/login">登录</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import router from '@/router'
import axios from 'axios'

const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  verifyCode: ''
})

const countdown = ref(0)
const errorMsg = ref('')
const showPwd = ref(false)
const showConfirmPwd = ref(false)

const getVerifyCode = async () => {
  if (!registerForm.value.username || !registerForm.value.password) {
    ElMessage.warning('用户名和密码不能为空')
    return
  }
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    ElMessage.warning('两次密码不一致')
    return
  }
  try {
    const res = await axios.post('/api/sendCode', {
      email: registerForm.value.email,
      username: registerForm.value.username,
      verifyCode: registerForm.value.verifyCode
    })
    console.log(res.data)
    if (res.data.code == 200) {
      ElMessage.success(res.data.message)
      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) clearInterval(timer)
      }, 1000)
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('发送失败，请稍后重试')
  }
}

const handleRegister = async () => {
  errorMsg.value = ''
  if (!registerForm.value.username || !registerForm.value.password) {
    ElMessage.warning('用户名和密码不能为空')
    return
  }
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    ElMessage.warning('两次密码不一致')
    return
  }
  if (!registerForm.value.email) {
    ElMessage.warning('请输入邮箱')
    return
  }
  if (!registerForm.value.verifyCode) {
    ElMessage.warning('请输入验证码')
    return
  }
  try {
    const res = await axios.post('/api/register', registerForm.value)
    console.log(res.data)
    if (res.data.code == 200) {
      ElMessage.success('注册成功')
      router.push('/login')
      errorMsg.value = ''
    } else {
      errorMsg.value = res.data.message
    }
  } catch (e) {
    ElMessage.error('注册失败，请稍后重试')
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=DM+Sans:wght@300;400;500&family=DM+Serif+Display&display=swap');

* { box-sizing: border-box; margin: 0; padding: 0; }

.error-tip {
  font-size: 12px;
  color: #e74c3c;
  padding-left: 2px;
}

.register-container {
  min-height: 100vh;
  background: #f9f9f7;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'DM Sans', sans-serif;
}

.register-card {
  width: 380px;
  background: #fff;
  border: 1px solid #ebebeb;
  border-radius: 16px;
  padding: 48px 40px 40px;
  animation: fadeUp 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

.card-header {
  margin-bottom: 32px;
}

.logo {
  font-size: 18px;
  color: #111;
  margin-bottom: 16px;
  display: inline-block;
  animation: spin 12s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
}

h1 {
  font-family: 'DM Serif Display', serif;
  font-size: 28px;
  font-weight: 400;
  color: #111;
  letter-spacing: -0.5px;
}

.form-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-item {
  animation: fadeIn 0.4s ease both;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to   { opacity: 1; transform: translateY(0); }
}

input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #e8e8e8;
  border-radius: 10px;
  font-size: 14px;
  font-family: 'DM Sans', sans-serif;
  color: #111;
  background: #fafafa;
  outline: none;
  transition: all 0.2s ease;
}

input:focus {
  border-color: #111;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(0,0,0,0.05);
}

input::placeholder {
  color: #bbb;
}

/* 眼睛 */
.input-eye {
  position: relative;
}

.input-eye input {
  padding-right: 40px;
}

.eye {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  font-size: 15px;
  user-select: none;
}

.code-row {
  display: flex;
  gap: 8px;
}

.code-row input {
  flex: 1;
}

.code-btn {
  flex-shrink: 0;
  padding: 0 16px;
  border: 1px solid #e8e8e8;
  border-radius: 10px;
  background: #fafafa;
  font-size: 13px;
  font-family: 'DM Sans', sans-serif;
  color: #111;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.code-btn:hover:not(:disabled) {
  background: #111;
  color: #fff;
  border-color: #111;
}

.code-btn:disabled {
  color: #bbb;
  cursor: not-allowed;
}

.submit-btn {
  margin-top: 4px;
  width: 100%;
  padding: 13px;
  background: #111;
  border: none;
  border-radius: 10px;
  color: #fff;
  font-size: 14px;
  font-family: 'DM Sans', sans-serif;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all 0.2s ease;
  animation: fadeIn 0.4s ease both;
}

.submit-btn:hover {
  background: #333;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.12);
}

.submit-btn:active {
  transform: translateY(0);
}

.login-link {
  text-align: center;
  font-size: 13px;
  color: #aaa;
  animation: fadeIn 0.4s ease both;
}

.login-link a {
  color: #111;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>