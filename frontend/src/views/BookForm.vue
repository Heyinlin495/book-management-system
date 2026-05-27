<template>
  <div class="book-form">
    <div class="form-container">
      <h2>{{ isEdit ? '编辑图书' : '新增图书' }}</h2>
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label>图书标题 *</label>
          <input v-model="form.title" type="text" required />
        </div>

        <div class="form-group">
          <label>作者 *</label>
          <input v-model="form.author" type="text" required />
        </div>

        <div class="form-group">
          <label>ISBN</label>
          <input v-model="form.isbn" type="text" />
        </div>

        <div class="form-group">
          <label>出版社</label>
          <input v-model="form.publisher" type="text" />
        </div>

        <div class="form-group">
          <label>出版日期</label>
          <input v-model="form.publishDate" type="date" />
        </div>

        <div class="form-group">
          <label>价格 *</label>
          <input v-model="form.price" type="number" step="0.01" required />
        </div>

        <div class="form-group">
          <label>分类</label>
          <input v-model="form.category" type="text" />
        </div>

        <div class="form-group">
          <label>库存数量 *</label>
          <input v-model="form.stockQuantity" type="number" required />
        </div>

        <div class="form-group">
          <label>描述</label>
          <textarea v-model="form.description" rows="5"></textarea>
        </div>

        <div class="button-group">
          <button type="submit" class="btn btn-submit" :disabled="loading">
            {{ loading ? '提交中...' : isEdit ? '更新' : '创建' }}
          </button>
          <RouterLink to="/books" class="btn btn-cancel">取消</RouterLink>
        </div>
      </form>
      <p class="error-message" v-if="errorMessage">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { bookAPI } from '../api'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  title: '',
  author: '',
  isbn: '',
  publisher: '',
  publishDate: '',
  price: '',
  category: '',
  stockQuantity: '',
  description: ''
})

const loading = ref(false)
const errorMessage = ref('')

onMounted(async () => {
  if (isEdit.value) {
    try {
      const response = await bookAPI.getById(route.params.id)
      if (response.data.code === 200) {
        const book = response.data.data
        Object.assign(form, book)
      }
    } catch (error) {
      errorMessage.value = '获取图书信息失败'
    }
  }
})

const submitForm = async () => {
  if (!form.title || !form.author || !form.price || form.stockQuantity === '') {
    errorMessage.value = '请填写必填字段'
    return
  }

  loading.value = true
  try {
    if (isEdit.value) {
      await bookAPI.update(route.params.id, form)
      alert('图书更新成功')
    } else {
      await bookAPI.create(form)
      alert('图书创建成功')
    }
    errorMessage.value = ''
    router.push('/books')
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '操作失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.book-form {
  max-width: 800px;
  margin: 0 auto;
}

.form-container {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-container h2 {
  margin-top: 0;
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

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
  font-family: inherit;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 5px rgba(102, 126, 234, 0.3);
}

.button-group {
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
  font-weight: bold;
  transition: background-color 0.3s;
}

.btn-submit {
  background-color: #667eea;
  color: white;
  flex: 1;
}

.btn-submit:hover:not(:disabled) {
  background-color: #5568d3;
}

.btn-submit:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.btn-cancel {
  background-color: #ddd;
  color: #333;
  flex: 1;
  text-align: center;
}

.btn-cancel:hover {
  background-color: #ccc;
}

.error-message {
  color: #e74c3c;
  margin-top: 15px;
  text-align: center;
}
</style>
