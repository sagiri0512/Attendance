import { defineStore } from 'pinia'
import { ref } from 'vue'
import { logout as logoutApi, getCurrentUser } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)

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

  return { token, user, setToken, setUser, fetchUser, logout }
})
