<template>
  <div class="forum-page">
    <div class="page-header">
      <h1>图书社区</h1>
      <p class="subtitle">分享阅读心得，交流学习经验</p>
    </div>

    <!-- 搜索和筛选栏 -->
    <div class="search-filter-bar">
      <div class="search-box">
        <input v-model="searchKeyword" 
               placeholder="搜索帖子标题或内容..." 
               @keyup.enter="handleSearch"
               class="search-input" />
        <button class="btn-search" @click="handleSearch">🔍 搜索</button>
        <button v-if="searchKeyword" class="btn-clear" @click="clearSearch">×</button>
      </div>
      <div class="sort-options">
        <span class="sort-label">排序：</span>
        <button :class="['sort-btn', { active: sortBy === 'latest' }]" @click="setSortBy('latest')">最新</button>
        <button :class="['sort-btn', { active: sortBy === 'hot' }]" @click="setSortBy('hot')">最热</button>
        <button :class="['sort-btn', { active: sortBy === 'likes' }]" @click="setSortBy('likes')">点赞</button>
      </div>
    </div>

    <!-- 版块列表 -->
    <div class="sections-container">
      <div class="section-card all" :class="{ active: currentSection === null }" @click="selectSection(null)">
        <span class="section-icon">🌐</span>
        <div class="section-info">
          <h3>全部</h3>
          <p>所有帖子</p>
        </div>
      </div>
      <div class="section-card" 
           v-for="section in sections" 
           :key="section.id"
           :class="{ active: currentSection === section.id }"
           @click="selectSection(section.id)">
        <span class="section-icon">{{ section.icon || '📚' }}</span>
        <div class="section-info">
          <h3>{{ section.name }}</h3>
          <p>{{ section.postCount || 0 }} 篇帖子</p>
        </div>
      </div>
    </div>

    <!-- 发帖按钮 -->
    <div class="post-actions" v-if="isLoggedIn">
      <button class="btn-create-post" @click="showCreatePostDialog = true">
        ✏️ 发布新帖
      </button>
      <button class="btn-my-favorites" @click="showMyFavorites = !showMyFavorites">
        {{ showMyFavorites ? '📚 所有帖子' : '⭐ 我的收藏' }}
      </button>
    </div>

    <!-- 搜索结果提示 -->
    <div v-if="isSearching" class="search-result-tip">
      <span>搜索 "{{ searchKeyword }}" 的结果，共 {{ displayPosts.length }} 篇帖子</span>
    </div>

    <!-- 帖子列表 -->
    <div class="posts-container">
      <div class="post-card" v-for="post in displayPosts" :key="post.id">
        <div class="post-header">
          <div class="post-author" @click.stop="viewPost(post.id)">
            <img v-if="post.userAvatar" :src="post.userAvatar" class="author-avatar" />
            <div v-else class="author-avatar placeholder">{{ post.username?.charAt(0) }}</div>
            <span class="author-name">{{ post.username }}</span>
          </div>
          <div class="post-badges">
            <span class="badge badge-top" v-if="post.isTop">置顶</span>
            <span class="badge badge-hot" v-if="post.isHot">热门</span>
          </div>
        </div>
        <div class="post-body" @click="viewPost(post.id)">
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-preview">{{ post.content?.substring(0, 150) }}{{ post.content?.length > 150 ? '...' : '' }}</p>
        </div>
        <div class="post-footer">
          <div class="post-meta">
            <span class="meta-item">👁️ {{ post.viewCount }}</span>
            <span class="meta-item">💬 {{ post.commentCount }}</span>
            <span class="meta-item time">📂 {{ post.sectionName }}</span>
            <span class="meta-item time">{{ formatTime(post.createdAt) }}</span>
          </div>
          <div class="post-actions-inline">
            <button 
              :class="['action-btn', 'like-btn', { active: post.isLiked }]" 
              @click.stop="toggleLike(post)"
              :disabled="!isLoggedIn">
              <span>{{ post.isLiked ? '❤️' : '🤍' }}</span>
              <span>{{ post.likeCount }}</span>
            </button>
          </div>
        </div>
      </div>
      <div v-if="displayPosts.length === 0" class="empty-state">
        <span class="empty-icon">📭</span>
        <p>{{ isSearching ? '未找到相关帖子' : '暂无帖子' }}</p>
      </div>
    </div>

    <!-- 发帖对话框 -->
    <div class="dialog-overlay" v-if="showCreatePostDialog" @click.self="showCreatePostDialog = false">
      <div class="dialog">
        <h2>发布新帖</h2>
        <div class="form-group">
          <label>选择版块</label>
          <select v-model="newPost.sectionId">
            <option v-for="s in sections" :key="s.id" :value="s.id">{{ s.name }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>标题</label>
          <input v-model="newPost.title" placeholder="请输入帖子标题" maxlength="100" />
          <span class="char-count">{{ newPost.title?.length || 0 }}/100</span>
        </div>
        <div class="form-group">
          <label>内容</label>
          <textarea v-model="newPost.content" placeholder="请输入帖子内容..." rows="8"></textarea>
        </div>
        <div class="dialog-actions">
          <button class="btn-cancel" @click="showCreatePostDialog = false">取消</button>
          <button class="btn-submit" @click="createPost">发布</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { forumAPI } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const sections = ref([])
const posts = ref([])
const favoritePosts = ref([])
const currentSection = ref(null)
const showCreatePostDialog = ref(false)
const newPost = ref({ sectionId: null, title: '', content: '' })

// 搜索和排序
const searchKeyword = ref('')
const sortBy = ref('latest')
const isSearching = ref(false)
const showMyFavorites = ref(false)

const isLoggedIn = computed(() => userStore.isLoggedIn)

// 显示的帖子列表
const displayPosts = computed(() => {
  let result = showMyFavorites.value ? favoritePosts.value : posts.value
  
  // 按版块筛选
  if (currentSection.value !== null && !showMyFavorites.value) {
    result = result.filter(p => p.sectionId === currentSection.value)
  }
  
  return result
})

const selectSection = (id) => {
  currentSection.value = id
  showMyFavorites.value = false
  if (isSearching.value) {
    handleSearch()
  }
}

const viewPost = (id) => {
  router.push(`/forum/post/${id}`)
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)} 天前`
  return date.toLocaleDateString('zh-CN')
}

// 搜索功能
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    clearSearch()
    return
  }
  isSearching.value = true
  try {
    const res = await forumAPI.searchPosts(searchKeyword.value, currentSection.value)
    if (res.data.code === 200) {
      posts.value = res.data.data
    }
  } catch (error) {
    console.error('搜索失败:', error)
  }
}

const clearSearch = async () => {
  searchKeyword.value = ''
  isSearching.value = false
  await loadPosts()
}

// 排序功能
const setSortBy = async (type) => {
  sortBy.value = type
  try {
    let res
    switch (type) {
      case 'hot':
        res = await forumAPI.getHotPosts()
        break
      case 'likes':
        res = await forumAPI.getPostsByLikes()
        break
      default:
        res = await forumAPI.getLatestPosts()
    }
    if (res.data.code === 200) {
      posts.value = res.data.data
    }
  } catch (error) {
    console.error('加载失败:', error)
  }
}

// 点赞功能
const toggleLike = async (post) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const res = await forumAPI.toggleLike(post.id)
    if (res.data.code === 200) {
      post.isLiked = res.data.data
      post.likeCount = post.isLiked ? post.likeCount + 1 : Math.max(0, post.likeCount - 1)
      ElMessage.success(res.data.message)
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const loadData = async () => {
  try {
    const sectionsRes = await forumAPI.getActiveSections()
    if (sectionsRes.data.code === 200) sections.value = sectionsRes.data.data
  } catch (error) {
    console.error('加载版块失败:', error)
  }
  await loadPosts()
}

const loadPosts = async () => {
  try {
    const postsRes = await forumAPI.getPosts()
    if (postsRes.data.code === 200) posts.value = postsRes.data.data
  } catch (error) {
    console.error('加载帖子失败:', error)
  }
}

const loadFavorites = async () => {
  if (!isLoggedIn.value) return
  try {
    const res = await forumAPI.getMyFavorites()
    if (res.data.code === 200) favoritePosts.value = res.data.data
  } catch (error) {
    console.error('加载收藏失败:', error)
  }
}

const createPost = async () => {
  if (!newPost.value.sectionId || !newPost.value.title || !newPost.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    const res = await forumAPI.createPost({
      ...newPost.value
    })
    if (res.data.code === 200 || res.data.code === 201) {
      ElMessage.success('发布成功')
      showCreatePostDialog.value = false
      newPost.value = { sectionId: null, title: '', content: '' }
      loadData()
    }
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

onMounted(() => {
  loadData()
  loadFavorites()
})
</script>

<style scoped>
.forum-page { max-width: 1200px; margin: 0 auto; padding: 20px; }
.page-header { text-align: center; margin-bottom: 30px; }
.page-header h1 { font-size: 32px; color: #333; margin-bottom: 10px; }
.subtitle { color: #666; font-size: 16px; }

/* 搜索和筛选栏 */
.search-filter-bar { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 15px; margin-bottom: 20px; padding: 15px 20px; background: white; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
.search-box { display: flex; align-items: center; gap: 10px; flex: 1; min-width: 250px; position: relative; }
.search-input { flex: 1; padding: 10px 15px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; transition: border-color 0.3s; }
.search-input:focus { outline: none; border-color: #667eea; }
.btn-search { padding: 10px 16px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 14px; }
.btn-clear { position: absolute; right: 90px; background: none; border: none; color: #999; cursor: pointer; font-size: 18px; padding: 5px; }
.sort-options { display: flex; align-items: center; gap: 8px; }
.sort-label { color: #666; font-size: 14px; }
.sort-btn { padding: 8px 16px; border: 1px solid #ddd; background: white; border-radius: 6px; cursor: pointer; transition: all 0.3s; font-size: 13px; }
.sort-btn:hover, .sort-btn.active { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-color: transparent; }

/* 搜索结果提示 */
.search-result-tip { padding: 12px 20px; background: #e8f4fd; border-radius: 8px; margin-bottom: 15px; color: #1976d2; font-size: 14px; }

.sections-container { display: flex; gap: 15px; flex-wrap: wrap; margin-bottom: 20px; }
.section-card { display: flex; align-items: center; gap: 12px; padding: 15px 20px; background: white; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); cursor: pointer; transition: all 0.3s; }
.section-card:hover, .section-card.active { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; transform: translateY(-2px); }
.section-icon { font-size: 28px; }
.section-info h3 { font-size: 16px; margin: 0; }
.section-info p { font-size: 12px; opacity: 0.8; margin: 4px 0 0; }

.post-actions { margin-bottom: 20px; display: flex; gap: 12px; }
.btn-create-post { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; padding: 12px 24px; border-radius: 8px; font-size: 16px; cursor: pointer; transition: all 0.3s; }
.btn-create-post:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4); }
.btn-my-favorites { background: white; color: #667eea; border: 2px solid #667eea; padding: 10px 20px; border-radius: 8px; font-size: 14px; cursor: pointer; transition: all 0.3s; }
.btn-my-favorites:hover { background: #667eea; color: white; }

.posts-container { display: flex; flex-direction: column; gap: 15px; }
.post-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); transition: all 0.3s; }
.post-card:hover { transform: translateY(-2px); box-shadow: 0 4px 16px rgba(0,0,0,0.12); }
.post-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.post-author { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.author-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; }
.author-avatar.placeholder { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; display: flex; align-items: center; justify-content: center; font-weight: bold; }
.author-name { font-weight: 500; color: #333; }
.post-badges { display: flex; gap: 8px; }
.badge { padding: 4px 8px; border-radius: 4px; font-size: 12px; }
.badge-top { background: #ff6b6b; color: white; }
.badge-hot { background: #ffa502; color: white; }

.post-body { cursor: pointer; }
.post-title { font-size: 18px; color: #333; margin: 0 0 10px; }
.post-preview { color: #666; font-size: 14px; line-height: 1.6; margin: 0 0 15px; }

.post-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 12px; border-top: 1px solid #f0f0f0; }
.post-meta { display: flex; gap: 15px; color: #999; font-size: 13px; }
.meta-item.time { margin-left: 0; }

.post-actions-inline { display: flex; gap: 10px; }
.action-btn { display: flex; align-items: center; gap: 5px; padding: 6px 12px; border: 1px solid #ddd; background: white; border-radius: 20px; cursor: pointer; transition: all 0.3s; font-size: 13px; }
.action-btn:hover:not(:disabled) { border-color: #667eea; color: #667eea; }
.action-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.like-btn.active { background: #fff0f0; border-color: #ff6b6b; color: #ff6b6b; }

.empty-state { text-align: center; padding: 60px 20px; color: #999; background: white; border-radius: 12px; }
.empty-icon { font-size: 48px; display: block; margin-bottom: 15px; }

.dialog-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.dialog { background: white; border-radius: 16px; padding: 30px; width: 90%; max-width: 600px; max-height: 90vh; overflow-y: auto; }
.dialog h2 { margin: 0 0 20px; }
.form-group { margin-bottom: 20px; position: relative; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 500; }
.form-group input, .form-group select, .form-group textarea { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; box-sizing: border-box; }
.form-group textarea { resize: vertical; min-height: 120px; }
.char-count { position: absolute; right: 10px; top: 38px; font-size: 12px; color: #999; }
.dialog-actions { display: flex; justify-content: flex-end; gap: 12px; }
.btn-cancel { padding: 10px 20px; border: 1px solid #ddd; background: white; border-radius: 8px; cursor: pointer; }
.btn-submit { padding: 10px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 8px; cursor: pointer; }
</style>
