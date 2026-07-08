import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/my-record',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'home',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'clock',
        name: 'Clock',
        component: () => import('@/views/attendance/Clock.vue'),
        meta: { title: '打卡' }
      },
      {
        path: 'my-record',
        name: 'Record',
        component: () => import('@/views/attendance/Record.vue'),
        meta: { title: '我的记录' }
      },
      {
        path: 'leave/apply',
        name: 'LeaveApply',
        component: () => import('@/views/leave/Apply.vue'),
        meta: { title: '请假申请' }
      },
      {
        path: 'leave/approve',
        name: 'LeaveApprove',
        component: () => import('@/views/leave/Approve.vue'),
        meta: { title: '审批管理' }
      },
      {
        path: 'stat',
        name: 'Stat',
        component: () => import('@/views/stat/Index.vue'),
        meta: { title: '统计报表' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? to.meta.title + ' - 考勤面板' : '考勤面板'
  const userStore = useUserStore()
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else if (to.meta.requiresAuth && token && !userStore.user) {
    await userStore.fetchUser()
    next()
  } else {
    next()
  }
})

export default router
