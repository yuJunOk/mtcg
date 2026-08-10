<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cardFeatureApi } from '@mtcg/common/api'
import { CardFeatureFormDialog } from '@/components'
import type { CardFeatureFormMode } from '@/components/CardFeatureFormDialog.vue'
import type { CardFeatureVO } from '@mtcg/common/types'

// ========== 查询 ==========
const loading = ref(false)
const tableData = ref<CardFeatureVO[]>([])
const query = reactive({
  keyword: '',
})

async function loadData() {
  const features = await cardFeatureApi.list(loading)
  if (query.keyword) {
    tableData.value = features.filter(f =>
      f.code.includes(query.keyword) || f.name.includes(query.keyword)
    )
  } else {
    tableData.value = features
  }
}

function handleReset() {
  query.keyword = ''
  loadData()
}

// ========== 弹窗 ==========
const dialogVisible = ref(false)
const dialogMode = ref<CardFeatureFormMode>('create')
const editingFeature = ref<CardFeatureVO | null>(null)

function openCreate() {
  dialogMode.value = 'create'
  editingFeature.value = null
  dialogVisible.value = true
}

function openEdit(row: CardFeatureVO) {
  dialogMode.value = 'edit'
  editingFeature.value = row
  dialogVisible.value = true
}

// ========== 删除 ==========
async function handleDelete(row: CardFeatureVO) {
  try {
    await ElMessageBox.confirm(`确认删除特征「${row.name}」吗？`, '提示', {
      type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消',
    })
    const deleteLoading = ref(false)
    await cardFeatureApi.delete(row.id, deleteLoading)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      // 其余错误由拦截器统一弹提示
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="card-feature-list">
    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="编码/名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="openCreate">新增特征</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe height="100%">
        <el-table-column prop="code" label="编码" width="180" />
        <el-table-column prop="name" label="名称" min-width="200" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <CardFeatureFormDialog
      v-model:visible="dialogVisible"
      :mode="dialogMode"
      :feature="editingFeature"
      @success="loadData"
    />
  </div>
</template>

<style scoped>
.card-feature-list {
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
.table-card :deep(.el-table) {
  height: 100%;
  flex: 1;
}
</style>
