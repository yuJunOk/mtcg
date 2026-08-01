<script setup lang="ts">
</script>

<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-value">--</div>
          <div class="stat-label">卡牌总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-value">--</div>
          <div class="stat-label">产品总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-value">--</div>
          <div class="stat-label">用户总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-value">--</div>
          <div class="stat-label">今日对局</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>快速操作</span>
          </template>
          <el-button type="primary">新增卡牌</el-button>
          <el-button>新增产品</el-button>
          <el-button>卡牌列表</el-button>
          <el-button>产品列表</el-button>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>系统状态</span>
          </template>
          <div class="status-item">
            <el-tag type="success" size="small">运行中</el-tag>
            <span style="margin-left: 8px">后端服务</span>
            <span style="margin-left: auto; color: #909399">等待启动</span>
          </div>
          <div class="status-item">
            <el-tag type="warning" size="small">待连接</el-tag>
            <span style="margin-left: 8px">数据库</span>
            <span style="margin-left: auto; color: #909399">等待连接</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  text-align: center;
  margin-top: 4px;
}

.status-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.status-item:last-child {
  border-bottom: none;
}
</style>