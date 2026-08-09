<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { client } from '@mtcg/common/api'
import type {
  UserVO,
  UserQueryDTO,
  AdminUserCreateDTO,
  AdminUserUpdateDTO,
  UserRole,
} from '@mtcg/common/api'

// ========== 查询 ==========
const loading = ref(false)
const tableData = ref<UserVO[]>([])
const total = ref(0)
const query = reactive<UserQueryDTO>({
  username: '',
  role: '',
  status: '',
  page: 1,
  size: 10,
})

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

function roleTagType(role: string): '' | 'success' | 'warning' | 'info' | 'primary' | 'danger' {
  switch (role) {
    case 'SYS_ADMIN': return 'danger'
    case 'CARD_ADMIN': return 'warning'
    case 'AI': return 'success'
    default: return 'info'
  }
}

function roleLabel(role: string) {
  return roleOptions.find((o) => o.code === role)?.desc ?? role
}

async function loadData() {
  loading.value = true
  try {
    const page = await client.admin.listUsers({ ...query })
    tableData.value = page.records
    total.value = page.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  query.username = ''
  query.role = ''
  query.status = ''
  query.page = 1
  loadData()
}

// ========== 新增/编辑弹窗 ==========
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive({
  username: '',
  password: '',
  nickname: '',
  role: 'PLAYER' as UserRole,
  status: 'ACTIVE',
})

const rules = computed<FormRules>(() => ({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 32, message: '3-32 字符', trigger: 'blur' }],
  password: dialogMode.value === 'create'
    ? [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 32, message: '6-32 字符', trigger: 'blur' }]
    : [],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
}))

function openCreate() {
  dialogMode.value = 'create'
  editingId.value = null
  Object.assign(form, { username: '', password: '', nickname: '', role: 'PLAYER', status: 'ACTIVE' })
  dialogVisible.value = true
}

function openEdit(row: UserVO) {
  dialogMode.value = 'edit'
  editingId.value = row.id
  Object.assign(form, {
    username: row.username,
    password: '',
    nickname: row.nickname ?? '',
    role: row.role,
    status: row.status,
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (dialogMode.value === 'create') {
        const dto: AdminUserCreateDTO = {
          username: form.username,
          password: form.password,
          nickname: form.nickname || undefined,
          role: form.role,
        }
        await client.admin.createUser(dto)
        ElMessage.success('新增成功')
      } else if (editingId.value !== null) {
        const dto: AdminUserUpdateDTO = {
          nickname: form.nickname || undefined,
          role: form.role,
          status: form.status,
        }
        await client.admin.updateUser(editingId.value, dto)
        ElMessage.success('更新成功')
      }
      dialogVisible.value = false
      loadData()
    } finally {
      submitting.value = false
    }
  })
}

// ========== 状态切换 ==========
async function handleStatusChange(row: UserVO, val: string | number | boolean) {
  try {
    await client.admin.updateUserStatus(row.id, val as string)
    row.status = val as UserVO['status']
    ElMessage.success('状态已更新')
  } catch {
    // 拦截器统一弹提示
  }
}

// ========== 重置密码 ==========
const pwdDialogVisible = ref(false)
const pwdFormRef = ref<FormInstance>()
const pwdSubmitting = ref(false)
const pwdTarget = ref<UserVO | null>(null)
const pwdForm = reactive({ newPassword: '' })

const pwdRules: FormRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 32, message: '6-32 字符', trigger: 'blur' },
  ],
}

function openResetPwd(row: UserVO) {
  pwdTarget.value = row
  pwdForm.newPassword = ''
  pwdDialogVisible.value = true
}

async function handleResetPwd() {
  if (!pwdFormRef.value || !pwdTarget.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    pwdSubmitting.value = true
    try {
      await client.admin.resetUserPassword(pwdTarget.value!.id, { newPassword: pwdForm.newPassword })
      ElMessage.success('密码已重置')
      pwdDialogVisible.value = false
    } finally {
      pwdSubmitting.value = false
    }
  })
}

// ========== 删除 ==========
async function handleDelete(row: UserVO) {
  try {
    await ElMessageBox.confirm(`确认删除用户「${row.username}」吗？此操作不可恢复。`, '危险操作', {
      type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消',
    })
    await client.admin.deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    // ElMessageBox cancel 静默，其余错误由拦截器提示
  }
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="user-list">
    <el-card class="search-card" shadow="never">
      <el-form :model="query" inline>
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="模糊搜索" clearable style="width: 180px" @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="query.role" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="o in roleOptions" :key="o.code" :label="o.desc" :value="o.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="openCreate">新增用户</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe height="100%">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column label="用户名" min-width="140">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="32" :src="row.avatar || undefined">
                {{ row.username.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="user-text">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" width="140" show-overflow-tooltip />
        <el-table-column label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="roleTagType(row.role)" size="small">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status"
              active-value="ACTIVE"
              inactive-value="DISABLED"
              :disabled="row.role === 'SYS_ADMIN'"
              @change="(val: string | number | boolean) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button
              size="small"
              type="warning"
              link
              :disabled="row.role === 'SYS_ADMIN'"
              @click="openResetPwd(row)"
            >
              重置密码
            </el-button>
            <el-button
              size="small"
              type="danger"
              link
              :disabled="row.role === 'SYS_ADMIN'"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <div class="pagination">
          <el-pagination
            v-model:current-page="query.page"
            v-model:page-size="query.size"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @current-change="loadData"
            @size-change="loadData"
          />
        </div>
      </template>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增用户' : '编辑用户'"
      width="480px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="dialogMode === 'edit'" maxlength="32" show-word-limit />
        </el-form-item>
        <el-form-item v-if="dialogMode === 'create'" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password maxlength="32" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" maxlength="64" show-word-limit />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width: 100%">
            <el-option v-for="o in roleOptions" :key="o.code" :label="o.desc" :value="o.code" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialogMode === 'edit'" label="状态">
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

    <!-- 重置密码弹窗 -->
    <el-dialog v-model="pwdDialogVisible" title="重置密码" width="420px">
      <el-alert
        v-if="pwdTarget"
        :title="`即将重置用户「${pwdTarget.username}」的密码`"
        type="warning"
        :closable="false"
        show-icon
        style="margin-bottom: 16px"
      />
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password maxlength="32" placeholder="6-32 字符" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="pwdSubmitting" @click="handleResetPwd">确定重置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-list {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.search-card { flex-shrink: 0; }
.table-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
.table-card :deep(.el-card__body) {
  flex: 1;
  min-height: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.table-card :deep(.el-card__footer) {
  flex-shrink: 0;
  padding: 12px 16px;
  border-top: 1px solid #ebeef5;
}
.table-card :deep(.el-table) {
  height: 100%;
  flex: 1;
}
.pagination { display: flex; justify-content: flex-end; }
.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.user-text {
  font-weight: 500;
  color: #303133;
}
</style>
