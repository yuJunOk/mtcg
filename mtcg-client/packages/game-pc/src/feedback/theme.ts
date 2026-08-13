import type { GlobalThemeOverrides } from 'naive-ui'

/** 将 Naive 主题对齐游戏端设计 token（青绿强调色） */
export function buildThemeOverrides(mode: 'dark' | 'light'): GlobalThemeOverrides {
  if (mode === 'dark') {
    return {
      common: {
        primaryColor: '#2EE6C5',
        primaryColorHover: '#4AEDD1',
        primaryColorPressed: '#1BC4A8',
        primaryColorSuppl: '#2EE6C5',
        successColor: '#4CAF50',
        errorColor: '#E53935',
        infoColor: '#6B7FD7',
        warningColor: '#C9A227',
        borderRadius: '6px',
        fontFamily:
          "PingFang SC, Microsoft YaHei, 'Helvetica Neue', Arial, sans-serif",
      },
      Message: {
        borderRadius: '10px',
      },
      Dialog: {
        borderRadius: '14px',
      },
    }
  }

  return {
    common: {
      primaryColor: '#0F9B86',
      primaryColorHover: '#12B09A',
      primaryColorPressed: '#0C8573',
      primaryColorSuppl: '#0F9B86',
      successColor: '#2E7D32',
      errorColor: '#D32F2F',
      infoColor: '#3D5AFE',
      warningColor: '#B8860B',
      borderRadius: '6px',
      fontFamily:
        "PingFang SC, Microsoft YaHei, 'Helvetica Neue', Arial, sans-serif",
    },
    Message: {
      borderRadius: '10px',
    },
    Dialog: {
      borderRadius: '14px',
    },
  }
}
