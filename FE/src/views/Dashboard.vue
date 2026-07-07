<template>
  <div class="dashboard">
    <!-- 顶部栏 -->
    <div class="topbar">
      <span class="topbar-title">考勤面板</span>
      <el-button type="danger" plain size="small" @click="handleLogout">退出登录</el-button>
    </div>

    <el-card class="welcome-card">
      <h2>考勤面板</h2>
      <p>欢迎回来</p>
    </el-card>

    <el-row :gutter="20" class="quick-actions">
      <el-col :span="8">
        <el-card shadow="hover" class="action-card" @click="$router.push('/clock')">
          <h3>打卡</h3>
          <p>上下班打卡</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="action-card" @click="$router.push('/my-record')">
          <h3>我的记录</h3>
          <p>查看考勤记录</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="action-card" @click="$router.push('/leave/apply')">
          <h3>请假</h3>
          <p>提交请假申请</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

async function handleLogout() {
  await userStore.logout()
  ElMessage.success('已退出')
  router.push('/login')
}
</script>

<style scoped>
.dashboard {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}
.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.topbar-title {
  font-size: 20px;
  font-weight: 600;
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
</style>
