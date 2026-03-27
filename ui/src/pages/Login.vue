<template>
  <div class="login-container">
    <div class="login-card">
      <div class="card-header">
        <div class="logo">◆</div>
        <h1>登录</h1>
      </div>

      <div class="form-body">
        <div class="form-item" style="animation-delay: 0s">
          <input type="text" v-model="username" placeholder="用户名" @keyup.enter="handleLogin" />
        </div>
        <div class="form-item" style="animation-delay: 0.07s">
          <input type="password" v-model="password" placeholder="密码" @keyup.enter="handleLogin" />
        </div>

        <button class="submit-btn" @click="handleLogin"  style="animation-delay: 0.14s">
          登录
        </button>

        <p class="register-link" style="animation-delay: 0.21s">
          没有账号？<a @click="handleRegister">注册</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const username = ref('')
const password = ref('')
const router = useRouter()

const handleRegister = () => {
  router.push('/Register')
}

const handleLogin = async () => {
  if (!username.value || !password.value) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  try {
    const res = await axios.post('/api/login', {
      username: username.value,
      password: password.value
    })
  if (res.data.code === 200) {
  const token = res.data.data.token
  localStorage.setItem('token', token)
  localStorage.setItem('role', res.data.data.role)  
  localStorage.setItem('username',res.data.data.username)
  localStorage.setItem('email',res.data.data.email??'未绑定邮箱')
  router.replace('/home')
  ElMessage.success('登录成功')
} else {
      console.log(res.data.message)
      ElMessage.error('用户名或密码错误')
    }
  } catch (error) {
    console.error('登录失败', error)
  }
}
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.login-container {
  min-height: 100vh;
  background: #f9f9f7;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.login-card {
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
  font-size: 28px;
  font-weight: 600;
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
  font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
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

.submit-btn {
  margin-top: 4px;
  width: 100%;
  padding: 13px;
  background: #111;
  border: none;
  border-radius: 10px;
  color: #fff;
  font-size: 14px;
  font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
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

.register-link {
  text-align: center;
  font-size: 13px;
  color: #aaa;
  animation: fadeIn 0.4s ease both;
}

.register-link a {
  color: #111;
  text-decoration: none;
  font-weight: 500;
  cursor: pointer;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>