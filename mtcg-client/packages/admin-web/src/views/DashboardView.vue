<script setup lang="ts">
</script>

<template>
  <div class="dashboard">
    <el-card class="stat-card">
      <template #header>卡牌统计</template>
      <div class="stat-value">--</div>
      <div class="stat-label">卡牌总数</div>
    </el-card>
    <el-card class="stat-card">
      <template #header>产品统计</template>
      <div class="stat-value">--</div>
      <div class="stat-label">产品总数</div>
    </el-card>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  gap: 20px;
}

.stat-card {
  width: 240px;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #4a90d9;
}

.stat-label {
  color: #888;
  font-size: 14px;
  margin-top: 4px;
}
</style>