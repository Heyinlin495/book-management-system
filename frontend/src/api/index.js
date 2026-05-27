import request from './request'

// 验证码API
export const captchaAPI = {
  generate: () => request.get('/captcha/generate')
}

// 书籍API
export const bookAPI = {
  getAll: () => request.get('/books'),
  getById: (id) => request.get(`/books/${id}`),
  create: (data) => request.post('/books', data),
  update: (id, data) => request.put(`/books/${id}`, data),
  delete: (id) => request.delete(`/books/${id}`),
  searchByTitle: (title) => request.get('/books/search/title', { params: { title } }),
  searchByAuthor: (author) => request.get('/books/search/author', { params: { author } }),
  searchByCategory: (category) => request.get('/books/search/category', { params: { category } }),
  updateStock: (id, quantity) => request.put(`/books/${id}/stock`, null, { params: { quantity } })
}

// 用户API
export const userAPI = {
  getAll: () => request.get('/users'),
  getById: (id) => request.get(`/users/${id}`),
  login: (username, password, captchaCode, captchaKey) => request.post('/users/login', { username, password, captchaCode, captchaKey }),
  adminLogin: (username, password, captchaCode, captchaKey) => request.post('/users/admin/login', { username, password, captchaCode, captchaKey }),
  register: (username, password, email) => request.post('/users/register', 
    { username, password }, 
    { params: { email } }
  ),
  update: (id, email, role) => request.put(`/users/${id}`, null, { params: { email, role } }),
  delete: (id) => request.delete(`/users/${id}`),
  changePassword: (id, newPassword) => request.put(`/users/${id}/password`, null, { params: { newPassword } }),
  adminAdd: (data) => request.post('/users/admin/add', data),
  updateProfile: (id, data) => request.put(`/users/${id}/profile`, data),
  updateAvatar: (id, avatar) => request.put(`/users/${id}/avatar`, { avatar })
}

// 借阅API
export const borrowAPI = {
  getAll: () => request.get('/borrows'),
  getUserBorrows: () => request.get('/borrows/user/me'),
  getMyRecords: () => request.get('/borrows/user/me'),
  getBookBorrows: (bookId) => request.get(`/borrows/book/${bookId}`),
  getByStatus: (status) => request.get(`/borrows/status/${status}`),
  borrow: (data) => request.post('/borrows', data),
  return: (recordId) => request.put(`/borrows/${recordId}/return`),
  updateOverdue: () => request.put('/borrows/update-overdue'),
  getRanking: () => request.get('/borrows/ranking'),
  getAllUsers: () => request.get('/users')
}

// 图书分类API
export const categoryAPI = {
  getAll: () => request.get('/categories'),
  getActive: () => request.get('/categories/active'),
  getById: (id) => request.get(`/categories/${id}`),
  create: (data) => request.post('/categories', data),
  update: (id, data) => request.put(`/categories/${id}`, data),
  delete: (id) => request.delete(`/categories/${id}`)
}

// 社区论坛 API
export const forumAPI = {
  // 版块
  getSections: () => request.get('/forum/sections'),
  getActiveSections: () => request.get('/forum/sections/active'),
  createSection: (data) => request.post('/forum/sections', data),
  updateSection: (id, data) => request.put(`/forum/sections/${id}`, data),
  deleteSection: (id) => request.delete(`/forum/sections/${id}`),
  // 帖子
  getPosts: () => request.get('/forum/posts'),
  getPostsBySection: (sectionId) => request.get(`/forum/posts/section/${sectionId}`),
  getMyPosts: () => request.get('/forum/posts/me'),
  getHotPosts: () => request.get('/forum/posts/hot'),
  getLatestPosts: () => request.get('/forum/posts/latest'),
  getPostsByLikes: () => request.get('/forum/posts/likes'),
  getPostById: (id) => request.get(`/forum/posts/${id}`),
  createPost: (data) => request.post('/forum/posts', data),
  updatePost: (id, data) => request.put(`/forum/posts/${id}`, data),
  deletePost: (id) => request.delete(`/forum/posts/${id}`),
  // 搜索
  searchPosts: (keyword, sectionId) => request.get('/forum/posts/search', { params: { keyword, sectionId } }),
  // 点赞
  toggleLike: (postId) => request.post(`/forum/posts/${postId}/like`),
  checkLiked: (postId) => request.get(`/forum/posts/${postId}/like`),
  // 收藏
  toggleFavorite: (postId) => request.post(`/forum/posts/${postId}/favorite`),
  checkFavorited: (postId) => request.get(`/forum/posts/${postId}/favorite`),
  getMyFavorites: () => request.get('/forum/posts/favorites/me'),
  // 评论
  getComments: (postId) => request.get(`/forum/comments/post/${postId}`),
  createComment: (data) => request.post('/forum/comments', data),
  deleteComment: (id) => request.delete(`/forum/comments/${id}`)
}

// 活动API
export const activityAPI = {
  getAll: () => request.get('/activities'),
  getUpcoming: () => request.get('/activities/upcoming'),
  getHot: () => request.get('/activities/hot'),
  getById: (id) => request.get(`/activities/${id}`),
  create: (data) => request.post('/activities', data),
  update: (id, data) => request.put(`/activities/${id}`, data),
  delete: (id) => request.delete(`/activities/${id}`),
  // 报名
  getRegistrations: (activityId) => request.get(`/activities/${activityId}/registrations`),
  getMyRegistrations: () => request.get('/activities/registrations/me'),
  getMyCollected: () => request.get('/activities/registrations/collected/me'),
  register: (activityId) => request.post(`/activities/${activityId}/register`),
  cancelRegister: (activityId) => request.delete(`/activities/${activityId}/register`),
  checkIn: (registrationId) => request.put(`/activities/registrations/${registrationId}/checkin`),
  toggleCollect: (activityId) => request.post(`/activities/${activityId}/collect`)
}

// 阅览室API
export const readingRoomAPI = {
  // 阅览室
  getAll: () => request.get('/reading-rooms'),
  getActive: () => request.get('/reading-rooms/active'),
  getById: (id) => request.get(`/reading-rooms/${id}`),
  create: (data) => request.post('/reading-rooms', data),
  update: (id, data) => request.put(`/reading-rooms/${id}`, data),
  delete: (id) => request.delete(`/reading-rooms/${id}`),
  // 座位
  getSeats: (roomId) => request.get(`/reading-rooms/${roomId}/seats`),
  getAvailableSeats: (roomId) => request.get(`/reading-rooms/${roomId}/seats/available`),
  createSeat: (data) => request.post('/reading-rooms/seats', data),
  updateSeat: (id, data) => request.put(`/reading-rooms/seats/${id}`, data),
  deleteSeat: (id) => request.delete(`/reading-rooms/seats/${id}`),
  // 座位预约
  getMyReservations: () => request.get('/reading-rooms/reservations/me'),
  getReservationsByRoom: (roomId, date) => request.get(`/reading-rooms/${roomId}/reservations`, { params: { date } }),
  reserveSeat: (seatId, date, startTime, endTime) =>
    request.post(`/reading-rooms/seats/${seatId}/reserve`, null, { params: { date, startTime, endTime } }),
  checkInSeat: (reservationId) => request.put(`/reading-rooms/reservations/${reservationId}/checkin`),
  checkOutSeat: (reservationId) => request.put(`/reading-rooms/reservations/${reservationId}/checkout`),
  cancelReservation: (reservationId) => request.delete(`/reading-rooms/reservations/${reservationId}`)
}

// 统计API
export const statisticsAPI = {
  get: () => request.get('/statistics')
}