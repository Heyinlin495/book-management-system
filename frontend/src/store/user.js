import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userAPI } from '../api'

export const useUserStore = defineStore('user', () => {
  const user = ref(localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : null)
  const token = ref(localStorage.getItem('token') || '')
  const isLoggedIn = ref(!!token.value)

  const login = async (username, password, captchaCode, captchaKey) => {
    try {
      const response = await userAPI.login(username, password, captchaCode, captchaKey)
      if (response.data.code === 200) {
        const authData = response.data.data
        token.value = authData.token
        user.value = authData.user
        localStorage.setItem('token', token.value)
        localStorage.setItem('user', JSON.stringify(user.value))
        isLoggedIn.value = true
        return authData.user
      }
    } catch (error) {
      console.error('登录失败', error)
      throw error
    }
  }

  const adminLogin = async (username, password, captchaCode, captchaKey) => {
    try {
      const response = await userAPI.adminLogin(username, password, captchaCode, captchaKey)
      if (response.data.code === 200) {
        const authData = response.data.data
        token.value = authData.token
        user.value = authData.user
        localStorage.setItem('token', token.value)
        localStorage.setItem('user', JSON.stringify(user.value))
        isLoggedIn.value = true
        return authData.user
      }
    } catch (error) {
      console.error('管理员登录失败', error)
      throw error
    }
  }

  const register = async (username, password, email) => {
    try {
      const response = await userAPI.register(username, password, email)
      return response.data.data
    } catch (error) {
      console.error('注册失败', error)
      throw error
    }
  }

  const logout = () => {
    user.value = null
    token.value = ''
    isLoggedIn.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const setUser = (newUser) => {
    user.value = newUser
    localStorage.setItem('user', JSON.stringify(newUser))
  }

  return {
    user,
    token,
    isLoggedIn,
    login,
    adminLogin,
    register,
    logout,
    setUser
  }
})
