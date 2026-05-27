import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import BookList from '../views/BookList.vue'
import BookDetail from '../views/BookDetail.vue'
import BookForm from '../views/BookForm.vue'
import Login from '../views/Login.vue'
import AdminLogin from '../views/AdminLogin.vue'
import Register from '../views/Register.vue'
import UserManage from '../views/UserManage.vue'
import Profile from '../views/Profile.vue'
import BookRanking from '../views/BookRanking.vue'
import Forum from '../views/Forum.vue'
import PostDetail from '../views/PostDetail.vue'
import Activities from '../views/Activities.vue'
import ActivityDetail from '../views/ActivityDetail.vue'
import ActivityForm from '../views/ActivityForm.vue'
import ReadingRoom from '../views/ReadingRoom.vue'

// 端口配置
const USER_PORT = 5173      // 普通用户端口
const ADMIN_PORT = 5174     // 管理员端口

// 获取当前端口
const getCurrentPort = () => {
  return window.location.port ? parseInt(window.location.port) : 80
}

// 判断是否是管理员端口
const isAdminPort = () => {
  return getCurrentPort() === ADMIN_PORT
}

const routes = [
  {
    path: '/',
    component: Home,
    meta: { title: '首页' }
  },
  {
    path: '/login',
    component: Login,
    meta: { title: '用户登录', userPortOnly: true }
  },
  {
    path: '/admin/login',
    component: AdminLogin,
    meta: { title: '管理员登录', adminPortOnly: true }
  },
  {
    path: '/register',
    component: Register,
    meta: { title: '注册', userPortOnly: true }
  },
  {
    path: '/books',
    component: BookList,
    meta: { title: '图书列表', requiresAuth: true }
  },
  {
    path: '/books/:id',
    component: BookDetail,
    meta: { title: '图书详情', requiresAuth: true }
  },
  {
    path: '/books/add',
    component: BookForm,
    meta: { title: '新增图书', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/books/:id/edit',
    component: BookForm,
    meta: { title: '编辑图书', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/users',
    component: UserManage,
    meta: { title: '用户管理', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/profile',
    component: Profile,
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/ranking',
    component: BookRanking,
    meta: { title: '借阅排行榜', requiresAuth: true }
  },
  {
    path: '/forum',
    component: Forum,
    meta: { title: '图书社区', requiresAuth: true }
  },
  {
    path: '/forum/post/:id',
    component: PostDetail,
    meta: { title: '帖子详情', requiresAuth: true }
  },
  {
    path: '/activities',
    component: Activities,
    meta: { title: '图书活动', requiresAuth: true }
  },
  {
    path: '/activities/add',
    component: ActivityForm,
    meta: { title: '创建活动', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/activities/:id/edit',
    component: ActivityForm,
    meta: { title: '编辑活动', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/activities/:id',
    component: ActivityDetail,
    meta: { title: '活动详情', requiresAuth: true }
  },
  {
    path: '/reading-room',
    component: ReadingRoom,
    meta: { title: '阅览室', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : null
  const adminPort = isAdminPort()

  // 根据端口重定向到对应的登录页面
  if (to.path === '/' && !token) {
    // 未登录时访问首页，根据端口跳转到对应登录页
    if (adminPort) {
      next('/admin/login')
    } else {
      next('/login')
    }
    return
  }

  // 管理员端口不允许访问用户登录页和注册页
  if (adminPort && to.meta.userPortOnly) {
    next('/admin/login')
    return
  }

  // 用户端口不允许访问管理员登录页
  if (!adminPort && to.meta.adminPortOnly) {
    next('/login')
    return
  }

  // 需要登录但未登录
  if (to.meta.requiresAuth && !token) {
    if (adminPort) {
      next('/admin/login')
    } else {
      next('/login')
    }
    return
  }
  
  // 需要管理员权限但不是管理员
  if (to.meta.requiresAdmin && user?.role !== 'ADMIN') {
    next('/')
    return
  }
  
  next()
})

export default router