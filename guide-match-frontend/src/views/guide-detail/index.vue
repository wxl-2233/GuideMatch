<template>
  <div class="guide-detail-page">
    <PageContainer>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="!guide" class="empty">向导不存在</div>
      <div v-else>
        <!-- 向导信息 -->
        <div class="guide-header">
          <img
            :src="getAvatarUrl(guide.avatarPath, guide.avatarStatus)"
            :alt="guide.nickname"
            class="guide-avatar-large"
          />
          <div class="guide-basic-info">
            <h1 class="guide-name">{{ guide.nickname }}</h1>
            <div class="guide-rating">
              <span class="stars">★★★★★</span>
              <span class="rating-value">{{ formatRating(guide.rating) }}</span>
              <span class="reviews">({{ guide.totalReviews || 0 }}评价)</span>
            </div>
            <div class="guide-tags">
              <span v-for="tag in guide.tagsArray" :key="tag" class="tag">
                {{ tag }}
              </span>
            </div>
          </div>
        </div>

        <!-- 详细信息 -->
        <div class="guide-details">
          <div class="detail-section">
            <h3>服务城市</h3>
            <div class="cities">
              <span v-for="city in guide.citiesArray" :key="city" class="city">
                {{ city }}
              </span>
            </div>
          </div>

          <div class="detail-section">
            <h3>语言能力</h3>
            <div class="languages">
              <span v-for="lang in guide.languagesArray" :key="lang" class="language">
                {{ lang }}
              </span>
            </div>
          </div>

          <div class="detail-section">
            <h3>个人简介</h3>
            <p class="bio">{{ guide.bio || '暂无简介' }}</p>
          </div>

          <div class="detail-section">
            <h3>价格</h3>
            <div class="prices">
              <div class="price-item">
                <span class="price-label">时薪：</span>
                <span class="price-value">¥{{ guide.hourlyRate || 0 }}/小时</span>
              </div>
              <div class="price-item">
                <span class="price-label">日薪：</span>
                <span class="price-value">¥{{ guide.dailyRate || 0 }}/天</span>
              </div>
            </div>
          </div>

          <div class="detail-section calendar-section">
            <div class="section-header">
              <h3>📅 可接单日历</h3>
              <span class="calendar-hint">点击日期选择出行时间，系统会自动避免时间冲突</span>
            </div>
            <div class="calendar-legend">
              <span class="legend-item">
                <span class="legend-color available"></span>
                <span>可接单</span>
              </span>
              <span class="legend-item">
                <span class="legend-color booked"></span>
                <span>已约满</span>
              </span>
              <span class="legend-item">
                <span class="legend-color unavailable"></span>
                <span>请假</span>
              </span>
            </div>
            <div v-if="availableDates.length === 0 && (!guide.bookedDates || guide.bookedDates.length === 0) && (!guide.unavailableDates || guide.unavailableDates.length === 0)" class="calendar-empty">
              <p>该向导暂无可接单日期，请选择其他向导或联系向导设置可接单时间</p>
            </div>
            <div v-else class="calendar">
              <!-- 可接单日期 -->
              <div
                v-for="date in availableDates"
                :key="date"
                class="calendar-date available"
                :class="{ selected: selectedDates.includes(date) }"
                @click="toggleDate(date)"
                :title="formatDate(date) + ' - 可接单'"
              >
                <div class="date-day">{{ formatDate(date).split(' ')[1] }}</div>
                <div class="date-month">{{ formatDate(date).split(' ')[0] }}</div>
              </div>
              <!-- 已约满日期 -->
              <div
                v-for="date in (guide.bookedDates || [])"
                :key="'booked-' + date"
                class="calendar-date booked"
                :title="formatDate(date) + ' - 已约满'"
              >
                <div class="date-day">{{ formatDate(date).split(' ')[1] }}</div>
                <div class="date-month">{{ formatDate(date).split(' ')[0] }}</div>
                <div class="date-status">已约满</div>
              </div>
              <!-- 请假日期 -->
              <div
                v-for="date in (guide.unavailableDates || [])"
                :key="'unavailable-' + date"
                class="calendar-date unavailable"
                :title="formatDate(date) + ' - 请假'"
              >
                <div class="date-day">{{ formatDate(date).split(' ')[1] }}</div>
                <div class="date-month">{{ formatDate(date).split(' ')[0] }}</div>
                <div class="date-status">请假</div>
              </div>
            </div>
            <div v-if="selectedDates.length > 0" class="selected-dates-info">
              <span class="info-label">已选择：</span>
              <span class="info-value">{{ selectedDates.join('、') }}</span>
            </div>
          </div>

          <!-- 评价列表 -->
          <div class="detail-section reviews-section">
            <h3>用户评价 ({{ reviews.length }})</h3>
            <div v-if="reviewsLoading" class="reviews-loading">加载评价中...</div>
            <div v-else-if="reviews.length === 0" class="reviews-empty">
              暂无评价
            </div>
            <div v-else class="reviews-list">
              <ReviewCard
                v-for="review in reviews"
                :key="review.id"
                :review="review"
              />
            </div>
          </div>
        </div>

        <!-- 下单区域 -->
        <div class="order-section">
          <h2>创建订单</h2>
          <div class="order-form">
            <div class="form-group">
              <label>出行日期</label>
              <div class="date-range">
                <input
                  v-model="orderForm.startDate"
                  type="date"
                  class="date-input"
                  :min="today"
                  @change="validateDateRange"
                />
                <span>至</span>
                <input
                  v-model="orderForm.endDate"
                  type="date"
                  class="date-input"
                  :min="orderForm.startDate || today"
                  @change="validateDateRange"
                />
              </div>
              <div v-if="dateRangeError" class="error-message">{{ dateRangeError }}</div>
            </div>

            <div class="form-group">
              <label>出行人数</label>
              <input
                v-model.number="orderForm.participants"
                type="number"
                min="1"
                class="input"
              />
            </div>

            <div class="form-group">
              <label>订单类型</label>
              <select v-model="orderForm.orderType" class="select">
                <option value="fixed">固定路线</option>
                <option value="custom">定制路线</option>
              </select>
            </div>

            <div class="form-group">
              <label>特殊需求</label>
              <textarea
                v-model="orderForm.specialRequirements"
                placeholder="请输入特殊需求..."
                class="textarea"
              ></textarea>
            </div>

            <div class="price-calculation">
              <div class="price-row">
                <span>日薪：</span>
                <span>¥{{ guide.dailyRate || 0 }}/天</span>
              </div>
              <div class="price-row">
                <span>天数：</span>
                <span>{{ calculateDays() }}天</span>
              </div>
              <div class="price-row total">
                <span>总价：</span>
                <span>¥{{ calculateTotalPrice() }}</span>
              </div>
            </div>

            <button class="btn-submit" @click="createOrder" :disabled="!canSubmit">
              提交订单
            </button>
          </div>
        </div>
      </div>
    </PageContainer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/api/request'
import PageContainer from '@/components/layout/PageContainer.vue'
import ReviewCard from '@/components/business/ReviewCard.vue'

const route = useRoute()
const router = useRouter()
const guideId = route.params.id

const guide = ref(null)
const loading = ref(false)
const availableDates = ref([])
const selectedDates = ref([])
const reviews = ref([])
const reviewsLoading = ref(false)

const getAvatarUrl = (avatarPath, avatarStatus) => {
  // 只有审核通过的头像才显示，否则显示默认头像
  if (!avatarPath || avatarStatus !== 'approved') {
    return '/default-avatar.png'
  }
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

const orderForm = ref({
  startDate: '',
  endDate: '',
  participants: 1,
  orderType: 'custom',
  specialRequirements: ''
})

const dateRangeError = ref('')
const today = new Date().toISOString().split('T')[0]

const validateDateRange = () => {
  dateRangeError.value = ''
  if (!orderForm.value.startDate || !orderForm.value.endDate) {
    return
  }

  const startDate = new Date(orderForm.value.startDate)
  const endDate = new Date(orderForm.value.endDate)

  if (startDate > endDate) {
    dateRangeError.value = '结束日期不能早于开始日期'
    return
  }

  // 检查日期范围内是否有已约满或请假的日期
  const bookedDates = guide.value.bookedDates || []
  const unavailableDates = guide.value.unavailableDates || []
  
  let currentDate = new Date(startDate)
  while (currentDate <= endDate) {
    const dateStr = currentDate.toISOString().split('T')[0]
    
    if (bookedDates.includes(dateStr)) {
      dateRangeError.value = `所选日期范围内包含已约满的日期：${dateStr}，请重新选择`
      return
    }
    
    if (unavailableDates.includes(dateStr)) {
      dateRangeError.value = `所选日期范围内包含请假的日期：${dateStr}，请重新选择`
      return
    }
    
    currentDate.setDate(currentDate.getDate() + 1)
  }
}

const canSubmit = computed(() => {
  return orderForm.value.startDate &&
    orderForm.value.endDate &&
    orderForm.value.participants > 0 &&
    !dateRangeError.value
})

// 加载向导详情
const loadGuideDetail = async () => {
  loading.value = true
  try {
    const response = await request.get(`/guides/${guideId}`)
    const data = response.data
    
    // 安全解析JSON字符串
    const parseJsonSafely = (jsonStr, defaultValue = []) => {
      if (!jsonStr) return defaultValue
      if (Array.isArray(jsonStr)) return jsonStr
      try {
        return JSON.parse(jsonStr)
      } catch (e) {
        console.error('解析JSON失败:', e, '原始数据:', jsonStr)
        return defaultValue
      }
    }
    
    guide.value = {
      ...data.guide,
      ...data.user,
      tagsArray: parseJsonSafely(data.guide.tags, []),
      citiesArray: parseJsonSafely(data.guide.cities, []),
      languagesArray: parseJsonSafely(data.guide.languages, []),
      certificatesArray: parseJsonSafely(data.guide.certificates, [])
    }
    
    // 调试：打印解析后的数据
    console.log('向导详情数据:', {
      languages: guide.value.languagesArray,
      cities: guide.value.citiesArray,
      tags: guide.value.tagsArray,
      certificates: guide.value.certificatesArray
    })
    
    // 处理日历数据：区分不同状态的日期
    const calendarData = data.calendar || []
    const calendarMap = {}
    calendarData.forEach(item => {
      calendarMap[item.date] = item.status || 'available'
    })
    
    // 保存完整的日历数据，包括状态
    guide.value.calendarMap = calendarMap
    
    // 只显示可接单的日期
    availableDates.value = calendarData
      .filter(item => item.status === 'available')
      .map(item => item.date)
    
    // 保存已约满和请假的日期，用于禁用
    guide.value.bookedDates = calendarData
      .filter(item => item.status === 'booked')
      .map(item => item.date)
    guide.value.unavailableDates = calendarData
      .filter(item => item.status === 'unavailable')
      .map(item => item.date)
    
    // 加载评价列表
    await loadReviews()
  } catch (error) {
    console.error('加载向导详情失败:', error)
    alert('加载向导详情失败，请刷新页面重试')
  } finally {
    loading.value = false
  }
}

// 加载评价列表
const loadReviews = async () => {
  reviewsLoading.value = true
  try {
    // 使用向导的 userId（因为后端 reviews 表的 guide_id 是 user_id）
    const guideUserId = guide.value?.userId
    if (!guideUserId) {
      reviews.value = []
      return
    }
    
    const response = await request.get(`/reviews/guide/${guideUserId}`)
    reviews.value = response.data.list || []
  } catch (error) {
    console.error('加载评价失败:', error)
    reviews.value = []
  } finally {
    reviewsLoading.value = false
  }
}

// 切换日期选择
const toggleDate = (date) => {
  const index = selectedDates.value.indexOf(date)
  if (index > -1) {
    selectedDates.value.splice(index, 1)
  } else {
    selectedDates.value.push(date)
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()}`
}

// 格式化评分
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

// 计算天数
const calculateDays = () => {
  if (!orderForm.value.startDate || !orderForm.value.endDate) return 0
  const start = new Date(orderForm.value.startDate)
  const end = new Date(orderForm.value.endDate)
  const diff = (end - start) / (1000 * 60 * 60 * 24) + 1
  return diff > 0 ? diff : 0
}

// 计算总价
const calculateTotalPrice = () => {
  if (!guide.value) return 0
  const days = calculateDays()
  return (guide.value.dailyRate || 0) * days * orderForm.value.participants
}

// 创建订单
const createOrder = async () => {
  if (!canSubmit.value) {
    alert('请填写完整信息')
    return
  }

  // 检查选择的日期是否包含已约满或请假的日期
  if (orderForm.value.startDate && orderForm.value.endDate) {
    const startDate = new Date(orderForm.value.startDate)
    const endDate = new Date(orderForm.value.endDate)
    const selectedDateRange = []
    
    let currentDate = new Date(startDate)
    while (currentDate <= endDate) {
      const dateStr = currentDate.toISOString().split('T')[0]
      selectedDateRange.push(dateStr)
      currentDate.setDate(currentDate.getDate() + 1)
    }

    // 检查是否有已约满的日期
    const bookedDates = guide.value.bookedDates || []
    const hasBookedDate = selectedDateRange.some(date => bookedDates.includes(date))
    if (hasBookedDate) {
      alert('您选择的日期中包含已约满的日期，请重新选择')
      return
    }

    // 检查是否有请假的日期
    const unavailableDates = guide.value.unavailableDates || []
    const hasUnavailableDate = selectedDateRange.some(date => unavailableDates.includes(date))
    if (hasUnavailableDate) {
      alert('您选择的日期中包含向导请假的日期，请重新选择')
      return
    }
  }

  try {
    // 使用guide.userId作为guideId（因为后端orders表的guide_id是user_id）
    await request.post('/orders/create', {
      guideId: guide.value.userId,
      startDate: orderForm.value.startDate,
      endDate: orderForm.value.endDate,
      participants: orderForm.value.participants,
      orderType: orderForm.value.orderType,
      specialRequirements: orderForm.value.specialRequirements,
      totalPrice: calculateTotalPrice()
    })
    alert('订单创建成功')
    window.location.href = '/tourist/orders'
  } catch (error) {
    alert(error.response?.data?.error || '创建订单失败')
  }
}

onMounted(() => {
  loadGuideDetail()
})
</script>

<style scoped>
.guide-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24px 0;
}

.guide-header {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  margin-bottom: 24px;
  display: flex;
  gap: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.guide-avatar-large {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
}

.guide-basic-info {
  flex: 1;
}

.guide-name {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 12px 0;
}

.guide-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.stars {
  color: #ffc107;
  font-size: 18px;
}

.rating-value {
  font-size: 18px;
  font-weight: 600;
}

.reviews {
  color: #999;
}

.guide-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 6px 12px;
  background: #f0f0f0;
  border-radius: 16px;
  font-size: 14px;
}

.guide-details {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.detail-section {
  margin-bottom: 32px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 12px 0;
}

.cities,
.languages {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.city,
.language {
  padding: 6px 12px;
  background: #e0e7ff;
  border-radius: 16px;
  font-size: 14px;
  color: #6366f1;
}

.bio {
  line-height: 1.6;
  color: #666;
}

.prices {
  display: flex;
  gap: 24px;
}

.price-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-label {
  color: #999;
}

.price-value {
  color: #2563eb;
  font-size: 20px;
  font-weight: 600;
}

.calendar-section {
  background: #f9fafb;
  padding: 20px;
  border-radius: 8px;
  border: 2px solid #e5e7eb;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  color: #2563eb;
}

.calendar-hint {
  font-size: 12px;
  color: #6b7280;
}

.calendar-legend {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  padding: 12px;
  background: #fff;
  border-radius: 6px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #666;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.legend-color.available {
  background: #10b981;
}

.legend-color.booked {
  background: #f59e0b;
}

.legend-color.unavailable {
  background: #ef4444;
}

.calendar-empty {
  padding: 40px;
  text-align: center;
  color: #999;
  background: #fff;
  border-radius: 8px;
}

.calendar {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.calendar-date {
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 70px;
}

.calendar-date:hover {
  border-color: #2563eb;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(37, 99, 235, 0.2);
}

.calendar-date.selected {
  background: #2563eb;
  border-color: #2563eb;
  color: #fff;
}

.calendar-date.booked {
  background: #fef3c7;
  border-color: #f59e0b;
  cursor: not-allowed;
  opacity: 0.7;
}

.calendar-date.unavailable {
  background: #fee2e2;
  border-color: #ef4444;
  cursor: not-allowed;
  opacity: 0.6;
}

.date-day {
  font-size: 18px;
  font-weight: 600;
}

.date-month {
  font-size: 12px;
  opacity: 0.8;
}

.date-status {
  font-size: 10px;
  margin-top: 4px;
  font-weight: 600;
}

.calendar-date.booked .date-status {
  color: #d97706;
}

.calendar-date.unavailable .date-status {
  color: #dc2626;
}

.selected-dates-info {
  padding: 12px;
  background: #dbeafe;
  border-radius: 6px;
  font-size: 14px;
}

.info-label {
  color: #1e40af;
  font-weight: 600;
}

.info-value {
  color: #1e40af;
}

.order-section {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-section h2 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 24px 0;
}

.order-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 12px;
}

.date-input,
.input,
.select,
.textarea {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
}

.date-input:focus,
.input:focus,
.select:focus,
.textarea:focus {
  border-color: #2563eb;
}

.textarea {
  min-height: 100px;
  resize: vertical;
}

.error-message {
  color: #ef4444;
  font-size: 14px;
  margin-top: 8px;
  padding: 8px;
  background: #fee2e2;
  border-radius: 4px;
}

.price-calculation {
  padding: 16px;
  background: #f9fafb;
  border-radius: 6px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.price-row.total {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #ddd;
  font-size: 18px;
  font-weight: 600;
  color: #2563eb;
}

.btn-submit {
  height: 44px;
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-submit:hover:not(:disabled) {
  background: #1d4ed8;
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.reviews-section {
  margin-top: 32px;
}

.reviews-loading,
.reviews-empty {
  text-align: center;
  padding: 40px;
  color: #999;
  background: #f9fafb;
  border-radius: 8px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>