<template>
  <header class="header">
    <div class="header-content">
      <h1 class="logo">📚 图书管理系统</h1>
      <nav class="nav">
        <RouterLink to="/">首页</RouterLink>
        <RouterLink v-if="isLoggedIn" to="/books">图书馆</RouterLink>
        <RouterLink v-if="isLoggedIn" to="/forum">社区</RouterLink>
        <RouterLink v-if="isLoggedIn" to="/activities">活动</RouterLink>
        <RouterLink v-if="isLoggedIn" to="/reading-room">阅览室</RouterLink>
        <RouterLink v-if="isLoggedIn" to="/ranking">排行榜</RouterLink>
        <RouterLink v-if="isLoggedIn && isAdmin" to="/users">用户管理</RouterLink>
        <div v-if="isLoggedIn" class="user-info">
          <RouterLink to="/profile" class="profile-link">
            <span>👤 {{ user?.username }}</span>
          </RouterLink>
          <button @click="handleLogout" class="logout-btn">退出登录</button>
        </div>
        <div v-else class="auth-links">
          <RouterLink to="/login">登录</RouterLink>
          <RouterLink to="/register">注册</RouterLink>
        </div>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

const user = computed(() => userStore.user)
const isLoggedIn = computed(() => userStore.isLoggedIn)
const isAdmin = computed(() => user.value?.role === 'ADMIN')

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.header {
  background: linear-gradient(90deg, #2c3e50 0%, #34495e 100%);
  color: white;
  padding: 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.logo {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
}

.nav {
  display: flex;
  gap: 20px;
  align-items: center;
}

.nav a {
  color: white;
  text-decoration: none;
  transition: all 0.3s;
  padding: 8px 0;
  position: relative;
  font-weight: 500;
}

.nav a:hover {
  color: #42b983;
}

.nav a::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #42b983, #5cc4a1);
  transition: width 0.3s;
}

.nav a:hover::after {
  width: 100%;
}

.nav a.router-link-active {
  color: #42b983;
}

.nav a.router-link-active::after {
  width: 100%;
}

.user-info {
  display: flex;
  gap: 10px;
  align-items: center;
}

.profile-link {
  color: white;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.profile-link:hover {
  background: rgba(102, 126, 234, 0.3);
  transform: translateY(-2px);
  border-color: rgba(102, 126, 234, 0.4);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.logout-btn {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  color: white;
  border: none;
  padding: 8px 18px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(231, 76, 60, 0.2);
}

.logout-btn:hover {
  background: linear-gradient(135deg, #c0392b, #a93226);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(231, 76, 60, 0.3);
}

.auth-links {
  display: flex;
  gap: 15px;
}

.auth-links a {
  padding: 8px 18px;
  border-radius: 8px;
  background: linear-gradient(135deg, #42b983, #359970);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  font-weight: 500;
  border: 1px solid rgba(66, 185, 131, 0.2);
}

.auth-links a:hover {
  background: linear-gradient(135deg, #359970, #2a7e58);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(66, 185, 131, 0.3);
}
</style>