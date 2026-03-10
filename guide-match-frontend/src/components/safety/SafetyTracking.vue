<template>
  <div class="safety-tracking">
    <!-- 紧急求助按钮 -->
    <div class="emergency-button-container">
      <button 
        class="emergency-btn"
        @click="triggerEmergency"
        :disabled="isEmergencyTriggered"
      >
        <span class="emergency-icon">🆘</span>
        <span class="emergency-text">一键报警</span>
      </button>
    </div>

    <!-- 位置轨迹显示 -->
    <div class="tracking-map">
      <div class="map-placeholder">
        <div class="tracking-info">
          <h3>实时位置追踪</h3>
          <p>当前行程: {{ currentTripId || '未开始' }}</p>
          <p>记录点数: {{ trackingPoints.length }}</p>
          <p>最后更新: {{ lastUpdateTime }}</p>
        </div>
        
        <!-- 轨迹点列表 -->
        <div class="tracking-points">
          <div 
            v-for="(point, index) in trackingPoints" 
            :key="index"
            class="tracking-point"
          >
            <div class="point-marker">
              📍
            </div>
            <div class="point-info">
              <p class="point-time">{{ formatTime(point.timestamp) }}</p>
              <p class="point-location">{{ point.locationName || '未知位置' }}</p>
              <p class="point-coords">{{ point.latitude.toFixed(4) }}, {{ point.longitude.toFixed(4) }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 行程分享 -->
    <div class="route-sharing">
      <h3>行程分享</h3>
      <div class="share-controls">
        <input
          v-model="shareEmail"
          type="email"
          placeholder="输入家人朋友的邮箱"
          class="share-input"
        />
        <button 
          class="share-btn"
          @click="shareRoute"
          :disabled="!shareEmail || isSharing"
        >
          {{ isSharing ? '分享中...' : '分享行程' }}
        </button>
      </div>
      <div v-if="sharedUsers.length > 0" class="shared-users">
        <h4>已分享给:</h4>
        <div class="user-list">
          <span v-for="user in sharedUsers" :key="user" class="user-tag">
            {{ user }}
          </span>
        </div>
      </div>
    </div>

    <!-- 紧急状态显示 -->
    <div v-if="emergencyAlerts.length > 0" class="emergency-status">
      <h3>紧急求助状态</h3>
      <div 
        v-for="alert in emergencyAlerts" 
        :key="alert.id"
        class="alert-item"
        :class="alert.emergencyStatus.toLowerCase()"
      >
        <div class="alert-header">
          <span class="alert-type">{{ getEmergencyTypeName(alert.emergencyType) }}</span>
          <span class="alert-time">{{ formatTime(alert.emergencyTime) }}</span>
        </div>
        <div class="alert-status">
          状态: {{ getEmergencyStatusName(alert.emergencyStatus) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import request from '@/api/request'

const trackingPoints = ref([])
const emergencyAlerts = ref([])
const currentTripId = ref('')
const lastUpdateTime = ref('')
const shareEmail = ref('')
const sharedUsers = ref([])
const isEmergencyTriggered = ref(false)
const isSharing = ref(false)

let locationInterval = null

onMounted(() => {
  startLocationTracking()
  loadEmergencyAlerts()
})

onUnmounted(() => {
  if (locationInterval) {
    clearInterval(locationInterval)
  }
})

const startLocationTracking = () => {
  // 请求位置权限
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        console.log('位置权限获取成功')
        // 开始定时记录位置
        locationInterval = setInterval(() => {
          recordLocation()
        }, 30000) // 每30秒记录一次
      },
      (error) => {
        console.error('位置权限获取失败:', error)
        alert('无法获取位置权限，安全功能将受限')
      }
    )
  } else {
    alert('您的浏览器不支持位置追踪功能')
  }
}

const recordLocation = async () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      async (position) => {
        const locationData = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
          locationName: '未知位置', // 可以通过地理编码API获取
          tripId: currentTripId.value || 'default',
          isEmergency: false
        }

        try {
          await request.post('/safety/tracking', locationData)
          trackingPoints.value.unshift({
            ...locationData,
            timestamp: new Date()
          })
          lastUpdateTime.value = formatTime(new Date())
          
          // 限制显示数量
          if (trackingPoints.value.length > 50) {
            trackingPoints.value = trackingPoints.value.slice(0, 50)
          }
        } catch (error) {
          console.error('位置记录失败:', error)
        }
      },
      (error) => {
        console.error('获取位置失败:', error)
      }
    )
  }
}

const triggerEmergency = async () => {
  if (!confirm('确定要触发紧急求助吗？平台客服将立即收到您的位置信息。')) {
    return
  }

  isEmergencyTriggered.value = true

  try {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          const emergencyData = {
            emergencyType: 'GENERAL',
            latitude: position.coords.latitude,
            longitude: position.coords.longitude
          }

          await request.post('/safety/emergency', emergencyData)
          
          // 立即加载紧急状态
          loadEmergencyAlerts()
          
          alert('紧急求助已发送！平台客服将尽快联系您。')
        },
        (error) => {
          console.error('获取紧急位置失败:', error)
          alert('无法获取当前位置，请确保位置权限已开启')
        }
      )
    }
  } catch (error) {
    console.error('紧急求助发送失败:', error)
    alert('紧急求助发送失败，请重试或直接拨打紧急电话')
  } finally {
    isEmergencyTriggered.value = false
  }
}

const shareRoute = async () => {
  if (!shareEmail.value.trim()) {
    return
  }

  isSharing.value = true

  try {
    await request.post('/safety/share-route', {
      tripId: currentTripId.value || 'default',
      sharedUsers: [shareEmail.value.trim()]
    })

    if (!sharedUsers.value.includes(shareEmail.value.trim())) {
      sharedUsers.value.push(shareEmail.value.trim())
    }

    shareEmail.value = ''
    alert('行程分享成功！')
  } catch (error) {
    console.error('行程分享失败:', error)
    alert('行程分享失败，请重试')
  } finally {
    isSharing.value = false
  }
}

const loadEmergencyAlerts = async () => {
  try {
    const response = await request.get('/safety/emergency-status/me')
    emergencyAlerts.value = response.data || []
  } catch (error) {
    console.error('加载紧急状态失败:', error)
  }
}

const formatTime = (date) => {
  return new Date(date).toLocaleString('zh-CN')
}

const getEmergencyTypeName = (type) => {
  const types = {
    'GENERAL': '一般求助',
    'MEDICAL': '医疗紧急',
    'SECURITY': '安全威胁',
    'ACCIDENT': '意外事故'
  }
  return types[type] || '未知类型'
}

const getEmergencyStatusName = (status) => {
  const statuses = {
    'PENDING': '待处理',
    'PROCESSING': '处理中',
    'RESOLVED': '已解决'
  }
  return statuses[status] || '未知状态'
}
</script>

<style scoped>
.safety-tracking {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.emergency-button-container {
  text-align: center;
  margin-bottom: 30px;
}

.emergency-btn {
  background: linear-gradient(135deg, #ff4757, #ff3838);
  color: white;
  border: none;
  border-radius: 50px;
  padding: 15px 30px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 71, 87, 0.3);
  display: flex;
  align-items: center;
  gap: 10px;
}

.emergency-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 71, 87, 0.4);
}

.emergency-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.emergency-icon {
  font-size: 24px;
}

.emergency-text {
  font-size: 16px;
}

.tracking-map {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.tracking-info h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.tracking-info p {
  margin: 5px 0;
  color: #666;
}

.tracking-points {
  max-height: 300px;
  overflow-y: auto;
  margin-top: 20px;
}

.tracking-point {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.point-marker {
  font-size: 20px;
  flex-shrink: 0;
}

.point-info {
  flex: 1;
}

.point-time {
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.point-location {
  color: #333;
  margin: 2px 0;
}

.point-coords {
  font-size: 12px;
  color: #999;
  margin: 2px 0 0 0;
}

.route-sharing {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.route-sharing h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.share-controls {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.share-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.share-btn {
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 10px 20px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.share-btn:hover:not(:disabled) {
  background: #2980b9;
}

.share-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.shared-users h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.user-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.user-tag {
  background: #e3f2fd;
  color: #3498db;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
}

.emergency-status {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.emergency-status h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.alert-item {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
}

.alert-item.pending {
  border-left: 4px solid #f39c12;
  background: #fef3c7;
}

.alert-item.processing {
  border-left: 4px solid #3498db;
  background: #e3f2fd;
}

.alert-item.resolved {
  border-left: 4px solid #27ae60;
  background: #d5f4e6;
}

.alert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.alert-type {
  font-weight: 600;
  color: #2c3e50;
}

.alert-time {
  font-size: 12px;
  color: #666;
}

.alert-status {
  font-size: 14px;
  color: #666;
}
</style>
