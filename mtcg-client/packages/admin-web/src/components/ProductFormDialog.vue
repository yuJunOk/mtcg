<script setup lang="ts">
/**
 * 产品新增/编辑弹窗（支持多图：追加上传 / 单张删除）
 */
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { adminProductApi } from '@mtcg/common/api'
import { productCoverPath, resolveCardImageUrl } from '@mtcg/common'
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
/** 当前选择器内的单文件；选中后立刻记入 imageFiles */
const pendingFile = ref<File | null>(null)
const form = reactive({
  productCode: '',
  productName: '',
  releaseDate: '',
  description: '',
  imageFiles: [] as File[],
  imagePaths: [] as string[],
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

const previewUrls = computed(() => form.imagePaths.map((p) => resolveCardImageUrl(p)))

function resetForm(): void {
  Object.assign(form, {
    productCode: '',
    productName: '',
    releaseDate: '',
    description: '',
    imageFiles: [],
    imagePaths: [],
  })
  pendingFile.value = null
}

function fillFromProduct(row: ProductVO): void {
  const paths =
    row.imagePaths?.filter((p) => Boolean(p?.trim())) ??
    (productCoverPath(row) ? [productCoverPath(row)!] : [])
  Object.assign(form, {
    productCode: row.productCode,
    productName: row.productName,
    releaseDate: row.releaseDate ?? '',
    description: row.description ?? '',
    imageFiles: [],
    imagePaths: [...paths],
  })
  pendingFile.value = null
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

async function handleRemoveImage(path: string): Promise<void> {
  if (props.mode !== 'edit' || props.product?.id == null) {
    form.imagePaths = form.imagePaths.filter((p) => p !== path)
    return
  }
  try {
    await ElMessageBox.confirm('确定删除这张产品图？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }
  try {
    await adminProductApi.deleteImage(props.product.id, path)
    form.imagePaths = form.imagePaths.filter((p) => p !== path)
    ElMessage.success('已删除')
    emit('success')
  } catch {
    // notifier
  }
}

function removePendingFile(index: number): void {
  form.imageFiles.splice(index, 1)
}

async function handleSubmit(): Promise<void> {
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
        for (const file of form.imageFiles) {
          await uploadImage(id, file)
        }
        ElMessage.success('新增成功')
      } else if (props.product?.id != null) {
        const dto: ProductUpdateDTO = {
          productName: form.productName,
          releaseDate: form.releaseDate || undefined,
          description: form.description || undefined,
        }
        await adminProductApi.update(props.product.id, dto, submitting)
        for (const file of form.imageFiles) {
          const path = await uploadImage(props.product.id, file)
          if (path) form.imagePaths.push(path)
        }
        form.imageFiles = []
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

watch(pendingFile, (file) => {
  if (!file) return
  if (!form.imageFiles.includes(file)) {
    form.imageFiles.push(file)
  }
})
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    width="640px"
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
        <div v-if="form.imagePaths.length > 0" class="gallery">
          <div v-for="(path, idx) in form.imagePaths" :key="path" class="gallery-item">
            <el-image
              :src="resolveCardImageUrl(path)"
              fit="contain"
              class="image-preview"
              :preview-src-list="previewUrls"
              :initial-index="idx"
              preview-teleported
            />
            <el-button type="danger" link size="small" @click="handleRemoveImage(path)">删除</el-button>
          </div>
        </div>
        <div v-if="form.imageFiles.length > 0" class="pending">
          <div v-for="(file, idx) in form.imageFiles" :key="`${file.name}-${idx}`" class="pending-row">
            <span>{{ file.name }}</span>
            <el-button type="danger" link size="small" @click="removePendingFile(idx)">移除</el-button>
          </div>
        </div>
        <ImageUploader v-model="pendingFile" />
        <div class="image-tip">可多张：每次选择一张追加；首张作为列表封面</div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.gallery {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 12px;
  width: 100%;
}
.gallery-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}
.image-preview {
  width: 120px;
  height: 120px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #f5f7fa;
}
.image-preview :deep(.el-image__inner) {
  object-fit: contain;
}
.pending {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 8px;
  width: 100%;
}
.pending-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  font-size: 12px;
  color: #606266;
}
.image-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>
