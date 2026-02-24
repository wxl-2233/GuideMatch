<template>
  <div class="profile-page">
    <PageContainer>
      <h1 class="page-title">{{ t('profile.title') }}</h1>

      <div class="profile-content">
        <!-- 基本信息卡片 -->
        <div class="profile-card">
          <h2 class="card-title">{{ t('profile.basicInfo') }}</h2>
          
          <div class="avatar-section">
            <div class="avatar-container">
              <img
                :src="getAvatarUrl(userInfo?.avatarPath, userInfo?.avatarStatus)"
                :alt="userInfo?.nickname"
                class="avatar-large"
              />
              <div v-if="userInfo?.avatarStatus === 'pending'" class="avatar-status-badge">
                {{ t('profile.avatarPending') }}
              </div>
              <div v-if="userInfo?.avatarStatus === 'rejected'" class="avatar-status-badge rejected">
                {{ t('profile.avatarRejected') }}
              </div>
              <div class="avatar-overlay" @click="triggerAvatarUpload">
                <span class="upload-icon">📷</span>
                <span class="upload-text">{{ t('profile.changeAvatar') }}</span>
              </div>
            </div>
            <input
              ref="avatarInput"
              type="file"
              accept="image/*"
              class="avatar-input"
              @change="handleAvatarChange"
            />
          </div>

          <div class="info-grid">
            <div class="info-item">
              <label>{{ t('profile.nickname') }} <span class="required">*</span></label>
              <div class="info-value">{{ userInfo?.nickname || '-' }}</div>
            </div>
            <div class="info-item">
              <label>{{ t('profile.email') }} <span class="required">*</span></label>
              <div class="info-value">{{ userInfo?.email || '-' }}</div>
            </div>
            <div class="info-item">
              <label>{{ t('profile.role') }}</label>
              <div class="info-value">{{ getRoleText(userInfo?.role) }}</div>
            </div>
            <div class="info-item">
              <label>{{ t('profile.fullName') }} <span class="optional">（{{ t('profile.optional') }}）</span></label>
              <div v-if="editingField !== 'fullName'" class="info-value editable" @click="editField('fullName')">
                {{ userInfo?.fullName || '-' }}
                <span class="edit-hint">{{ t('profile.clickToEdit') }}</span>
              </div>
              <div v-else class="edit-input-group">
                <input v-model="editValue" type="text" class="edit-input" />
                <button class="edit-btn save" @click="saveField">{{ t('profile.save') }}</button>
                <button class="edit-btn cancel" @click="cancelEdit">{{ t('profile.cancel') }}</button>
              </div>
            </div>
            <div class="info-item">
              <label>{{ t('profile.phonenumber') }} <span class="optional">（{{ t('profile.optional') }}）</span></label>
              <div v-if="editingField !== 'phonenumber'" class="info-value editable" @click="editField('phonenumber')">
                {{ userInfo?.phonenumber || '-' }}
                <span class="edit-hint">{{ t('profile.clickToEdit') }}</span>
              </div>
              <div v-else class="edit-input-group">
                <input v-model="editValue" type="tel" class="edit-input" />
                <button class="edit-btn save" @click="saveField">{{ t('profile.save') }}</button>
                <button class="edit-btn cancel" @click="cancelEdit">{{ t('profile.cancel') }}</button>
              </div>
            </div>
            <div class="info-item">
              <label>{{ t('profile.level') }}</label>
              <div class="info-value">Lv.{{ userInfo?.lv || 1 }}</div>
            </div>
            <div class="info-item">
              <label>{{ t('profile.experience') }}</label>
              <div class="info-value">{{ userInfo?.exp || 0 }} {{ t('profile.exp') }}</div>
            </div>
            <div class="info-item">
              <label>{{ t('profile.registerTime') }}</label>
              <div class="info-value">{{ formatDate(userInfo?.createTime) }}</div>
            </div>
          </div>
        </div>

        <!-- 其他信息卡片（根据角色显示不同内容） -->
        <div v-if="userInfo?.role === 'guide'" class="profile-card">
          <h2 class="card-title">{{ t('profile.guideInfo') }}</h2>
          <div class="info-grid">
            <div class="info-item">
              <label>{{ t('profile.verificationStatus') }}</label>
              <div class="info-value">
                <span :class="['status-badge', getVerificationStatusClass()]">
                  {{ getVerificationStatusText() }}
                </span>
              </div>
            </div>
            <div v-if="guideInfo?.rejectionReason" class="info-item full-width">
              <label>{{ t('profile.rejectionReason') }}</label>
              <div class="info-value">{{ guideInfo.rejectionReason }}</div>
            </div>
            <div v-if="guideInfo" class="info-item">
              <label>{{ t('profile.rating') }}</label>
              <div class="info-value">{{ formatRating(guideInfo.rating) }}</div>
            </div>
            <div v-if="guideInfo" class="info-item">
              <label>{{ t('profile.reviewCount') }}</label>
              <div class="info-value">{{ guideInfo.totalReviews || 0 }}</div>
            </div>
            <div v-if="guideInfo" class="info-item">
              <label>{{ t('profile.orderCount') }}</label>
              <div class="info-value">{{ guideInfo.totalOrders || 0 }}</div>
            </div>
            <div v-if="guideInfo" class="info-item">
              <label>{{ t('profile.totalIncome') }}</label>
              <div class="info-value">¥{{ formatMoney(guideInfo.totalIncome) }}</div>
            </div>
          </div>
          <div class="action-buttons">
            <button class="btn btn-primary" @click="goToGuideProfile">
              {{ t('profile.manageGuideProfile') }}
            </button>
          </div>
        </div>

        <!-- 快捷入口 -->
        <div class="profile-card">
          <h2 class="card-title">{{ t('profile.quickActions') }}</h2>
          <div class="quick-actions">
            <button v-if="userInfo?.role === 'tourist'" class="action-btn" @click="goToGuides">
              <span class="action-icon">🔍</span>
              <span class="action-text">{{ t('profile.browseGuides') }}</span>
            </button>
            <button v-if="userInfo?.role === 'tourist'" class="action-btn" @click="goToOrders">
              <span class="action-icon">📋</span>
              <span class="action-text">{{ t('profile.myOrders') }}</span>
            </button>
            <button v-if="userInfo?.role === 'tourist'" class="action-btn" @click="goToCommunity">
              <span class="action-icon">💬</span>
              <span class="action-text">{{ t('profile.community') }}</span>
            </button>
            <button v-if="userInfo?.role === 'guide'" class="action-btn" @click="goToGuideDashboard">
              <span class="action-icon">📊</span>
              <span class="action-text">{{ t('profile.dashboard') }}</span>
            </button>
            <button v-if="userInfo?.role === 'guide'" class="action-btn" @click="goToGuideCalendar">
              <span class="action-icon">📅</span>
              <span class="action-text">{{ t('profile.calendarManagement') }}</span>
            </button>
            <button v-if="userInfo?.role === 'guide'" class="action-btn" @click="goToGuideIncome">
              <span class="action-icon">💰</span>
              <span class="action-text">{{ t('profile.incomeManagement') }}</span>
            </button>
            <button v-if="userInfo?.role === 'admin'" class="action-btn" @click="goToAdmin">
              <span class="action-icon">⚙️</span>
              <span class="action-text">{{ t('profile.adminPanel') }}</span>
            </button>
          </div>
        </div>
      </div>
    </PageContainer>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from '@/composables/useI18n'
import request from '@/api/request'
import axios from 'axios'
import PageContainer from '@/components/layout/PageContainer.vue'

const { t } = useI18n()
const router = useRouter()

const userInfo = ref(null)
const guideInfo = ref(null)
const avatarInput = ref(null)
const uploading = ref(false)
const editingField = ref(null)
const editValue = ref('')

const loadUserInfo = async () => {
  try {
    const response = await request.get('/auth/me')
    userInfo.value = response.data
    
    // 同时更新本地存储
    localStorage.setItem('user', JSON.stringify(response.data))
    
    // 如果是向导，加载向导信息
    if (userInfo.value?.role === 'guide') {
      await loadGuideInfo()
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

const loadGuideInfo = async () => {
  try {
    // 获取向导的完整信息
    const guideResponse = await request.get('/guides/profile')
    if (guideResponse.data) {
      guideInfo.value = guideResponse.data
    }
  } catch (error) {
    console.error('加载向导信息失败:', error)
    // 如果获取失败，尝试通过stats获取基本信息
    try {
      const statsResponse = await request.get('/guides/stats')
      guideInfo.value = {
        verificationStatus: 'pending', // 默认值
        rating: statsResponse.data?.rating || 0,
        totalReviews: 0,
        totalOrders: 0,
        totalIncome: statsResponse.data?.totalIncome || 0
      }
    } catch (e) {
      console.error('获取向导统计信息失败:', e)
      // 如果都失败了，设置默认值
      guideInfo.value = {
        verificationStatus: 'pending',
        rating: 0,
        totalReviews: 0,
        totalOrders: 0,
        totalIncome: 0
      }
    }
  }
}

const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

const getAvatarUrl = (avatarPath, avatarStatus) => {
  // 如果头像路径不存在，显示默认头像
  if (!avatarPath) {
    return '/default-avatar.png'
  }
  // 如果头像状态存在且不是 'approved'，显示默认头像
  if (avatarStatus && avatarStatus !== 'approved') {
    return '/default-avatar.png'
  }
  // 如果头像状态是 null 或 'approved'，显示头像
  if (avatarPath.startsWith('http://') || avatarPath.startsWith('https://')) {
    return avatarPath
  }
  if (avatarPath.startsWith('/img/avatar/')) {
    return `http://localhost:8080${avatarPath}`
  }
  if (avatarPath.startsWith('/img/')) {
    return `http://localhost:8080${avatarPath}`
  }
  if (avatarPath.startsWith('/static/')) {
    const filename = avatarPath.split('/').pop()
    return `http://localhost:8080/img/avatar/${filename}`
  }
  return `http://localhost:8080/img/avatar/${avatarPath}`
}

const handleAvatarChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    alert(t('profile.invalidImageType'))
    return
  }

  // 验证文件大小（限制为 5MB）
  if (file.size > 5 * 1024 * 1024) {
    alert(t('profile.imageTooLarge'))
    return
  }

  uploading.value = true
  try {
    // 上传头像
    const token = localStorage.getItem('token')
    const cleanToken = token?.startsWith('Bearer ') ? token.substring(7) : token
    
    const formData = new FormData()
    formData.append('file', file)
    const uploadResponse = await axios.post('http://localhost:8080/api/upload/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': token || ''
      }
    })
    
    const avatarPath = uploadResponse.data
    console.log('上传成功，头像路径:', avatarPath)
    
    // 更新用户头像
    const updateResponse = await request.post('/auth/update-avatar', {
      avatarPath: avatarPath
    })
    
    console.log('更新头像响应:', updateResponse.data)
    
    // 更新本地用户信息（使用后端返回的完整数据）
    if (updateResponse.data) {
      userInfo.value = { ...updateResponse.data }
      localStorage.setItem('user', JSON.stringify(updateResponse.data))
      
      // 强制触发响应式更新
      await nextTick()
    }
    
    alert(t('profile.avatarUploadSuccess'))
    // 重新加载用户信息以确保数据同步
    await loadUserInfo()
  } catch (error) {
    console.error('更新头像失败:', error)
alert(t('profile.avatarUpdateFailed') + ': ' + (error.response?.data?.error || error.message))
  } finally {
    uploading.value = false
    // 清空 input，允许重复选择同一文件
    if (avatarInput.value) {
      avatarInput.value.value = ''
    }
  }
}

const getRoleText = (role) => {
  const map = {
    tourist: t('role.tourist'),
    guide: t('role.guide'),
    admin: t('role.admin')
  }
  return map[role] || role
}

const getVerificationStatusText = () => {
  if (!guideInfo.value) return t('profile.pending')
  const status = guideInfo.value.verificationStatus || 'pending'
  const map = {
    pending: t('profile.pending'),
    approved: t('profile.approved'),
    rejected: t('profile.rejected')
  }
  return map[status] || status
}

const getVerificationStatusClass = () => {
  if (!guideInfo.value) return 'pending'
  const status = guideInfo.value.verificationStatus || 'pending'
  return status
}

const goToGuideProfile = () => {
  router.push('/guide/profile')
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const formatRating = (rating) => {
  if (!rating) return '0.0'
  if (typeof rating === 'number') {
    return rating.toFixed(1)
  }
  if (typeof rating === 'string') {
    return parseFloat(rating).toFixed(1)
  }
  return '0.0'
}

const formatMoney = (amount) => {
  if (!amount) return '0.00'
  if (typeof amount === 'number') {
    return amount.toFixed(2)
  }
  if (typeof amount === 'string') {
    return parseFloat(amount).toFixed(2)
  }
  return '0.00'
}

// 快捷入口导航
const goToGuides = () => {
  router.push('/tourist')
}

const goToOrders = () => {
  router.push('/tourist/orders')
}

const goToCommunity = () => {
  router.push('/community')
}

const goToGuideDashboard = () => {
  router.push('/guide/dashboard')
}

const goToGuideCalendar = () => {
  router.push('/guide/calendar')
}

const goToGuideIncome = () => {
  router.push('/guide/income')
}

const goToAdmin = () => {
  router.push('/admin')
}

// 编辑字段
const editField = (fieldName) => {
  editingField.value = fieldName
  editValue.value = userInfo.value?.[fieldName] || ''
}

// 保存编辑
const saveField = async () => {
  if (!editingField.value) return

  try {
    const response = await request.post('/auth/update-profile', {
      [editingField.value]: editValue.value
    })
    
    if (response.data) {
      userInfo.value = { ...userInfo.value, ...response.data }
      localStorage.setItem('user', JSON.stringify(userInfo.value))
      alert(t('profile.updateSuccess'))
    }
    
    editingField.value = null
    editValue.value = ''
  } catch (error) {
    alert(t('profile.updateFailed') + ': ' + (error.response?.data?.error || error.message))
  }
}

// 取消编辑
const cancelEdit = () => {
  editingField.value = null
  editValue.value = ''
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24px 0;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #333;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-card {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 24px 0;
  color: #333;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 32px;
}

.avatar-container {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.avatar-large {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: #fff;
}

.upload-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.upload-text {
  font-size: 12px;
}

.avatar-input {
  display: none;
}

.avatar-status-badge {
  position: absolute;
  bottom: 8px;
  left: 50%;
  transform: translateX(-50%);
  padding: 4px 12px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.avatar-status-badge.rejected {
  background: rgba(220, 38, 38, 0.9);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-item label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.info-value {
  font-size: 16px;
  color: #333;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.pending {
  background: #fef3c7;
  color: #d97706;
}

.status-badge.approved {
  background: #d1fae5;
  color: #059669;
}

.status-badge.rejected {
  background: #fee2e2;
  color: #dc2626;
}

.action-buttons {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: #2563eb;
  color: #fff;
}

.btn-primary:hover {
  background: #1d4ed8;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border: 2px solid #e5e5e5;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn:hover {
  border-color: #2563eb;
  background: #f0f7ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.1);
}

.action-icon {
  font-size: 32px;
}

.action-text {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.required {
  color: #ef4444;
  margin-left: 4px;
}

.optional {
  color: #787774;
  font-size: 12px;
  font-weight: normal;
}

.editable {
  cursor: pointer;
  position: relative;
  padding-right: 60px;
}

.edit-hint {
  position: absolute;
  right: 0;
  font-size: 12px;
  color: #2563eb;
  opacity: 0;
  transition: opacity 0.2s;
}

.editable:hover .edit-hint {
  opacity: 1;
}

.edit-input-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

.edit-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
}

.edit-input:focus {
  border-color: #2563eb;
}

.edit-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-btn.save {
  background: #2563eb;
  color: #fff;
}

.edit-btn.save:hover {
  background: #1d4ed8;
}

.edit-btn.cancel {
  background: #f3f4f6;
  color: #333;
}

.edit-btn.cancel:hover {
  background: #e5e7eb;
}
</style>

