<template>
  <div class="layout">
    <!-- 移动端汉堡按钮 -->
    <button class="burger-btn" @click="sidebarOpen = !sidebarOpen">
      <span class="burger-line"></span>
      <span class="burger-line"></span>
      <span class="burger-line"></span>
    </button>

    <!-- 移动端遮罩 -->
    <div
      class="sidebar-overlay"
      :class="{ show: sidebarOpen }"
      @click="sidebarOpen = false"
    ></div>

    <!-- 左侧栏 -->
    <aside class="sidebar" :class="{ open: sidebarOpen }">
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
        <template v-for="item in menuItems" :key="item.path || item.label">
          <router-link
            v-if="!item.children"
            :to="item.path"
            :class="['menu-item', { active: isActive(item.path) }]"
            @click="closeSidebar"
          >
            <span class="menu-icon">{{ item.icon }}</span>
            <span class="menu-text">{{ item.label }}</span>
          </router-link>

          <div v-else class="submenu" :class="{ active: isGroupActive(item) }">
            <div class="menu-item submenu-title">
              <span class="menu-icon">{{ item.icon }}</span>
              <span class="menu-text">{{ item.label }}</span>
            </div>
            <div class="submenu-list">
              <router-link
                v-for="sub in item.children"
                :key="sub.path"
                :to="sub.path"
                :class="['submenu-item', { active: route.path === sub.path }]"
                @click="closeSidebar"
              >
                <span class="submenu-dot"></span>
                <span class="menu-text">{{ sub.label }}</span>
              </router-link>
            </div>
          </div>
        </template>
      </nav>
    </aside>

    <!-- 右侧内容区 -->
    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const sidebarOpen = ref(false)

function closeSidebar() {
  sidebarOpen.value = false
}

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
  {
    label: '员工管理',
    icon: '👥',
    roles: [3],
    children: [
      { path: '/employee/add', label: '添加员工' },
      { path: '/employee/list', label: '员工一览' }
    ]
  },
  { path: '/stat', label: '统计报表', icon: '📊', roles: [3, 4] }
]

const menuItems = computed(() => {
  const role = userStore.user?.role != null ? Number(userStore.user.role) : userStore.role
  return allMenu.filter(item => {
    if (!item.roles) return true
    return item.roles.includes(role)
  }).map(item => {
    if (!item.children) return item
    return item
  })
})

function isActive(path) {
  return route.path === path || route.path.startsWith(path + '/')
}

function isGroupActive(item) {
  if (!item.children) return false
  return item.children.some(sub => route.path === sub.path || route.path.startsWith(sub.path + '/'))
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
  position: relative;
}

/* ── 移动端汉堡按钮 ── */
.burger-btn {
  display: none;
  position: fixed;
  top: 12px;
  left: 12px;
  z-index: 1100;
  width: 40px;
  height: 40px;
  padding: 8px;
  background: #1a1a2e;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 5px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}
.burger-line {
  display: block;
  width: 20px;
  height: 2px;
  background: #fff;
  border-radius: 1px;
  transition: all 0.2s;
}

/* ── 移动端遮罩 ── */
.sidebar-overlay {
  display: none;
  position: fixed;
  inset: 0;
  z-index: 999;
  background: rgba(0, 0, 0, 0.4);
  opacity: 0;
  transition: opacity 0.25s;
  pointer-events: none;
}
.sidebar-overlay.show {
  opacity: 1;
  pointer-events: auto;
}

/* ── 左侧栏 ── */
.sidebar {
  width: 240px;
  background: #1a1a2e;
  color: #fff;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  transition: transform 0.25s ease;
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

.menu-item,
.submenu-item {
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
.menu-item:hover,
.submenu-item:hover {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
}
.menu-item.active,
.submenu-item.active {
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

.submenu {
  margin-bottom: 4px;
}
.submenu-title {
  margin-bottom: 0;
  cursor: default;
}
.submenu-list {
  padding-left: 18px;
  border-left: 1px solid rgba(255, 255, 255, 0.08);
  margin-left: 20px;
  display: flex;
  flex-direction: column;
}
.submenu-item {
  padding: 10px 16px;
  margin-bottom: 2px;
  color: #94a3b8;
}
.submenu-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.5;
}
.submenu-item.active .submenu-dot {
  opacity: 1;
}

.main {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

/* ── 移动端适配 ── */
@media (max-width: 768px) {
  .burger-btn {
    display: flex;
  }
  .sidebar-overlay {
    display: block;
  }

  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    z-index: 1000;
    transform: translateX(-100%);
  }
  .sidebar.open {
    transform: translateX(0);
  }

  .main {
    padding: 60px 12px 16px;
  }
}

/* ── 平板端适配 ── */
@media (min-width: 769px) and (max-width: 1024px) {
  .main {
    padding: 20px 16px;
  }
}
</style>
