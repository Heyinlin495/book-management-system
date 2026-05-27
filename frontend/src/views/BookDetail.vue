<template>
  <div class="book-detail">
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="book" class="detail-container">
      <RouterLink to="/books" class="back-link">← 返回列表</RouterLink>
      
      <div class="detail-content">
        <div class="book-info">
          <h1>{{ book.title }}</h1>
          <div class="meta">
            <p><strong>作者：</strong> {{ book.author }}</p>
            <p><strong>ISBN：</strong> {{ book.isbn }}</p>
            <p><strong>出版社：</strong> {{ book.publisher }}</p>
            <p><strong>出版日期：</strong> {{ book.publishDate }}</p>
            <p><strong>分类：</strong> {{ book.category }}</p>
            <p><strong>价格：</strong> <span class="price">¥{{ book.price }}</span></p>
            <p><strong>库存：</strong> <span :class="{ 'out-of-stock': book.stockQuantity === 0 }">
              {{ book.stockQuantity > 0 ? book.stockQuantity + ' 本' : '已售罄' }}
            </span></p>
          </div>

          <div class="description">
            <h3>图书描述</h3>
            <p>{{ book.description || '暂无描述' }}</p>
          </div>

          <div class="actions">
            <button 
              v-if="book.stockQuantity > 0" 
              @click="showBorrowDialog" 
              class="btn btn-borrow"
            >
              📚 借阅图书
            </button>
            <template v-if="isAdmin">
              <RouterLink :to="`/books/${book.id}/edit`" class="btn btn-edit">编辑</RouterLink>
              <button @click="deleteBook" class="btn btn-delete">删除</button>
            </template>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="not-found">图书未找到</div>

    <!-- 借书对话框 -->
    <div v-if="showDialog" class="dialog-overlay" @click="closeDialog">
      <div class="dialog" @click.stop>
        <h3>借阅图书</h3>
        <div class="dialog-content">
          <p><strong>图书：</strong>{{ book?.title }}</p>
          <div class="form-group">
            <label>借阅天数：</label>
            <select v-model="borrowDays">
              <option :value="7">7天</option>
              <option :value="14">14天</option>
              <option :value="30" selected>30天</option>
              <option :value="60">60天</option>
              <option :value="90">90天</option>
            </select>
          </div>
          <div class="form-group">
            <label>备注：</label>
            <textarea v-model="borrowNotes" placeholder="选填" rows="3"></textarea>
          </div>
        </div>
        <div class="dialog-actions">
          <button @click="confirmBorrow" class="btn btn-primary">确认借阅</button>
          <button @click="closeDialog" class="btn btn-secondary">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { bookAPI, borrowAPI } from '../api'
import { useUserStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const book = ref(null)
const loading = ref(true)
const isAdmin = computed(() => userStore.user?.role === 'ADMIN')

// 借书相关
const showDialog = ref(false)
const borrowDays = ref(30)
const borrowNotes = ref('')

onMounted(async () => {
  try {
    const response = await bookAPI.getById(route.params.id)
    if (response.data.code === 200) {
      book.value = response.data.data
    }
  } catch (error) {
    console.error('获取图书详情失败', error)
  } finally {
    loading.value = false
  }
})

const deleteBook = async () => {
  if (confirm('确定要删除这本书吗？')) {
    try {
      await bookAPI.delete(route.params.id)
      alert('删除成功')
      router.push('/books')
    } catch (error) {
      alert('删除失败')
    }
  }
}

const showBorrowDialog = () => {
  showDialog.value = true
}

const closeDialog = () => {
  showDialog.value = false
  borrowDays.value = 30
  borrowNotes.value = ''
}

const confirmBorrow = async () => {
  // 检查用户是否登录
  if (!userStore.user || !userStore.user.id) {
    alert('请先登录')
    router.push('/login')
    return
  }

  try {
    const response = await borrowAPI.borrow({
      bookId: book.value.id,
      borrowDays: borrowDays.value,
      notes: borrowNotes.value
    })

    if (response.data.code === 200 || response.data.code === 201) {
      alert('借书成功！')
      closeDialog()
      // 重新加载图书信息以更新库存
      const bookResponse = await bookAPI.getById(route.params.id)
      if (bookResponse.data.code === 200) {
        book.value = bookResponse.data.data
      }
    } else {
      alert(response.data.message || '借书失败')
    }
  } catch (error) {
    console.error('借书失败详情:', error)
    console.error('错误响应:', error.response)
    const errorMsg = error.response?.data?.message || error.message || '借书失败，请稍后重试'
    alert(errorMsg)
  }
}
</script>

<style scoped>
.book-detail {
  max-width: 1000px;
  margin: 0 auto;
}

.back-link {
  display: inline-block;
  margin-bottom: 20px;
  color: #667eea;
  text-decoration: none;
  transition: color 0.3s;
}

.back-link:hover {
  color: #5568d3;
}

.detail-container {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.detail-content {
  display: flex;
}

.book-info {
  flex: 1;
}

.book-info h1 {
  margin-top: 0;
  color: #333;
  font-size: 28px;
}

.meta {
  background-color: #f5f5f5;
  padding: 20px;
  border-radius: 4px;
  margin: 20px 0;
}

.meta p {
  margin: 10px 0;
  color: #555;
  line-height: 1.8;
}

.price {
  color: #e74c3c;
  font-size: 20px;
  font-weight: bold;
}

.out-of-stock {
  color: #e74c3c;
  font-weight: bold;
}

.description {
  margin-top: 30px;
}

.description h3 {
  color: #333;
  margin-bottom: 15px;
}

.description p {
  color: #666;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
}

.actions {
  display: flex;
  gap: 10px;
  margin-top: 30px;
}

.btn {
  padding: 10px 20px;
  border-radius: 4px;
  text-decoration: none;
  cursor: pointer;
  border: none;
  font-size: 14px;
  transition: background-color 0.3s;
}

.btn-edit {
  background-color: #3498db;
  color: white;
}

.btn-edit:hover {
  background-color: #2980b9;
}

.btn-delete {
  background-color: #e74c3c;
  color: white;
}

.btn-delete:hover {
  background-color: #c0392b;
}

.loading, .not-found {
  text-align: center;
  padding: 40px;
  color: #999;
}

.btn-borrow {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: 600;
  padding: 12px 30px;
  font-size: 16px;
}

.btn-borrow:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
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

.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  font-family: inherit;
}

.form-group select:focus,
.form-group textarea:focus {
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
