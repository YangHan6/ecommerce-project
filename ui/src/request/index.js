import axios from 'axios'
import router from '../router'
import { ElMessage } from 'element-plus'

axios.defaults.baseURL = ''

// 请求拦截器 ✅ 把 token 加回来
axios.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = token
    }
    return config
})

// 响应拦截器
axios.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401) {
            const token = localStorage.getItem('token')
            if (token) {
                localStorage.removeItem('token')
                ElMessage.error('登录已过期，请重新登录')
            }
            router.replace('/Login')
        }
        return Promise.reject(error)
    }
)

export default axios