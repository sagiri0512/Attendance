<template>
  <div class="dashboard">
    <el-card class="welcome-card">
      <h2>欢迎回来，{{ userStore.user?.username || '' }}</h2>
      <p>今天是 {{ today }}</p>
    </el-card>

    <el-row :gutter="20" class="quick-actions">
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="action-card" @click="$router.push('/clock')">
          <h3>打卡</h3>
          <p>上下班打卡</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="action-card" @click="$router.push('/my-record')">
          <h3>我的记录</h3>
          <p>查看考勤记录</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="action-card" @click="$router.push('/leave/apply')">
          <h3>请假</h3>
          <p>提交请假申请</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const today = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
})
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}
.welcome-card {
  margin-bottom: 24px;
}
.welcome-card h2 {
  margin: 0 0 4px;
  font-size: 22px;
}
.welcome-card p {
  margin: 0;
  color: #909399;
}
.quick-actions {
  margin-top: 0;
}
.action-card {
  cursor: pointer;
  text-align: center;
  padding: 12px 0;
  transition: transform 0.2s;
}
.action-card:hover {
  transform: translateY(-2px);
}
.action-card h3 {
  margin: 0 0 4px;
  font-size: 18px;
}
.action-card p {
  margin: 0;
  color: #909399;
  font-size: 13px;
}

@media (max-width: 768px) {
  .welcome-card h2 {
    font-size: 18px;
  }
  .action-card {
    margin-bottom: 12px;
  }
}
</style>
