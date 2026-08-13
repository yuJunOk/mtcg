<script setup lang="ts">
/**
 * 产品新增/编辑弹窗
 */
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { adminProductApi } from '@mtcg/common/api'
import { resolveCardImageUrl } from '@mtcg/common'
import ImageUploader from './ImageUploader.vue'
import type { ProductVO, ProductCreateDTO, ProductUpdateDTO } from '@mtcg/common/types'

export type ProductFormMode = 'create' | 'edit'

const props = defineProps<{
  visible: boolean
  mode: ProductFormMode
  product?: ProductVO | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  success: []
}>()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive({
  productCode: '',
  productName: '',
  releaseDate: '',
  description: '',
  imageFile: null as File | null,
  imagePath: null as string | null,
})

const rules: FormRules = {
  productCode: [
    { required: true, message: '请输入产品编号', trigger: 'blur' },
    { max: 16, message: '最多 16 字符', trigger: 'blur' },
  ],
  productName: [
    { required: true, message: '请输入产品名称', trigger: 'blur' },
    { max: 128, message: '最多 128 字符', trigger: 'blur' },
  ],
}

const dialogVisible = computed({
  get: () => props.visible,
  set: (v: boolean) => emit('update:visible', v),
})

const title = computed(() => (props.mode === 'create' ? '新增产品' : '编辑产品'))

function resetForm() {
  Object.assign(form, {
    productCode: '',
    productName: '',
    releaseDate: '',
    description: '',
    imageFile: null,
    imagePath: null,
  })
}

function fillFromProduct(row: ProductVO) {
  Object.assign(form, {
    productCode: row.productCode,
    productName: row.productName,
    releaseDate: row.releaseDate ?? '',
    description: row.description ?? '',
    imageFile: null,
    imagePath: row.imagePath ?? null,
  })
}

async function uploadImage(productId: number, file: File): Promise<string | null> {
  try {
    const formData = new FormData()
    formData.append('file', file)
    return await adminProductApi.uploadImage(productId, formData)
  } catch {
    ElMessage.error('图片上传失败')
    return null
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (props.mode === 'create') {
        const dto: ProductCreateDTO = {
          productCode: form.productCode,
          productName: form.productName,
          releaseDate: form.releaseDate || undefined,
          description: form.description || undefined,
        }
        const id = await adminProductApi.create(dto, submitting)
        if (form.imageFile) {
          await uploadImage(id, form.imageFile)
        }
        ElMessage.success('新增成功')
      } else if (props.product?.id != null) {
        const dto: ProductUpdateDTO = {
          productName: form.productName,
          releaseDate: form.releaseDate || undefined,
          description: form.description || undefined,
        }
        await adminProductApi.update(props.product.id, dto, submitting)
        if (form.imageFile) {
          await uploadImage(props.product.id, form.imageFile)
        }
        ElMessage.success('更新成功')
      }
      dialogVisible.value = false
      emit('success')
    } finally {
      submitting.value = false
    }
  })
}

watch(
  () => props.visible,
  (vis) => {
    if (!vis) return
    if (props.mode === 'create') resetForm()
    else if (props.product) fillFromProduct(props.product)
  },
)
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    width="560px"
    :close-on-click-modal="false"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="90px"
      label-position="right"
    >
      <el-form-item label="产品编号" prop="productCode">
        <el-input v-model="form.productCode" :disabled="mode === 'edit'" maxlength="16" show-word-limit />
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
      <el-form-item label="产品图">
        <div v-if="mode === 'edit' && form.imagePath && !form.imageFile" class="existing-image">
          <el-image
            :src="resolveCardImageUrl(form.imagePath)"
            fit="contain"
            class="image-preview"
            :preview-src-list="[resolveCardImageUrl(form.imagePath)]"
          />
          <div class="image-tip">已有图片，可重新上传替换</div>
        </div>
        <ImageUploader v-model="form.imageFile" />
      </el-form-item>
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
