<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authApi, useThemeStore, useUserStore } from '@mtcg/common'

/** 与后端 UserConstant.USERCODE_BASE 对齐：usercode = BASE + id */
const USERCODE_BASE = 100_000
const REMEMBER_KEY = 'mtcg_remember_usercode'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
useThemeStore()

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
    return
  }
  if (!loginForm.password) {
    formError.value = '请输入密码'
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
    await router.replace(redirectPath.value)
  } catch {
    // 错误由 setHttpErrorNotifier 提示
  }
}

async function handleRegister(): Promise<void> {
  formError.value = ''
  registerHint.value = ''
  if (!registerForm.password || registerForm.password.length < 6) {
    formError.value = '密码至少 6 位'
    return
  }
  if (registerForm.password !== registerForm.confirmPassword) {
    formError.value = '两次密码不一致'
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
    registerHint.value = `注册成功，你的玩家编号是 ${usercode}，请妥善保存`
    localStorage.setItem(REMEMBER_KEY, usercode)
    await router.replace(redirectPath.value)
  } catch {
    // 错误由 setHttpErrorNotifier 提示
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-atmosphere" aria-hidden="true" />
    <div class="login-panel">
      <header class="login-brand">
        <p class="brand-mark">MTCG</p>
        <h1 class="brand-title">超英击战</h1>
        <p class="brand-sub">登录后构筑卡组，开启对战</p>
      </header>

      <p v-if="formError" class="form-error">{{ formError }}</p>
      <p v-if="registerHint" class="form-hint">{{ registerHint }}</p>

      <form class="login-form" @submit.prevent="handleLogin">
        <label class="field">
          <span class="field-label">玩家编号</span>
          <input
            v-model="loginForm.usercode"
            class="field-input"
            type="text"
            autocomplete="username"
            placeholder="如 100001"
            :disabled="loading"
          />
        </label>
        <label class="field">
          <span class="field-label">密码</span>
          <input
            v-model="loginForm.password"
            class="field-input"
            type="password"
            autocomplete="current-password"
            placeholder="请输入密码"
            :disabled="loading"
          />
        </label>
        <label class="remember">
          <input v-model="loginForm.remember" type="checkbox" :disabled="loading" />
          <span>记住玩家编号</span>
        </label>
        <button class="btn-primary" type="submit" :disabled="loading">
          {{ loading ? '请稍候…' : '进入游戏' }}
        </button>
      </form>

      <button
        class="toggle-register"
        type="button"
        :disabled="loading"
        @click="showRegister = !showRegister"
      >
        {{ showRegister ? '收起注册' : '没有账号？注册' }}
      </button>

      <form v-if="showRegister" class="register-form" @submit.prevent="handleRegister">
        <label class="field">
          <span class="field-label">昵称（可选）</span>
          <input
            v-model="registerForm.username"
            class="field-input"
            type="text"
            maxlength="64"
            placeholder="展示名"
            :disabled="loading"
          />
        </label>
        <label class="field">
          <span class="field-label">设置密码</span>
          <input
            v-model="registerForm.password"
            class="field-input"
            type="password"
            autocomplete="new-password"
            placeholder="至少 6 位"
            :disabled="loading"
          />
        </label>
        <label class="field">
          <span class="field-label">确认密码</span>
          <input
            v-model="registerForm.confirmPassword"
            class="field-input"
            type="password"
            autocomplete="new-password"
            placeholder="再次输入密码"
            :disabled="loading"
          />
        </label>
        <p class="register-note">玩家编号由系统自动分配，注册成功后会显示</p>
        <button class="btn-secondary" type="submit" :disabled="loading">
          {{ loading ? '注册中…' : '注册并登录' }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 20px;
  background: var(--bg-base);
  overflow: hidden;
}

.login-atmosphere {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse 80% 50% at 20% 10%, rgba(0, 212, 170, 0.12), transparent 55%),
    radial-gradient(ellipse 60% 40% at 90% 80%, rgba(92, 107, 192, 0.14), transparent 50%),
    linear-gradient(160deg, #151821 0%, var(--bg-base) 45%, #1a2030 100%);
  pointer-events: none;
}

.login-panel {
  position: relative;
  width: min(420px, 100%);
  padding: 40px 36px 32px;
  border-radius: 16px;
  background: color-mix(in srgb, var(--bg-surface) 88%, transparent);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-lg);
  backdrop-filter: blur(12px);
}

.login-brand {
  margin-bottom: 28px;
  text-align: center;
}

.brand-mark {
  margin: 0;
  font-size: 42px;
  font-weight: 800;
  letter-spacing: 0.12em;
  color: var(--accent);
  line-height: 1.1;
}

.brand-title {
  margin: 8px 0 0;
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--text-primary);
}

.brand-sub {
  margin: 8px 0 0;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.form-error {
  margin: 0 0 12px;
  padding: 10px 12px;
  border-radius: 8px;
  background: rgba(229, 57, 53, 0.12);
  color: var(--accent-red);
  font-size: var(--font-size-sm);
}

.form-hint {
  margin: 0 0 12px;
  padding: 10px 12px;
  border-radius: 8px;
  background: rgba(0, 212, 170, 0.12);
  color: var(--accent);
  font-size: var(--font-size-sm);
}

.login-form,
.register-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.register-form {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.field-input {
  height: 44px;
  padding: 0 14px;
  border-radius: 10px;
  border: 1px solid var(--border);
  background: var(--bg-surface-2);
  color: var(--text-primary);
  font-size: var(--font-size-md);
  outline: none;
  transition: border-color 0.15s ease;
}

.field-input:focus {
  border-color: var(--accent);
}

.field-input::placeholder {
  color: var(--text-disabled);
}

.remember {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  cursor: pointer;
  user-select: none;
}

.btn-primary,
.btn-secondary {
  height: 46px;
  border: none;
  border-radius: 10px;
  font-size: var(--font-size-md);
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.btn-primary {
  margin-top: 4px;
  background: var(--accent);
  color: #0b1220;
}

.btn-secondary {
  background: var(--bg-surface-2);
  color: var(--text-primary);
  border: 1px solid var(--border-light);
}

.btn-primary:hover:not(:disabled),
.btn-secondary:hover:not(:disabled) {
  opacity: 0.92;
  transform: translateY(-1px);
}

.btn-primary:disabled,
.btn-secondary:disabled,
.toggle-register:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.toggle-register {
  margin-top: 18px;
  width: 100%;
  border: none;
  background: transparent;
  color: var(--text-link);
  font-size: var(--font-size-sm);
  cursor: pointer;
  padding: 8px;
}

.register-note {
  margin: 0;
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}
</style>
