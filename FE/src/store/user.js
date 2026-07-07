import { defineStore } from 'pinia'
import { ref } from 'vue'
import { logout as logoutApi } from '@/api/auth'

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

  async function logout() {
    try {
      await logoutApi()         // 通知后端，token 加入 Redis 黑名单
    } catch {
      // 即使后端调用失败也清除本地状态，不阻塞登出
    }
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  return { token, user, setToken, setUser, logout }
})
