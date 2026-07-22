import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getRoleFromToken } from '@/utils/jwt'
import { ElMessage } from 'element-plus'

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
        meta: { title: '审批管理', roles: [1, 2, 3, 4] }
      },
      {
        path: 'stat',
        name: 'Stat',
        component: () => import('@/views/stat/Index.vue'),
        meta: { title: '统计报表', roles: [3, 4] }
      },
      {
        path: 'holiday',
        name: 'HolidayCalendar',
        component: () => import('@/views/holiday/Calendar.vue'),
        meta: { title: '节假日管理', roles: [4] }
      },
      {
        path: 'employee',
        name: 'Employee',
        component: () => import('@/views/employee/Index.vue'),
        redirect: '/employee/add',
        meta: { title: '员工管理', roles: [3] },
        children: [
          {
            path: 'add',
            name: 'EmployeeAdd',
            component: () => import('@/views/employee/AddEmployee.vue'),
            meta: { title: '添加员工', roles: [3] }
          },
          {
            path: 'list',
            name: 'EmployeeList',
            component: () => import('@/views/employee/EmployeeList.vue'),
            meta: { title: '员工一览', roles: [3] }
          }
        ]
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

  // 未登录拦截
  if (to.meta.requiresAuth && !token) {
    return next('/login')
  }

  // 已登录访问登录页 → 回首页
  if (to.path === '/login' && token) {
    return next('/')
  }

  // 首次进入（有 token 但 user 未拉取）→ 拉取用户信息
  if (to.meta.requiresAuth && token && !userStore.user) {
    await userStore.fetchUser()
  }

  // 角色权限校验：优先用 user.role，否则从 JWT 解析
  const role = userStore.user?.role != null
    ? Number(userStore.user.role)
    : getRoleFromToken(token)
  if (to.meta.roles && Array.isArray(to.meta.roles) && !to.meta.roles.includes(role)) {
    ElMessage.error('无权访问该页面')
    return next('/')
  }

  next()
})

export default router
