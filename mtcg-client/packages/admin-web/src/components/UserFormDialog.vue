<script setup lang="ts">
/**
 * 用户新增/编辑弹窗
 */
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { adminUserApi } from '@mtcg/common/api'
import type { UserVO, UserRole, AdminUserCreateDTO, AdminUserUpdateDTO } from '@mtcg/common/types'

export type UserFormMode = 'create' | 'edit'

const props = defineProps<{
  visible: boolean
  mode: UserFormMode
  user?: UserVO | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  success: []
}>()

const roleOptions: Array<{ code: UserRole; desc: string }> = [
  { code: 'PLAYER', desc: '玩家' },
  { code: 'CARD_ADMIN', desc: '卡牌管理员' },
  { code: 'SYS_ADMIN', desc: '系统管理员' },
  { code: 'AI', desc: 'AI' },
]
const statusOptions = [
  { label: '启用', value: 'ACTIVE' },
  { label: '禁用', value: 'DISABLED' },
]

const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive({
  usercode: '',
  password: '',
  username: '',
  role: 'PLAYER' as UserRole,
  status: 'ACTIVE',
})

const dialogVisible = computed({
  get: () => props.visible,
  set: (v: boolean) => emit('update:visible', v),
})

const title = computed(() => (props.mode === 'create' ? '新增用户' : '编辑用户'))

const rules = computed<FormRules>(() => ({
  password: props.mode === 'create'
    ? [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 32, message: '6-32 字符', trigger: 'blur' },
      ]
    : [],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
}))

function resetForm() {
  Object.assign(form, {
    usercode: '',
    password: '',
    username: '',
    role: 'PLAYER',
    status: 'ACTIVE',
  })
}

function fillFromUser(row: UserVO) {
  Object.assign(form, {
    usercode: row.usercode,
    password: '',
    username: row.username ?? '',
    role: row.role as UserRole,
    status: row.status,
  })
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (props.mode === 'create') {
        const dto: AdminUserCreateDTO = {
          password: form.password,
          username: form.username || undefined,
          role: form.role,
        }
        await adminUserApi.createUser(dto, submitting)
        ElMessage.success('新增成功')
      } else if (props.user?.id != null) {
        const dto: AdminUserUpdateDTO = {
          username: form.username || undefined,
          role: form.role,
          status: form.status,
        }
        await adminUserApi.updateUser(props.user.id, dto, submitting)
        ElMessage.success('更新成功')
      }
      dialogVisible.value = false
      emit('success')
    } finally {
      submitting.value = false
    }
  })
}

watch(
  () => props.visible,
  (vis) => {
    if (!vis) return
    if (props.mode === 'create') resetForm()
    else if (props.user) fillFromUser(props.user)
  },
)
</script>

<template>
  <el-dialog v-model="dialogVisible" :title="title" width="480px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item v-if="mode === 'edit'" label="玩家编号">
        <el-input :model-value="form.usercode" disabled />
      </el-form-item>
      <el-form-item v-if="mode === 'create'" label="密码" prop="password" required>
        <el-input v-model="form.password" type="password" show-password maxlength="32" />
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" maxlength="64" show-word-limit placeholder="展示名，可不填" />
      </el-form-item>
      <el-form-item label="角色" prop="role">
        <el-select v-model="form.role" style="width: 100%">
          <el-option v-for="o in roleOptions" :key="o.code" :label="o.desc" :value="o.code" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="mode === 'edit'" label="状态">
        <el-select v-model="form.status" style="width: 100%">
          <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>
