<template>
  <div class="home-container">
    <section class="hero-section">
      <!-- 深色模式星星背景 -->
      <div class="stars" v-if="isDark">
        <span v-for="i in 60" :key="i" class="star" :style="randomStar()"></span>
      </div>

      <div class="hero-content">
        <div class="badge">练习商城</div>
        <h1>探索优质商品</h1>
        <p>基于 Vue + SpringBoot 的个人学习项目实战</p>
        <div class="cta-buttons">
          <router-link to="/products" class="btn primary">立即购物</router-link>
          <router-link to="/cart" class="btn secondary">查看购物车</router-link>
        </div>
      </div>
    </section>

    <section class="features">
      <div class="feature-card" v-for="(f, i) in features" :key="i" :style="{ animationDelay: i * 0.1 + 's' }">
        <div class="icon">{{ f.icon }}</div>
        <h3>{{ f.title }}</h3>
        <p>{{ f.desc }}</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const features = [
  { icon: '🚀', title: '快速响应', desc: '基于 Vite + Spring Boot 实现前后端极速交互' },
  { icon: '🔒', title: '安全存储', desc: 'Redis 令牌存储与拦截器保护接口安全' },
  { icon: '🛠️', title: '精简后端', desc: 'MyBatis-Plus 助力高效数据库 ORM 操作' },
]

// 监听主题变化
const isDark = ref(document.documentElement.getAttribute('data-theme') === 'dark')

const observer = new MutationObserver(() => {
  isDark.value = document.documentElement.getAttribute('data-theme') === 'dark'
})

onMounted(() => {
  observer.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] })
})

onUnmounted(() => {
  observer.disconnect()
})

// 随机星星位置和动画
const randomStar = () => {
  const size = Math.random() * 2.5 + 0.5
  return {
    left: Math.random() * 100 + '%',
    top: Math.random() * 100 + '%',
    width: size + 'px',
    height: size + 'px',
    animationDelay: Math.random() * 4 + 's',
    animationDuration: Math.random() * 3 + 2 + 's',
    opacity: Math.random() * 0.7 + 0.3,
  }
}
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.home-container {
  min-height: calc(100vh - 200px);
  font-family: -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: var(--bg-color);
  transition: background 0.3s ease;
}

/* ===== Hero ===== */
.hero-section {
  position: relative;
  padding: 100px 20px;
  text-align: center;
  background: var(--hero-bg, #fff);
  border-bottom: 1px solid var(--border-color);
  animation: fadeUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
  overflow: hidden;
  transition: background 0.3s ease;
}

/* 深色模式 hero 渐变背景 */
[data-theme="dark"] .hero-section {
  background: linear-gradient(135deg, #0d0f1a 0%, #131629 40%, #1a1040 100%);
}

/* 星星 */
.stars {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.star {
  position: absolute;
  background: #fff;
  border-radius: 50%;
  animation: twinkle var(--dur, 3s) ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% { opacity: 0.2; transform: scale(1); }
  50%       { opacity: 1;   transform: scale(1.4); }
}

/* 深色模式下 hero 左上角光晕 */
[data-theme="dark"] .hero-section::before {
  content: '';
  position: absolute;
  top: -80px;
  left: -80px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.2) 0%, transparent 70%);
  pointer-events: none;
  animation: pulse 6s ease-in-out infinite;
}

[data-theme="dark"] .hero-section::after {
  content: '';
  position: absolute;
  bottom: -60px;
  right: -60px;
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, rgba(0, 174, 203, 0.15) 0%, transparent 70%);
  pointer-events: none;
  animation: pulse 8s ease-in-out infinite reverse;
}

@keyframes pulse {
  0%, 100% { transform: scale(1);   opacity: 0.8; }
  50%       { transform: scale(1.2); opacity: 1;   }
}

.hero-content {
  position: relative;
  z-index: 1;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

.badge {
  display: inline-block;
  padding: 4px 14px;
  background: var(--badge-bg, #f0f0f0);
  border-radius: 20px;
  font-size: 12px;
  color: var(--badge-color, #888);
  letter-spacing: 1px;
  margin-bottom: 24px;
  transition: all 0.3s ease;
}

[data-theme="dark"] .badge {
  background: rgba(99, 102, 241, 0.15);
  color: #a5b4fc;
  border: 1px solid rgba(99, 102, 241, 0.3);
}

.hero-content h1 {
  font-size: 3rem;
  font-weight: 700;
  color: var(--text-color);
  letter-spacing: -1px;
  margin-bottom: 16px;
  transition: color 0.3s ease;
}

/* 深色模式标题渐变 */
[data-theme="dark"] .hero-content h1 {
  background: linear-gradient(135deg, #e0e7ff, #a5b4fc, #67e8f9);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-content p {
  font-size: 1rem;
  color: var(--muted-color);
  margin-bottom: 40px;
}

.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.btn {
  padding: 12px 28px;
  border-radius: 10px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn.primary {
  background: var(--text-color);
  color: var(--card-bg);
}

[data-theme="dark"] .btn.primary {
  background: linear-gradient(135deg, #6366f1, #00AECB);
  color: #fff;
  box-shadow: 0 4px 20px rgba(99, 102, 241, 0.4);
}

.btn.primary:hover {
  opacity: 0.85;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.12);
}

[data-theme="dark"] .btn.primary:hover {
  box-shadow: 0 6px 24px rgba(99, 102, 241, 0.6);
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

/* ===== Features ===== */
.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  padding: 60px 10%;
}

.feature-card {
  background: var(--card-bg);
  padding: 32px;
  border-radius: 16px;
  border: 1px solid var(--border-color);
  text-align: center;
  animation: fadeIn 0.4s ease both;
  transition: all 0.25s ease;
}

.feature-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.06);
}

/* 深色模式卡片悬停发光 */
[data-theme="dark"] .feature-card:hover {
  border-color: rgba(99, 102, 241, 0.4);
  box-shadow: 0 8px 30px rgba(99, 102, 241, 0.15);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(12px); }
  to   { opacity: 1; transform: translateY(0); }
}

.feature-card .icon {
  font-size: 2rem;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 10px;
}

.feature-card p {
  font-size: 13px;
  color: var(--muted-color);
  line-height: 1.7;
}
</style>