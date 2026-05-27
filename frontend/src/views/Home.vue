<template>
  <div class="home">
    <!-- 未登录时的 Hero 区域 -->
    <div v-if="!isLoggedIn" class="hero">
      <!-- 动态粒子背景 -->
      <div class="hero-particles">
        <span v-for="n in 20" :key="n" class="particle" :style="getParticleStyle(n)"></span>
      </div>
      <!-- 浮动书籍装饰 -->
      <div class="floating-books">
        <span class="floating-book" v-for="n in 5" :key="n" :style="getFloatingBookStyle(n)">{{ ['📖', '📚', '📕', '📗', '📘'][n-1] }}</span>
      </div>
      <div class="hero-content">
        <div class="book-icon">📚</div>
        <h1 class="hero-title">
          <span class="title-line">欢迎使用</span>
          <span class="title-highlight">图书管理系统</span>
        </h1>
        <p class="hero-subtitle">发现知识的海洋，探索阅读的乐趣</p>
        <div class="hero-features">
          <div class="feature-item">
            <span class="feature-icon">🔍</span>
            <span>智能搜索</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">📊</span>
            <span>借阅管理</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">⭐</span>
            <span>热门推荐</span>
          </div>
        </div>
        <div class="cta-buttons">
          <RouterLink to="/login" class="btn btn-primary">
            <span class="btn-icon">🔑</span>
            立即登录
          </RouterLink>
          <RouterLink to="/register" class="btn btn-secondary">
            <span class="btn-icon">✨</span>
            免费注册
          </RouterLink>
        </div>
      </div>
    </div>

    <!-- 登录后的个性化欢迎区 -->
    <div v-else class="welcome-section">
      <div class="welcome-card">
        <div class="welcome-particles">
          <span v-for="n in 10" :key="n" class="w-particle"></span>
        </div>
        <div class="welcome-left">
          <div class="welcome-avatar" :class="{ 'avatar-pulse': true }">
            <img 
              v-if="userStore.user?.avatar" 
              :src="userStore.user.avatar" 
              alt="用户头像" 
              class="avatar-img"
            />
            <span v-else class="avatar-icon">{{ userStore.user?.username?.charAt(0)?.toUpperCase() || '👤' }}</span>
            <span class="online-indicator"></span>
          </div>
          <div class="welcome-info">
            <p class="welcome-greeting">{{ getGreeting() }}</p>
            <h1>{{ userStore.user?.username || '用户' }}！</h1>
            <p class="welcome-subtitle">{{ getMotivationalQuote() }}</p>
            <div class="user-badge">
              <span :class="['badge', isAdmin ? 'badge-admin' : 'badge-user']">
                {{ isAdmin ? '👑 管理员' : '📖 普通用户' }}
              </span>
              <span class="login-days" v-if="userStore.user?.createdAt">
                📅 已加入 {{ getDaysSinceJoin() }} 天
              </span>
            </div>
          </div>
        </div>
        <div class="welcome-right">
          <div class="quick-actions">
            <RouterLink to="/books" class="action-btn action-browse">
              <span class="action-icon">📚</span>
              <span>浏览图书</span>
              <span class="action-badge">{{ stats.totalBooks }}</span>
            </RouterLink>
            <RouterLink to="/profile" class="action-btn action-profile">
              <span class="action-icon">👤</span>
              <span>个人中心</span>
            </RouterLink>
            <RouterLink v-if="isAdmin" to="/users" class="action-btn action-admin">
              <span class="action-icon">👥</span>
              <span>用户管理</span>
            </RouterLink>
            <RouterLink v-if="isAdmin" to="/books/add" class="action-btn action-add">
              <span class="action-icon">➕</span>
              <span>添加图书</span>
            </RouterLink>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索快捷入口 -->
    <div v-if="isLoggedIn" class="search-quick-entry">
      <div class="search-box" @click="goToSearch">
        <span class="search-icon">🔍</span>
        <span class="search-placeholder">搜索书名、作者、分类...</span>
        <span class="search-shortcut">按 / 快捷搜索</span>
      </div>
    </div>

    <!-- 统计数据区域 -->
    <div v-if="isLoggedIn" class="stats-section">
      <div class="stat-card stat-books" @click="router.push('/books')">
        <div class="stat-icon-wrap">
          <span class="stat-icon">📖</span>
        </div>
        <div class="stat-content">
          <div class="stat-number">
            <span class="counter" ref="booksCounter">{{ animatedStats.totalBooks }}</span>
          </div>
          <div class="stat-label">馆藏图书</div>
          <div class="stat-trend stat-trend-up">📈 持续更新</div>
        </div>
        <div class="stat-bg-icon">📖</div>
      </div>
      <div class="stat-card stat-borrowed" @click="router.push('/profile')">
        <div class="stat-icon-wrap">
          <span class="stat-icon">📋</span>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ animatedStats.myBorrowCount }}</div>
          <div class="stat-label">我的借阅</div>
          <div class="stat-trend">📚 阅读记录</div>
        </div>
        <div class="stat-bg-icon">📋</div>
      </div>
      <div class="stat-card stat-users" v-if="isAdmin" @click="router.push('/users')">
        <div class="stat-icon-wrap">
          <span class="stat-icon">👥</span>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ animatedStats.totalUsers }}</div>
          <div class="stat-label">注册用户</div>
          <div class="stat-trend stat-trend-up">👋 活跃社区</div>
        </div>
        <div class="stat-bg-icon">👥</div>
      </div>
      <div class="stat-card stat-new">
        <div class="stat-icon-wrap">
          <span class="stat-icon">⭐</span>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ animatedStats.newBooks }}</div>
          <div class="stat-label">本月新书</div>
          <div class="stat-trend stat-trend-new">🆕 新书上架</div>
        </div>
        <div class="stat-bg-icon">⭐</div>
      </div>
    </div>

    <!-- 今日推荐 -->
    <div v-if="isLoggedIn && todayRecommend" class="today-recommend-section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-icon">💡</span>
          今日推荐
          <span class="today-date">{{ getTodayDate() }}</span>
        </h2>
        <button class="refresh-btn" @click="refreshRecommend" :class="{ spinning: isRefreshing }">
          🔄 换一本
        </button>
      </div>
      <div class="today-recommend-card" @click="goToBookDetail(todayRecommend.id)">
        <div class="recommend-cover" :class="'book-theme-' + (todayRecommend.id % 6)">
          <div class="recommend-3d-book">
            <div class="r-book-spine"></div>
            <div class="r-book-front">
              <div class="r-book-title">{{ todayRecommend.title }}</div>
              <div class="r-book-author">{{ todayRecommend.author }}</div>
            </div>
          </div>
          <div class="recommend-badge">今日推荐</div>
        </div>
        <div class="recommend-info">
          <h3 class="recommend-title">{{ todayRecommend.title }}</h3>
          <p class="recommend-author">✍️ {{ todayRecommend.author }}</p>
          <p class="recommend-category">📂 {{ todayRecommend.category }}</p>
          <p class="recommend-desc" v-if="todayRecommend.description">
            {{ todayRecommend.description?.slice(0, 100) }}{{ todayRecommend.description?.length > 100 ? '...' : '' }}
          </p>
          <div class="recommend-meta">
            <span class="recommend-year">📅 {{ todayRecommend.publishYear }}</span>
            <span class="recommend-price">💰 ¥{{ todayRecommend.price }}</span>
            <span class="recommend-stock" :class="{ 'low-stock': todayRecommend.stock < 3 }">
              📦 库存: {{ todayRecommend.stock }}
            </span>
          </div>
          <button class="recommend-action-btn" @click.stop="goToBookDetail(todayRecommend.id)">
            查看详情 →
          </button>
        </div>
      </div>
    </div>

    <!-- 借阅排行榜 -->
    <div v-if="isLoggedIn && bookRankings.length > 0" class="ranking-section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-icon">🏆</span>
          借阅排行榜
        </h2>
        <RouterLink to="/ranking" class="view-all-btn">查看完整榜单 →</RouterLink>
      </div>
      <div class="ranking-grid">
        <div 
          v-for="(book, index) in bookRankings.slice(0, 5)" 
          :key="book.bookId" 
          class="ranking-card"
          :class="{ 'top-three': index < 3 }"
          @click="goToBookDetail(book.bookId)"
        >
          <div class="ranking-medal">
            <span v-if="index === 0" class="medal gold">🥇</span>
            <span v-else-if="index === 1" class="medal silver">🥈</span>
            <span v-else-if="index === 2" class="medal bronze">🥉</span>
            <span v-else class="rank-number">{{ index + 1 }}</span>
          </div>
          <div class="ranking-book-info">
            <h4 class="ranking-book-title">{{ book.bookTitle }}</h4>
            <p class="ranking-book-author">{{ book.bookAuthor }}</p>
            <span class="ranking-category">{{ book.bookCategory || '未分类' }}</span>
          </div>
          <div class="ranking-count">
            <span class="count-number">{{ book.borrowCount }}</span>
            <span class="count-label">次借阅</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 社区热帖 -->
    <div v-if="isLoggedIn && hotPosts.length > 0" class="hot-posts-section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-icon">📝</span>
          社区热帖
        </h2>
        <RouterLink to="/forum" class="view-all-btn">更多帖子 →</RouterLink>
      </div>
      <div class="hot-posts-grid">
        <div v-for="post in hotPosts.slice(0, 4)" :key="post.id" class="hot-post-card" @click="router.push(`/forum/post/${post.id}`)">
          <h4>{{ post.title }}</h4>
          <p>{{ post.content?.substring(0, 60) }}...</p>
          <div class="post-footer">
            <span class="author">👤 {{ post.username }}</span>
            <span class="stats">👁️ {{ post.viewCount }} · 💬 {{ post.commentCount }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 热门活动 -->
    <div v-if="isLoggedIn && hotActivities.length > 0" class="activities-section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-icon">🎉</span>
          热门活动
        </h2>
        <RouterLink to="/activities" class="view-all-btn">更多活动 →</RouterLink>
      </div>
      <div class="activities-grid">
        <div v-for="activity in hotActivities.slice(0, 3)" :key="activity.id" class="activity-card" @click="router.push(`/activities/${activity.id}`)">
          <div class="activity-badge" v-if="activity.isHot">🔥 热门</div>
          <h4>{{ activity.title }}</h4>
          <p class="activity-desc">{{ activity.description?.substring(0, 80) }}</p>
          <div class="activity-meta">
            <span>📅 {{ formatActivityDate(activity.startTime) }}</span>
            <span>📍 {{ activity.location || '待定' }}</span>
          </div>
          <div class="activity-participants">
            <span>{{ activity.currentParticipants || 0 }}/{{ activity.maxParticipants || '∞' }} 人报名</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { useBookStore } from '../store/book'
import { borrowAPI, userAPI, forumAPI, activityAPI } from '../api'

const router = useRouter()
const userStore = useUserStore()
const bookStore = useBookStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const isAdmin = computed(() => userStore.user?.role === 'ADMIN')

// 统计数据
const stats = ref({
  totalBooks: 0,
  categories: 0,
  totalUsers: 0,
  newBooks: 0,
  myBorrowCount: 0
})

// 动画统计数据
const animatedStats = ref({
  totalBooks: 0,
  categories: 0,
  totalUsers: 0,
  newBooks: 0,
  myBorrowCount: 0
})

// 借阅排行榜
const bookRankings = ref([])

// 社区热帖
const hotPosts = ref([])

// 热门活动
const hotActivities = ref([])

// 今日推荐
const todayRecommend = ref(null)
const isRefreshing = ref(false)

// 动効计时器
let animationTimers = []

// 书籍装饰符号
const bookOrnaments = ['✦', '❋', '✿', '◆', '★', '❖']

// 励志语录
const motivationalQuotes = [
  '今天想读些什么书呢？',
  '阅读是心灵的旅行！',
  '一本好书，一段好时光',
  '让我们开始今天的阅读之旅',
  '知识的海洋等待你探索',
  '书籍是人类进步的阶梯'
]

// 获取问候语
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 6) return '🌙 夜深了'
  if (hour < 9) return '🌅 早上好'
  if (hour < 12) return '☀️ 上午好'
  if (hour < 14) return '🌞 中午好'
  if (hour < 18) return '🌤️ 下午好'
  if (hour < 22) return '🌆 晚上好'
  return '🌙 夜深了'
}

// 获取励志语
const getMotivationalQuote = () => {
  const index = new Date().getDate() % motivationalQuotes.length
  return motivationalQuotes[index]
}

// 获取加入天数
const getDaysSinceJoin = () => {
  if (!userStore.user?.createdAt) return 0
  const joinDate = new Date(userStore.user.createdAt)
  const today = new Date()
  const diffTime = Math.abs(today - joinDate)
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
}

// 获取今日日期
const getTodayDate = () => {
  const today = new Date()
  return `${today.getMonth() + 1}月${today.getDate()}日`
}

// 获取粒子样式
const getParticleStyle = (n) => {
  const size = Math.random() * 10 + 5
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 5}s`,
    animationDuration: `${Math.random() * 10 + 10}s`
  }
}

// 获取浮动书籍样式
const getFloatingBookStyle = (n) => {
  return {
    left: `${10 + (n - 1) * 20}%`,
    animationDelay: `${n * 0.5}s`,
    fontSize: `${30 + Math.random() * 20}px`
  }
}

// 数字滚动动画
const animateNumber = (key, target, duration = 1500) => {
  const start = animatedStats.value[key]
  const startTime = Date.now()
  
  const animate = () => {
    const elapsed = Date.now() - startTime
    const progress = Math.min(elapsed / duration, 1)
    
    // 使用 easeOutExpo 缓动函数
    const easeProgress = progress === 1 ? 1 : 1 - Math.pow(2, -10 * progress)
    animatedStats.value[key] = Math.floor(start + (target - start) * easeProgress)
    
    if (progress < 1) {
      const timer = requestAnimationFrame(animate)
      animationTimers.push(timer)
    }
  }
  animate()
}

// 跳转到搜索
const goToSearch = () => {
  router.push('/books')
}

// 刷新推荐
const refreshRecommend = async () => {
  if (isRefreshing.value) return
  isRefreshing.value = true
  
  const books = bookStore.books
  if (books.length > 0) {
    const randomIndex = Math.floor(Math.random() * books.length)
    todayRecommend.value = books[randomIndex]
  }
  
  setTimeout(() => {
    isRefreshing.value = false
  }, 500)
}



// 跳转到图书详情
const goToBookDetail = (id) => {
  router.push(`/books/${id}`)
}

// 加载排行榜数据
const loadRankings = async () => {
  try {
    const response = await borrowAPI.getRanking()
    if (response.data.code === 200) {
      bookRankings.value = response.data.data
    }
  } catch (error) {
    console.error('获取排行榜失败:', error)
  }
}

// 加载我的借阅记录数量
const loadMyBorrowCount = async () => {
  try {
    const response = await borrowAPI.getMyRecords()
    if (response.data.code === 200) {
      const records = response.data.data || []
      stats.value.myBorrowCount = records.filter(r => r.status === 'BORROWED').length
    }
  } catch (error) {
    console.error('获取借阅记录失败:', error)
  }
}

// 加载用户数量（管理员）
const loadUserCount = async () => {
  if (isAdmin.value) {
    try {
      const response = await userAPI.getAllUsers()
      if (response.data.code === 200) {
        stats.value.totalUsers = response.data.data?.length || 0
      }
    } catch (error) {
      console.error('获取用户数量失败:', error)
    }
  }
}

// 加载社区热帖
const loadHotPosts = async () => {
  try {
    const response = await forumAPI.getHotPosts()
    if (response.data.code === 200) {
      hotPosts.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取社区热帖失败:', error)
  }
}

// 加载热门活动
const loadHotActivities = async () => {
  try {
    const response = await activityAPI.getUpcoming()
    if (response.data.code === 200) {
      hotActivities.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取热门活动失败:', error)
  }
}

// 格式化活动日期
const formatActivityDate = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

// 加载数据
const loadData = async () => {
  if (isLoggedIn.value) {
    try {
      // 获取图书列表
      await bookStore.fetchBooks()
      const books = bookStore.books
      
      // 计算统计数据
      stats.value.totalBooks = books.length
      
      // 计算分类数量
      const categories = new Set(books.map(book => book.category).filter(c => c))
      stats.value.categories = categories.size
      
      // 本月新书数量
      stats.value.newBooks = Math.min(books.length, 8)
      
      // 设置今日推荐（基于日期的随机推荐）
      if (books.length > 0) {
        const dayIndex = new Date().getDate() % books.length
        todayRecommend.value = books[dayIndex]
      }
      


      // 加载排行榜
      await loadRankings()
      
      // 加载我的借阅记录
      await loadMyBorrowCount()
      
      // 加载用户数量
      await loadUserCount()
      
      // 加载社区热帖
      await loadHotPosts()
      
      // 加载热门活动
      await loadHotActivities()
      
      // 触发数字动画
      setTimeout(() => {
        animateNumber('totalBooks', stats.value.totalBooks)
        animateNumber('categories', stats.value.categories)
        animateNumber('totalUsers', stats.value.totalUsers)
        animateNumber('newBooks', stats.value.newBooks)
        animateNumber('myBorrowCount', stats.value.myBorrowCount)
      }, 300)
      
    } catch (error) {
      console.error('加载数据失败:', error)
    }
  }
}

// 键盘快捷键支持
const handleKeyPress = (e) => {
  if (e.key === '/' && isLoggedIn.value) {
    e.preventDefault()
    goToSearch()
  }
}

onMounted(() => {
  loadData()
  window.addEventListener('keydown', handleKeyPress)
})

onUnmounted(() => {
  // 清理动画计时器
  animationTimers.forEach(timer => cancelAnimationFrame(timer))
  window.removeEventListener('keydown', handleKeyPress)
})
</script>

<style scoped>
.home {
  max-width: 1400px;
  margin: 0 auto;
  padding-bottom: 60px;
}

/* Hero区域 - 未登录 */
.hero {
  text-align: center;
  padding: 100px 20px 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #6c5ce7 100%);
  color: white;
  border-radius: 24px;
  margin-bottom: 60px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.3);
}

/* 粒子动画 */
.hero-particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  pointer-events: none;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: particleFloat linear infinite;
}

@keyframes particleFloat {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) rotate(720deg);
    opacity: 0;
  }
}

/* 浮动书籍 */
.floating-books {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.floating-book {
  position: absolute;
  opacity: 0.15;
  animation: floatBook 8s ease-in-out infinite;
}

@keyframes floatBook {
  0%, 100% { transform: translateY(0) rotate(-5deg); }
  50% { transform: translateY(-30px) rotate(5deg); }
}

.hero::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  animation: float 15s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(50px, 50px) rotate(180deg); }
}

.hero-content {
  position: relative;
  z-index: 1;
}

.book-icon {
  font-size: 80px;
  margin-bottom: 20px;
  animation: bounce 2s ease-in-out infinite;
  filter: drop-shadow(0 10px 20px rgba(0,0,0,0.2));
}

@keyframes bounce {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-20px) scale(1.05); }
}

.hero-title {
  margin-bottom: 20px;
}

.title-line {
  display: block;
  font-size: 28px;
  font-weight: 400;
  opacity: 0.9;
  margin-bottom: 8px;
}

.title-highlight {
  display: block;
  font-size: 52px;
  font-weight: 900;
  background: linear-gradient(90deg, #fff 0%, #f0e6ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -1px;
}

.hero-subtitle {
  font-size: 20px;
  margin-bottom: 30px;
  opacity: 0.95;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.hero-features {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 40px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  opacity: 0.9;
}

.feature-icon {
  font-size: 20px;
}

.cta-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.btn {
  padding: 16px 40px;
  border-radius: 12px;
  text-decoration: none;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  display: inline-flex;
  align-items: center;
  gap: 8px;
  letter-spacing: 0.3px;
}

.btn-icon {
  font-size: 18px;
}

.btn-primary {
  background: white;
  color: #667eea;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  border: 2px solid white;
}

.btn-primary:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.25);
  background: #f8f9ff;
  color: #5c63d8;
}

.btn-secondary {
  background: rgba(255,255,255,0.15);
  color: white;
  border: 2px solid rgba(255,255,255,0.4);
  backdrop-filter: blur(10px);
}

.btn-secondary:hover {
  background: rgba(255,255,255,0.25);
  color: white;
  border-color: white;
  transform: translateY(-6px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.2);
}

/* 欢迎区域 - 登录后 */
.welcome-section {
  margin-bottom: 30px;
  animation: fadeInScale 0.6s ease-out;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #6c5ce7 100%);
  border-radius: 24px;
  padding: 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.25);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.welcome-particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.w-particle {
  position: absolute;
  width: 6px;
  height: 6px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  animation: wParticle 10s linear infinite;
}

.w-particle:nth-child(1) { left: 10%; top: 20%; animation-delay: 0s; }
.w-particle:nth-child(2) { left: 20%; top: 80%; animation-delay: 1s; }
.w-particle:nth-child(3) { left: 30%; top: 40%; animation-delay: 2s; }
.w-particle:nth-child(4) { left: 50%; top: 60%; animation-delay: 3s; }
.w-particle:nth-child(5) { left: 60%; top: 30%; animation-delay: 4s; }
.w-particle:nth-child(6) { left: 70%; top: 70%; animation-delay: 5s; }
.w-particle:nth-child(7) { left: 80%; top: 20%; animation-delay: 6s; }
.w-particle:nth-child(8) { left: 85%; top: 50%; animation-delay: 7s; }
.w-particle:nth-child(9) { left: 90%; top: 80%; animation-delay: 8s; }
.w-particle:nth-child(10) { left: 95%; top: 40%; animation-delay: 9s; }

@keyframes wParticle {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.2; }
  50% { transform: translateY(-20px) scale(1.5); opacity: 0.6; }
}

.welcome-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 60%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
}

.welcome-left {
  display: flex;
  align-items: center;
  gap: 24px;
  position: relative;
  z-index: 1;
}

.welcome-avatar {
  width: 90px;
  height: 90px;
  background: rgba(255,255,255,0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(20px);
  overflow: hidden;
  position: relative;
  border: 3px solid rgba(255,255,255,0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.avatar-pulse::after {
  content: '';
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  border-radius: 50%;
  border: 3px solid rgba(255,255,255,0.5);
  animation: avatarPulse 2s ease-in-out infinite;
}

@keyframes avatarPulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.1); opacity: 0.5; }
}

.online-indicator {
  position: absolute;
  bottom: 5px;
  right: 5px;
  width: 16px;
  height: 16px;
  background: #2ecc71;
  border-radius: 50%;
  border: 3px solid white;
  animation: onlinePulse 2s ease-in-out infinite;
}

@keyframes onlinePulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-icon {
  font-size: 40px;
  font-weight: bold;
}

.welcome-greeting {
  font-size: 15px;
  opacity: 0.9;
  margin-bottom: 4px;
  font-weight: 500;
  letter-spacing: 0.2px;
}

.welcome-info h1 {
  font-size: 36px;
  margin-bottom: 8px;
  font-weight: 800;
  letter-spacing: -0.8px;
  background: linear-gradient(90deg, #ffffff 0%, #f0e6ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 14px;
  font-weight: 500;
}

.user-badge {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.badge {
  display: inline-block;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  letter-spacing: 0.3px;
}

.badge-admin {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 6px 20px rgba(245, 87, 108, 0.3);
}

.badge-user {
  background: rgba(255,255,255,0.25);
  border: 1px solid rgba(255,255,255,0.3);
}

.login-days {
  font-size: 13px;
  opacity: 0.9;
  background: rgba(255,255,255,0.15);
  padding: 6px 12px;
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.2);
  font-weight: 500;
  letter-spacing: 0.2px;
}

.welcome-right {
  position: relative;
  z-index: 1;
}

.quick-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 18px 22px;
  background: rgba(255,255,255,0.15);
  border-radius: 16px;
  text-decoration: none;
  color: white;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  backdrop-filter: blur(20px);
  min-width: 95px;
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.action-btn:hover {
  background: rgba(255,255,255,0.25);
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
}

.action-icon {
  font-size: 26px;
}

.action-btn span:nth-child(2) {
  font-size: 13px;
  font-weight: 500;
}

.action-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #f5576c;
  color: white;
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 10px;
  font-weight: 600;
}

/* 搜索快捷入口 */
.search-quick-entry {
  margin-bottom: 30px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 18px 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 2px solid rgba(102, 126, 234, 0.1);
}

.search-box:hover {
  border-color: #667eea;
  box-shadow: 0 16px 48px rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
}

.search-icon {
  font-size: 22px;
}

.search-placeholder {
  flex: 1;
  color: #999;
  font-size: 15px;
}

.search-shortcut {
  background: #f5f7fa;
  color: #666;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
}

/* 统计数据区域 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
  animation: slideInUp 0.7s ease-out;
}

.stat-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
  padding: 24px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(102, 126, 234, 0.05);
}

.stat-card:hover {
  transform: translateY(-12px) scale(1.02);
  box-shadow: 0 24px 48px rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.15);
}

.stat-bg-icon {
  position: absolute;
  right: -10px;
  bottom: -10px;
  font-size: 80px;
  opacity: 0.06;
  transform: rotate(-15deg);
  pointer-events: none;
}

.stat-icon-wrap {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-books .stat-icon-wrap {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-categories .stat-icon-wrap {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.stat-users .stat-icon-wrap {
  background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%);
}

.stat-borrowed .stat-icon-wrap {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-new .stat-icon-wrap {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon {
  font-size: 26px;
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-number {
  font-size: 32px;
  font-weight: 800;
  color: #1a1a1a;
  line-height: 1.2;
  letter-spacing: -0.5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
  font-weight: 500;
}

.stat-trend {
  font-size: 11px;
  margin-top: 6px;
  padding: 3px 8px;
  border-radius: 8px;
  display: inline-block;
  background: #f5f7fa;
  color: #666;
}

.stat-trend-up {
  background: #e8f5e9;
  color: #2ecc71;
}

.stat-trend-new {
  background: #fff3e0;
  color: #ff9800;
}

/* 今日推荐区域 */
.today-recommend-section {
  margin-bottom: 40px;
  animation: slideInUp 0.8s ease-out;
}

.today-date {
  font-size: 14px;
  font-weight: 400;
  color: #999;
  margin-left: 10px;
}

.refresh-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.refresh-btn:hover {
  transform: scale(1.08) translateY(-2px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.refresh-btn.spinning {
  animation: spin 0.6s linear;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes shimmer {
  0% {
    background-position: -1000px 0;
  }
  100% {
    background-position: 1000px 0;
  }
}

.today-recommend-card {
  display: flex;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid rgba(102, 126, 234, 0.08);
}

.today-recommend-card:hover {
  transform: translateY(-12px) scale(1.01);
  box-shadow: 0 32px 60px rgba(102, 126, 234, 0.25);
}

.recommend-cover {
  width: 280px;
  min-height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #6c5ce7 100%);
  box-shadow: inset 0 0 60px rgba(255, 255, 255, 0.1);
}

.recommend-badge {
  position: absolute;
  top: 20px;
  left: 20px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  padding: 10px 18px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  box-shadow: 0 6px 20px rgba(240, 147, 251, 0.5);
  letter-spacing: 0.3px;
}

.recommend-3d-book {
  width: 120px;
  height: 160px;
  position: relative;
  transform-style: preserve-3d;
  transform: rotateY(-20deg) rotateX(5deg);
  transition: transform 0.5s ease;
}

.today-recommend-card:hover .recommend-3d-book {
  transform: rotateY(-30deg) rotateX(10deg) scale(1.1);
}

.r-book-spine {
  position: absolute;
  left: 0;
  top: 0;
  width: 20px;
  height: 100%;
  background: linear-gradient(to right, #c0392b, #e74c3c, #c0392b);
  transform: rotateY(90deg) translateZ(-10px);
  border-radius: 2px 0 0 2px;
}

.r-book-front {
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  border-radius: 0 4px 4px 0;
  transform: translateZ(10px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
  box-shadow: 0 5px 20px rgba(0,0,0,0.3);
}

.r-book-title {
  color: white;
  font-size: 14px;
  font-weight: 700;
  text-align: center;
  line-height: 1.3;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.r-book-author {
  color: rgba(255,255,255,0.8);
  font-size: 11px;
  text-align: center;
}

.recommend-info {
  flex: 1;
  padding: 30px;
  display: flex;
  flex-direction: column;
}

.recommend-title {
  font-size: 26px;
  font-weight: 800;
  color: #1a1a1a;
  margin-bottom: 12px;
  letter-spacing: -0.5px;
}

.recommend-author {
  font-size: 16px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 500;
}

.recommend-category {
  font-size: 15px;
  color: #667eea;
  margin-bottom: 16px;
  font-weight: 500;
  letter-spacing: 0.2px;
}

.recommend-desc {
  font-size: 15px;
  color: #555;
  line-height: 1.8;
  margin-bottom: 20px;
  flex: 1;
}

.recommend-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.recommend-meta span {
  font-size: 15px;
  color: #777;
  font-weight: 500;
}

.recommend-stock {
  color: #2ecc71 !important;
}

.recommend-stock.low-stock {
  color: #e74c3c !important;
}

.recommend-action-btn {
  align-self: flex-start;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 14px 32px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
  letter-spacing: 0.3px;
}

.recommend-action-btn:hover {
  transform: translateX(6px) translateY(-2px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.4);
}

/* 区域标题 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  animation: slideInUp 0.5s ease-out backwards;
}

.section-title {
  font-size: 28px;
  font-weight: 800;
  color: #1a1a1a;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
  letter-spacing: -0.5px;
}

.title-icon {
  font-size: 28px;
}

.view-all-btn {
  color: #667eea;
  text-decoration: none;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  display: flex;
  align-items: center;
  gap: 4px;
  letter-spacing: 0.2px;
}

.view-all-btn:hover {
  color: #764ba2;
  transform: translateX(4px);
}



/* 排行榜区域 */
.ranking-section {
  margin-bottom: 50px;
  animation: slideInUp 0.9s ease-out;
}

.ranking-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-card {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  cursor: pointer;
  border: 1px solid rgba(102, 126, 234, 0.03);
}

.ranking-card:hover {
  transform: translateX(8px) translateY(-4px);
  box-shadow: 0 16px 40px rgba(102, 126, 234, 0.15);
  border-color: rgba(102, 126, 234, 0.1);
}

.ranking-card.top-three {
  background: linear-gradient(90deg, #f8f9ff 0%, #ffffff 100%);
  border-left: 4px solid #667eea;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.12);
}

.ranking-medal {
  width: 50px;
  text-align: center;
  margin-right: 20px;
}

.medal {
  font-size: 2rem;
}

.rank-number {
  font-size: 1.5rem;
  font-weight: 700;
  color: #999;
}

.ranking-book-info {
  flex: 1;
  min-width: 0;
}

.ranking-book-title {
  font-size: 1.15rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 6px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  letter-spacing: -0.3px;
}

.ranking-book-author {
  font-size: 0.9rem;
  color: #666;
  margin: 0 0 8px 0;
}

.ranking-category {
  display: inline-block;
  padding: 6px 14px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
  border-radius: 20px;
  font-size: 0.8rem;
  color: #667eea;
  font-weight: 600;
  letter-spacing: 0.2px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
}

.ranking-count {
  text-align: center;
  padding-left: 20px;
}

.count-number {
  display: block;
  font-size: 2rem;
  font-weight: 800;
  color: #667eea;
  line-height: 1;
  letter-spacing: -0.5px;
}

.count-label {
  font-size: 0.85rem;
  color: #888;
  font-weight: 500;
}

@media (max-width: 768px) {
  .ranking-card {
    flex-wrap: wrap;
    padding: 15px;
  }
  
  .ranking-medal {
    margin-right: 15px;
  }
  
  .ranking-book-info {
    flex: 1;
    min-width: calc(100% - 150px);
  }
  
  .ranking-count {
    padding-left: 0;
    padding-top: 10px;
    width: 100%;
    text-align: left;
    display: flex;
    align-items: center;
    gap: 5px;
  }
  
  .count-number {
    font-size: 1.2rem;
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .welcome-card {
    flex-direction: column;
    gap: 30px;
    text-align: center;
  }
  
  .welcome-left {
    flex-direction: column;
  }
  
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .today-recommend-card {
    flex-direction: column;
  }
  
  .recommend-cover {
    width: 100%;
    min-height: 220px;
  }
  
  .hero-features {
    flex-wrap: wrap;
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .hero {
    padding: 60px 20px;
    border-radius: 16px;
  }
  
  .title-line {
    font-size: 20px;
  }
  
  .title-highlight {
    font-size: 36px;
  }
  
  .hero-subtitle {
    font-size: 16px;
  }
  
  .hero-features {
    display: none;
  }
  
  .welcome-info h1 {
    font-size: 24px;
  }
  
  .quick-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .action-btn {
    min-width: 80px;
    padding: 14px 16px;
  }
  
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .stat-number {
    font-size: 24px;
  }
  
  .stat-bg-icon {
    display: none;
  }
  
  .section-title {
    font-size: 20px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .book-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .cta-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .btn {
    width: 100%;
    max-width: 280px;
    justify-content: center;
  }
  
  .search-box {
    padding: 14px 18px;
  }
  
  .search-shortcut {
    display: none;
  }
  
  .recommend-info {
    padding: 20px;
  }
  
  .recommend-title {
    font-size: 20px;
  }
  
  .recommend-meta {
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .stats-section {
    grid-template-columns: 1fr;
  }
}

/* 社区热帖区域 */
.hot-posts-section {
  margin-bottom: 40px;
  animation: slideInUp 1s ease-out;
}

.hot-posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.hot-post-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid rgba(102, 126, 234, 0.05);
}

.hot-post-card:hover {
  transform: translateY(-6px) scale(1.01);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.1);
}

.hot-post-card h4 {
  margin: 0 0 10px;
  font-size: 17px;
  color: #1a1a1a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 700;
  letter-spacing: -0.3px;
}

.hot-post-card p {
  color: #666;
  font-size: 15px;
  line-height: 1.6;
  margin: 0 0 15px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 13px;
  font-weight: 500;
}

/* 热门活动区域 */
.activities-section {
  margin-bottom: 40px;
  animation: slideInUp 1.1s ease-out;
}

.activities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.activity-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  border: 1px solid rgba(102, 126, 234, 0.05);
}

.activity-card:hover {
  transform: translateY(-6px) scale(1.01);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.1);
}

.activity-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  color: white;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.activity-card h4 {
  margin: 0 0 10px;
  font-size: 19px;
  color: #1a1a1a;
  padding-right: 70px;
  font-weight: 700;
  letter-spacing: -0.3px;
}

.activity-desc {
  color: #666;
  font-size: 15px;
  line-height: 1.6;
  margin: 0 0 15px;
}

.activity-meta {
  display: flex;
  gap: 15px;
  color: #888;
  font-size: 14px;
  margin-bottom: 12px;
  font-weight: 500;
}

.activity-participants {
  color: #667eea;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.2px;
}
</style>
