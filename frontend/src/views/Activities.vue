<template>
  <div class="activities-page">
    <div class="page-header">
      <h1>图书活动</h1>
      <p class="subtitle">参与精彩活动，共享阅读时光</p>
      <button v-if="isAdmin" class="btn-create" @click="router.push('/activities/add')">+ 创建活动</button>
    </div>

    <!-- 活动分类 -->
    <div class="filter-tabs">
      <button :class="{ active: filter === 'all' }" @click="filter = 'all'">全部活动</button>
      <button :class="{ active: filter === 'upcoming' }" @click="filter = 'upcoming'">即将开始</button>
      <button :class="{ active: filter === 'hot' }" @click="filter = 'hot'">热门活动</button>
    </div>

    <!-- 活动列表 -->
    <div class="activities-grid">
      <div class="activity-card" v-for="activity in filteredActivities" :key="activity.id" @click="viewActivity(activity.id)">
        <div class="card-actions" v-if="isAdmin" @click.stop>
          <button class="btn-action btn-edit" @click="editActivity(activity.id)" title="编辑">✏️</button>
          <button class="btn-action btn-delete" @click="deleteActivity(activity.id)" title="删除">🗑️</button>
        </div>
        <div class="activity-cover" :style="getActivityBg(activity)">
          <span class="activity-status" :class="activity.status">{{ getStatusText(activity.status) }}</span>
          <span v-if="activity.isHot" class="hot-badge">🔥 热门</span>
        </div>
        <div class="activity-info">
          <h3>{{ activity.title }}</h3>
          <p class="activity-desc">{{ activity.description?.substring(0, 80) }}...</p>
          <div class="activity-meta">
            <span>📅 {{ formatDate(activity.startTime) }}</span>
            <span>📍 {{ activity.location || '待定' }}</span>
          </div>
          <div class="activity-participants">
            <span class="count">{{ activity.currentParticipants || 0 }}/{{ activity.maxParticipants ? activity.maxParticipants : '∞' }} 人</span>
            <div v-if="!activity.maxParticipants" class="unlimited-badge">无限制</div>
            <div class="progress-bar" v-else>
              <div class="progress" :style="{ width: (activity.currentParticipants / activity.maxParticipants * 100) + '%' }"></div>
            </div>
          </div>
        </div>
      </div>
      <div v-if="filteredActivities.length === 0" class="empty-state">
        <span class="empty-icon">📅</span>
        <p>暂无活动</p>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { activityAPI } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const activities = ref([])
const filter = ref('all')

const isAdmin = computed(() => userStore.user?.role === 'ADMIN')

const filteredActivities = computed(() => {
  if (filter.value === 'all') return activities.value
  if (filter.value === 'upcoming') return activities.value.filter(a => a.status === 'UPCOMING')
  if (filter.value === 'hot') return activities.value.filter(a => a.isHot)
  return activities.value
})

const getStatusText = (status) => {
  const map = { 'UPCOMING': '即将开始', 'ONGOING': '进行中', 'ENDED': '已结束', 'CANCELLED': '已取消' }
  return map[status] || status
}

const getActivityBg = (activity) => {
  const colors = ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#00f2fe']
  const index = activity.id % colors.length
  return { background: `linear-gradient(135deg, ${colors[index]} 0%, ${colors[(index + 1) % colors.length]} 100%)` }
}

const formatDate = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const viewActivity = (id) => {
  router.push(`/activities/${id}`)
}

const loadActivities = async () => {
  try {
    const res = await activityAPI.getAll()
    if (res.data.code === 200) activities.value = res.data.data
  } catch (error) {
    console.error('加载活动失败:', error)
  }
}

const editActivity = (id) => {
  router.push(`/activities/${id}/edit`)
}

const deleteActivity = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此活动吗？', '确认')
    await activityAPI.delete(id)
    ElMessage.success('删除成功')
    loadActivities()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.activities-page { max-width: 1200px; margin: 0 auto; padding: 20px; }
.page-header { text-align: center; margin-bottom: 30px; position: relative; }
.page-header h1 { font-size: 32px; color: #333; margin-bottom: 10px; }
.subtitle { color: #666; font-size: 16px; }
.btn-create { position: absolute; right: 0; top: 10px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; padding: 12px 24px; border-radius: 8px; cursor: pointer; }

.activity-card { position: relative; }
.card-actions { position: absolute; top: 10px; right: 10px; display: flex; gap: 8px; z-index: 10; }
.btn-action { width: 36px; height: 36px; border-radius: 50%; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 16px; background: rgba(255, 255, 255, 0.9); transition: all 0.3s; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); }
.btn-action:hover { background: white; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); transform: scale(1.1); }
.btn-edit:hover { color: #667eea; }
.btn-delete:hover { color: #ff6b6b; }

.filter-tabs { display: flex; justify-content: center; gap: 10px; margin-bottom: 30px; }
.filter-tabs button { padding: 10px 24px; border: 1px solid #ddd; background: white; border-radius: 20px; cursor: pointer; transition: all 0.3s; }
.filter-tabs button.active { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-color: transparent; }

.activities-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 25px; }
.activity-card { background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08); cursor: pointer; transition: all 0.3s; }
.activity-card:hover { transform: translateY(-5px); box-shadow: 0 8px 24px rgba(0,0,0,0.15); }
.activity-cover { height: 140px; position: relative; display: flex; align-items: center; justify-content: center; color: white; font-size: 48px; }
.activity-status { position: absolute; top: 12px; left: 12px; padding: 4px 10px; background: rgba(255,255,255,0.2); border-radius: 4px; font-size: 12px; }
.activity-status.ONGOING { background: #2ecc71; }
.activity-status.ENDED { background: #95a5a6; }
.hot-badge { position: absolute; top: 12px; right: 12px; font-size: 14px; }
.activity-info { padding: 20px; }
.activity-info h3 { font-size: 18px; margin: 0 0 10px; color: #333; }
.activity-desc { color: #666; font-size: 14px; line-height: 1.5; margin: 0 0 15px; }
.activity-meta { display: flex; flex-direction: column; gap: 6px; color: #888; font-size: 13px; margin-bottom: 15px; }
.activity-participants { display: flex; align-items: center; gap: 10px; }
.count { font-size: 13px; color: #667eea; font-weight: 500; }
.unlimited-badge { font-size: 11px; padding: 2px 8px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-radius: 4px; font-weight: 500; }
.progress-bar { flex: 1; height: 6px; background: #eee; border-radius: 3px; overflow: hidden; }
.progress { height: 100%; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); transition: width 0.3s; }

.empty-state { grid-column: 1 / -1; text-align: center; padding: 60px 20px; color: #999; }
.empty-icon { font-size: 48px; display: block; margin-bottom: 15px; }

.dialog-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.dialog { background: white; border-radius: 16px; padding: 30px; width: 90%; max-width: 600px; max-height: 90vh; overflow-y: auto; }
.dialog h2 { margin: 0 0 20px; }
.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 500; }
.form-group input, .form-group textarea { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; box-sizing: border-box; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.dialog-actions { display: flex; justify-content: flex-end; gap: 12px; }
.btn-cancel { padding: 10px 20px; border: 1px solid #ddd; background: white; border-radius: 8px; cursor: pointer; }
.btn-submit { padding: 10px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 8px; cursor: pointer; }
</style>
