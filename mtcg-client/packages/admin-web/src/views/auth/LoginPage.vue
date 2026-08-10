<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@mtcg/common/stores'

const REMEMBER_KEY = 'mtcg_remember_user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({
  usercode: localStorage.getItem(REMEMBER_KEY) || '',
  password: '',
  remember: !!localStorage.getItem(REMEMBER_KEY),
})

const rules: FormRules = {
  usercode: [{ required: true, message: '请输入玩家编号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await userStore.login({ usercode: form.usercode, password: form.password }, loading)
      // login 已写入 userInfo，无需再请求 /users/me
      if (form.remember) {
        localStorage.setItem(REMEMBER_KEY, form.usercode)
      } else {
        localStorage.removeItem(REMEMBER_KEY)
      }
      ElMessage.success('登录成功')
      router.push('/')
    } catch {
      // 拦截器统一弹提示
    }
  })
}

</script>

<template>
  <div class="login-page">
    <div class="login-bg" />
    <el-card class="login-card" shadow="always">
      <div class="login-header">
        <div class="logo-icon">M</div>
        <div class="login-title">MTCG 管理后台</div>
        <div class="login-subtitle">超英击战 · 运营管理平台</div>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        @submit.prevent
      >
        <el-form-item prop="usercode">
          <el-input
            v-model="form.usercode"
            placeholder="玩家编号"
            size="large"
            clearable
            :prefix-icon="'User'"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            size="large"
            show-password
            :prefix-icon="'Lock'"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.remember">记住用户名</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">MTCG © 2026 Hero Rush TCG</div>
    </el-card>
  </div>
</template>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}
.login-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1a2a3a 0%, #2d3a4b 40%, #1e3a5f 100%);
}
.login-bg::before {
  content: '';
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(64, 158, 255, 0.15) 0%, transparent 70%);
  top: -200px;
  right: -100px;
}
.login-bg::after {
  content: '';
  position: absolute;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(103, 194, 58, 0.1) 0%, transparent 70%);
  bottom: -150px;
  left: -100px;
}
.login-card {
  width: 400px;
  position: relative;
  z-index: 1;
  border-radius: 12px;
  border: none;
}
.login-card :deep(.el-card__body) {
  padding: 36px 32px 24px;
}
.login-header {
  text-align: center;
  margin-bottom: 28px;
}
.logo-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 12px;
  border-radius: 14px;
  background: linear-gradient(135deg, #409EFF, #1e3a5f);
  color: #fff;
  font-size: 28px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.35);
}
.login-title {
  font-size: 22px;
  font-weight: 700;
  color: #1a2a3a;
}
.login-subtitle {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
.login-btn {
  width: 100%;
  letter-spacing: 4px;
}
.login-footer {
  text-align: center;
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 8px;
}
</style>
