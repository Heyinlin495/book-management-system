<template>
  <div class="book-list">
    <div class="header">
      <h2>{{ currentCategory ? `「${currentCategory}」图书` : '图书列表' }}</h2>
      <RouterLink v-if="isAdmin" to="/books/add" class="btn btn-add">+ 新增图书</RouterLink>
    </div>

    <!-- 分类筛选条 -->
    <div class="filter-bar">
      <div class="category-filter">
        <button 
          :class="['filter-btn', !currentCategory ? 'active' : '']"
          @click="clearCategory"
        >
          全部
        </button>
        <button 
          v-for="cat in categories" 
          :key="cat"
          :class="['filter-btn', currentCategory === cat ? 'active' : '']"
          @click="filterByCategory(cat)"
        >
          {{ cat }}
        </button>
      </div>
    </div>

    <div class="search-bar">
      <input 
        v-model="searchKeyword" 
        type="text" 
        placeholder="搜索图书标题..." 
        @input="handleSearch"
      />
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="displayedBooks.length === 0" class="empty">
      暂无图书
    </div>
    <div v-else class="table-container">
      <table class="books-table">
        <thead>
          <tr>
            <th>标题</th>
            <th>作者</th>
            <th>分类</th>
            <th>价格</th>
            <th>库存</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="book in displayedBooks" :key="book.id">
            <td>{{ book.title }}</td>
            <td>{{ book.author }}</td>
            <td>{{ book.category }}</td>
            <td>¥{{ book.price }}</td>
            <td>{{ book.stockQuantity }}</td>
            <td class="actions">
              <RouterLink :to="`/books/${book.id}`" class="btn btn-small">详情</RouterLink>
              <RouterLink v-if="isAdmin" :to="`/books/${book.id}/edit`" class="btn btn-small btn-edit">编辑</RouterLink>
              <button v-if="isAdmin" @click="deleteBook(book.id)" class="btn btn-small btn-delete">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBookStore } from '../store/book'
import { useUserStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const bookStore = useBookStore()
const userStore = useUserStore()
const searchKeyword = ref('')
const currentCategory = ref('')
const categories = ref([])

const loading = computed(() => bookStore.loading)
const books = computed(() => bookStore.books)
const isAdmin = computed(() => userStore.user?.role === 'ADMIN')

const displayedBooks = computed(() => {
  if (!searchKeyword.value) {
    return books.value
  }
  return books.value.filter(book => 
    book.title.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

// 加载所有分类
const loadCategories = async () => {
  await bookStore.fetchBooks()
  categories.value = bookStore.getCategories()
}

// 按分类筛选
const filterByCategory = async (category) => {
  currentCategory.value = category
  router.push({ path: '/books', query: { category } })
  try {
    await bookStore.searchByCategory(category)
  } catch (error) {
    console.error('按分类筛选失败', error)
  }
}

// 清除分类筛选
const clearCategory = () => {
  currentCategory.value = ''
  router.push({ path: '/books' })
  bookStore.fetchBooks()
}

// 监听路由参数变化
watch(() => route.query.category, async (newCategory) => {
  if (newCategory) {
    currentCategory.value = newCategory
    await bookStore.searchByCategory(newCategory)
  } else {
    currentCategory.value = ''
    await bookStore.fetchBooks()
  }
}, { immediate: false })

onMounted(async () => {
  // 先加载所有分类
  await loadCategories()
  
  // 检查URL中是否有分类参数
  const categoryFromUrl = route.query.category
  if (categoryFromUrl) {
    currentCategory.value = categoryFromUrl
    await bookStore.searchByCategory(categoryFromUrl)
  }
})

const handleSearch = async () => {
  if (searchKeyword.value.trim()) {
    try {
      const results = await bookStore.searchByTitle(searchKeyword.value)
      bookStore.books = results
    } catch (error) {
      console.error('搜索失败', error)
    }
  } else {
    bookStore.fetchBooks()
  }
}

const deleteBook = async (id) => {
  if (confirm('确定要删除这本书吗？')) {
    try {
      await bookStore.deleteBook(id)
      alert('删除成功')
    } catch (error) {
      alert('删除失败')
    }
  }
}
</script>

<style scoped>
.book-list {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header h2 {
  margin: 0;
  color: #333;
}

/* 分类筛选条 */
.filter-bar {
  margin-bottom: 20px;
}

.category-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 20px;
  background: white;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.filter-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.filter-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
}

.btn-add {
  background-color: #27ae60;
  color: white;
  padding: 10px 20px;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.btn-add:hover {
  background-color: #229954;
}

.search-bar {
  margin-bottom: 20px;
}

.search-bar input {
  width: 100%;
  max-width: 400px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-bar input:focus {
  outline: none;
  border-color: #667eea;
}

.loading, .empty {
  text-align: center;
  padding: 40px;
  color: #999;
}

.table-container {
  overflow-x: auto;
}

.books-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.books-table thead {
  background-color: #f5f5f5;
  border-bottom: 2px solid #ddd;
}

.books-table th {
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #333;
}

.books-table td {
  padding: 12px;
  border-bottom: 1px solid #eee;
}

.books-table tbody tr:hover {
  background-color: #f9f9f9;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-small {
  padding: 6px 12px;
  font-size: 12px;
  border-radius: 3px;
  text-decoration: none;
  cursor: pointer;
  border: none;
  background-color: #667eea;
  color: white;
  transition: background-color 0.3s;
}

.btn-small:hover {
  background-color: #5568d3;
}

.btn-edit {
  background-color: #3498db;
}

.btn-edit:hover {
  background-color: #2980b9;
}

.btn-delete {
  background-color: #e74c3c;
}

.btn-delete:hover {
  background-color: #c0392b;
}
</style>
