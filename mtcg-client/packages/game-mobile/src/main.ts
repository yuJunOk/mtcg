import { createApp } from 'vue'
import { createPinia } from 'pinia'
import '@mtcg/common/styles'
import App from './App.vue'

const app = createApp(App)
app.use(createPinia())
app.mount('#app')