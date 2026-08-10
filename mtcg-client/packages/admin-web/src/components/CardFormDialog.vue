<script setup lang="ts">
/**
 * 卡牌新增/编辑弹窗
 * - 创建：提交后按 selectedFeatureIds 逐个 addFeature
 * - 编辑：打开时拉取已关联特征；提交时 diff 增删关联
 * - 不发送伪造 effectJson
 */
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { adminCardApi } from '@mtcg/common/api'
import ImageUploader from './ImageUploader.vue'
import CardFeatureSelector from './CardFeatureSelector.vue'
import ProductSelector from './ProductSelector.vue'
import {
  CARD_TYPE_OPTIONS,
  CARD_COLOR_OPTIONS,
  CARD_RARITY_OPTIONS,
} from '@mtcg/common/types'
import type { CardVO, CardType, CardColor, CardRarity } from '@mtcg/common/types'

export type CardFormMode = 'create' | 'edit'

const props = defineProps<{
  visible: boolean
  mode: CardFormMode
  card?: CardVO | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  success: []
}>()

// ========== 状态 ==========
const formRef = ref<FormInstance>()
const submitting = ref(false)
const featuresLoading = ref(false)
const selectedFeatureIds = ref<number[]>([])
/** 编辑打开时的原始特征 id，用于提交时 diff */
const initialFeatureIds = ref<number[]>([])

const form = reactive({
  cardCode: '',
  productCode: '',
  cardName: '',
  cardType: 'CHARACTER' as CardType,
  level: null as number | null,
  color: '' as CardColor | '',
  environment: '',
  attackRange: null as number | null,
  power: null as number | null,
  rarity: 'C' as CardRarity,
  effectText: '',
  imageFile: null as File | null,
  imagePath: null as string | null,
})

const rules: FormRules = {
  cardCode: [{ required: true, message: '请输入卡牌编号', trigger: 'blur' }],
  cardName: [{ required: true, message: '请输入卡牌名称', trigger: 'blur' }],
  cardType: [{ required: true, message: '请选择卡牌类型', trigger: 'change' }],
  rarity: [{ required: true, message: '请选择稀有度', trigger: 'change' }],
}

const dialogVisible = computed({
  get: () => props.visible,
  set: (v: boolean) => emit('update:visible', v),
})

const title = computed(() => (props.mode === 'create' ? '新增卡牌' : '编辑卡牌'))

// ========== 方法 ==========
function resetForm() {
  Object.assign(form, {
    cardCode: '',
    productCode: '',
    cardName: '',
    cardType: 'CHARACTER',
    level: null,
    color: '',
    environment: '',
    attackRange: null,
    power: null,
    rarity: 'C',
    effectText: '',
    imageFile: null,
    imagePath: null,
  })
  selectedFeatureIds.value = []
  initialFeatureIds.value = []
}

function fillFromCard(row: CardVO) {
  Object.assign(form, {
    cardCode: row.cardCode,
    productCode: row.productCode ?? '',
    cardName: row.cardName,
    cardType: row.cardType as CardType,
    level: row.level ?? null,
    color: (row.color ?? '') as CardColor,
    environment: row.environment ?? '',
    attackRange: row.attackRange ?? null,
    power: row.power ?? null,
    rarity: (row.rarity ?? 'C') as CardRarity,
    effectText: row.effectText ?? '',
    imageFile: null,
    imagePath: row.imagePath,
  })
}

async function loadCardFeatures(cardId: number) {
  featuresLoading.value = true
  try {
    const list = await adminCardApi.listFeatures(cardId)
    const ids = list.map((f) => f.id)
    selectedFeatureIds.value = ids
    initialFeatureIds.value = [...ids]
  } finally {
    featuresLoading.value = false
  }
}

async function uploadImage(cardId: number, file: File): Promise<string | null> {
  try {
    const formData = new FormData()
    formData.append('file', file)
    return await adminCardApi.uploadImage(cardId, formData)
  } catch {
    ElMessage.error('图片上传失败')
    return null
  }
}

async function syncFeatures(cardId: number) {
  const current = new Set(selectedFeatureIds.value)
  const initial = new Set(initialFeatureIds.value)
  for (const id of current) {
    if (!initial.has(id)) {
      await adminCardApi.addFeature(cardId, id)
    }
  }
  for (const id of initial) {
    if (!current.has(id)) {
      await adminCardApi.removeFeature(cardId, id)
    }
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const dto = {
        cardCode: form.cardCode,
        productCode: form.productCode || undefined,
        cardName: form.cardName,
        cardType: form.cardType,
        level: form.level ?? undefined,
        color: form.color || undefined,
        environment: form.environment || undefined,
        attackRange: form.attackRange ?? undefined,
        power: form.power ?? undefined,
        rarity: form.rarity,
        effectText: form.effectText || undefined,
        // 不发送伪造 effectJson；编辑时由后端保留原值
      }

      if (props.mode === 'create') {
        const id = await adminCardApi.create(dto, submitting)
        if (form.imageFile) {
          await uploadImage(id, form.imageFile)
        }
        for (const featureId of selectedFeatureIds.value) {
          await adminCardApi.addFeature(id, featureId)
        }
        ElMessage.success('新增成功')
      } else if (props.card?.id != null) {
        await adminCardApi.update(props.card.id, dto, submitting)
        if (form.imageFile) {
          await uploadImage(props.card.id, form.imageFile)
        }
        await syncFeatures(props.card.id)
        ElMessage.success('更新成功')
      }
      dialogVisible.value = false
      emit('success')
    } finally {
      submitting.value = false
    }
  })
}

// ========== 生命周期 / 监听 ==========
watch(
  () => props.visible,
  async (vis) => {
    if (!vis) return
    if (props.mode === 'create') {
      resetForm()
    } else if (props.card) {
      fillFromCard(props.card)
      await loadCardFeatures(props.card.id)
    }
  },
)
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    width="820px"
    :close-on-click-modal="false"
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
            <el-input v-model="form.cardCode" :disabled="mode === 'edit'" maxlength="32" show-word-limit />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属产品">
            <ProductSelector v-model="form.productCode" />
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
        <el-col :span="8">
          <el-form-item label="环境">
            <el-input v-model="form.environment" maxlength="16" />
          </el-form-item>
        </el-col>
        <el-col :span="16">
          <el-form-item label="特征" v-loading="featuresLoading">
            <CardFeatureSelector v-model="selectedFeatureIds" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="效果文本">
            <el-input
              v-model="form.effectText"
              type="textarea"
              :rows="3"
              maxlength="500"
              show-word-limit
              placeholder="输入效果描述（结构化效果由后续引擎迭代接入）"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="卡图">
            <div v-if="mode === 'edit' && form.imagePath && !form.imageFile" class="existing-image">
              <el-image
                :src="form.imagePath"
                fit="contain"
                class="image-preview"
                :preview-src-list="[form.imagePath]"
              />
              <div class="image-tip">已有图片，可重新上传替换</div>
            </div>
            <ImageUploader v-model="form.imageFile" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.existing-image {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}
.image-preview {
  width: 100%;
  height: 120px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #f5f7fa;
}
.image-preview :deep(.el-image__inner) {
  object-fit: contain;
}
.image-tip {
  font-size: 12px;
  color: #909399;
}
</style>
