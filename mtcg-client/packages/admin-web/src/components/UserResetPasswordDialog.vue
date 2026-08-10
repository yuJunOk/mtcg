<script setup lang="ts">
/**
 * 用户重置密码弹窗
 */
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { adminUserApi } from '@mtcg/common/api'
import type { UserVO } from '@mtcg/common/types'

const props = defineProps<{
  visible: boolean
  user: UserVO | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  success: []
}>()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive({ newPassword: '' })

const rules: FormRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 32, message: '6-32 字符', trigger: 'blur' },
  ],
}

const dialogVisible = computed({
  get: () => props.visible,
  set: (v: boolean) => emit('update:visible', v),
})

async function handleSubmit() {
  if (!formRef.value || !props.user) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    await adminUserApi.resetUserPassword(
      props.user!.id,
      { newPassword: form.newPassword },
      submitting,
    )
    ElMessage.success('密码已重置')
    dialogVisible.value = false
    emit('success')
  })
}

watch(
  () => props.visible,
  (vis) => {
    if (vis) form.newPassword = ''
  },
)
</script>

<template>
  <el-dialog v-model="dialogVisible" title="重置密码" width="420px">
    <el-alert
      v-if="user"
      :title="`即将重置用户「${user.usercode}」的密码`"
      type="warning"
      :closable="false"
      show-icon
      style="margin-bottom: 16px"
    />
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="form.newPassword"
          type="password"
          show-password
          maxlength="32"
          placeholder="6-32 字符"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定重置</el-button>
    </template>
  </el-dialog>
</template>
