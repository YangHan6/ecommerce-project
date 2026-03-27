import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import elementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './request'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
app.use(elementPlus)
