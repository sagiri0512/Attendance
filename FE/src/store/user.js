import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { logout as logoutApi, getCurrentUser } from '@/api/auth'
import { getRoleFromToken, getRealNameFromToken } from '@/utils/jwt'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)

  // 登录后立即从 JWT 解析 role，无需等 fetchUser 返回
  // role 用于菜单显示、路由守卫、按钮权限判断
  const role = computed(() => {
    if (user.value?.role != null) return Number(user.value.role)
    return getRoleFromToken(token.value)
  })
  const realName = computed(() => user.value?.realName || getRealNameFromToken(token.value) || '')

  // 常用权限判断
  const isHr = computed(() => role.value === 3)

  function setToken(val) {
    token.value = val
    localStorage.setItem('token', val)
  }

  function setUser(val) {
    user.value = val
  }

  async function fetchUser() {
    try {
      const res = await getCurrentUser()
      user.value = res.data.data
    } catch {
      user.value = null
    }
  }

  async function logout() {
    try {
      await logoutApi()
    } catch {
      // 即使后端调用失败也清除本地状态，不阻塞登出
    }
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  return {
    token, user, role, realName, isHr,
    setToken, setUser, fetchUser, logout
  }
})
