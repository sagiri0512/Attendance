import { defineStore } from 'pinia'
import { ref } from 'vue'

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

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  return { token, user, setToken, setUser, logout }
})
