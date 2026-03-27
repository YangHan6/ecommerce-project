import { createRouter, createWebHistory } from "vue-router"
import Products from "@/components/Products.vue"
import Home from '@/components/Home.vue'
import Cart from "@/components/Cart.vue"
import Login from "@/pages/Login.vue"
import LoginLayout from "@/LoginLayout.vue"
import Register from "@/pages/Register.vue"
import User from "@/pages/User.vue"
import Chatting from "@/components/Chatting.vue"
import Communication from "@/components/Communication.vue"

const routes=[
    {
        path:'/Login',
        name:'login',
        component:Login     // 登录页,不需要导航栏
    },
    {
        path:'/Register',
        name:'register',
        component:Register     // 注册页,不需要导航栏
    },
    {
        path: '/User',
        name: 'user',
        component: User,     // 用户中心页,不需要导航栏
    },
    {
        path: '/',
        component: LoginLayout,  // 带导航栏的布局
        meta: { requiresAuth: true },
        children: [
            { path: '', redirect: '/home' },   // 访问 / 时默认跳到 /home
            { path: 'home', name: 'home', component: Home },
            { path: 'products', name: 'products', component: Products },
            { path: 'cart', name: 'cart', component: Cart },
            { path: 'communication', name: 'communication', component: Communication, meta: { hideFooter: true } },
            { path: 'chatting', name: 'chatting', component: Chatting, meta: { hideFooter: true } }
        ]
    }  
]
//创建路由实例
const router = createRouter({
    history:createWebHistory(),
    routes
})
// 路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    
    if (to.meta.requiresAuth && !token) {
        next('/login')
    } else if (to.path === '/login' && token) {
        next('/home')
    } else {
        next()
    }
})

export default router