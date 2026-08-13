<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cardApi, adminCardApi } from '@mtcg/common/api'
import { resolveCardImageUrl } from '@mtcg/common'
import { CardFormDialog, ProductSelector } from '@/components'
import type { CardFormMode } from '@/components/CardFormDialog.vue'
import {
  CARD_TYPE_OPTIONS,
  CARD_COLOR_OPTIONS,
  CARD_RARITY_OPTIONS,
  CARD_TRAIT_FILTER_OPTIONS,
  codeToDesc,
  CardType,
  CardColor,
  CardRarity,
} from '@mtcg/common/types'
import type { CardVO } from '@mtcg/common/types'

const route = useRoute()

// ========== 查询 ==========
const loading = ref(false)
const tableData = ref<CardVO[]>([])
const total = ref(0)
const query = reactive({
  cardName: '',
  cardType: '',
  color: '',
  rarity: '',
  trait: '',
  productCode: (route.query.productCode as string) || '',
  pageNum: 1,
  pageSize: 10,
})

async function loadData() {
  const page = await cardApi.list({ ...query }, loading)
  tableData.value = page.records
  total.value = page.total
}

function handleReset() {
  query.cardName = ''
  query.cardType = ''
  query.color = ''
  query.rarity = ''
  query.trait = ''
  query.productCode = ''
  query.pageNum = 1
  loadData()
}

// ========== 枚举显示 ==========
function typeLabel(c: string) { return codeToDesc<CardType>(CARD_TYPE_OPTIONS, c as CardType) }
function colorLabel(c: string) { return codeToDesc<CardColor>(CARD_COLOR_OPTIONS, c as CardColor) }
function rarityLabel(r: string) { return codeToDesc<CardRarity>(CARD_RARITY_OPTIONS, r as CardRarity) }

function rarityTagType(r: string): '' | 'success' | 'warning' | 'info' | 'primary' | 'danger' {
  switch (r) {
    case 'C': return 'info'
    case 'R': return ''
    case 'SR': return 'primary'
    case 'GR': return 'success'
    case 'UR': return 'warning'
    case 'MR':
    case 'SEC':
    case 'HR':
    case 'LR': return 'danger'
    default: return 'info'
  }
}

// ========== 弹窗 ==========
const dialogVisible = ref(false)
const dialogMode = ref<CardFormMode>('create')
const editingCard = ref<CardVO | null>(null)

function openCreate() {
  dialogMode.value = 'create'
  editingCard.value = null
  dialogVisible.value = true
}

function openEdit(row: CardVO) {
  dialogMode.value = 'edit'
  editingCard.value = row
  dialogVisible.value = true
}

// ========== 删除 ==========
async function handleDelete(row: CardVO) {
  try {
    await ElMessageBox.confirm(`确认删除卡牌「${row.cardName}」吗？`, '提示', {
      type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消',
    })
    const deleteLoading = ref(false)
    await adminCardApi.delete(row.id, deleteLoading)
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
  <div class="card-list">
    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="名称">
          <el-input v-model="query.cardName" placeholder="模糊搜索" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.cardType" placeholder="全部" clearable style="width: 120px">
            <el-option
              v-for="o in CARD_TYPE_OPTIONS"
              :key="o.code"
              :label="o.desc"
              :value="o.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="颜色">
          <el-select v-model="query.color" placeholder="全部" clearable style="width: 100px">
            <el-option
              v-for="o in CARD_COLOR_OPTIONS"
              :key="o.code"
              :label="o.desc"
              :value="o.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="稀有度">
          <el-select v-model="query.rarity" placeholder="全部" clearable style="width: 120px">
            <el-option
              v-for="o in CARD_RARITY_OPTIONS"
              :key="o.code"
              :label="`${o.code} - ${o.desc}`"
              :value="o.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="特征">
          <el-select
            v-model="query.trait"
            placeholder="全部"
            clearable
            filterable
            style="width: 140px"
          >
            <el-option
              v-for="t in CARD_TRAIT_FILTER_OPTIONS"
              :key="t"
              :label="t"
              :value="t"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="产品" style="width: 220px">
          <ProductSelector v-model="query.productCode" placeholder="点击选择产品" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="openCreate">新增卡牌</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe height="100%">
        <el-table-column prop="cardCode" label="编号" width="120" />
        <el-table-column label="卡图" width="88">
          <template #default="{ row }">
            <el-image
              v-if="row.imagePath"
              :src="resolveCardImageUrl(row.imagePath)"
              fit="contain"
              class="thumb"
              :preview-src-list="[resolveCardImageUrl(row.imagePath)]"
              preview-teleported
            />
            <span v-else class="no-img">—</span>
          </template>
        </el-table-column>
        <el-table-column prop="cardName" label="名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="productCode" label="所属产品" width="110" show-overflow-tooltip />
        <el-table-column prop="traits" label="特征" min-width="160" show-overflow-tooltip />
        <el-table-column label="类型" width="90">
          <template #default="{ row }">{{ typeLabel(row.cardType) }}</template>
        </el-table-column>
        <el-table-column label="颜色" width="70">
          <template #default="{ row }">{{ colorLabel(row.color ?? '') }}</template>
        </el-table-column>
        <el-table-column prop="level" label="Lv" width="56" />
        <el-table-column prop="power" label="战力" width="72" />
        <el-table-column prop="attackRange" label="R" width="52" />
        <el-table-column label="稀有度" width="100">
          <template #default="{ row }">
            <el-tag :type="rarityTagType(row.rarity ?? '')">{{ rarityLabel(row.rarity ?? '') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
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

    <CardFormDialog
      v-model:visible="dialogVisible"
      :mode="dialogMode"
      :card="editingCard"
      @success="loadData"
    />
  </div>
</template>

<style scoped>
.card-list {
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
.pagination {
  display: flex;
  justify-content: flex-end;
}
.thumb {
  width: 48px;
  height: 68px;
  border-radius: 4px;
  background: #f5f7fa;
}
.no-img {
  color: #c0c4cc;
  font-size: 12px;
}
</style>
