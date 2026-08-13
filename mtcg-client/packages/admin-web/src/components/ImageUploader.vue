<script setup lang="ts">
/**
 * 图片上传组件（仅本地预览，提交表单时由父组件上传）
 *
 * - 单图：v-model = File | null
 * - 多图：multiple + @select，每次追加后仍可继续选择
 */
import { ref, watch } from 'vue'
import { ElImage, ElButton, ElIcon, ElMessage } from 'element-plus'
import { Delete, Upload } from '@element-plus/icons-vue'

const props = withDefaults(
  defineProps<{
    modelValue?: File | null
    /** 多选时不占用 v-model，通过 select 事件回传 */
    multiple?: boolean
    accept?: string
    maxSize?: number
  }>(),
  {
    modelValue: null,
    multiple: false,
    accept: 'image/*',
    maxSize: 5,
  },
)

const emit = defineEmits<{
  'update:modelValue': [value: File | null]
  select: [files: File[]]
}>()

const dragOver = ref(false)
const inputRef = ref<HTMLInputElement>()
const previewUrl = ref('')

watch(
  () => props.modelValue,
  (newFile) => {
    if (previewUrl.value) {
      URL.revokeObjectURL(previewUrl.value)
      previewUrl.value = ''
    }
    if (newFile && !props.multiple) {
      previewUrl.value = URL.createObjectURL(newFile)
    }
  },
  { immediate: true },
)

function handleClick() {
  inputRef.value?.click()
}

function handleDragOver(e: DragEvent) {
  e.preventDefault()
  dragOver.value = true
}

function handleDragLeave() {
  dragOver.value = false
}

function handleDrop(e: DragEvent) {
  e.preventDefault()
  dragOver.value = false
  const list = e.dataTransfer?.files
  if (list?.length) {
    handleFiles(Array.from(list))
  }
}

function handleChange(e: Event) {
  const input = e.target as HTMLInputElement
  const list = input.files
  if (list?.length) {
    handleFiles(Array.from(list))
  }
  input.value = ''
}

function validateFile(file: File): boolean {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return false
  }
  if (file.size / 1024 / 1024 > props.maxSize) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB`)
    return false
  }
  return true
}

function handleFiles(files: File[]) {
  const valid = files.filter(validateFile)
  if (valid.length === 0) return
  if (props.multiple) {
    emit('select', valid)
    return
  }
  emit('update:modelValue', valid[0])
}

function clearImage() {
  emit('update:modelValue', null)
}

function handleAreaClick() {
  handleClick()
}
</script>

<template>
  <div class="image-uploader">
    <!-- 单图预览 -->
    <div v-if="!multiple && modelValue" class="preview-container">
      <el-image
        :src="previewUrl"
        fit="contain"
        class="preview-image"
        :preview-src-list="[previewUrl]"
      />
      <div class="preview-actions">
        <el-button size="small" type="danger" @click.stop="clearImage">
          <el-icon><Delete /></el-icon>
          移除
        </el-button>
      </div>
    </div>

    <!-- 上传区：多图始终显示；单图在无文件时显示 -->
    <div v-if="multiple || !modelValue" class="upload-container">
      <input
        ref="inputRef"
        type="file"
        :accept="accept"
        :multiple="multiple"
        class="upload-input"
        @change="handleChange"
      />
      <div
        class="upload-area"
        :class="{ 'drag-over': dragOver }"
        @click="handleAreaClick"
        @dragover="handleDragOver"
        @dragleave="handleDragLeave"
        @drop="handleDrop"
      >
        <el-icon class="upload-icon"><Upload /></el-icon>
        <div class="upload-text">
          {{ multiple ? '点击或拖拽，可一次选择多张图片' : '点击或拖拽图片到此处上传' }}
        </div>
        <div class="upload-hint">支持 JPG、PNG、GIF、WebP，最大 {{ maxSize }}MB</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.image-uploader {
  width: 100%;
}

.preview-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.preview-image {
  width: 100%;
  height: 160px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #f5f7fa;
}

.preview-image :deep(.el-image__inner) {
  object-fit: contain;
}

.preview-actions {
  display: flex;
  gap: 8px;
}

.upload-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.upload-input {
  display: none;
}

.upload-area {
  width: 100%;
  height: 140px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #fafafa;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.upload-area:hover {
  border-color: #409eff;
  background: #f0f7ff;
}

.upload-area.drag-over {
  border-color: #409eff;
  background: #ecf5ff;
  border-style: solid;
}

.upload-icon {
  font-size: 32px;
  color: #909399;
}

.upload-text {
  font-size: 14px;
  color: #606266;
}

.upload-hint {
  font-size: 12px;
  color: #909399;
}
</style>
