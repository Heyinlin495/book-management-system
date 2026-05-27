<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="login-box">
      <div class="login-header">
        <div class="logo-wrapper">
          <div class="logo-circle">
            <svg class="logo-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M18 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zM6 4h5v8l-2.5-1.5L6 12V4z" fill="currentColor"/>
            </svg>
          </div>
        </div>
        <h2>欢迎回来</h2>
        <p class="subtitle">登录以继续使用图书管理系统</p>
      </div>
      
      <form @submit.prevent="handleLogin">
        <div class="form-group" :class="{ 'has-error': usernameError }">
          <label>
            <svg class="label-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" fill="currentColor"/>
            </svg>
            用户名
          </label>
          <div class="input-wrapper">
            <input 
              v-model="username" 
              type="text" 
              placeholder="请输入用户名" 
              @blur="validateUsername"
              @input="clearError('username')"
              required
            />
            <div class="input-border"></div>
          </div>
          <transition name="error-fade">
            <span v-if="usernameError" class="error-text">{{ usernameError }}</span>
          </transition>
        </div>
        
        <div class="form-group" :class="{ 'has-error': passwordError }">
          <label>
            <svg class="label-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z" fill="currentColor"/>
            </svg>
            密码
          </label>
          <div class="input-wrapper password-wrapper">
            <input
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="请输入密码"
              @blur="validatePassword"
              @input="clearError('password')"
              required
            />
            <button type="button" class="toggle-password" @click="showPassword = !showPassword" tabindex="-1">
              <svg v-if="!showPassword" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" fill="currentColor"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z" fill="currentColor"/>
              </svg>
            </button>
            <div class="input-border"></div>
          </div>
          <transition name="error-fade">
            <span v-if="passwordError" class="error-text">{{ passwordError }}</span>
          </transition>
        </div>

        <div class="form-group" :class="{ 'has-error': captchaError }">
          <label>
            <svg class="label-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z" fill="currentColor"/>
            </svg>
            验证码
          </label>
          <div class="captcha-row">
            <div class="input-wrapper captcha-input-wrapper">
              <input
                v-model="captchaCode"
                type="text"
                placeholder="请输入验证码"
                @input="clearError('captcha')"
                maxlength="4"
                required
              />
              <div class="input-border"></div>
            </div>
            <div class="captcha-image-wrapper" @click="refreshCaptcha" title="点击刷新验证码">
              <img v-if="captchaImage" :src="captchaImage" alt="验证码" class="captcha-image" />
              <div v-else class="captcha-placeholder">加载中...</div>
            </div>
          </div>
          <transition name="error-fade">
            <span v-if="captchaError" class="error-text">{{ captchaError }}</span>
          </transition>
        </div>

        <transition name="error-slide">
          <div class="error-message" v-if="errorMessage">
            <svg class="error-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" fill="currentColor"/>
            </svg>
            {{ errorMessage }}
          </div>
        </transition>
        
        <button type="submit" class="btn-login" :disabled="loading">
          <span class="btn-content">
            <svg v-if="loading" class="loading-spinner" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" opacity="0.25"/>
              <path d="M12 2a10 10 0 0 1 10 10" stroke="currentColor" stroke-width="3" fill="none" stroke-linecap="round"/>
            </svg>
            <span>{{ loading ? '登录中...' : '登录' }}</span>
          </span>
        </button>
      </form>
      
      <div class="footer">
        <p class="register-link">
          还没有账户？<RouterLink to="/register">立即注册</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { captchaAPI } from '../api'

const router = useRouter()
const userStore = useUserStore()

const username = ref('')
const password = ref('')
const captchaCode = ref('')
const captchaKey = ref('')
const captchaImage = ref('')
const errorMessage = ref('')
const usernameError = ref('')
const passwordError = ref('')
const captchaError = ref('')
const loading = ref(false)
const showPassword = ref(false)

const refreshCaptcha = async () => {
  try {
    const res = await captchaAPI.generate()
    captchaKey.value = res.data.key
    captchaImage.value = res.data.image
    captchaCode.value = ''
  } catch (e) {
    console.error('获取验证码失败', e)
  }
}

onMounted(() => {
  refreshCaptcha()
})

const validateUsername = () => {
  if (!username.value) {
    usernameError.value = '请输入用户名'
    return false
  }
  if (username.value.length < 3) {
    usernameError.value = '用户名至少3个字符'
    return false
  }
  usernameError.value = ''
  return true
}

const validatePassword = () => {
  if (!password.value) {
    passwordError.value = '请输入密码'
    return false
  }
  if (password.value.length < 6) {
    passwordError.value = '密码至少6个字符'
    return false
  }
  passwordError.value = ''
  return true
}

const validateCaptcha = () => {
  if (!captchaCode.value) {
    captchaError.value = '请输入验证码'
    return false
  }
  captchaError.value = ''
  return true
}

const clearError = (field) => {
  if (field === 'username') {
    usernameError.value = ''
  } else if (field === 'password') {
    passwordError.value = ''
  } else if (field === 'captcha') {
    captchaError.value = ''
  }
  errorMessage.value = ''
}

const handleLogin = async () => {
  const isUsernameValid = validateUsername()
  const isPasswordValid = validatePassword()
  const isCaptchaValid = validateCaptcha()

  if (!isUsernameValid || !isPasswordValid || !isCaptchaValid) {
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    await userStore.login(username.value, password.value, captchaCode.value, captchaKey.value)
    router.push('/')
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '登录失败,请检查用户名和密码'
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 主容器 */
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px);
  background: linear-gradient(120deg, #a1c4fd 0%, #c2e9fb 100%);
  padding: 20px;
  overflow: hidden;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0.1));
  backdrop-filter: blur(10px);
  animation: float-circle 20s infinite ease-in-out;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 400px;
  height: 400px;
  bottom: -150px;
  right: -150px;
  animation-delay: -7s;
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  right: 10%;
  animation-delay: -14s;
}

@keyframes float-circle {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -50px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 30px) scale(0.9);
  }
}

/* 登录卡片 */
.login-box {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 50px 45px;
  width: 100%;
  max-width: 440px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.3);
  animation: slideIn 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 头部 */
.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.logo-circle {
  width: 90px;
  height: 90px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 12px 32px rgba(102, 126, 234, 0.5);
  }
}

.logo-icon {
  width: 45px;
  height: 45px;
  color: white;
}

.login-header h2 {
  color: #1a202c;
  font-size: 32px;
  margin: 0 0 10px 0;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.subtitle {
  color: #4a5568;
  font-size: 15px;
  margin: 0;
  font-weight: 400;
}

/* 表单组 */
.form-group {
  margin-bottom: 28px;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  color: #2d3748;
  font-weight: 600;
  font-size: 14px;
}

.label-icon {
  width: 18px;
  height: 18px;
  color: #667eea;
}

/* 输入框 */
.input-wrapper {
  position: relative;
}

.input-wrapper input {
  width: 100%;
  padding: 14px 18px;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  color: #1a202c;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  outline: none;
  box-sizing: border-box;
}

.input-wrapper input::placeholder {
  color: #a0aec0;
}

.input-wrapper input:focus {
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 
    0 4px 16px rgba(102, 126, 234, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 1);
}

.input-wrapper input:focus + .input-border {
  transform: scaleX(1);
}

.input-border {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px;
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

/* 密码输入框 */
.password-wrapper {
  position: relative;
}

.password-wrapper input {
  padding-right: 50px;
}

.toggle-password {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  width: 22px;
  height: 22px;
  background: none;
  border: none;
  cursor: pointer;
  color: #718096;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  z-index: 2;
}

.toggle-password:hover {
  color: #667eea;
  transform: translateY(-50%) scale(1.1);
}

.toggle-password svg {
  width: 100%;
  height: 100%;
}

/* 错误提示 */
.error-text {
  display: block;
  color: #e53e3e;
  font-size: 13px;
  margin-top: 8px;
  margin-left: 4px;
  font-weight: 500;
}

.error-fade-enter-active,
.error-fade-leave-active {
  transition: all 0.3s ease;
}

.error-fade-enter-from,
.error-fade-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}

.has-error input {
  background: rgba(254, 226, 226, 0.6) !important;
  box-shadow: 0 0 0 2px rgba(229, 62, 62, 0.2) !important;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(254, 226, 226, 0.8);
  backdrop-filter: blur(10px);
  color: #c53030;
  padding: 14px 16px;
  border-radius: 12px;
  font-size: 14px;
  margin-bottom: 24px;
  border: 1px solid rgba(229, 62, 62, 0.3);
  font-weight: 500;
}

.error-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.error-slide-enter-active,
.error-slide-leave-active {
  transition: all 0.4s ease;
}

.error-slide-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.error-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 登录按钮 */
.btn-login {
  width: 100%;
  padding: 0;
  margin-top: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 
    0 4px 16px rgba(102, 126, 234, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.btn-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 15px;
}

.btn-login::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s;
}

.btn-login:hover::before {
  left: 100%;
}

.btn-login:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 
    0 6px 24px rgba(102, 126, 234, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.btn-login:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}

.btn-login:disabled {
  background: linear-gradient(135deg, #cbd5e0, #a0aec0);
  cursor: not-allowed;
  box-shadow: none;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 页脚 */
.footer {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.4);
}

.register-link {
  text-align: center;
  margin: 0;
  color: #4a5568;
  font-size: 14px;
  font-weight: 500;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 700;
  transition: all 0.3s;
  position: relative;
}

.register-link a::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transition: width 0.3s;
}

.register-link a:hover {
  color: #764ba2;
}

.register-link a:hover::after {
  width: 100%;
}

/* 验证码 */
.captcha-row {
  display: flex;
  gap: 12px;
  align-items: center;
}

.captcha-input-wrapper {
  flex: 1;
}

.captcha-image-wrapper {
  flex-shrink: 0;
  width: 120px;
  height: 44px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.6);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.captcha-image-wrapper:hover {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transform: scale(1.02);
}

.captcha-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.captcha-placeholder {
  font-size: 12px;
  color: #a0aec0;
}

/* 响应式 */
@media (max-width: 480px) {
  .login-box {
    padding: 40px 30px;
  }
  
  .login-header h2 {
    font-size: 28px;
  }
  
  .logo-circle {
    width: 75px;
    height: 75px;
  }
  
  .logo-icon {
    width: 38px;
    height: 38px;
  }
  
  .circle-1,
  .circle-2,
  .circle-3 {
    display: none;
  }
}
</style>
