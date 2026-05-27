<template>
  <div class="activity-form-page">
    <div class="form-container">
      <h1>{{ isEdit ? '编辑活动' : '创建活动' }}</h1>
      
      <form @submit.prevent="submitForm" class="activity-form">
        <!-- 基本信息 -->
        <div class="form-section">
          <h3>基本信息</h3>
          
          <div class="form-group">
            <label>活动标题 <span class="required">*</span></label>
            <input 
              v-model="formData.title" 
              type="text" 
              placeholder="请输入活动标题"
              required
              maxlength="200"
            />
            <span class="char-count">{{ formData.title.length }}/200</span>
          </div>

          <div class="form-group">
            <label>活动描述 <span class="required">*</span></label>
            <textarea 
              v-model="formData.description" 
              placeholder="请输入活动详细描述"
              required
              rows="6"
            ></textarea>
          </div>

          <div class="form-group">
            <label>活动封面图片</label>
            <textarea 
              v-model="formData.coverImage" 
              placeholder="输入图片Base64编码或URL"
              rows="3"
            ></textarea>
            <div v-if="formData.coverImage" class="image-preview">
              <img :src="formData.coverImage" alt="预览" />
            </div>
          </div>
        </div>

        <!-- 时间和地点 -->
        <div class="form-section">
          <h3>时间和地点</h3>
          
          <div class="form-row">
            <div class="form-group">
              <label>开始时间 <span class="required">*</span></label>
              <input 
                v-model="formData.startTime" 
                type="datetime-local"
                required
              />
            </div>
            <div class="form-group">
              <label>结束时间 <span class="required">*</span></label>
              <input 
                v-model="formData.endTime" 
                type="datetime-local"
                required
              />
            </div>
          </div>

          <div class="form-group">
            <label>活动地点</label>
            <input 
              v-model="formData.location" 
              type="text"
              placeholder="请输入活动地点"
              maxlength="200"
            />
          </div>

          <div class="form-group">
            <label>报名截止时间</label>
            <input 
              v-model="formData.registrationDeadline" 
              type="datetime-local"
            />
            <p v-if="formData.registrationDeadline" class="form-hint">
              {{formatDeadlineHint()}}
            </p>
            <p v-else class="form-hint">未设置截止时间，用户可随时报名</p>
          </div>
        </div>

        <!-- 参与人数 -->
        <div class="form-section">
          <h3>参与人数</h3>
          
          <div class="form-group">
            <label>最大参与人数</label>
            <div class="input-with-hint">
              <input 
                v-model.number="formData.maxParticipants" 
                type="number"
                placeholder="不填则无限制"
                min="1"
              />
              <span v-if="!formData.maxParticipants" class="hint-text">无限制</span>
            </div>
            <p class="form-hint">不填或为空表示无人数限制</p>
          </div>
        </div>

        <!-- 活动状态 -->
        <div class="form-section">
          <h3>活动状态</h3>
          
          <div class="form-group">
            <label>状态</label>
            <select v-model="formData.status">
              <option value="UPCOMING">即将开始</option>
              <option value="ONGOING">进行中</option>
              <option value="ENDED">已结束</option>
              <option value="CANCELLED">已取消</option>
            </select>
          </div>

          <div class="form-group checkbox">
            <label>
              <input 
                v-model="formData.isHot" 
                type="checkbox"
              />
              设为热门活动
            </label>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="goBack">取消</button>
          <button type="submit" class="btn-submit" :disabled="submitting">
            {{ submitting ? '提交中...' : (isEdit ? '更新活动' : '创建活动') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { activityAPI } from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const submitting = ref(false)

const formData = ref({
  title: '',
  description: '',
  coverImage: '',
  startTime: '',
  endTime: '',
  location: '',
  maxParticipants: null,
  registrationDeadline: '',
  status: 'UPCOMING',
  isHot: false
})

const formatDateTimeLocal = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day}T${hours}:${minutes}`
}

const loadActivity = async () => {
  if (!isEdit.value) return
  try {
    const res = await activityAPI.getById(route.params.id)
    if (res.data.code === 200) {
      const activity = res.data.data
      formData.value = {
        title: activity.title || '',
        description: activity.description || '',
        coverImage: activity.coverImage || '',
        startTime: formatDateTimeLocal(activity.startTime),
        endTime: formatDateTimeLocal(activity.endTime),
        location: activity.location || '',
        maxParticipants: activity.maxParticipants || null,
        registrationDeadline: formatDateTimeLocal(activity.registrationDeadline),
        status: activity.status || 'UPCOMING',
        isHot: activity.isHot || false
      }
    }
  } catch (error) {
    console.error('加载活动失败:', error)
    ElMessage.error('加载活动失败')
  }
}

const submitForm = async () => {
  if (!formData.value.title || !formData.value.startTime || !formData.value.endTime) {
    ElMessage.warning('请填写必要信息（标题、开始时间、结束时间）')
    return
  }

  if (new Date(formData.value.startTime) >= new Date(formData.value.endTime)) {
    ElMessage.warning('结束时间必须晚于开始时间')
    return
  }

  if (formData.value.registrationDeadline && new Date(formData.value.registrationDeadline) > new Date(formData.value.startTime)) {
    ElMessage.warning('报名截止时间应该在活动开始前')
    return
  }

  submitting.value = true
  try {
    const data = {
      ...formData.value,
      maxParticipants: formData.value.maxParticipants || null
    }

    if (isEdit.value) {
      const res = await activityAPI.update(route.params.id, data)
      if (res.data.code === 200) {
        ElMessage.success('更新成功')
        router.push(`/activities/${route.params.id}`)
      }
    } else {
      const res = await activityAPI.create(data)
      if (res.data.code === 200 || res.data.code === 201) {
        ElMessage.success('创建成功')
        router.push('/activities')
      }
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || (isEdit.value ? '更新失败' : '创建失败'))
  } finally {
    submitting.value = false
  }
}

const formatDeadlineHint = () => {
  const deadline = new Date(formData.value.registrationDeadline)
  const startTime = new Date(formData.value.startTime)
  const diff = startTime - deadline
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(hours / 24)
  
  if (days >= 1) {
    return `距活动开始还有 ${days} 天 ${hours % 24} 小时`
  } else if (hours > 0) {
    return `距活动开始还有 ${hours} 小时`
  } else if (diff > 0) {
    return `距活动开始还有 ${Math.floor(diff / (1000 * 60))} 分钟`
  } else {
    return '⚠️ 截止时间已超过活动开始时间'
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadActivity()
})
</script>

<style scoped>
.activity-form-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}

.form-container {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.form-container h1 {
  font-size: 28px;
  margin: 0 0 30px;
  color: #333;
}

.form-section {
  margin-bottom: 30px;
}

.form-section h3 {
  font-size: 18px;
  color: #333;
  margin: 0 0 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.required {
  color: #f5576c;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.form-group.checkbox label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 0;
}

.form-group.checkbox input[type="checkbox"] {
  width: auto;
  margin: 0;
}

.form-hint {
  font-size: 12px;
  color: #999;
  margin-top: 6px;
  margin-bottom: 0;
}

.input-with-hint {
  position: relative;
}

.hint-text {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  pointer-events: none;
}

.char-count {
  display: block;
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.image-preview {
  margin-top: 12px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
}

.image-preview img {
  max-width: 100%;
  max-height: 300px;
  border-radius: 4px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid #f0f0f0;
}

.btn-cancel,
.btn-submit {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel {
  background: #f0f0f0;
  color: #333;
  border: 1px solid #ddd;
}

.btn-cancel:hover {
  background: #e8e8e8;
}

.btn-submit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  min-width: 120px;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .form-container {
    padding: 20px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .btn-cancel,
  .btn-submit {
    width: 100%;
  }
}
</style>
