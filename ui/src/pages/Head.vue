<template>
    <nav class="navbar">
    <div class="logo"></div>

    <!-- 汉堡菜单按钮 -->
    <div class="hamburger" @click="menuOpen = !menuOpen" :class="{ active: menuOpen }">
        <span></span><span></span><span></span>
    </div>

        <ul class="daohanlan" :class="{ open: menuOpen }">
            <li @click="menuOpen = false"><router-link to="/home">主页</router-link></li>
            <li @click="menuOpen = false"><router-link to="/Products">产品</router-link></li>
            <li @click="menuOpen = false"><router-link to="/Cart">购物车</router-link></li>
            <li @click="menuOpen = false"><router-link to="/Communication">交流频道</router-link></li>
            <li @click="menuOpen = false"><router-link to="/Chatting">联系客服</router-link></li>
        </ul>

    <div class="right-area">
        <button class="theme-toggle" @click="toggleTheme">
            <transition name="icon-fade" mode="out-in">
                <span v-if="isDark" key="dark">🌙</span>
                <span v-else key="light">☀️</span>
            </transition>
        </button>

        <div class="profile" @mouseenter="showmenu=true" @mouseleave="showmenu=false">
            <div class="avatar-text" :style="{ background: avatarColor }">{{ username[0]?.toUpperCase() }}</div>
            <transition name="dropdown">
            <div v-if="showmenu" class="dropdown">
                <div class="dropdown-user">
                    <div class="dropdown-avatar" :style="{ background: avatarColor }">{{ username[0]?.toUpperCase() }}</div>
                    <div class="dropdown-name">{{ username }}</div>
                </div>
                <div class="item">
                    <router-link to="/user">个人中心</router-link>
                </div>
                <div class="logout" @click="logout">
                    <router-link to="/login">退出登录</router-link>
                </div>
            </div>
            </transition>
        </div>
    </div>
    </nav>
</template>

<script setup>
import { ref } from 'vue'
import router from '@/router'
import { ElMessage } from 'element-plus'

let showmenu = ref(false)
const menuOpen = ref(false)
const username = ref(localStorage.getItem('username') || '?')
const isDark = ref(localStorage.getItem('theme') === 'dark')
const avatarColor = ref(localStorage.getItem(`avatarColor_${username.value}`) || '#00AECB')

if (isDark.value) {
    document.documentElement.setAttribute('data-theme', 'dark')
}

const toggleTheme = () => {
    isDark.value = !isDark.value
    if (isDark.value) {
        document.documentElement.setAttribute('data-theme', 'dark')
        localStorage.setItem('theme', 'dark')
    } else {
        document.documentElement.removeAttribute('data-theme')
        localStorage.setItem('theme', 'light')
    }
}

let logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('username')
    router.replace('/login')
    ElMessage.success('退出登录成功')
}
</script>

<style>
:root {
    --bg-color: #f9f9f7;
    --text-color: #111;
    --card-bg: #ffffff;
    --border-color: #ebebeb;
    --muted-color: #999;
    --table-header-bg: #fafafa;
    --row-hover-bg: #fafafa;
    --products-bg: #2c2f36;
    --products-filter-bg: #25272E;
    --products-input-bg: #fff;
    --products-input-color: #111;
    --products-card-bg: #fff;
    --products-card-color: #333;
    --products-btn-bg: #2c2f36;
    --products-btn-border: #444;
    --products-btn-color: #ccc;
    --chat-body-bg: linear-gradient(to bottom, #fcfcfc, #f7f7f5);
    --nav-bg: #111111;
    --dropdown-bg: #ffffff;
    --dropdown-user-bg: #f9f9f9;
    --dropdown-border: #eeeeee;
    --dropdown-text: #333333;
    --dropdown-logout-text: #e74c3c;
    --dropdown-item-hover: #f5f5f5;
    --dropdown-logout-hover: #fff0f0;
}

[data-theme="dark"] {
    --bg-color: #0d1117;
    --text-color: #c9d1d9;
    --card-bg: #161b22;
    --border-color: #30363d;
    --muted-color: #8b949e;
    --table-header-bg: #1c2128;
    --row-hover-bg: #1c2128;
    --products-bg: #0d1117;
    --products-filter-bg: #161b22;
    --products-input-bg: #21262d;
    --products-input-color: #c9d1d9;
    --products-card-bg: #161b22;
    --products-card-color: #c9d1d9;
    --products-btn-bg: #21262d;
    --products-btn-border: #30363d;
    --products-btn-color: #8b949e;
    --chat-body-bg: #0d1117;
    --nav-bg: #010409;
    --dropdown-bg: #161b22;
    --dropdown-user-bg: #1c2128;
    --dropdown-border: #30363d;
    --dropdown-text: #c9d1d9;
    --dropdown-logout-text: #f87171;
    --dropdown-item-hover: #1c2128;
    --dropdown-logout-hover: #2a1515;
}

body {
    background-color: var(--bg-color);
    color: var(--text-color);
    transition: background-color 0.3s ease, color 0.3s ease;
}

a { text-decoration: none; }

.navbar {
    display: flex;
    background-color: var(--nav-bg);
    align-items: center;
    position: relative;
    flex-wrap: wrap;
    transition: background-color 0.3s ease;
}
.daohanlan {
    display: flex;
    list-style: none;
    margin: 0;
    padding: 0;
}
.daohanlan li {
    margin: 50px;
    font-size: larger;
    position: relative;
}
.daohanlan li a {
    text-decoration: none;
    color: rgba(255,255,255,0.55);
    padding-bottom: 4px;
    border-bottom: 2px solid transparent;
    transition: color 0.25s ease, border-bottom-color 0.25s ease;
    user-select: none;   /* 取消光标，防止选中文字 */
}
.daohanlan li a:hover,
.daohanlan li a.router-link-active {
    color: #ffffff;
    border-bottom-color: #ffffff;
}

.right-area {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-left: auto;
    padding-right: 30px;
}

.theme-toggle {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    border: 2px solid rgba(255,255,255,0.2);
    background: rgba(255,255,255,0.08);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    transition: all 0.2s ease;
}
.theme-toggle:hover {
    background: rgba(255,255,255,0.15);
    border-color: rgba(255,255,255,0.4);
}

.icon-fade-enter-active,
.icon-fade-leave-active { transition: all 0.15s ease; }
.icon-fade-enter-from { opacity: 0; transform: scale(0.5) rotate(-30deg); }
.icon-fade-leave-to { opacity: 0; transform: scale(0.5) rotate(30deg); }

.profile {
    display: flex;
    position: relative;
    padding: 10px;
}

.avatar-text {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    font-weight: 700;
    cursor: pointer;
    transition: opacity 0.2s ease;
    user-select: none;
    border: 2px solid rgba(255,255,255,0.2);
}
.avatar-text:hover { opacity: 0.85; }

.dropdown {
    position: absolute;
    top: calc(100% + 1px);
    transform: translateX(-50%);
    left: 50%;
    width: 140px;
    background-color: var(--dropdown-bg);
    box-shadow: 0 8px 24px rgba(0,0,0,.25);
    border: 1px solid var(--dropdown-border);
    border-radius: 12px;
    overflow: hidden;
    z-index: 1000;
    transition: background-color 0.3s ease, border-color 0.3s ease;
}

.dropdown-user {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px;
    background: var(--dropdown-user-bg);
    border-bottom: 1px solid var(--dropdown-border);
    transition: background-color 0.3s ease;
}
.dropdown-avatar {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: 700;
    flex-shrink: 0;
}
.dropdown-name {
    font-size: 13px;
    font-weight: 500;
    color: var(--dropdown-text);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.item {
    padding: 12px;
    text-align: center;
    cursor: pointer;
    border-bottom: 1px solid var(--dropdown-border);
    transition: background-color 0.15s ease;
}
.item:hover { background-color: var(--dropdown-item-hover); }
.logout {
    padding: 12px;
    text-align: center;
    cursor: pointer;
    transition: background-color 0.15s ease;
}
.logout:hover { background-color: var(--dropdown-logout-hover); }

.dropdown-enter-active,
.dropdown-leave-active { transition: all 0.2s ease; }
.dropdown-enter-from { opacity: 0; transform: translateX(-50%) translateY(-10px); }
.dropdown-enter-to { opacity: 1; transform: translateX(-50%) translateY(0); }
.dropdown-leave-from { opacity: 1; transform: translateX(-50%) translateY(0); }
.dropdown-leave-to { opacity: 0; transform: translateX(-50%) translateY(-10px); }

.dropdown .item a { color: var(--dropdown-text); }
.dropdown .logout a { color: var(--dropdown-logout-text); }

/* 汉堡按钮 */
.hamburger {
    display: none;
    flex-direction: column;
    gap: 5px;
    cursor: pointer;
    padding: 16px;
    margin-left: 16px;
}
.hamburger span {
    display: block;
    width: 24px;
    height: 2px;
    background: #fff;
    border-radius: 2px;
    transition: all 0.3s ease;
    transform-origin: center;
}
.hamburger.active span:nth-child(1) {
    transform: translateY(7px) rotate(45deg);
}
.hamburger.active span:nth-child(2) {
    opacity: 0;
}
.hamburger.active span:nth-child(3) {
    transform: translateY(-7px) rotate(-45deg);
}

/* 移动端适配 */
@media (max-width: 768px) {
    .hamburger {
        display: flex;
    }
    .daohanlan {
        display: none;
        flex-direction: column;
        width: 100%;
        background: var(--nav-bg);
        padding: 8px 0 16px;
        transition: background-color 0.3s ease;
    }
    .daohanlan.open {
        display: flex;
    }
    .daohanlan li {
        margin: 12px 24px;
    }
    .right-area {
        padding-right: 10px;
    }
}
</style>