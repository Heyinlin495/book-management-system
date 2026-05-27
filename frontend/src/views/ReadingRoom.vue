<template>
  <div class="reading-room-page">
    <div class="page-header">
      <h1>阅览室预约</h1>
      <p class="subtitle">选择座位，开始你的学习之旅</p>
      <button v-if="isAdmin" class="btn-create" @click="showCreateRoomDialog = true">+ 添加阅览室</button>
    </div>

    <!-- 阅览室列表 -->
    <div class="rooms-grid">
      <div class="room-card" v-for="room in rooms" :key="room.id">
        <div class="room-card-content" @click="selectRoom(room)">
          <div class="room-icon">📚</div>
          <div class="room-info">
            <h3>{{ room.name }}</h3>
            <p class="room-location">📍 {{ room.location }}</p>
            <p class="room-time">🕐 {{ room.openTime }} - {{ room.closeTime }}</p>
          </div>
          <div class="room-seats">
            <div class="seats-info">
              <span class="available">{{ room.availableSeats }}</span>
              <span class="divider">/</span>
              <span class="total">{{ room.totalSeats }}</span>
            </div>
            <p>可用座位</p>
            <div class="seats-bar">
              <div class="seats-progress" :style="{ width: (room.availableSeats / room.totalSeats * 100) + '%' }"></div>
            </div>
          </div>
        </div>
        <div v-if="isAdmin" class="room-actions">
          <button class="btn-edit" @click="editRoom(room)">编辑</button>
          <button class="btn-delete" @click="confirmDelete(room)">删除</button>
        </div>
      </div>
    </div>

    <!-- 座位选择对话框 -->
    <div class="dialog-overlay" v-if="selectedRoom" @click.self="selectedRoom = null">
      <div class="dialog seat-dialog">
        <h2>{{ selectedRoom.name }} - 选择座位</h2>
        <div class="date-picker">
          <label>选择日期：</label>
          <input type="date" v-model="selectedDate" :min="today" @change="onDateChange" />
        </div>
        <div class="seats-map">
          <div class="seat" 
               v-for="seat in seats" 
               :key="seat.id"
               :class="{ occupied: seat.status === 'OCCUPIED', selected: selectedSeat?.id === seat.id }"
               @click="selectSeat(seat)">
            <span class="seat-number">{{ seat.seatNumber }}</span>
            <span v-if="seat.hasPower" class="seat-feature">⚡</span>
          </div>
        </div>
        <div class="legend">
          <span class="legend-item"><span class="dot available"></span> 可用</span>
          <span class="legend-item"><span class="dot occupied"></span> 已占用</span>
          <span class="legend-item"><span class="dot selected"></span> 已选择</span>
        </div>
        <div class="dialog-actions">
          <button class="btn-cancel" @click="selectedRoom = null">取消</button>
          <button class="btn-submit" @click="reserveSeat" :disabled="!selectedSeat">确认预约</button>
        </div>
      </div>
    </div>

    <!-- 我的预约 -->
    <div class="my-reservations" v-if="myReservations.length > 0">
      <h2>我的预约</h2>
      <div class="reservations-list">
        <div class="reservation-card" v-for="res in myReservations" :key="res.id">
          <div class="res-info">
            <h4>{{ res.roomName }} - 座位 {{ res.seatNumber }}</h4>
            <p>📅 {{ res.reservationDate }}</p>
            <span class="res-status" :class="res.status">{{ getStatusText(res.status) }}</span>
          </div>
          <div class="res-actions">
            <button v-if="res.status === 'RESERVED'" class="btn-checkin" @click="checkIn(res.id)">签到</button>
            <button v-if="res.status === 'CHECKED_IN'" class="btn-checkout" @click="checkOut(res.id)">归还</button>
            <button v-if="res.status === 'RESERVED'" class="btn-cancel-res" @click="cancelReservation(res.id)">取消</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建阅览室对话框 -->
    <div class="dialog-overlay" v-if="showCreateRoomDialog" @click.self="showCreateRoomDialog = false">
      <div class="dialog">
        <h2>添加阅览室</h2>
        <div class="form-group">
          <label>名称</label>
          <input v-model="newRoom.name" placeholder="请输入阅览室名称" />
        </div>
        <div class="form-group">
          <label>位置</label>
          <input v-model="newRoom.location" placeholder="请输入位置" />
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>座位数</label>
            <input type="number" v-model="newRoom.totalSeats" placeholder="座位数量" />
          </div>
          <div class="form-group">
            <label>开放时间</label>
            <input v-model="newRoom.openTime" placeholder="08:00" />
          </div>
          <div class="form-group">
            <label>关闭时间</label>
            <input v-model="newRoom.closeTime" placeholder="22:00" />
          </div>
        </div>
        <div class="dialog-actions">
          <button class="btn-cancel" @click="showCreateRoomDialog = false">取消</button>
          <button class="btn-submit" @click="createRoom">创建</button>
        </div>
      </div>
    </div>

    <!-- 编辑阅览室对话框 -->
    <div class="dialog-overlay" v-if="showEditRoomDialog" @click.self="showEditRoomDialog = false">
      <div class="dialog">
        <h2>编辑阅览室</h2>
        <div class="form-group">
          <label>名称</label>
          <input v-model="editRoomForm.name" placeholder="请输入阅览室名称" />
        </div>
        <div class="form-group">
          <label>位置</label>
          <input v-model="editRoomForm.location" placeholder="请输入位置" />
        </div>
        <div class="form-group">
          <label>描述</label>
          <textarea v-model="editRoomForm.description" placeholder="请输入阅览室描述" rows="3"></textarea>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>开放时间</label>
            <input v-model="editRoomForm.openTime" placeholder="08:00" />
          </div>
          <div class="form-group">
            <label>关闭时间</label>
            <input v-model="editRoomForm.closeTime" placeholder="22:00" />
          </div>
        </div>
        <div class="dialog-actions">
          <button class="btn-cancel" @click="showEditRoomDialog = false">取消</button>
          <button class="btn-submit" @click="updateRoom">保存</button>
        </div>
      </div>
    </div>

    <!-- 删除阅览室确认对话框 -->
    <div class="dialog-overlay" v-if="showDeleteConfirm" @click.self="showDeleteConfirm = false">
      <div class="dialog">
        <h2>确认删除</h2>
        <p>你确定要删除「{{ roomToDelete?.name }}」阅览室吗？删除后将一并删除所有相关的座位和预约记录。</p>
        <div class="dialog-actions">
          <button class="btn-cancel" @click="showDeleteConfirm = false">取消</button>
          <button class="btn-delete-confirm" @click="deleteRoom">确定删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../store/user'
import { readingRoomAPI } from '../api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const rooms = ref([])
const seats = ref([])
const myReservations = ref([])
const selectedRoom = ref(null)
const selectedSeat = ref(null)
const selectedDate = ref(new Date().toISOString().split('T')[0])
const showCreateRoomDialog = ref(false)
const showEditRoomDialog = ref(false)
const showDeleteConfirm = ref(false)
const roomToDelete = ref(null)
const newRoom = ref({ name: '', location: '', totalSeats: 50, openTime: '08:00', closeTime: '22:00' })
const editRoomForm = ref({ id: null, name: '', location: '', description: '', openTime: '08:00', closeTime: '22:00' })

const isAdmin = computed(() => userStore.user?.role === 'ADMIN')
const today = computed(() => new Date().toISOString().split('T')[0])

const getStatusText = (status) => {
  const map = { 'RESERVED': '已预约', 'CHECKED_IN': '使用中', 'COMPLETED': '已完成', 'CANCELLED': '已取消' }
  return map[status] || status
}

const loadRooms = async () => {
  try {
    const res = await readingRoomAPI.getActive()
    if (res.data.code === 200) rooms.value = res.data.data
  } catch (error) {
    console.error('加载阅览室失败:', error)
  }
}

const loadMyReservations = async () => {
  if (!userStore.user?.id) return
  try {
    const res = await readingRoomAPI.getMyReservations()
    if (res.data.code === 200) myReservations.value = res.data.data
  } catch (error) {
    console.error('加载预约失败:', error)
  }
}

const selectRoom = async (room) => {
  selectedRoom.value = room
  selectedSeat.value = null
  await loadSeatsWithReservationStatus(room.id)
}

const loadSeatsWithReservationStatus = async (roomId) => {
  try {
    // 加载座位列表
    const seatsRes = await readingRoomAPI.getSeats(roomId)
    if (seatsRes.data.code === 200) {
      let seatList = seatsRes.data.data
      
      // 获取选定日期的预约情况
      try {
        const reservationsRes = await readingRoomAPI.getReservationsByRoom(roomId, selectedDate.value)
        if (reservationsRes.data.code === 200) {
          const reservations = reservationsRes.data.data
          // 标记已被预约的座位
          const reservedSeatIds = reservations
            .filter(r => r.status === 'RESERVED' || r.status === 'CHECKED_IN')
            .map(r => r.seatId)
          seatList = seatList.map(seat => ({
            ...seat,
            status: reservedSeatIds.includes(seat.id) ? 'OCCUPIED' : 'AVAILABLE'
          }))
        }
      } catch (e) {
        console.error('加载预约状态失败:', e)
      }
      
      seats.value = seatList
    }
  } catch (error) {
    console.error('加载座位失败:', error)
  }
}

const selectSeat = (seat) => {
  if (seat.status === 'OCCUPIED') {
    ElMessage.warning('该座位在此日期已被预约')
    return
  }
  selectedSeat.value = seat
}

const onDateChange = () => {
  selectedSeat.value = null
  if (selectedRoom.value) {
    loadSeatsWithReservationStatus(selectedRoom.value.id)
  }
}

const reserveSeat = async () => {
  if (!selectedSeat.value) return
  
  // 检查用户是否登录
  if (!userStore.user?.id) {
    ElMessage.warning('请先登录后再预约座位')
    return
  }
  
  try {
    const res = await readingRoomAPI.reserveSeat(
      selectedSeat.value.id,
      selectedDate.value,
      '08:00',
      '22:00'
    )
    if (res.data.code === 200 || res.data.code === 201) {
      ElMessage.success('预约成功')
      selectedRoom.value = null
      selectedSeat.value = null
      loadRooms()
      loadMyReservations()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '预约失败')
  }
}

const checkIn = async (id) => {
  try {
    await readingRoomAPI.checkInSeat(id)
    ElMessage.success('签到成功')
    loadMyReservations()
  } catch (error) {
    ElMessage.error('签到失败')
  }
}

const checkOut = async (id) => {
  try {
    await readingRoomAPI.checkOutSeat(id)
    ElMessage.success('归还成功')
    loadRooms()
    loadMyReservations()
  } catch (error) {
    ElMessage.error('归还失败')
  }
}

const cancelReservation = async (id) => {
  try {
    await readingRoomAPI.cancelReservation(id)
    ElMessage.success('取消成功')
    loadRooms()
    loadMyReservations()
  } catch (error) {
    ElMessage.error('取消失败')
  }
}

const createRoom = async () => {
  if (!newRoom.value.name || !newRoom.value.totalSeats) {
    ElMessage.warning('请填写必要信息')
    return
  }
  try {
    const res = await readingRoomAPI.create(newRoom.value)
    if (res.data.code === 200 || res.data.code === 201) {
      ElMessage.success('创建成功')
      showCreateRoomDialog.value = false
      newRoom.value = { name: '', location: '', totalSeats: 50, openTime: '08:00', closeTime: '22:00' }
      loadRooms()
    }
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

const editRoom = (room) => {
  // 加载当前房间信息到编辑表单
  editRoomForm.value = {
    id: room.id,
    name: room.name,
    location: room.location,
    description: room.description || '',
    openTime: room.openTime,
    closeTime: room.closeTime
  }
  showEditRoomDialog.value = true
}

const updateRoom = async () => {
  if (!editRoomForm.value.name || !editRoomForm.value.location) {
    ElMessage.warning('请填写必要信息')
    return
  }
  try {
    const res = await readingRoomAPI.update(editRoomForm.value.id, editRoomForm.value)
    if (res.data.code === 200) {
      ElMessage.success('更新成功')
      showEditRoomDialog.value = false
      editRoomForm.value = { id: null, name: '', location: '', description: '', openTime: '08:00', closeTime: '22:00' }
      loadRooms()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '更新失败')
  }
}

const confirmDelete = (room) => {
  roomToDelete.value = room
  showDeleteConfirm.value = true
}

const deleteRoom = async () => {
  if (!roomToDelete.value) return
  try {
    await readingRoomAPI.delete(roomToDelete.value.id)
    ElMessage.success('删除成功')
    showDeleteConfirm.value = false
    roomToDelete.value = null
    loadRooms()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '删除失败')
  }
}

onMounted(() => {
  loadRooms()
  loadMyReservations()
})
</script>

<style scoped>
.reading-room-page { max-width: 1200px; margin: 0 auto; padding: 20px; }
.page-header { text-align: center; margin-bottom: 30px; position: relative; }
.page-header h1 { font-size: 32px; color: #333; margin-bottom: 10px; }
.subtitle { color: #666; font-size: 16px; }
.btn-create { position: absolute; right: 0; top: 10px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; padding: 12px 24px; border-radius: 8px; cursor: pointer; }

.rooms-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 20px; margin-bottom: 40px; }
.room-card { background: white; border-radius: 16px; padding: 24px; box-shadow: 0 4px 12px rgba(0,0,0,0.08); cursor: pointer; transition: all 0.3s; display: flex; flex-direction: column; gap: 12px; }
.room-card:hover { box-shadow: 0 8px 24px rgba(0,0,0,0.12); }
.room-card-content { display: flex; gap: 20px; align-items: center; cursor: pointer; }
.room-icon { font-size: 40px; }
.room-info { flex: 1; }
.room-info h3 { margin: 0 0 8px; font-size: 18px; }
.room-location, .room-time { margin: 4px 0; color: #666; font-size: 13px; }
.room-seats { text-align: center; }
.seats-info { font-size: 20px; font-weight: bold; }
.seats-info .available { color: #2ecc71; }
.seats-info .total { color: #999; }
.room-seats p { margin: 4px 0; font-size: 12px; color: #888; }
.seats-bar { width: 80px; height: 6px; background: #eee; border-radius: 3px; overflow: hidden; }
.seats-progress { height: 100%; background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%); }
.room-actions { display: flex; gap: 8px; justify-content: flex-end; }
.btn-edit { padding: 8px 16px; background: #3498db; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 12px; }
.btn-edit:hover { background: #2980b9; }
.btn-delete { padding: 8px 16px; background: #e74c3c; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 12px; }
.btn-delete:hover { background: #c0392b; }

.seat-dialog { max-width: 800px; }
.date-picker { margin-bottom: 20px; }
.date-picker label { margin-right: 10px; }
.date-picker input { padding: 8px 12px; border: 1px solid #ddd; border-radius: 6px; }
.seats-map { display: grid; grid-template-columns: repeat(10, 1fr); gap: 10px; margin-bottom: 20px; }
.seat { width: 50px; height: 50px; border: 2px solid #ddd; border-radius: 8px; display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; transition: all 0.2s; background: #f9f9f9; }
.seat:hover:not(.occupied) { border-color: #667eea; background: #f0f4ff; }
.seat.occupied { background: #ffebee; border-color: #ff6b6b; cursor: not-allowed; }
.seat.selected { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-color: #667eea; color: white; }
.seat-number { font-size: 12px; font-weight: 500; }
.seat-feature { font-size: 10px; }
.legend { display: flex; gap: 20px; justify-content: center; margin-bottom: 20px; }
.legend-item { display: flex; align-items: center; gap: 6px; font-size: 13px; }
.dot { width: 12px; height: 12px; border-radius: 3px; }
.dot.available { background: #f9f9f9; border: 2px solid #ddd; }
.dot.occupied { background: #ffebee; border: 2px solid #ff6b6b; }
.dot.selected { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }

.my-reservations { background: white; border-radius: 16px; padding: 24px; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
.my-reservations h2 { margin: 0 0 20px; font-size: 20px; }
.reservations-list { display: flex; flex-direction: column; gap: 15px; }
.reservation-card { display: flex; justify-content: space-between; align-items: center; padding: 15px; background: #f9f9f9; border-radius: 10px; }
.res-info h4 { margin: 0 0 6px; }
.res-info p { margin: 0; color: #666; font-size: 13px; }
.res-status { display: inline-block; padding: 3px 8px; border-radius: 4px; font-size: 12px; margin-top: 6px; }
.res-status.RESERVED { background: #fff3cd; color: #856404; }
.res-status.CHECKED_IN { background: #d4edda; color: #155724; }
.res-status.COMPLETED { background: #e2e3e5; color: #383d41; }
.res-actions { display: flex; gap: 8px; }
.btn-checkin { padding: 8px 16px; background: #2ecc71; color: white; border: none; border-radius: 6px; cursor: pointer; }
.btn-checkout { padding: 8px 16px; background: #3498db; color: white; border: none; border-radius: 6px; cursor: pointer; }
.btn-cancel-res { padding: 8px 16px; background: #ff6b6b; color: white; border: none; border-radius: 6px; cursor: pointer; }

.dialog-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.dialog { background: white; border-radius: 16px; padding: 30px; width: 90%; max-width: 500px; max-height: 90vh; overflow-y: auto; }
.dialog h2 { margin: 0 0 20px; }
.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 500; }
.form-group input { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; box-sizing: border-box; }
.form-group textarea { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; box-sizing: border-box; font-family: inherit; }
.form-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; }
.dialog-actions { display: flex; justify-content: flex-end; gap: 12px; }
.btn-cancel { padding: 10px 20px; border: 1px solid #ddd; background: white; border-radius: 8px; cursor: pointer; }
.btn-submit { padding: 10px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 8px; cursor: pointer; }
.btn-submit:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-delete-confirm { padding: 10px 20px; background: #e74c3c; color: white; border: none; border-radius: 8px; cursor: pointer; }
.btn-delete-confirm:hover { background: #c0392b; }
</style>
