<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { productApi } from '@mtcg/common/api'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const categories = ref<any[]>([])
const query = ref({
  productName: '',
  categoryCode: '',
  page: 1,
  size: 10,
})

const columns = [
  { prop: 'productCode', label: '产品编号', width: 140 },
  { prop: 'productName', label: '产品名称', width: 200 },
  { prop: 'categoryCode', label: '分类', width: 120 },
  { prop: 'releaseDate', label: '发售日', width: 120 },
]

async function loadData() {
  loading.value = true
  try {
    // TODO: 调用 productApi.list(query.value)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function fetchCategories() {
  try {
    categories.value = await productApi.getCategories()
  } catch (e) {
    categories.value = []
  }
}

function handleCreate() {
  // TODO: 打开新增表单
}

onMounted(() => {
  loadData()
  fetchCategories()
})
</script>

<template>
  <div class="product-list">
    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="名称">
          <el-input v-model="query.productName" placeholder="模糊搜索" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.categoryCode" placeholder="全部" clearable style="width: 140px">
            <el-option
              v-for="cat in categories"
              :key="cat.categoryCode"
              :label="cat.categoryName"
              :value="cat.categoryCode"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button type="success" @click="handleCreate">新增产品</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe height="100%">
        <el-table-column v-for="col in columns" :key="col.prop" v-bind="col" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="loadData()">详情</el-button>
            <el-button size="small" type="primary" @click="loadData()">编辑</el-button>
            <el-button size="small" type="danger">删除</el-button>
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
  </div>
</template>

<style scoped>
.product-list {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-card {
  flex-shrink: 0;
}

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

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>