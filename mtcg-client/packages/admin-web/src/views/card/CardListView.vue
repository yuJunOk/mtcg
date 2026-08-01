<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { CardVO, CardQueryDTO } from '@mtcg/common/types'

const loading = ref(false)
const tableData = ref<CardVO[]>([])
const total = ref(0)
const query = ref<CardQueryDTO>({
  cardName: '',
  cardType: '',
  color: '',
  page: 1,
  size: 10,
})

const columns = [
  { prop: 'cardCode', label: '编号', width: 120 },
  { prop: 'cardName', label: '名称', width: 160 },
  { prop: 'cardType', label: '类型', width: 100 },
  { prop: 'color', label: '颜色', width: 80 },
  { prop: 'level', label: 'Lv', width: 60 },
  { prop: 'power', label: '战力', width: 80 },
  { prop: 'attackRange', label: 'R', width: 60 },
  { prop: 'rarity', label: '稀有度', width: 80 },
]

async function loadData() {
  loading.value = true
  try {
    // TODO: 调用 cardApi.list(query.value)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleCreate() {
  // TODO: 打开新增表单
}

function handleDelete(id: number) {
  // TODO: 删除确认
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="card-list">
    <!-- 查询条件 -->
    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="名称">
          <el-input v-model="query.cardName" placeholder="模糊搜索" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.cardType" placeholder="全部" clearable style="width: 120px">
            <el-option label="角色卡" value="CHARACTER" />
            <el-option label="冲击卡" value="RUSH_POINT" />
          </el-select>
        </el-form-item>
        <el-form-item label="颜色">
          <el-select v-model="query.color" placeholder="全部" clearable style="width: 100px">
            <el-option label="红" value="RED" />
            <el-option label="黄" value="YELLOW" />
            <el-option label="蓝" value="BLUE" />
            <el-option label="绿" value="GREEN" />
            <el-option label="橙" value="ORANGE" />
            <el-option label="紫" value="PURPLE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button type="success" @click="handleCreate">新增卡牌</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe height="100%">
        <el-table-column v-for="col in columns" :key="col.prop" v-bind="col" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="loadData()">详情</el-button>
            <el-button size="small" type="primary" @click="loadData()">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
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
.card-list {
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