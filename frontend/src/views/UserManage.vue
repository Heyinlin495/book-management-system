<template>
  <div class="user-manage">
    <div class="header">
      <h2>用户管理</h2>
      <button @click="showAddForm = true" class="btn btn-primary">添加用户</button>
    </div>

    <!-- 添加用户表单 -->
    <div v-if="showAddForm" class="modal-overlay" @click="showAddForm = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>添加新用户</h3>
          <button class="close-btn" @click="showAddForm = false">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="addUser">
            <div class="form-group">
              <label>用户名</label>
              <input 
                v-model="newUser.username" 
                type="text" 
                required
                placeholder="请输入用户名"
              />
            </div>
            <div class="form-group">
              <label>密码</label>
              <input 
                v-model="newUser.password" 
                type="password" 
                required
                placeholder="请输入密码"
              />
            </div>
            <div class="form-group">
              <label>邮箱</label>
              <input 
                v-model="newUser.email" 
                type="email" 
                required
                placeholder="请输入邮箱"
              />
            </div>
            <div class="form-group">
              <label>角色</label>
              <select v-model="newUser.role">
                <option value="USER">普通用户</option>
                <option value="ADMIN">管理员</option>
              </select>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" @click="showAddForm = false">
                取消
              </button>
              <button type="submit" class="btn btn-primary">
                添加
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="users.length === 0" class="empty">
      暂无用户
    </div>
    <div v-else class="table-container">
      <table class="users-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>
              <select 
                :value="user.role" 
                @change="(e) => updateUserRole(user.id, e.target.value)"
                :disabled="user.id === currentUser.id"
              >
                <option value="USER">普通用户</option>
                <option value="ADMIN">管理员</option>
              </select>
            </td>
            <td>
              <span :class="{ 'active': user.isActive, 'inactive': !user.isActive }">
                {{ user.isActive ? '活跃' : '禁用' }}
              </span>
            </td>
            <td class="actions">
              <button 
                v-if="user.id !== currentUser.id" 
                @click="deleteUser(user.id)" 
                class="btn btn-small btn-delete"
              >
                删除
              </button>
              <span v-else class="badge">当前用户</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { userAPI } from '../api'
import { useUserStore } from '../store/user'

const userStore = useUserStore()
const users = ref([])
const loading = ref(false)
const showAddForm = ref(false)
const currentUser = computed(() => userStore.user)
const newUser = ref({
  username: '',
  password: '',
  email: '',
  role: 'USER'
})

onMounted(async () => {
  await fetchUsers()
})

const fetchUsers = async () => {
  loading.value = true
  try {
    const response = await userAPI.getAll()
    if (response.data.code === 200) {
      users.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取用户列表失败', error)
  } finally {
    loading.value = false
  }
}

const addUser = async () => {
  if (!newUser.value.username || !newUser.value.password || !newUser.value.email) {
    alert('请填一个所有字段')
    return
  }

  try {
    const response = await userAPI.adminAdd({
      username: newUser.value.username,
      password: newUser.value.password,
      email: newUser.value.email,
      role: newUser.value.role
    })
    
    if (response.status === 201 || response.data.code === 200) {
      alert('用户添加成功')
      showAddForm.value = false
      // 重置表单
      newUser.value = {
        username: '',
        password: '',
        email: '',
        role: 'USER'
      }
      // 刷新用户列表
      await fetchUsers()
    }
  } catch (error) {
    console.error('添加用户失败', error)
    let errorMsg = '添加用户失败'
    if (error.response && error.response.data) {
      errorMsg = error.response.data.message || error.response.data.msg || errorMsg
    }
    alert(errorMsg)
  }
}

const updateUserRole = async (userId, newRole) => {
  try {
    await userAPI.update(userId, null, newRole)
    const user = users.value.find(u => u.id === userId)
    if (user) {
      user.role = newRole
    }
    alert('角色更新成功')
  } catch (error) {
    alert('角色更新失败')
  }
}

const deleteUser = async (userId) => {
  if (confirm('确定要删除这个用户吗？')) {
    try {
      const response = await userAPI.delete(userId)
      if (response.data.code === 200) {
        users.value = users.value.filter(u => u.id !== userId)
        alert('用户删除成功')
      } else {
        alert(response.data.message || '用户删除失败')
      }
    } catch (error) {
      console.error('删除用户失败', error)
      let errorMsg = '用户删除失败'
      if (error.response && error.response.data) {
        errorMsg = error.response.data.message || error.response.data.msg || errorMsg
      }
      alert(errorMsg)
    }
  }
}
</script>

<style scoped>
.user-manage {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  margin-bottom: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
  color: #333;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.btn-primary {
  background-color: #667eea;
  color: white;
}

.btn-primary:hover {
  background-color: #5568d3;
}

.btn-secondary {
  background-color: #999;
  color: white;
}

.btn-secondary:hover {
  background-color: #888;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  max-width: 400px;
  width: 90%;
  z-index: 1001;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  color: #999;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #333;
  font-weight: 500;
  font-size: 13px;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #ddd;
  border-radius: 3px;
  font-size: 13px;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

.loading, .empty {
  text-align: center;
  padding: 40px;
  color: #999;
}

.table-container {
  overflow-x: auto;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.users-table thead {
  background-color: #f5f5f5;
  border-bottom: 2px solid #ddd;
}

.users-table th {
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #333;
}

.users-table td {
  padding: 12px;
  border-bottom: 1px solid #eee;
}

.users-table tbody tr:hover {
  background-color: #f9f9f9;
}

.users-table select {
  padding: 6px;
  border: 1px solid #ddd;
  border-radius: 3px;
  font-size: 12px;
  cursor: pointer;
}

.users-table select:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.active {
  color: #27ae60;
  font-weight: bold;
}

.inactive {
  color: #e74c3c;
}

.actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.btn-small {
  padding: 6px 12px;
  font-size: 12px;
  border-radius: 3px;
  cursor: pointer;
  border: none;
  background-color: #e74c3c;
  color: white;
  transition: background-color 0.3s;
}

.btn-delete:hover {
  background-color: #c0392b;
}

.badge {
  display: inline-block;
  background-color: #667eea;
  color: white;
  padding: 4px 8px;
  border-radius: 3px;
  font-size: 12px;
}
</style>
