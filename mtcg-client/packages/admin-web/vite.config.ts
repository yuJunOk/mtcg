import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  base: './',
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 5175,
    proxy: {
      // API 请求代理到后端
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      // 卡图 / 上传文件：/files/... → /api/files/...
      '/files': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path: string) => `/api${path}`,
      },
      // 兼容旧路径
      '/uploads': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path: string) => `/api/files${path.slice('/uploads'.length)}`,
      },
    },
  },
})
