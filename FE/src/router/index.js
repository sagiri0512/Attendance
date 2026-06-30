import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '首页', requiresAuth: true }
  },
  {
    path: '/clock',
    name: 'Clock',
    component: () => import('@/views/attendance/Clock.vue'),
    meta: { title: '打卡', requiresAuth: true }
  },
  {
    path: '/my-record',
    name: 'Record',
    component: () => import('@/views/attendance/Record.vue'),
    meta: { title: '我的记录', requiresAuth: true }
  },
  {
    path: '/leave/apply',
    name: 'LeaveApply',
    component: () => import('@/views/leave/Apply.vue'),
    meta: { title: '请假申请', requiresAuth: true }
  },
  {
    path: '/leave/approve',
    name: 'LeaveApprove',
    component: () => import('@/views/leave/Approve.vue'),
    meta: { title: '审批管理', requiresAuth: true }
  },
  {
    path: '/stat',
    name: 'Stat',
    component: () => import('@/views/stat/Index.vue'),
    meta: { title: '统计报表', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title + ' - 考勤面板'
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router
