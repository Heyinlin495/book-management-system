<template>
  <div class="activity-detail-page">
    <div class="back-btn" @click="$router.back()">← 返回</div>
    
    <!-- 加载中 -->
    <div v-if="loading" class="loading-state">
      <span class="loading-icon">⏳</span>
      <p>加载中...</p>
    </div>
    
    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      <span class="error-icon">⚠️</span>
      <p>{{ error }}</p>
      <button class="btn-retry" @click="loadActivity">重试</button>
    </div>
    
    <!-- 活动内容 -->
    <template v-else-if="activity">
    
    <div class="activity-header" :style="getActivityBg()">
      <div class="header-content">
        <span class="status-badge" :class="activity.status">{{ getStatusText(activity.status) }}</span>
        <h1>{{ activity.title }}</h1>
        <div class="activity-meta">
          <span>📅 {{ formatDate(activity.startTime) }} - {{ formatDate(activity.endTime) }}</span>
          <span>📍 {{ activity.location || '待定' }}</span>
        </div>
      </div>
    </div>

    <div class="activity-content">
      <div class="main-info">
        <h2>活动详情</h2>
        <p class="description">{{ activity.description }}</p>
        
        <div class="info-cards">
          <div class="info-card">
            <span class="info-icon">👥</span>
            <div class="info-text">
              <span class="info-value">{{ activity.currentParticipants || 0 }}/{{ activity.maxParticipants ? activity.maxParticipants : '∞' }}</span>
              <span class="info-label">报名人数</span>
              <span v-if="!activity.maxParticipants" class="info-badge">无限制</span>
            </div>
          </div>
          <div class="info-card">
            <span class="info-icon">📅</span>
            <div class="info-text">
              <span class="info-value">{{ activity.registrationDeadline ? formatDate(activity.registrationDeadline) : '无截止' }}</span>
              <span class="info-label">报名截止</span>
              <span v-if="!activity.registrationDeadline" class="info-badge">随时报名</span>
            </div>
          </div>
        </div>
      </div>

      <div class="action-panel">
        <div v-if="activity.isRegistered" class="registered-info">
          <span class="check-icon">✅</span>
          <p>您已报名此活动</p>
          <button class="btn-cancel" @click="cancelRegistration">取消报名</button>
        </div>
        <button v-else class="btn-register" @click="register" :disabled="!canRegister">
          {{ canRegister ? '立即报名' : '报名已结束' }}
        </button>
        <button class="btn-collect" @click="toggleCollect">
          {{ activity.isCollected ? '❤️ 已收藏' : '🤍 收藏' }}
        </button>
      </div>
    </div>

    <!-- 管理员操作 -->
    <div class="admin-panel" v-if="isAdmin && activity">
      <h3>活动管理</h3>
      <div class="admin-actions">
        <button class="btn-edit" @click="editActivity">编辑活动</button>
        <button class="btn-delete" @click="deleteActivity">删除活动</button>
      </div>
      <h4>报名列表 ({{ registrations.length }})</h4>
      <div class="registrations-list">
        <div class="reg-item" v-for="reg in registrations" :key="reg.id">
          <span class="reg-user">{{ reg.username }}</span>
          <span class="reg-time">{{ formatDate(reg.registrationTime) }}</span>
          <span class="reg-status" :class="reg.status">{{ getRegStatusText(reg.status) }}</span>
          <button v-if="reg.status === 'REGISTERED'" class="btn-checkin" @click="checkIn(reg.id)">签到</button>
        </div>
      </div>
    </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { activityAPI } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activity = ref(null)
const registrations = ref([])
const loading = ref(true)
const error = ref('')

const isAdmin = computed(() => userStore.user?.role === 'ADMIN')
const canRegister = computed(() => {
  if (!activity.value) return false
  if (activity.value.status !== 'UPCOMING') return false
  if (activity.value.maxParticipants && activity.value.currentParticipants >= activity.value.maxParticipants) return false
  if (activity.value.registrationDeadline && new Date() > new Date(activity.value.registrationDeadline)) return false
  return true
})

const getStatusText = (status) => {
  const map = { 'UPCOMING': '即将开始', 'ONGOING': '进行中', 'ENDED': '已结束', 'CANCELLED': '已取消' }
  return map[status] || status
}

const getRegStatusText = (status) => {
  const map = { 'REGISTERED': '已报名', 'CHECKED_IN': '已签到', 'CANCELLED': '已取消' }
  return map[status] || status
}

const getActivityBg = () => {
  const colors = ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#00f2fe']
  const index = (activity.value?.id || 0) % colors.length
  return { background: `linear-gradient(135deg, ${colors[index]} 0%, ${colors[(index + 1) % colors.length]} 100%)` }
}

const formatDate = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const loadActivity = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await activityAPI.getById(route.params.id)
    if (res.data.code === 200) {
      activity.value = res.data.data
    } else {
      error.value = res.data.message || '活动加载失败'
    }
  } catch (err) {
    console.error('加载活动失败:', err)
    error.value = err.response?.data?.message || '加载活动失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const loadRegistrations = async () => {
  if (!isAdmin.value) return
  try {
    const res = await activityAPI.getRegistrations(route.params.id)
    if (res.data.code === 200) registrations.value = res.data.data
  } catch (error) {
    console.error('加载报名列表失败:', error)
  }
}

const register = async () => {
  try {
    const res = await activityAPI.register(route.params.id)
    if (res.data.code === 200 || res.data.code === 201) {
      ElMessage.success('报名成功')
      loadActivity()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '报名失败')
  }
}

const cancelRegistration = async () => {
  try {
    await ElMessageBox.confirm('确定取消报名吗？', '确认')
    await activityAPI.cancelRegister(route.params.id)
    ElMessage.success('取消成功')
    loadActivity()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('取消失败')
  }
}

const toggleCollect = async () => {
  try {
    await activityAPI.toggleCollect(route.params.id)
    loadActivity()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const checkIn = async (regId) => {
  try {
    await activityAPI.checkIn(regId)
    ElMessage.success('签到成功')
    loadRegistrations()
  } catch (error) {
    ElMessage.error('签到失败')
  }
}

const editActivity = () => {
  router.push(`/activities/${route.params.id}/edit`)
}

const deleteActivity = async () => {
  try {
    await ElMessageBox.confirm('确定删除此活动吗？', '确认')
    await activityAPI.delete(route.params.id)
    ElMessage.success('删除成功')
    router.push('/activities')
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadActivity()
  loadRegistrations()
})
</script>

<style scoped>
.activity-detail-page { max-width: 1000px; margin: 0 auto; padding: 20px; min-height: 400px; }
.back-btn { color: #667eea; cursor: pointer; margin-bottom: 20px; font-size: 16px; }

.loading-state, .error-state { text-align: center; padding: 80px 20px; }
.loading-icon, .error-icon { font-size: 48px; display: block; margin-bottom: 15px; }
.loading-state p, .error-state p { color: #666; font-size: 16px; margin: 0 0 20px; }
.btn-retry { padding: 10px 24px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 14px; }

.activity-header { border-radius: 20px; padding: 60px 40px; color: white; margin-bottom: 30px; }
.header-content { max-width: 600px; }
.status-badge { display: inline-block; padding: 6px 12px; background: rgba(255,255,255,0.2); border-radius: 6px; font-size: 13px; margin-bottom: 15px; }
.status-badge.ONGOING { background: rgba(46, 204, 113, 0.8); }
.activity-header h1 { font-size: 36px; margin: 0 0 20px; }
.activity-meta { display: flex; flex-direction: column; gap: 8px; font-size: 16px; opacity: 0.9; }

.activity-content { display: grid; grid-template-columns: 2fr 1fr; gap: 30px; }
.main-info { background: white; border-radius: 16px; padding: 30px; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
.main-info h2 { margin: 0 0 20px; font-size: 22px; }
.description { color: #555; line-height: 1.8; font-size: 16px; white-space: pre-wrap; }
.info-cards { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-top: 30px; }
.info-card { display: flex; align-items: center; gap: 15px; padding: 20px; background: #f9f9f9; border-radius: 12px; }
.info-icon { font-size: 32px; }
.info-text { display: flex; flex-direction: column; }
.info-value { font-size: 18px; font-weight: 600; color: #333; }
.info-label { font-size: 13px; color: #888; }
.info-badge { display: inline-block; margin-top: 6px; padding: 2px 8px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-radius: 4px; font-size: 11px; font-weight: 500; }

.action-panel { background: white; border-radius: 16px; padding: 30px; box-shadow: 0 4px 12px rgba(0,0,0,0.08); height: fit-content; }
.btn-register { width: 100%; padding: 16px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 10px; font-size: 18px; font-weight: 600; cursor: pointer; margin-bottom: 15px; }
.btn-register:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-collect { width: 100%; padding: 14px; background: white; border: 2px solid #eee; border-radius: 10px; font-size: 16px; cursor: pointer; }
.registered-info { text-align: center; margin-bottom: 20px; }
.check-icon { font-size: 48px; display: block; margin-bottom: 10px; }
.registered-info p { color: #2ecc71; font-weight: 500; margin: 0 0 15px; }
.btn-cancel { padding: 10px 20px; background: #ff6b6b; color: white; border: none; border-radius: 6px; cursor: pointer; }

.admin-panel { background: white; border-radius: 16px; padding: 30px; box-shadow: 0 4px 12px rgba(0,0,0,0.08); margin-top: 30px; }
.admin-panel h3 { margin: 0 0 15px; }
.admin-panel h4 { margin: 20px 0 15px; }
.admin-actions { display: flex; gap: 10px; margin-bottom: 20px; }
.btn-edit { padding: 10px 20px; background: #667eea; color: white; border: none; border-radius: 6px; cursor: pointer; }
.btn-delete { padding: 10px 20px; background: #ff6b6b; color: white; border: none; border-radius: 6px; cursor: pointer; }
.registrations-list { display: flex; flex-direction: column; gap: 10px; }
.reg-item { display: flex; align-items: center; gap: 15px; padding: 12px; background: #f9f9f9; border-radius: 8px; }
.reg-user { font-weight: 500; }
.reg-time { color: #888; font-size: 13px; }
.reg-status { padding: 3px 8px; border-radius: 4px; font-size: 12px; margin-left: auto; }
.reg-status.REGISTERED { background: #fff3cd; color: #856404; }
.reg-status.CHECKED_IN { background: #d4edda; color: #155724; }
.btn-checkin { padding: 6px 12px; background: #2ecc71; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 12px; }

@media (max-width: 768px) {
  .activity-content { grid-template-columns: 1fr; }
}
</style>
