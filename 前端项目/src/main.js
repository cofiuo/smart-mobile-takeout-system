import { createApp } from 'vue'
import App from './App.vue'
import router from './router/index.js'
import ElementPlus from 'element-plus'
import "/node_modules/element-plus/dist/index.css"
import { getSessionStorage } from '@/common.js';
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// createApp(App).use(router).mount('#app')

const app = createApp(App)

router.beforeEach((to, from, next) => {
    let account = getSessionStorage('account')
    if (to.path == '/businessInfo' || to.path == '/cart' || to.path == '/order') {
        if (account == null) {
            ElMessage({
                message: '请先进行登录操作',
                type: 'warning',
            });
            router.push('/login')
        }
    }
    next()
})

// 全局注册图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.mount('#app')

