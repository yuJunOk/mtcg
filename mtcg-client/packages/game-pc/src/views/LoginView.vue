<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NCheckbox, NForm, NFormItem, NInput } from 'naive-ui'
import { authApi, useThemeStore, useUserStore } from '@mtcg/common'
import { toast } from '@/feedback'

const USERCODE_BASE = 100_000
const REMEMBER_KEY = 'mtcg_remember_usercode'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const theme = useThemeStore()

const loading = ref(false)
const showRegister = ref(false)
const registerHint = ref('')
const formError = ref('')

const loginForm = reactive({
  usercode: localStorage.getItem(REMEMBER_KEY) || '',
  password: '',
  remember: !!localStorage.getItem(REMEMBER_KEY),
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
})

const redirectPath = computed(() => {
  const raw = route.query.redirect
  return typeof raw === 'string' && raw.startsWith('/') ? raw : '/'
})

async function handleLogin(): Promise<void> {
  formError.value = ''
  registerHint.value = ''
  if (!loginForm.usercode.trim()) {
    formError.value = '请输入玩家编号'
    toast.warning('请输入玩家编号')
    return
  }
  if (!loginForm.password) {
    formError.value = '请输入密码'
    toast.warning('请输入密码')
    return
  }
  try {
    await userStore.login(
      { usercode: loginForm.usercode.trim(), password: loginForm.password },
      loading,
    )
    if (loginForm.remember) {
      localStorage.setItem(REMEMBER_KEY, loginForm.usercode.trim())
    } else {
      localStorage.removeItem(REMEMBER_KEY)
    }
    toast.success('登录成功')
    await router.replace(redirectPath.value)
  } catch {
    // HTTP 错误由全局 notifier 提示
  }
}

async function handleRegister(): Promise<void> {
  formError.value = ''
  registerHint.value = ''
  if (!registerForm.password || registerForm.password.length < 6) {
    formError.value = '密码至少 6 位'
    toast.warning('密码至少 6 位')
    return
  }
  if (registerForm.password !== registerForm.confirmPassword) {
    formError.value = '两次密码不一致'
    toast.warning('两次密码不一致')
    return
  }
  try {
    const userId = await authApi.register(
      {
        password: registerForm.password,
        username: registerForm.username.trim() || undefined,
      },
      loading,
    )
    const usercode = String(USERCODE_BASE + userId)
    await userStore.login({ usercode, password: registerForm.password }, loading)
    loginForm.usercode = usercode
    registerHint.value = `注册成功，玩家编号 ${usercode}，请妥善保存`
    localStorage.setItem(REMEMBER_KEY, usercode)
    toast.success(`注册成功，玩家编号 ${usercode}`)
    await router.replace(redirectPath.value)
  } catch {
    // HTTP 错误由全局 notifier 提示
  }
}
</script>

<template>
  <div class="page">
    <aside class="brand-pane">
      <p class="mark">MARVEL</p>
      <h1>超英击战</h1>
      <p class="tag">构筑超英卡组，击战多元宇宙。</p>
    </aside>

    <section class="form-pane">
      <n-button
        quaternary
        circle
        class="theme-btn"
        :title="theme.theme === 'dark' ? '切换亮色' : '切换暗色'"
        @click="theme.toggle()"
      >
        {{ theme.theme === 'dark' ? '☀️' : '🌙' }}
      </n-button>

      <div class="panel">
        <header class="head">
          <h2>登录</h2>
          <p>使用玩家编号进入游戏</p>
        </header>

        <p v-if="formError" class="banner err">{{ formError }}</p>
        <p v-if="registerHint" class="banner ok">{{ registerHint }}</p>

        <n-form class="form" @submit.prevent="handleLogin">
          <n-form-item label="玩家编号" :show-feedback="false">
            <n-input
              v-model:value="loginForm.usercode"
              autocomplete="username"
              placeholder="如 100001"
              :disabled="loading"
              size="large"
            />
          </n-form-item>
          <n-form-item label="密码" :show-feedback="false">
            <n-input
              v-model:value="loginForm.password"
              type="password"
              show-password-on="click"
              autocomplete="current-password"
              placeholder="请输入密码"
              :disabled="loading"
              size="large"
            />
          </n-form-item>
          <n-checkbox v-model:checked="loginForm.remember" :disabled="loading">
            记住玩家编号
          </n-checkbox>
          <n-button type="primary" attr-type="submit" block size="large" :loading="loading">
            进入游戏
          </n-button>
        </n-form>

        <n-button
          text
          type="primary"
          class="toggle-reg"
          :disabled="loading"
          @click="showRegister = !showRegister"
        >
          {{ showRegister ? '收起注册' : '没有账号？注册' }}
        </n-button>

        <n-form v-if="showRegister" class="form register" @submit.prevent="handleRegister">
          <n-form-item label="昵称（可选）" :show-feedback="false">
            <n-input
              v-model:value="registerForm.username"
              maxlength="64"
              :disabled="loading"
              size="large"
            />
          </n-form-item>
          <n-form-item label="设置密码" :show-feedback="false">
            <n-input
              v-model:value="registerForm.password"
              type="password"
              show-password-on="click"
              autocomplete="new-password"
              placeholder="至少 6 位"
              :disabled="loading"
              size="large"
            />
          </n-form-item>
          <n-form-item label="确认密码" :show-feedback="false">
            <n-input
              v-model:value="registerForm.confirmPassword"
              type="password"
              show-password-on="click"
              autocomplete="new-password"
              :disabled="loading"
              size="large"
            />
          </n-form-item>
          <p class="note">玩家编号由系统分配，注册成功后显示</p>
          <n-button attr-type="submit" block size="large" :loading="loading">
            注册并登录
          </n-button>
        </n-form>
      </div>
    </section>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(280px, 0.9fr) minmax(360px, 1.1fr);
  background: var(--bg-base);
}

.brand-pane {
  position: relative;
  isolation: isolate;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 64px 56px;
  overflow: hidden;
  border-right: 1px solid var(--border);
}

.brand-pane::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 0;
  background: var(--shell-art) center / cover no-repeat;
  opacity: var(--shell-art-opacity);
  filter: var(--shell-art-filter);
}

.brand-pane::after {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 0;
  background: var(--shell-scrim);
}

.brand-pane > * {
  position: relative;
  z-index: 1;
}

.mark {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.28em;
  color: var(--accent);
  line-height: 1;
}

.brand-pane h1 {
  margin: 18px 0 0;
  font-size: clamp(40px, 5vw, 56px);
  font-weight: 800;
  letter-spacing: 0.08em;
}

.tag {
  margin: 18px 0 0;
  font-size: var(--font-size-md);
  color: var(--text-secondary);
  max-width: 18em;
  line-height: 1.7;
}

.form-pane {
  position: relative;
  display: grid;
  place-items: center;
  padding: var(--space-lg);
}

.theme-btn {
  position: absolute;
  top: var(--space-md);
  right: var(--space-md);
  font-size: 16px;
}

.panel {
  width: min(400px, 100%);
}

.head {
  margin-bottom: 24px;
}

.head h2 {
  margin: 0;
  font-size: var(--font-size-xl);
  font-weight: 600;
}

.head p {
  margin: 8px 0 0;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.banner {
  margin: 0 0 12px;
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-sm);
}

.banner.err {
  background: color-mix(in srgb, var(--accent-red) 12%, transparent);
  color: var(--accent-red);
}

.banner.ok {
  background: var(--accent-soft);
  color: var(--accent);
}

.form {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form.register {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}

.toggle-reg {
  margin-top: 16px;
}

.note {
  margin: 0 0 8px;
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

@media (max-width: 860px) {
  .page {
    grid-template-columns: 1fr;
  }

  .brand-pane {
    padding: 32px 24px 24px;
    border-right: none;
    border-bottom: 1px solid var(--border);
  }

  .mark {
    font-size: 13px;
    letter-spacing: 0.22em;
  }

  .brand-pane h1 {
    font-size: clamp(32px, 8vw, 44px);
  }
}
</style>
