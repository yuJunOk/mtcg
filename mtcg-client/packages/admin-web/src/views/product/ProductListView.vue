<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { productApi, adminProductApi } from '@mtcg/common/api'
import { ProductFormDialog } from '@/components'
import type { ProductFormMode } from '@/components/ProductFormDialog.vue'
import type { ProductVO } from '@mtcg/common/types'

const router = useRouter()

// ========== 查询 ==========
const loading = ref(false)
const tableData = ref<ProductVO[]>([])
const total = ref(0)
const query = reactive({
  productName: '',
  productCode: '',
  pageNum: 1,
  pageSize: 10,
})

async function loadData() {
  const page = await productApi.list({ ...query }, loading)
  tableData.value = page.records
  total.value = page.total
}

function handleReset() {
  query.productName = ''
  query.productCode = ''
  query.pageNum = 1
  loadData()
}

// ========== 弹窗 ==========
const dialogVisible = ref(false)
const dialogMode = ref<ProductFormMode>('create')
const editingProduct = ref<ProductVO | null>(null)

function openCreate() {
  dialogMode.value = 'create'
  editingProduct.value = null
  dialogVisible.value = true
}

function openEdit(row: ProductVO) {
  dialogMode.value = 'edit'
  editingProduct.value = row
  dialogVisible.value = true
}

// ========== 删除 ==========
async function handleDelete(row: ProductVO) {
  try {
    await ElMessageBox.confirm(`确认删除产品「${row.productName}」吗？关联卡牌不会被删除。`, '提示', {
      type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消',
    })
    const deleteLoading = ref(false)
    await adminProductApi.delete(row.id, deleteLoading)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // ElMessageBox cancel 或其余错误：取消静默，其他由拦截器提示
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="product-list">
    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="编号">
          <el-input v-model="query.productCode" placeholder="产品编号" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="query.productName" placeholder="模糊搜索" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="openCreate">新增产品</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe height="100%">
        <el-table-column prop="productCode" label="产品编号" width="140" />
        <el-table-column prop="productName" label="产品名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="releaseDate" label="发售日期" width="130" />
        <el-table-column prop="description" label="描述" min-width="260" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button
              type="success"
              link
              @click="router.push(`/cards?productCode=${encodeURIComponent(row.productCode)}`)"
            >
              查看卡牌
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <ProductFormDialog
      v-model:visible="dialogVisible"
      :mode="dialogMode"
      :product="editingProduct"
      @success="loadData"
    />
  </div>
</template>

<style scoped>
.product-list {
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
</style>
