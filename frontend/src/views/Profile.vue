<template>
  <div class="profile-page">
    <div class="profile-container">
      <h1>📋 个人中心</h1>
      
      <!-- 个人信息卡片 -->
      <div class="info-card">
        <h2>👤 个人信息</h2>
        <div class="info-content">
          <!-- 头像区域 -->
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <img 
                :src="user.avatar || defaultAvatar" 
                alt="用户头像" 
                class="user-avatar"
              />
              <div class="avatar-overlay" @click="triggerFileInput">
                <span>📷 更换头像</span>
              </div>
            </div>
            <input 
              type="file" 
              ref="fileInput" 
              @change="handleFileChange" 
              accept="image/*" 
              style="display: none"
            />
          </div>
          <div class="info-item">
            <span class="label">用户名：</span>
            <span class="value">{{ user.username }}</span>
          </div>
          <div class="info-item">
            <span class="label">邮箱：</span>
            <span class="value">{{ user.email || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">角色：</span>
            <span class="value role-badge" :class="user.role">
              {{ user.role === 'ADMIN' ? '管理员' : '普通用户' }}
            </span>
          </div>
        </div>
        <button @click="showEditDialog = true" class="btn btn-edit">编辑个人信息</button>
      </div>

      <!-- 我的借阅记录 -->
      <div class="borrow-card">
        <h2>📚 我的借阅记录</h2>
        <div v-if="borrowRecords.length === 0" class="empty-state">
          暂无借阅记录
        </div>
        <div v-else class="borrow-list">
          <div v-for="record in borrowRecords" :key="record.id" class="borrow-item">
            <div class="borrow-header">
              <h3>{{ record.bookTitle }}</h3>
              <span class="status-badge" :class="record.status.toLowerCase()">
                {{ getStatusText(record.status) }}
              </span>
            </div>
            <div class="borrow-details">
              <p><strong>作者：</strong>{{ record.bookAuthor }}</p>
              <p><strong>借阅时间：</strong>{{ formatDate(record.borrowDate) }}</p>
              <p><strong>应还时间：</strong>{{ formatDate(record.dueDate) }}</p>
              <p v-if="record.returnDate"><strong>归还时间：</strong>{{ formatDate(record.returnDate) }}</p>
              <p v-if="record.notes"><strong>备注：</strong>{{ record.notes }}</p>
            </div>
            <div class="borrow-actions">
              <button 
                v-if="record.status === 'BORROWED' || record.status === 'OVERDUE'" 
                @click="returnBook(record.id)"
                class="btn btn-return"
              >
                归还图书
              </button>
              <RouterLink :to="`/books/${record.bookId}`" class="btn btn-view">
                查看图书
              </RouterLink>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑个人信息对话框 -->
    <div v-if="showEditDialog" class="dialog-overlay" @click="closeEditDialog">
      <div class="dialog" @click.stop>
        <h3>编辑个人信息</h3>
        <div class="dialog-content">
          <div class="form-group">
            <label>邮箱：</label>
            <input v-model="editForm.email" type="email" placeholder="请输入邮箱" />
          </div>
          <div class="form-group">
            <label>当前密码：</label>
            <input v-model="editForm.currentPassword" type="password" placeholder="如需修改密码请输入" />
          </div>
          <div class="form-group">
            <label>新密码：</label>
            <input v-model="editForm.newPassword" type="password" placeholder="留空则不修改密码" />
          </div>
        </div>
        <div class="dialog-actions">
          <button @click="updateProfile" class="btn btn-primary">保存</button>
          <button @click="closeEditDialog" class="btn btn-secondary">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '../store/user'
import { userAPI, borrowAPI } from '../api'

const userStore = useUserStore()
const user = computed(() => userStore.user)

const borrowRecords = ref([])
const showEditDialog = ref(false)
const fileInput = ref(null)
const uploadingAvatar = ref(false)

// 默认头像
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiB2aWV3Qm94PSIwIDAgMTAwIDEwMCI+PHJlY3Qgd2lkdGg9IjEwMCIgaGVpZ2h0PSIxMDAiIGZpbGw9IiNlMGUwZTAiLz48Y2lyY2xlIGN4PSI1MCIgY3k9IjM1IiByPSIyMCIgZmlsbD0iIzk5OSIvPjxlbGxpcHNlIGN4PSI1MCIgY3k9Ijk1IiByeD0iMzUiIHJ5PSIzMCIgZmlsbD0iIzk5OSIvPjwvc3ZnPg=='

const editForm = ref({
  email: '',
  currentPassword: '',
  newPassword: ''
})

onMounted(async () => {
  editForm.value.email = user.value.email || ''
  await loadBorrowRecords()
})

const loadBorrowRecords = async () => {
  try {
    const response = await borrowAPI.getUserBorrows()
    if (response.data.code === 200) {
      borrowRecords.value = response.data.data
    }
  } catch (error) {
    console.error('获取借阅记录失败', error)
  }
}

const closeEditDialog = () => {
  showEditDialog.value = false
  editForm.value = {
    email: user.value.email || '',
    currentPassword: '',
    newPassword: ''
  }
}

const updateProfile = async () => {
  try {
    const response = await userAPI.updateProfile(user.value.id, editForm.value)
    if (response.data.code === 200) {
      alert('个人信息更新成功')
      // 更新本地用户信息
      const updatedUser = response.data.data
      userStore.setUser(updatedUser)
      closeEditDialog()
    } else {
      alert(response.data.message || '更新失败')
    }
  } catch (error) {
    console.error('更新个人信息失败', error)
    alert(error.response?.data?.message || '更新失败，请稍后重试')
  }
}

const returnBook = async (recordId) => {
  if (confirm('确定要归还这本书吗？')) {
    try {
      const response = await borrowAPI.return(recordId)
      if (response.data.code === 200) {
        alert('归还成功')
        await loadBorrowRecords()
      } else {
        alert(response.data.message || '归还失败')
      }
    } catch (error) {
      console.error('归还图书失败', error)
      alert(error.response?.data?.message || '归还失败，请稍后重试')
    }
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getStatusText = (status) => {
  const statusMap = {
    'BORROWED': '借阅中',
    'RETURNED': '已归还',
    'OVERDUE': '已逾期'
  }
  return statusMap[status] || status
}

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value.click()
}

// 处理文件选择
const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件')
    return
  }
  
  // 验证文件大小（最大 2MB）
  if (file.size > 2 * 1024 * 1024) {
    alert('图片大小不能超过 2MB')
    return
  }
  
  uploadingAvatar.value = true
  
  try {
    // 读取文件并转换为 Base64
    const base64 = await readFileAsBase64(file)
    
    // 上传头像
    const response = await userAPI.updateAvatar(user.value.id, base64)
    if (response.data.code === 200) {
      alert('头像更新成功')
      // 更新本地用户信息
      const updatedUser = response.data.data
      userStore.setUser(updatedUser)
    } else {
      alert(response.data.message || '头像更新失败')
    }
  } catch (error) {
    console.error('更新头像失败', error)
    alert(error.response?.data?.message || '更新头像失败，请稍后重试')
  } finally {
    uploadingAvatar.value = false
    // 清空文件输入
    event.target.value = ''
  }
}

// 读取文件为 Base64
const readFileAsBase64 = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result)
    reader.onerror = reject
    reader.readAsDataURL(file)
  })
}
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-container h1 {
  color: #333;
  margin-bottom: 30px;
  font-size: 32px;
}

.info-card, .borrow-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
}

.info-card h2, .borrow-card h2 {
  color: #333;
  margin-bottom: 20px;
  font-size: 24px;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 10px;
}

.info-content {
  margin-bottom: 25px;
}

/* 头像样式 */
.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
}

.user-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay span {
  color: white;
  font-size: 14px;
  font-weight: 500;
}

.info-item {
  padding: 15px 0;
  border-bottom: 1px solid #f5f5f5;
  display: flex;
  align-items: center;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  font-weight: 600;
  color: #666;
  width: 120px;
}

.info-item .value {
  color: #333;
  flex: 1;
}

.role-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
}

.role-badge.ADMIN {
  background-color: #ffeaa7;
  color: #d63031;
}

.role-badge.USER {
  background-color: #dfe6e9;
  color: #2d3436;
}

.btn {
  padding: 10px 24px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-edit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-edit:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}

.borrow-list {
  display: grid;
  gap: 20px;
}

.borrow-item {
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  transition: all 0.3s;
}

.borrow-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.borrow-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.borrow-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.borrowed {
  background-color: #74b9ff;
  color: #0984e3;
}

.status-badge.returned {
  background-color: #55efc4;
  color: #00b894;
}

.status-badge.overdue {
  background-color: #ff7675;
  color: #d63031;
}

.borrow-details {
  margin-bottom: 15px;
}

.borrow-details p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
}

.borrow-actions {
  display: flex;
  gap: 10px;
}

.btn-return {
  background-color: #00b894;
  color: white;
}

.btn-return:hover {
  background-color: #00a383;
}

.btn-view {
  background-color: #667eea;
  color: white;
  text-decoration: none;
  display: inline-block;
}

.btn-view:hover {
  background-color: #5568d3;
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  padding: 30px;
  border-radius: 12px;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.dialog h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
  font-size: 22px;
}

.dialog-content {
  margin-bottom: 25px;
}

.form-group {
  margin: 15px 0;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.dialog-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn-primary {
  background-color: #667eea;
  color: white;
}

.btn-primary:hover {
  background-color: #5568d3;
}

.btn-secondary {
  background-color: #e0e0e0;
  color: #333;
}

.btn-secondary:hover {
  background-color: #d0d0d0;
}
</style>
