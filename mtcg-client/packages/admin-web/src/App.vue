<script setup lang="ts">
</script>

<template>
  <router-view />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: 'Microsoft YaHei', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 全局滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
}
::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

/* el-card 统一圆角 */
.el-card {
  border-radius: 8px;
}

/* el-table 表头加粗 */
.el-table th.el-table__cell {
  font-weight: 600;
  color: #303133;
  background: #f5f7fa;
}

/* el-dialog 圆角 */
.el-dialog {
  border-radius: 10px;
}

/* 列表页筛选/操作行：去掉 inline form-item 底部多余空白 */
.search-card .el-card__body {
  padding: 16px;
}
.search-card .el-form--inline .el-form-item {
  margin-bottom: 0;
  margin-right: 16px;
}
.search-card .el-form--inline .el-form-item:last-child {
  margin-right: 0;
}
</style>
