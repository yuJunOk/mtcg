<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { client } from '@mtcg/common/api'
import type {
  CardVO,
  CardQueryDTO,
  CardCreateDTO,
  CardUpdateDTO,
  CardType,
  CardColor,
  CardRarity,
} from '@mtcg/common/api'
import {
  CARD_TYPE_OPTIONS,
  CARD_COLOR_OPTIONS,
  CARD_RARITY_OPTIONS,
  codeToDesc,
} from '@mtcg/common/types'

const route = useRoute()

// ========== 查询 ==========
const loading = ref(false)
const tableData = ref<CardVO[]>([])
const total = ref(0)
const query = reactive<CardQueryDTO>({
  cardName: '',
  cardType: '',
  color: '',
  rarity: '',
  productCode: (route.query.productCode as string) || '',
  page: 1,
  size: 10,
})

async function loadData() {
  loading.value = true
  try {
    const page = await client.admin.listCards({ ...query })
    tableData.value = page.records
    total.value = page.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  query.cardName = ''
  query.cardType = ''
  query.color = ''
  query.rarity = ''
  query.productCode = ''
  query.page = 1
  loadData()
}

// 枚举显示
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

// ========== 新增/编辑弹窗 ==========
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive({
  cardCode: '',
  productCode: '',
  cardName: '',
  cardType: 'CHARACTER' as CardType,
  level: null as number | null,
  color: '' as CardColor | '',
  environment: '',
  traits: '',
  attackRange: null as number | null,
  power: null as number | null,
  rarity: 'C' as CardRarity,
  effectText: '',
  effectJson: '',
  imagePath: '',
})

const rules = computed<FormRules>(() => ({
  cardCode: [{ required: true, message: '请输入卡牌编号', trigger: 'blur' }],
  cardName: [{ required: true, message: '请输入卡牌名称', trigger: 'blur' }],
  cardType: [{ required: true, message: '请选择卡牌类型', trigger: 'change' }],
  rarity: [{ required: true, message: '请选择稀有度', trigger: 'change' }],
}))

function openCreate() {
  dialogMode.value = 'create'
  editingId.value = null
  Object.assign(form, {
    cardCode: '', productCode: '', cardName: '',
    cardType: 'CHARACTER', level: null, color: '',
    environment: '', traits: '', attackRange: null, power: null,
    rarity: 'C', effectText: '', effectJson: '', imagePath: '',
  })
  dialogVisible.value = true
}

function openEdit(row: CardVO) {
  dialogMode.value = 'edit'
  editingId.value = row.id
  Object.assign(form, {
    cardCode: row.cardCode,
    productCode: row.productCode ?? '',
    cardName: row.cardName,
    cardType: row.cardType,
    level: row.level ?? null,
    color: row.color ?? '',
    environment: row.environment ?? '',
    traits: row.traits ?? '',
    attackRange: row.attackRange ?? null,
    power: row.power ?? null,
    rarity: row.rarity,
    effectText: row.effectText ?? '',
    effectJson: row.effectJson ?? '',
    imagePath: row.imagePath ?? '',
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
        const dto: CardCreateDTO = {
          cardCode: form.cardCode,
          productCode: form.productCode || undefined,
          cardName: form.cardName,
          cardType: form.cardType,
          level: form.level ?? undefined,
          color: (form.color as CardColor) || undefined,
          environment: form.environment || undefined,
          traits: form.traits || undefined,
          attackRange: form.attackRange ?? undefined,
          power: form.power ?? undefined,
          rarity: form.rarity,
          effectText: form.effectText || undefined,
          effectJson: form.effectJson || undefined,
          imagePath: form.imagePath || undefined,
        }
        await client.admin.createCard(dto)
        ElMessage.success('新增成功')
      } else if (editingId.value !== null) {
        const dto: CardUpdateDTO = {
          productCode: form.productCode || undefined,
          cardName: form.cardName,
          cardType: form.cardType,
          level: form.level ?? undefined,
          color: (form.color as CardColor) || undefined,
          environment: form.environment || undefined,
          traits: form.traits || undefined,
          attackRange: form.attackRange ?? undefined,
          power: form.power ?? undefined,
          rarity: form.rarity,
          effectText: form.effectText || undefined,
          effectJson: form.effectJson || undefined,
          imagePath: form.imagePath || undefined,
        }
        await client.admin.updateCard(editingId.value, dto)
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
async function handleDelete(row: CardVO) {
  try {
    await ElMessageBox.confirm(`确认删除卡牌「${row.cardName}」吗？`, '提示', {
      type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消',
    })
    await client.admin.deleteCard(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      // 其余错误由拦截器统一弹提示
    }
  }
}

onMounted(() => { loadData() })
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
        <el-form-item label="产品">
          <el-input v-model="query.productCode" placeholder="产品编号" clearable style="width: 140px" />
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
        <el-table-column prop="cardName" label="名称" min-width="160" show-overflow-tooltip />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">{{ typeLabel(row.cardType) }}</template>
        </el-table-column>
        <el-table-column label="颜色" width="80">
          <template #default="{ row }">{{ colorLabel(row.color ?? '') }}</template>
        </el-table-column>
        <el-table-column prop="level" label="Lv" width="60" />
        <el-table-column prop="power" label="战力" width="80" />
        <el-table-column prop="attackRange" label="R" width="60" />
        <el-table-column label="稀有度" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="rarityTagType(row.rarity)">{{ rarityLabel(row.rarity) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
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
      :title="dialogMode === 'create' ? '新增卡牌' : '编辑卡牌'"
      width="680px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        label-position="right"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="卡牌编号" prop="cardCode">
              <el-input v-model="form.cardCode" :disabled="dialogMode === 'edit'" maxlength="32" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属产品">
              <el-input v-model="form.productCode" maxlength="16" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="卡牌名称" prop="cardName">
              <el-input v-model="form.cardName" maxlength="128" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="卡牌类型" prop="cardType">
              <el-select v-model="form.cardType" style="width: 100%">
                <el-option v-for="o in CARD_TYPE_OPTIONS" :key="o.code" :label="o.desc" :value="o.code" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="颜色">
              <el-select v-model="form.color" clearable style="width: 100%">
                <el-option v-for="o in CARD_COLOR_OPTIONS" :key="o.code" :label="o.desc" :value="o.code" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="稀有度" prop="rarity">
              <el-select v-model="form.rarity" style="width: 100%">
                <el-option
                  v-for="o in CARD_RARITY_OPTIONS"
                  :key="o.code"
                  :label="`${o.code} - ${o.desc}`"
                  :value="o.code"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="等级">
              <el-input-number v-model="form.level" :min="1" :max="6" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="战力">
              <el-input-number v-model="form.power" :min="0" :max="30000" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="攻击距离">
              <el-input-number v-model="form.attackRange" :min="0" :max="5" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="环境">
              <el-input v-model="form.environment" maxlength="16" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="特征">
              <el-input v-model="form.traits" maxlength="256" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="效果文本">
              <el-input v-model="form.effectText" type="textarea" :rows="3" maxlength="500" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="效果JSON">
              <el-input v-model="form.effectJson" type="textarea" :rows="2" placeholder="{...}" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="卡图路径">
              <el-input v-model="form.imagePath" maxlength="256" placeholder="/cards/xxx.png" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.card-list {
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
.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
