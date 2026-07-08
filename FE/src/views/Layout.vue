<template>
  <div class="layout">
    <!-- 左侧栏 -->
    <aside class="sidebar">
      <!-- 1 区域：用户信息 -->
      <div class="user-card">
        <div class="user-info">
          <div class="user-name">{{ userName }}</div>
          <div class="user-role">{{ roleLabel }}</div>
          <div class="user-meta" v-if="userStore.user?.empNo">
            <span>{{ userStore.user.empNo }}</span>
          </div>
          <div class="user-meta" v-if="userStore.user?.PLName">
            <span>PL: {{ userStore.user.PLName }}</span>
          </div>
          <div class="user-meta" v-if="userStore.user?.PMName">
            <span>PM: {{ userStore.user.PMName }}</span>
          </div>
        </div>
        <el-button
          type="danger"
          plain
          size="small"
          class="logout-btn"
          @click="handleLogout"
        >
          退出登录
        </el-button>
      </div>

      <!-- 2 区域：菜单 -->
      <nav class="menu">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          :class="['menu-item', { active: isActive(item.path) }]"
        >
          <span class="menu-icon">{{ item.icon }}</span>
          <span class="menu-text">{{ item.label }}</span>
        </router-link>
      </nav>
    </aside>

    <!-- 右侧内容区 -->
    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const roleMap = {
  0: '员工 (PG)',
  1: 'PL',
  2: 'PM',
  3: 'HR/管理部',
  4: '系统管理员'
}

const userName = computed(() => userStore.user?.username || '未知用户')
const roleLabel = computed(() => roleMap[userStore.user?.role] || '未知角色')

const allMenu = [
  { path: '/clock', label: '打卡', icon: '⏰' },
  { path: '/my-record', label: '我的记录', icon: '📋' },
  { path: '/leave/apply', label: '请假申请', icon: '📝' },
  { path: '/leave/approve', label: '审批管理', icon: '✅', roles: [1, 2, 3, 4] },
  { path: '/stat', label: '统计报表', icon: '📊', roles: [3, 4] }
]

const menuItems = computed(() => {
  const role = userStore.user?.role
  return allMenu.filter(item => {
    if (!item.roles) return true
    return item.roles.includes(role)
  })
})

function isActive(path) {
  return route.path === path || route.path.startsWith(path + '/')
}

async function handleLogout() {
  await userStore.logout()
  ElMessage.success('已退出')
  router.push('/login')
}
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

.sidebar {
  width: 240px;
  background: #1a1a2e;
  color: #fff;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.user-card {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  text-align: center;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.user-role {
  font-size: 12px;
  color: #8b5cf6;
  background: rgba(139, 92, 246, 0.15);
  display: inline-block;
  padding: 2px 10px;
  border-radius: 10px;
  margin-bottom: 8px;
}

.user-meta {
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 4px;
}

.logout-btn {
  margin-top: 12px;
  width: 100%;
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.3);
  color: #ef4444;
}
.logout-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.5);
}

.menu {
  flex: 1;
  padding: 16px 12px;
  overflow-y: auto;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 10px;
  color: #cbd5e1;
  text-decoration: none;
  margin-bottom: 4px;
  transition: all 0.2s;
  cursor: pointer;
}
.menu-item:hover {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
}
.menu-item.active {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
}

.menu-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}
.menu-text {
  font-size: 14px;
}

.main {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}
</style>
