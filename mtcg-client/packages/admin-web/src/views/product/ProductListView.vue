<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useRouter } from 'vue-router'
import { client } from '@mtcg/common/api'
import type { ProductVO, ProductQueryDTO, ProductCreateDTO, ProductUpdateDTO } from '@mtcg/common/api'

const router = useRouter()

// ========== 查询 ==========
const loading = ref(false)
const tableData = ref<ProductVO[]>([])
const total = ref(0)
const query = reactive<ProductQueryDTO>({
  productName: '',
  productCode: '',
  page: 1,
  size: 10,
})

async function loadData() {
  loading.value = true
  try {
    const page = await client.admin.listProducts({ ...query })
    tableData.value = page.records
    total.value = page.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  query.productName = ''
  query.productCode = ''
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
  productCode: '',
  productName: '',
  releaseDate: '',
  description: '',
})

const rules = computed<FormRules>(() => ({
  productCode: [{ required: true, message: '请输入产品编号', trigger: 'blur' }, { max: 16, message: '最多 16 字符', trigger: 'blur' }],
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }, { max: 128, message: '最多 128 字符', trigger: 'blur' }],
}))

function openCreate() {
  dialogMode.value = 'create'
  editingId.value = null
  Object.assign(form, { productCode: '', productName: '', releaseDate: '', description: '' })
  dialogVisible.value = true
}

function openEdit(row: ProductVO) {
  dialogMode.value = 'edit'
  editingId.value = row.id
  Object.assign(form, {
    productCode: row.productCode,
    productName: row.productName,
    releaseDate: row.releaseDate ?? '',
    description: row.description ?? '',
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
        const dto: ProductCreateDTO = {
          productCode: form.productCode,
          productName: form.productName,
          releaseDate: form.releaseDate || undefined,
          description: form.description || undefined,
        }
        await client.admin.createProduct(dto)
        ElMessage.success('新增成功')
      } else if (editingId.value !== null) {
        const dto: ProductUpdateDTO = {
          productName: form.productName,
          releaseDate: form.releaseDate || undefined,
          description: form.description || undefined,
        }
        await client.admin.updateProduct(editingId.value, dto)
        ElMessage.success('更新成功')
      }
      dialogVisible.value = false
      loadData()
    } finally {
      submitting.value = false
    }
  })
}

// ========== 删除 ==========
async function handleDelete(row: ProductVO) {
  try {
    await ElMessageBox.confirm(`确认删除产品「${row.productName}」吗？关联卡牌不会被删除。`, '提示', {
      type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消',
    })
    await client.admin.deleteProduct(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (_) {
    // ElMessageBox cancel 或其余错误：取消静默，其他由拦截器提示
  }
}

onMounted(() => { loadData() })
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
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button
              size="small"
              type="success"
              @click="router.push(`/cards?productCode=${encodeURIComponent(row.productCode)}`)"
            >
              查看卡牌
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增产品' : '编辑产品'"
      width="560px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        label-position="right"
      >
        <el-form-item label="产品编号" prop="productCode">
          <el-input v-model="form.productCode" :disabled="dialogMode === 'edit'" maxlength="16" show-word-limit />
        </el-form-item>
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="form.productName" maxlength="128" show-word-limit />
        </el-form-item>
        <el-form-item label="发售日期">
          <el-date-picker
            v-model="form.releaseDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.product-list {
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
</style>
