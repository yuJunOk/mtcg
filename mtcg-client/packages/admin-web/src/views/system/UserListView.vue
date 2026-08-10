<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminUserApi } from '@mtcg/common/api'
import { UserFormDialog, UserResetPasswordDialog } from '@/components'
import type { UserFormMode } from '@/components/UserFormDialog.vue'
import type { UserVO, UserRole, UserQueryDTO } from '@mtcg/common/types'

// ========== 查询 ==========
const loading = ref(false)
const tableData = ref<UserVO[]>([])
const total = ref(0)
const query = reactive<UserQueryDTO>({
  usercode: '',
  username: '',
  role: '',
  status: '',
  pageNum: 1,
  pageSize: 10,
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
  const page = await adminUserApi.list({ ...query }, loading)
  tableData.value = page.records
  total.value = page.total
}

function handleReset() {
  query.usercode = ''
  query.username = ''
  query.role = ''
  query.status = ''
  query.pageNum = 1
  loadData()
}

// ========== 新增/编辑弹窗 ==========
const dialogVisible = ref(false)
const dialogMode = ref<UserFormMode>('create')
const editingUser = ref<UserVO | null>(null)
const statusLoading = ref(false)

function openCreate() {
  dialogMode.value = 'create'
  editingUser.value = null
  dialogVisible.value = true
}

function openEdit(row: UserVO) {
  dialogMode.value = 'edit'
  editingUser.value = row
  dialogVisible.value = true
}

// ========== 状态切换 ==========
async function handleStatusChange(row: UserVO, val: string | number | boolean) {
  await adminUserApi.updateUserStatus(row.id, val as string, statusLoading)
  row.status = val as UserVO['status']
  ElMessage.success('状态已更新')
}

// ========== 重置密码 ==========
const pwdDialogVisible = ref(false)
const pwdTarget = ref<UserVO | null>(null)

function openResetPwd(row: UserVO) {
  pwdTarget.value = row
  pwdDialogVisible.value = true
}

// ========== 删除 ==========
async function handleDelete(row: UserVO) {
  try {
    await ElMessageBox.confirm(`确认删除用户「${row.usercode}」吗？此操作不可恢复。`, '危险操作', {
      type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消',
    })
    const deleteLoading = ref(false)
    await adminUserApi.deleteUser(row.id, deleteLoading)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // ElMessageBox cancel 静默，其余错误由拦截器提示
  }
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="user-list">
    <el-card class="search-card" shadow="never">
      <el-form :model="query" inline>
        <el-form-item label="玩家编号">
          <el-input v-model="query.usercode" placeholder="模糊搜索" clearable style="width: 180px" @keyup.enter="loadData" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="模糊搜索" clearable style="width: 160px" @keyup.enter="loadData" />
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
        <el-table-column label="玩家编号" min-width="160">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="32" :src="row.avatar || undefined">
                {{ row.usercode.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="user-text">{{ row.usercode }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="140" show-overflow-tooltip />
        <el-table-column label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="roleTagType(row.role)">{{ roleLabel(row.role) }}</el-tag>
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
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button
              type="warning"
              link
              :disabled="row.role === 'SYS_ADMIN'"
              @click="openResetPwd(row)"
            >
              重置密码
            </el-button>
            <el-button
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
            v-model:current-page="query.pageNum"
            v-model:page-size="query.pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @current-change="loadData"
            @size-change="loadData"
          />
        </div>
      </template>
    </el-card>

    <UserFormDialog
      v-model:visible="dialogVisible"
      :mode="dialogMode"
      :user="editingUser"
      @success="loadData"
    />
    <UserResetPasswordDialog
      v-model:visible="pwdDialogVisible"
      :user="pwdTarget"
    />
  </div>
</template>

<style scoped>
.user-list {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
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
  padding: 10px 16px;
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
