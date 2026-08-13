/**
 * 游戏端全局反馈（Naive discrete API）
 * - toast：成功 / 失败 / 提示，任意模块可直接调用
 * - confirm：替代 window.confirm，主题跟随明暗
 */
import { computed, ref } from 'vue'
import {
  createDiscreteApi,
  darkTheme,
  type ConfigProviderProps,
  type DialogOptions,
} from 'naive-ui'
import { buildThemeOverrides } from './theme'

export type FeedbackThemeMode = 'dark' | 'light'

const themeMode = ref<FeedbackThemeMode>('light')

const configProviderPropsRef = computed<ConfigProviderProps>(() => ({
  theme: themeMode.value === 'dark' ? darkTheme : null,
  themeOverrides: buildThemeOverrides(themeMode.value),
}))

const { message, dialog } = createDiscreteApi(['message', 'dialog'], {
  configProviderProps: configProviderPropsRef,
})

/** 与 themeStore 同步，保证 Toast/确认框跟壳层主题一致 */
export function syncFeedbackTheme(mode: FeedbackThemeMode): void {
  themeMode.value = mode
}

export const toast = {
  success(content: string): void {
    message.success(content, { duration: 2800 })
  },
  error(content: string): void {
    message.error(content, { duration: 3600 })
  },
  info(content: string): void {
    message.info(content, { duration: 2800 })
  },
  warning(content: string): void {
    message.warning(content, { duration: 3200 })
  },
}

export interface ConfirmOptions {
  title?: string
  content: string
  positiveText?: string
  negativeText?: string
  /** 危险操作（删除、认输等） */
  danger?: boolean
}

/** Promise 版确认框；点确定 true，取消/关闭 false */
export function confirm(options: ConfirmOptions): Promise<boolean> {
  return new Promise((resolve) => {
    let settled = false
    const finish = (ok: boolean) => {
      if (settled) return
      settled = true
      resolve(ok)
    }

    const base: DialogOptions = {
      title: options.title ?? '确认',
      content: options.content,
      positiveText: options.positiveText ?? '确定',
      negativeText: options.negativeText ?? '取消',
      maskClosable: true,
      onPositiveClick: () => {
        finish(true)
      },
      onNegativeClick: () => {
        finish(false)
      },
      onClose: () => {
        finish(false)
      },
      onMaskClick: () => {
        finish(false)
      },
    }

    if (options.danger) {
      dialog.error(base)
    } else {
      dialog.warning(base)
    }
  })
}

export { buildThemeOverrides } from './theme'
