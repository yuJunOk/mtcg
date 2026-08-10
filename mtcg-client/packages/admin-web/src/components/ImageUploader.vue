<script setup lang="ts">
/**
 * 图片上传组件（仅本地预览，提交表单时由父组件上传）
 * 
 * 功能：
 * - 支持拖拽/点击选择图片
 * - 本地预览
 * - 支持清除
 * 
 * 使用方式：
 * <ImageUploader v-model="file" />
 * // file 为 File | null
 */
import { ref, watch } from 'vue'
import { ElImage, ElButton, ElIcon, ElMessage } from 'element-plus'
import { Delete, Upload } from '@element-plus/icons-vue'

// Props 定义
const props = defineProps<{
  modelValue: File | null  // 选中的文件
  accept?: string  // 接受的文件类型，默认图片
  maxSize?: number  // 最大文件大小(MB)，默认 5MB
}>()

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: File | null]
}>()

// ========== 状态 ==========
const dragOver = ref(false)
const inputRef = ref<HTMLInputElement>()
const previewUrl = ref('')

// 监听文件变化，正确释放旧 object URL
watch(
  () => props.modelValue,
  (newFile) => {
    if (previewUrl.value) {
      URL.revokeObjectURL(previewUrl.value)
      previewUrl.value = ''
    }
    if (newFile) {
      previewUrl.value = URL.createObjectURL(newFile)
    }
  },
  { immediate: true },
)

// ========== 方法 ==========

// 点击选择
function handleClick() {
  inputRef.value?.click()
}

// 拖拽悬停
function handleDragOver(e: DragEvent) {
  e.preventDefault()
  dragOver.value = true
}

function handleDragLeave() {
  dragOver.value = false
}

// 放下文件
function handleDrop(e: DragEvent) {
  e.preventDefault()
  dragOver.value = false
  const file = e.dataTransfer?.files?.[0]
  if (file) {
    handleFile(file)
  }
}

// 选择文件
function handleChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (file) {
    handleFile(file)
  }
  // 清空 input，允许重复选择同一文件
  input.value = ''
}

// 处理文件
function handleFile(file: File) {
  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }
  // 检查文件大小
  const maxSize = props.maxSize || 5
  if (file.size / 1024 / 1024 > maxSize) {
    ElMessage.error(`图片大小不能超过 ${maxSize}MB`)
    return
  }
  emit('update:modelValue', file)
}

// 清除图片
function clearImage() {
  emit('update:modelValue', null)
}

// 点击上传区域
function handleAreaClick() {
  handleClick()
}
</script>

<template>
  <div class="image-uploader">
    <!-- 显示模式：有图片 -->
    <div v-if="modelValue" class="preview-container">
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

    <!-- 上传模式：无图片 -->
    <div v-else class="upload-container">
      <input
        ref="inputRef"
        type="file"
        :accept="accept || 'image/*'"
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
        <div class="upload-text">点击或拖拽图片到此处上传</div>
        <div class="upload-hint">支持 JPG、PNG、GIF、WebP，最大 {{ maxSize || 5 }}MB</div>
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
