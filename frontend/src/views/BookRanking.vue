<template>
  <div class="ranking-container">
    <div class="page-header">
      <h1 class="page-title">🏆 图书借阅排行榜</h1>
      <p class="page-subtitle">看看哪些图书最受读者欢迎</p>
    </div>

    <!-- 筛选工具栏 -->
    <div class="toolbar">
      <div class="filter-group">
        <label>时间范围：</label>
        <select v-model="timeRange" @change="loadRankings" class="filter-select">
          <option value="all">全部时间</option>
          <option value="month">本月</option>
          <option value="week">本周</option>
        </select>
      </div>
      <div class="filter-group">
        <label>显示数量：</label>
        <select v-model="limit" @change="loadRankings" class="filter-select">
          <option :value="10">前 10 名</option>
          <option :value="20">前 20 名</option>
          <option :value="50">前 50 名</option>
          <option :value="0">全部</option>
        </select>
      </div>
      <button @click="loadRankings" class="refresh-btn" :disabled="loading">
        🔄 刷新
      </button>
    </div>

    <!-- 统计概览 -->
    <div v-if="rankings.length > 0" class="stats-overview">
      <div class="stat-item">
        <span class="stat-value">{{ rankings.length }}</span>
        <span class="stat-label">上榜图书</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ totalBorrows }}</span>
        <span class="stat-label">总借阅次数</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ topBook?.bookTitle || '-' }}</span>
        <span class="stat-label">最热门图书</span>
      </div>
    </div>

    <div class="ranking-content">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <!-- 排行榜列表 -->
      <div v-else-if="rankings.length > 0" class="ranking-list">
        <div 
          v-for="(book, index) in displayRankings" 
          :key="book.bookId" 
          class="ranking-item"
          :class="getRankingClass(index)"
          @click="goToBookDetail(book.bookId)"
        >
          <div class="ranking-position">
            <span v-if="index === 0" class="medal gold">🥇</span>
            <span v-else-if="index === 1" class="medal silver">🥈</span>
            <span v-else-if="index === 2" class="medal bronze">🥉</span>
            <span v-else class="position-number">{{ index + 1 }}</span>
          </div>
          
          <div class="book-cover" :class="'theme-' + (index % 6)">
            <div class="cover-placeholder">
              <span class="cover-icon">📖</span>
            </div>
            <div v-if="index < 3" class="hot-badge">HOT</div>
          </div>
          
          <div class="book-info">
            <h3 class="book-title">{{ book.bookTitle }}</h3>
            <p class="book-author">✍️ {{ book.bookAuthor }}</p>
            <p class="book-category">📂 {{ book.bookCategory || '未分类' }}</p>
            <!-- 借阅进度条 -->
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ width: getProgressWidth(book.borrowCount) }"
              ></div>
            </div>
          </div>
          
          <div class="borrow-stats">
            <div class="borrow-count">{{ book.borrowCount }}</div>
            <div class="borrow-label">次借阅</div>
            <div class="borrow-percent">{{ getPercentage(book.borrowCount) }}%</div>
          </div>

          <div class="action-hint">
            <span>查看详情 →</span>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-else class="empty-state">
        <div class="empty-icon">📊</div>
        <p>暂无借阅数据</p>
        <p class="empty-hint">快去借阅一些图书吧！</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { borrowAPI } from '../api'

const router = useRouter()
const rankings = ref([])
const loading = ref(false)
const timeRange = ref('all')
const limit = ref(10)

// 计算属性
const displayRankings = computed(() => {
  if (limit.value === 0) return rankings.value
  return rankings.value.slice(0, limit.value)
})

const totalBorrows = computed(() => {
  return rankings.value.reduce((sum, book) => sum + book.borrowCount, 0)
})

const topBook = computed(() => {
  return rankings.value[0] || null
})

const maxBorrowCount = computed(() => {
  return rankings.value.length > 0 ? rankings.value[0].borrowCount : 1
})

// 获取排名样式类
const getRankingClass = (index) => {
  const classes = []
  if (index < 3) classes.push('top-three')
  if (index === 0) classes.push('rank-first')
  if (index === 1) classes.push('rank-second')
  if (index === 2) classes.push('rank-third')
  return classes
}

// 获取进度条宽度
const getProgressWidth = (count) => {
  return `${(count / maxBorrowCount.value) * 100}%`
}

// 获取百分比
const getPercentage = (count) => {
  if (totalBorrows.value === 0) return 0
  return ((count / totalBorrows.value) * 100).toFixed(1)
}

// 跳转到图书详情
const goToBookDetail = (bookId) => {
  router.push(`/books/${bookId}`)
}

// 加载排行榜数据
const loadRankings = async () => {
  loading.value = true
  try {
    const response = await borrowAPI.getRanking()
    if (response.data.code === 200) {
      rankings.value = response.data.data
    }
  } catch (error) {
    console.error('获取排行榜失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRankings()
})
</script>

<style scoped>
.ranking-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 10px;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #666;
}

/* 工具栏 */
.toolbar {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-group label {
  color: #666;
  font-size: 0.95rem;
}

.filter-select {
  padding: 8px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-select:hover {
  border-color: #667eea;
}

.refresh-btn {
  margin-left: auto;
  padding: 8px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: all 0.3s;
}

.refresh-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 统计概览 */
.stats-overview {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.stat-item {
  flex: 1;
  background: white;
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.stat-value {
  display: block;
  font-size: 1.5rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stat-label {
  font-size: 0.9rem;
  color: #999;
}

/* 主内容区 */
.ranking-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  margin: 0 auto 20px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.ranking-list {
  padding: 20px;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.ranking-item:last-child {
  border-bottom: none;
}

.ranking-item:hover {
  background-color: #f8f9ff;
  transform: translateX(5px);
}

.ranking-item:hover .action-hint {
  opacity: 1;
}

.top-three {
  background: linear-gradient(90deg, #f8f9ff 0%, #ffffff 100%);
}

.rank-first {
  border-left: 4px solid #ffd700;
}

.rank-second {
  border-left: 4px solid #c0c0c0;
}

.rank-third {
  border-left: 4px solid #cd7f32;
}

.ranking-position {
  width: 60px;
  text-align: center;
  font-size: 1.5rem;
  font-weight: bold;
}

.medal {
  font-size: 2.5rem;
  animation: bounce 2s ease infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.position-number {
  font-size: 1.8rem;
  color: #999;
}

.book-cover {
  width: 80px;
  height: 110px;
  margin: 0 20px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
  transition: transform 0.3s;
}

.ranking-item:hover .book-cover {
  transform: scale(1.05);
}

.book-cover::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, var(--theme-start, #667eea) 0%, var(--theme-end, #764ba2) 100%);
  opacity: 0.9;
}

.hot-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  color: white;
  font-size: 0.6rem;
  font-weight: bold;
  padding: 3px 6px;
  border-radius: 4px;
  z-index: 2;
  animation: pulse 1.5s ease infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.cover-placeholder {
  position: relative;
  z-index: 1;
  color: white;
  font-size: 2rem;
}

.book-info {
  flex: 1;
  padding: 0 20px;
  min-width: 0;
}

.book-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-author,
.book-category {
  font-size: 0.95rem;
  color: #666;
  margin: 4px 0;
}

.progress-bar {
  height: 6px;
  background: #eee;
  border-radius: 3px;
  margin-top: 10px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
  transition: width 0.5s ease;
}

.borrow-stats {
  text-align: center;
  padding: 0 20px;
  min-width: 100px;
}

.borrow-count {
  font-size: 2rem;
  font-weight: 700;
  color: #667eea;
}

.borrow-label {
  font-size: 0.85rem;
  color: #999;
  margin-top: 2px;
}

.borrow-percent {
  font-size: 0.8rem;
  color: #42b983;
  margin-top: 4px;
  font-weight: 500;
}

.action-hint {
  position: absolute;
  right: 20px;
  color: #667eea;
  font-size: 0.9rem;
  opacity: 0;
  transition: opacity 0.3s;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.empty-hint {
  font-size: 0.9rem;
  margin-top: 10px;
}

/* 主题色 */
.theme-0 { --theme-start: #667eea; --theme-end: #764ba2; }
.theme-1 { --theme-start: #11998e; --theme-end: #38ef7d; }
.theme-2 { --theme-start: #fc4a1a; --theme-end: #f7b733; }
.theme-3 { --theme-start: #f093fb; --theme-end: #f5576c; }
.theme-4 { --theme-start: #4facfe; --theme-end: #00f2fe; }
.theme-5 { --theme-start: #43e97b; --theme-end: #38f9d7; }

/* 响应式设计 */
@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .refresh-btn {
    margin-left: 0;
    margin-top: 10px;
  }
  
  .stats-overview {
    flex-direction: column;
  }
  
  .ranking-item {
    flex-direction: column;
    text-align: center;
    padding: 25px 15px;
  }
  
  .ranking-position {
    width: 100%;
    margin-bottom: 15px;
  }
  
  .book-cover {
    margin: 15px 0;
  }
  
  .book-info {
    padding: 15px 0;
    width: 100%;
  }
  
  .borrow-stats {
    padding: 15px 0;
  }
  
  .action-hint {
    position: static;
    opacity: 1;
    margin-top: 10px;
  }
  
  .page-title {
    font-size: 2rem;
  }
}
</style>