<script setup lang="ts">
/**
 * 产品新增/编辑弹窗（支持多图：一次多选 / 追加上传 / 单张删除）
 */
import { ref, reactive, watch, computed, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { adminProductApi } from '@mtcg/common/api'
import { productCoverPath, resolveCardImageUrl } from '@mtcg/common'
import ImageUploader from './ImageUploader.vue'
import {
  PRODUCT_CATEGORY_OPTIONS,
  resolveProductCategory,
} from '@mtcg/common/types'
import type { ProductVO, ProductCreateDTO, ProductUpdateDTO, ProductCategory } from '@mtcg/common/types'

export type ProductFormMode = 'create' | 'edit'

interface PendingImage {
  file: File
  previewUrl: string
}

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
  category: 'OTHER' as ProductCategory,
  releaseDate: '',
  description: '',
  pendingImages: [] as PendingImage[],
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
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
}

const dialogVisible = computed({
  get: () => props.visible,
  set: (v: boolean) => emit('update:visible', v),
})

const title = computed(() => (props.mode === 'create' ? '新增产品' : '编辑产品'))

const previewUrls = computed(() => form.imagePaths.map((p) => resolveCardImageUrl(p)))

function clearPendingImages(): void {
  for (const item of form.pendingImages) {
    URL.revokeObjectURL(item.previewUrl)
  }
  form.pendingImages = []
}

function resetForm(): void {
  clearPendingImages()
  Object.assign(form, {
    productCode: '',
    productName: '',
    category: 'OTHER',
    releaseDate: '',
    description: '',
    imagePaths: [],
  })
}

function fillFromProduct(row: ProductVO): void {
  clearPendingImages()
  const paths =
    row.imagePaths?.filter((p) => Boolean(p?.trim())) ??
    (productCoverPath(row) ? [productCoverPath(row)!] : [])
  Object.assign(form, {
    productCode: row.productCode,
    productName: row.productName,
    category: resolveProductCategory(row),
    releaseDate: row.releaseDate ?? '',
    description: row.description ?? '',
    imagePaths: [...paths],
  })
}

function onSelectFiles(files: File[]): void {
  for (const file of files) {
    form.pendingImages.push({
      file,
      previewUrl: URL.createObjectURL(file),
    })
  }
}

function removePendingImage(index: number): void {
  const [removed] = form.pendingImages.splice(index, 1)
  if (removed) URL.revokeObjectURL(removed.previewUrl)
}

async function uploadImage(productId: number, file: File): Promise<string | null> {
  try {
    const formData = new FormData()
    formData.append('file', file)
    return await adminProductApi.uploadImage(productId, formData)
  } catch {
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

async function handleSubmit(): Promise<void> {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      let productId: number | null = null
      if (props.mode === 'create') {
        const dto: ProductCreateDTO = {
          productCode: form.productCode,
          productName: form.productName,
          category: form.category,
          releaseDate: form.releaseDate || undefined,
          description: form.description || undefined,
        }
        productId = await adminProductApi.create(dto, submitting)
      } else if (props.product?.id != null) {
        const dto: ProductUpdateDTO = {
          productName: form.productName,
          category: form.category,
          releaseDate: form.releaseDate || undefined,
          description: form.description || undefined,
        }
        await adminProductApi.update(props.product.id, dto, submitting)
        productId = props.product.id
      }
      if (productId == null) return

      let uploadFail = 0
      for (const item of form.pendingImages) {
        const path = await uploadImage(productId, item.file)
        if (path) {
          if (props.mode === 'edit') form.imagePaths.push(path)
        } else {
          uploadFail += 1
        }
      }
      clearPendingImages()

      if (uploadFail > 0) {
        ElMessage.warning(
          props.mode === 'create'
            ? `产品已保存，但有 ${uploadFail} 张图片上传失败`
            : `资料已保存，但有 ${uploadFail} 张图片上传失败`,
        )
      } else {
        ElMessage.success(props.mode === 'create' ? '新增成功' : '更新成功')
      }
      dialogVisible.value = false
      emit('success')
    } finally {
      submitting.value = false
    }
  })
}

watch(
  () => form.productCode,
  (code) => {
    if (props.mode !== 'create') return
    form.category = resolveProductCategory({ productCode: code })
  },
)

watch(
  () => props.visible,
  (vis) => {
    if (!vis) return
    if (props.mode === 'create') resetForm()
    else if (props.product) fillFromProduct(props.product)
  },
)

onBeforeUnmount(() => {
  clearPendingImages()
})
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    width="640px"
    top="6vh"
    append-to-body
    :close-on-click-modal="false"
    class="product-form-dialog"
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
      <el-form-item label="分类" prop="category">
        <el-select v-model="form.category" style="width: 100%">
          <el-option
            v-for="o in PRODUCT_CATEGORY_OPTIONS"
            :key="o.code"
            :label="o.desc"
            :value="o.code"
          />
        </el-select>
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
        <el-input v-model="form.description" type="textarea" :rows="2" maxlength="500" show-word-limit />
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
        <div v-if="form.pendingImages.length > 0" class="gallery">
          <div
            v-for="(item, idx) in form.pendingImages"
            :key="`${item.file.name}-${idx}`"
            class="gallery-item"
          >
            <el-image :src="item.previewUrl" fit="contain" class="image-preview" />
            <el-button type="danger" link size="small" @click="removePendingImage(idx)">移除</el-button>
          </div>
        </div>
        <ImageUploader multiple @select="onSelectFiles" />
        <div class="image-tip">可一次选择多张；首张作为列表封面</div>
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
  width: 96px;
  height: 96px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #f5f7fa;
}
.image-preview :deep(.el-image__inner) {
  object-fit: contain;
}
.image-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>

<style>
/* 挂到 body 的弹窗：靠上 + 限高，避免顶出页面滚动条 */
.product-form-dialog.el-dialog {
  margin-top: 6vh !important;
  margin-bottom: 0;
  max-height: 88vh;
  display: flex;
  flex-direction: column;
}
.product-form-dialog .el-dialog__body {
  flex: 1;
  min-height: 0;
  overflow-x: hidden;
  overflow-y: auto;
  max-height: calc(88vh - 130px);
  padding-top: 12px;
  padding-bottom: 8px;
}
</style>
