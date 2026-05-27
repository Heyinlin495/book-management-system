<template>
  <div class="register-container">
    <div class="register-box">
      <h2>注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>用户名</label>
          <input 
            v-model="username" 
            type="text" 
            placeholder="请输入用户名" 
            required
          />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input 
            v-model="email" 
            type="email" 
            placeholder="请输入邮箱" 
            required
          />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input 
            v-model="password" 
            type="password" 
            placeholder="请输入密码" 
            required
          />
        </div>
        <div class="form-group">
          <label>确认密码</label>
          <input 
            v-model="confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            required
          />
        </div>
        <button type="submit" class="btn-register" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      <p class="error-message" v-if="errorMessage">{{ errorMessage }}</p>
      <p class="success-message" v-if="successMessage">{{ successMessage }}</p>
      <p class="login-link">
        已有账户？<RouterLink to="/login">立即登录</RouterLink>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')
const successMessage = ref('')
const loading = ref(false)

const handleRegister = async () => {
  if (!username.value || !email.value || !password.value || !confirmPassword.value) {
    errorMessage.value = '请填写所有字段'
    return
  }

  if (password.value !== confirmPassword.value) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }

  if (password.value.length < 6) {
    errorMessage.value = '密码长度不能少于 6 个字符'
    return
  }

  loading.value = true
  try {
    await userStore.register(username.value, password.value, email.value)
    errorMessage.value = ''
    successMessage.value = '注册成功！3 秒后跳转到登录页面...'
    setTimeout(() => {
      router.push('/login')
    }, 3000)
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  background-color: #f5f5f5;
  padding: 20px;
}

.register-box {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.register-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 5px rgba(102, 126, 234, 0.3);
}

.btn-register {
  width: 100%;
  padding: 10px;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-register:hover:not(:disabled) {
  background-color: #5568d3;
}

.btn-register:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.error-message {
  color: #e74c3c;
  margin-top: 15px;
  text-align: center;
  font-size: 14px;
}

.success-message {
  color: #27ae60;
  margin-top: 15px;
  text-align: center;
  font-size: 14px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.login-link a {
  color: #667eea;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
