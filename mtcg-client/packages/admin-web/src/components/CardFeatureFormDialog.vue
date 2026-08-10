<script setup lang="ts">
/**
 * 卡牌特征新增/编辑弹窗（普通特征值，无颜色）
 */
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { cardFeatureApi } from '@mtcg/common/api'
import type { CardFeatureVO, CardFeatureCreateDTO, CardFeatureUpdateDTO } from '@mtcg/common/types'

export type CardFeatureFormMode = 'create' | 'edit'

const props = defineProps<{
  visible: boolean
  mode: CardFeatureFormMode
  feature?: CardFeatureVO | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  success: []
}>()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive({
  code: '',
  name: '',
})

const rules: FormRules = {
  code: [
    { required: true, message: '请输入特征编码', trigger: 'blur' },
    { max: 32, message: '最多 32 字符', trigger: 'blur' },
    { pattern: /^[a-z0-9_]+$/, message: '只能包含小写字母、数字和下划线', trigger: 'blur' },
  ],
  name: [
    { required: true, message: '请输入特征名称', trigger: 'blur' },
    { max: 64, message: '最多 64 字符', trigger: 'blur' },
  ],
}

const dialogVisible = computed({
  get: () => props.visible,
  set: (v: boolean) => emit('update:visible', v),
})

const title = computed(() => (props.mode === 'create' ? '新增特征' : '编辑特征'))

function resetForm() {
  Object.assign(form, { code: '', name: '' })
}

function fillFromFeature(row: CardFeatureVO) {
  Object.assign(form, {
    code: row.code,
    name: row.name,
  })
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (props.mode === 'create') {
        await cardFeatureApi.create(
          {
            code: form.code,
            name: form.name,
          } as CardFeatureCreateDTO,
          submitting,
        )
        ElMessage.success('新增成功')
      } else if (props.feature?.id != null) {
        await cardFeatureApi.update(
          props.feature.id,
          {
            name: form.name,
          } as CardFeatureUpdateDTO,
          submitting,
        )
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
    else if (props.feature) fillFromFeature(props.feature)
  },
)
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    width="480px"
    :close-on-click-modal="false"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="90px"
      label-position="right"
    >
      <el-form-item v-if="mode === 'create'" label="特征编码" prop="code">
        <el-input v-model="form.code" maxlength="32" show-word-limit placeholder="如: hero, villain" />
      </el-form-item>
      <el-form-item v-else label="特征编码">
        <el-input v-model="form.code" disabled />
      </el-form-item>
      <el-form-item label="特征名称" prop="name">
        <el-input v-model="form.name" maxlength="64" show-word-limit />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>
