<template>
  <div class="post-detail-page" v-if="post">
    <div class="back-btn" @click="$router.back()">← 返回社区</div>
    
    <div class="post-container">
      <div class="post-header">
        <h1>{{ post.title }}</h1>
        <div class="post-info">
          <div class="author-info">
            <img v-if="post.userAvatar" :src="post.userAvatar" class="avatar" />
            <div v-else class="avatar placeholder">{{ post.username?.charAt(0) }}</div>
            <span class="author-name">{{ post.username }}</span>
          </div>
          <span class="section-tag">📂 {{ post.sectionName }}</span>
          <span class="post-time">{{ formatTime(post.createdAt) }}</span>
        </div>
        <div class="post-stats">
          <span>👁️ {{ post.viewCount }} 阅读</span>
          <span>💬 {{ post.commentCount }} 评论</span>
          <span>❤️ {{ post.likeCount }} 点赞</span>
        </div>
      </div>
      
      <div class="post-content">{{ post.content }}</div>
      
      <!-- 互动按钮 -->
      <div class="post-interactions" v-if="isLoggedIn">
        <button :class="['interact-btn', { active: post.isLiked }]" @click="toggleLike">
          <span>{{ post.isLiked ? '❤️' : '🤍' }}</span>
          <span>{{ post.isLiked ? '已点赞' : '点赞' }}</span>
        </button>
        <button :class="['interact-btn', { active: post.isFavorited }]" @click="toggleFavorite">
          <span>{{ post.isFavorited ? '⭐' : '☆' }}</span>
          <span>{{ post.isFavorited ? '已收藏' : '收藏' }}</span>
        </button>
        <button class="interact-btn" @click="scrollToComment">
          <span>💬</span>
          <span>评论</span>
        </button>
      </div>
      
      <div class="post-actions" v-if="isMyPost">
        <button class="btn-edit" @click="showEditDialog = true">✏️ 编辑</button>
        <button class="btn-delete" @click="deletePost">🗑️ 删除</button>
      </div>
    </div>

    <div class="comments-section" ref="commentSection">
      <h2>评论 ({{ comments.length }})</h2>
      
      <div class="comment-form" v-if="isLoggedIn">
        <div class="replying-to" v-if="replyingTo">
          回复 @{{ replyingTo.username }}：{{ replyingTo.content?.substring(0, 30) }}...
          <button class="btn-cancel-reply" @click="cancelReply">×</button>
        </div>
        <textarea v-model="newComment" :placeholder="replyingTo ? '写下你的回复...' : '写下你的评论...'" rows="3"></textarea>
        <button class="btn-comment" @click="submitComment">{{ replyingTo ? '发布回复' : '发布评论' }}</button>
      </div>
      <div v-else class="login-hint">
        <span>🔒 请<router-link to="/login">登录</router-link>后发表评论</span>
      </div>
      
      <div class="comments-list">
        <div class="comment-item" v-for="comment in comments" :key="comment.id">
          <div class="comment-header">
            <img v-if="comment.userAvatar" :src="comment.userAvatar" class="comment-avatar" />
            <div v-else class="comment-avatar placeholder">{{ comment.username?.charAt(0) }}</div>
            <div class="comment-info">
              <span class="comment-author">{{ comment.username }}</span>
              <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
            </div>
            <div class="comment-actions-right">
              <button class="btn-reply" @click="replyTo(comment)" v-if="isLoggedIn">💬 回复</button>
              <button v-if="comment.userId === userStore.user?.id" class="btn-delete-comment" @click="deleteComment(comment.id)">删除</button>
            </div>
          </div>
          <div class="comment-content">
            <span v-if="comment.parentId" class="reply-hint">回复 @{{ getParentCommentUsername(comment.parentId) }}: </span>
            {{ comment.content }}
          </div>
        </div>
        <div v-if="comments.length === 0" class="no-comments">
          <span class="empty-icon">💭</span>
          <p>暂无评论，来发表第一条评论吧！</p>
        </div>
      </div>
    </div>

    <!-- 编辑帖子对话框 -->
    <div class="dialog-overlay" v-if="showEditDialog" @click.self="showEditDialog = false">
      <div class="dialog">
        <h2>编辑帖子</h2>
        <div class="form-group">
          <label>标题</label>
          <input v-model="editForm.title" placeholder="请输入帖子标题" maxlength="100" />
        </div>
        <div class="form-group">
          <label>内容</label>
          <textarea v-model="editForm.content" placeholder="请输入帖子内容..." rows="10"></textarea>
        </div>
        <div class="dialog-actions">
          <button class="btn-cancel" @click="showEditDialog = false">取消</button>
          <button class="btn-submit" @click="updatePost">保存修改</button>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="loading">
    <span>加载中...</span>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { forumAPI } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const post = ref(null)
const comments = ref([])
const newComment = ref('')
const replyingTo = ref(null)
const showEditDialog = ref(false)
const editForm = ref({ title: '', content: '' })
const commentSection = ref(null)

const isLoggedIn = computed(() => userStore.isLoggedIn)
const isMyPost = computed(() => post.value?.userId === userStore.user?.id)

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  return date.toLocaleString('zh-CN')
}

const loadPost = async () => {
  try {
    const res = await forumAPI.getPostById(route.params.id)
    if (res.data.code === 200) {
      post.value = res.data.data
      editForm.value = { title: post.value.title, content: post.value.content }
    }
  } catch (error) {
    console.error('加载帖子失败:', error)
  }
}

const loadComments = async () => {
  try {
    const res = await forumAPI.getComments(route.params.id)
    if (res.data.code === 200) comments.value = res.data.data
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

// 点赞功能
const toggleLike = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const res = await forumAPI.toggleLike(post.value.id)
    if (res.data.code === 200) {
      post.value.isLiked = res.data.data
      post.value.likeCount = post.value.isLiked ? post.value.likeCount + 1 : Math.max(0, post.value.likeCount - 1)
      ElMessage.success(res.data.message)
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 收藏功能
const toggleFavorite = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const res = await forumAPI.toggleFavorite(post.value.id)
    if (res.data.code === 200) {
      post.value.isFavorited = res.data.data
      ElMessage.success(res.data.message)
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 滚动到评论区
const scrollToComment = () => {
  commentSection.value?.scrollIntoView({ behavior: 'smooth' })
}

// 回复评论
const replyTo = (comment) => {
  replyingTo.value = comment
  scrollToComment()
}

const cancelReply = () => {
  replyingTo.value = null
}

const getParentCommentUsername = (parentId) => {
  const parent = comments.value.find(c => c.id === parentId)
  return parent?.username || '用户'
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  try {
    const res = await forumAPI.createComment({
      postId: route.params.id,
      content: newComment.value,
      parentId: replyingTo.value?.id || null
    })
    if (res.data.code === 200 || res.data.code === 201) {
      ElMessage.success(replyingTo.value ? '回复成功' : '评论成功')
      newComment.value = ''
      replyingTo.value = null
      loadComments()
      loadPost()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteComment = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这条评论吗？', '确认')
    await forumAPI.deleteComment(id)
    ElMessage.success('删除成功')
    loadComments()
    loadPost()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const deletePost = async () => {
  try {
    await ElMessageBox.confirm('确定删除这篇帖子吗？删除后无法恢复。', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await forumAPI.deletePost(route.params.id)
    ElMessage.success('删除成功')
    router.push('/forum')
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const updatePost = async () => {
  if (!editForm.value.title.trim() || !editForm.value.content.trim()) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    const res = await forumAPI.updatePost(route.params.id, editForm.value)
    if (res.data.code === 200) {
      ElMessage.success('修改成功')
      showEditDialog.value = false
      post.value.title = editForm.value.title
      post.value.content = editForm.value.content
    }
  } catch (error) {
    ElMessage.error('修改失败')
  }
}

// 监听路由参数变化
watch(() => route.params.id, () => {
  if (route.params.id) {
    loadPost()
    loadComments()
  }
})

onMounted(() => {
  loadPost()
  loadComments()
})
</script>

<style scoped>
.post-detail-page { max-width: 900px; margin: 0 auto; padding: 20px; }
.back-btn { color: #667eea; cursor: pointer; margin-bottom: 20px; font-size: 16px; display: inline-flex; align-items: center; gap: 5px; }
.back-btn:hover { text-decoration: underline; }

.post-container { background: white; border-radius: 16px; padding: 30px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); margin-bottom: 30px; }
.post-header h1 { font-size: 28px; color: #333; margin: 0 0 20px; line-height: 1.4; }
.post-info { display: flex; align-items: center; gap: 20px; margin-bottom: 15px; color: #666; flex-wrap: wrap; }
.author-info { display: flex; align-items: center; gap: 10px; }
.avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; }
.avatar.placeholder { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; display: flex; align-items: center; justify-content: center; font-weight: bold; }
.author-name { font-weight: 500; }
.section-tag { background: #f0f0f0; padding: 4px 10px; border-radius: 4px; font-size: 13px; }
.post-stats { display: flex; gap: 20px; color: #999; font-size: 14px; }
.post-content { font-size: 16px; line-height: 1.8; color: #444; white-space: pre-wrap; margin-top: 25px; padding-top: 25px; border-top: 1px solid #eee; }

/* 互动按钮 */
.post-interactions { display: flex; gap: 15px; margin-top: 25px; padding-top: 20px; border-top: 1px solid #eee; }
.interact-btn { display: flex; align-items: center; gap: 8px; padding: 10px 20px; border: 1px solid #ddd; background: white; border-radius: 25px; cursor: pointer; transition: all 0.3s; font-size: 14px; }
.interact-btn:hover { border-color: #667eea; color: #667eea; }
.interact-btn.active { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-color: transparent; }

.post-actions { display: flex; gap: 12px; margin-top: 20px; padding-top: 20px; border-top: 1px solid #eee; }
.btn-edit { padding: 10px 20px; background: #667eea; color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 14px; transition: all 0.3s; }
.btn-edit:hover { background: #5569d9; }
.btn-delete { padding: 10px 20px; background: #ff6b6b; color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 14px; transition: all 0.3s; }
.btn-delete:hover { background: #ee5a5a; }

.comments-section { background: white; border-radius: 16px; padding: 30px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.comments-section h2 { margin: 0 0 20px; font-size: 20px; color: #333; }

.comment-form { margin-bottom: 25px; }
.replying-to { display: flex; align-items: center; gap: 10px; padding: 10px 15px; background: #f5f5f5; border-radius: 8px; margin-bottom: 10px; font-size: 13px; color: #666; }
.btn-cancel-reply { background: none; border: none; color: #999; cursor: pointer; font-size: 18px; margin-left: auto; }
.comment-form textarea { width: 100%; padding: 15px; border: 1px solid #ddd; border-radius: 10px; font-size: 14px; resize: vertical; box-sizing: border-box; min-height: 80px; }
.comment-form textarea:focus { outline: none; border-color: #667eea; }
.btn-comment { margin-top: 12px; padding: 12px 24px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 14px; transition: all 0.3s; }
.btn-comment:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3); }

.login-hint { padding: 20px; background: #f9f9f9; border-radius: 10px; text-align: center; color: #666; }
.login-hint a { color: #667eea; text-decoration: none; font-weight: 500; }
.login-hint a:hover { text-decoration: underline; }

.comments-list { display: flex; flex-direction: column; gap: 15px; }
.comment-item { padding: 18px; background: #f9f9f9; border-radius: 12px; transition: all 0.3s; }
.comment-item:hover { background: #f5f5f5; }
.comment-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.comment-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; }
.comment-avatar.placeholder { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: bold; }
.comment-info { display: flex; flex-direction: column; gap: 2px; }
.comment-author { font-weight: 500; color: #333; font-size: 14px; }
.comment-time { color: #999; font-size: 12px; }
.comment-actions-right { display: flex; gap: 10px; margin-left: auto; }
.btn-reply { background: none; border: none; color: #667eea; cursor: pointer; font-size: 12px; padding: 4px 8px; border-radius: 4px; transition: all 0.3s; }
.btn-reply:hover { background: #f0f0ff; }
.btn-delete-comment { background: none; border: none; color: #ff6b6b; cursor: pointer; font-size: 12px; padding: 4px 8px; }
.comment-content { color: #555; line-height: 1.6; font-size: 14px; }
.reply-hint { color: #667eea; font-weight: 500; }

.no-comments { text-align: center; color: #999; padding: 40px 20px; }
.no-comments .empty-icon { font-size: 48px; display: block; margin-bottom: 10px; }
.no-comments p { margin: 0; }

/* 对话框 */
.dialog-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.dialog { background: white; border-radius: 16px; padding: 30px; width: 90%; max-width: 600px; max-height: 90vh; overflow-y: auto; }
.dialog h2 { margin: 0 0 25px; font-size: 22px; }
.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 500; color: #333; }
.form-group input, .form-group textarea { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; box-sizing: border-box; }
.form-group input:focus, .form-group textarea:focus { outline: none; border-color: #667eea; }
.form-group textarea { resize: vertical; min-height: 200px; }
.dialog-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 10px; }
.btn-cancel { padding: 10px 20px; border: 1px solid #ddd; background: white; border-radius: 8px; cursor: pointer; font-size: 14px; }
.btn-cancel:hover { background: #f5f5f5; }
.btn-submit { padding: 10px 24px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 14px; }

.loading { display: flex; justify-content: center; align-items: center; min-height: 300px; color: #999; font-size: 16px; }
</style>
