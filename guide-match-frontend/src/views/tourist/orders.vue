<template>
  <div class="orders-page">
    <PageContainer>
      <h1 class="page-title">{{ t('order.myOrders') }}</h1>

      <div class="tabs">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          class="tab"
          :class="{ active: currentTab === tab.value }"
          @click="currentTab = tab.value; loadOrders()"
        >
          {{ tab.label }}
        </button>
      </div>

      <div v-if="loading" class="loading">{{ t('order.loading') }}</div>
      <div v-else-if="orders.length === 0" class="empty">{{ t('order.noOrders') }}</div>
      <div v-else class="orders-list">
        <div
          v-for="order in orders"
          :key="order.id"
          class="order-card"
        >
          <div class="order-content">
            <!-- 左侧信息 -->
            <div class="order-left">
              <div class="order-id">{{ t('order.orderId') }}：{{ order.id }}</div>
              <div class="order-info">
                <div class="info-item">
                  <span class="label">{{ t('order.guide') }}：</span>
                  <span class="value">{{ order.guideName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">{{ t('order.date') }}：</span>
                  <span class="value">{{ order.startDate }} {{ t('common.to') }} {{ order.endDate }}</span>
                </div>
              </div>
            </div>
            
            <!-- 右侧信息 -->
            <div class="order-right">
              <div class="order-status-large" :class="order.status">
                {{ getStatusText(order.status) }}
              </div>
              <div class="order-info">
                <div class="info-item">
                  <span class="label">{{ t('order.participants') }}：</span>
                  <span class="value">{{ order.participants }}{{ t('common.people') }}</span>
                </div>
                <div class="info-item">
                  <span class="label">{{ t('order.totalPrice') }}：</span>
                  <span class="value price">¥{{ order.totalPrice }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="order-actions">
            <button
              v-if="order.status === 'completed' && !order.reviewed"
              class="btn btn-primary"
              @click="showReviewModal(order)"
            >
              {{ t('order.reviewGuide') }}
            </button>
            <button
              v-if="order.status === 'pending' || order.status === 'confirmed'"
              class="btn btn-danger"
              @click="cancelOrder(order.id)"
            >
              {{ t('order.cancelOrder') }}
            </button>
            <button class="btn btn-secondary" @click="viewOrderDetail(order.id)">
              {{ t('order.viewDetail') }}
            </button>
          </div>

          <!-- 评价展示 -->
          <div v-if="order.review && order.reviewed" class="review-section">
            <h4 class="review-title">{{ t('order.myReview') }}</h4>
            <ReviewCard :review="order.review" />
          </div>
        </div>
      </div>
    </PageContainer>

    <!-- 订单详情弹窗 -->
    <div v-if="showOrderDetail && currentOrder" class="modal-overlay" @click.self="showOrderDetail = false">
      <div class="modal-content">
        <h3>订单详情</h3>
        <div class="order-detail">
          <div class="detail-item">
            <span class="label">订单号：</span>
            <span class="value">{{ currentOrder.id }}</span>
          </div>
          <div class="detail-item">
            <span class="label">状态：</span>
            <span class="value status" :class="currentOrder.status">{{ getStatusText(currentOrder.status) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">导游：</span>
            <span class="value">{{ currentOrder.guideName || '未知' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">日期：</span>
            <span class="value">{{ currentOrder.startDate || '-' }} 至 {{ currentOrder.endDate || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">参与人数：</span>
            <span class="value">{{ currentOrder.participants || 1 }}人</span>
          </div>
          <div class="detail-item">
            <span class="label">总价：</span>
            <span class="value price">¥{{ currentOrder.totalPrice || 0 }}</span>
          </div>
          <div v-if="currentOrder.specialRequirements" class="detail-item">
            <span class="label">特殊要求：</span>
            <span class="value">{{ currentOrder.specialRequirements }}</span>
          </div>
          <div v-if="currentOrder.cancelReason" class="detail-item">
            <span class="label">取消原因：</span>
            <span class="value">{{ currentOrder.cancelReason }}</span>
          </div>
          <div v-if="currentOrder.rejectReason" class="detail-item">
            <span class="label">拒绝原因：</span>
            <span class="value">{{ currentOrder.rejectReason }}</span>
          </div>
        </div>
        
        <!-- 分享按钮 -->
        <div class="share-section">
          <button 
            v-if="currentOrder.status === 'pending'"
            class="btn btn-share"
            @click="shareGuideInfo(currentOrder)"
            title="将导游简历和旅游规划一键分享到"
          >
            📤 分享导游信息
          </button>
          <button 
            v-else-if="currentOrder.status === 'in_progress'"
            class="btn btn-share"
            @click="shareRouteInfo(currentOrder)"
            title="将车牌号和路线一键分享到"
          >
            📤 分享路线信息
          </button>
        </div>
        
        <div class="modal-actions">
          <button class="btn btn-secondary" @click="showOrderDetail = false">关闭</button>
        </div>
      </div>
    </div>

    <!-- 评价弹窗 -->
    <div v-if="showReview" class="modal-overlay" @click.self="showReview = false">
      <div class="modal-content">
        <h3>{{ t('order.reviewGuide') }}</h3>
        <div class="review-form">
          <div class="rating-section">
            <label>{{ t('order.rating') }}：</label>
            <div class="stars-input">
              <span
                v-for="i in 5"
                :key="i"
                class="star"
                :class="{ active: i <= reviewForm.rating }"
                @click="reviewForm.rating = i"
              >
                ★
              </span>
            </div>
          </div>
          <div class="comment-section">
            <label>{{ t('order.comment') }}：</label>
            <textarea
              v-model="reviewForm.comment"
              :placeholder="t('order.comment') + '...'"
              class="comment-input"
            ></textarea>
          </div>
          <div class="modal-actions">
            <button class="btn btn-secondary" @click="showReview = false">{{ t('order.cancel') }}</button>
            <button class="btn btn-primary" @click="submitReview">{{ t('order.submitReview') }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from '@/composables/useI18n'
import request from '@/api/request'
import PageContainer from '@/components/layout/PageContainer.vue'
import ReviewCard from '@/components/business/ReviewCard.vue'

const { t } = useI18n()

const orders = ref([])
const loading = ref(false)
const currentTab = ref('all')
const showReview = ref(false)
const currentOrder = ref(null)
const showOrderDetail = ref(false)

const reviewForm = ref({
  rating: 5,
  comment: ''
})

const tabs = [
  { label: t('order.all'), value: 'all' },
  { label: t('order.pending'), value: 'pending' },
  { label: t('order.confirmed'), value: 'confirmed' },
  { label: t('order.inProgress'), value: 'in_progress' },
  { label: t('order.completed'), value: 'completed' },
  { label: t('order.cancelled'), value: 'cancelled' }
]

const getStatusText = (status) => {
  const map = {
    pending: t('order.pending'),
    confirmed: t('order.confirmed'),
    in_progress: t('order.inProgress'),
    completed: t('order.completed'),
    cancelled: t('order.cancelled')
  }
  return map[status] || status
}

const loadOrders = async () => {
  loading.value = true
  try {
    const params = currentTab.value !== 'all' ? { status: currentTab.value } : {}
    const response = await request.get('/orders/list', { params })
    orders.value = response.data.list || []
  } catch (error) {
    console.error('加载订单失败:', error)
    orders.value = []
  } finally {
    loading.value = false
  }
}

const cancelOrder = async (orderId) => {
  if (!confirm(t('order.cancelOrder') + '？')) return

  try {
    await request.post(`/orders/${orderId}/cancel`, {
      reason: t('order.cancelOrder')
    })
    window.location.reload()
  } catch (error) {
    alert(t('order.cancelOrder') + t('message.operationFailed'))
  }
}

const showReviewModal = (order) => {
  currentOrder.value = order
  reviewForm.value = { rating: 5, comment: '' }
  showReview.value = true
}

const submitReview = async () => {
  if (!reviewForm.value.comment.trim()) {
    alert(t('order.comment') + t('message.required'))
    return
  }

  if (!reviewForm.value.rating || reviewForm.value.rating < 1 || reviewForm.value.rating > 5) {
    alert(t('order.rating') + '（1-5' + t('common.star') + '）')
    return
  }

  try {
    await request.post('/reviews/create', {
      orderId: currentOrder.value.id,
      rating: reviewForm.value.rating,
      comment: reviewForm.value.comment
    })
    showReview.value = false
    alert(t('order.submitReview') + t('common.success'))
    await loadOrders()
  } catch (error) {
    const errorMsg = error.response?.data?.error || error.response?.data?.message || error.message || t('message.unknownError')
    alert(t('order.submitReview') + t('message.operationFailed') + ': ' + errorMsg)
    console.error('评价失败详情:', error)
  }
}

const viewOrderDetail = (orderId) => {
  const order = orders.value.find(o => o.id === orderId)
  if (!order) {
    alert(t('order.orderId') + t('message.notExist'))
    return
  }
  
  currentOrder.value = order
  showOrderDetail.value = true
}

// 分享导游简历和旅游规划（待确认订单）
const shareGuideInfo = (order) => {
  const shareContent = `
导游简历：
姓名：${order.guideName || '未知'}
${order.guideBio ? '简介：' + order.guideBio : ''}
${order.guideTags ? '标签：' + order.guideTags : ''}
${order.guideLanguages ? '语言：' + order.guideLanguages : ''}
${order.guideCities ? '服务城市：' + order.guideCities : ''}

旅游规划：
日期：${order.startDate || '-'} 至 ${order.endDate || '-'}
参与人数：${order.participants || 1}人
价格：¥${order.totalPrice || 0}
${order.specialRequirements ? '特殊要求：' + order.specialRequirements : ''}
  `.trim()
  
  // 复制到剪贴板
  navigator.clipboard.writeText(shareContent).then(() => {
    alert('导游简历和旅游规划已复制到剪贴板')
  }).catch(() => {
    alert('复制失败，请手动复制：\n' + shareContent)
  })
}

// 分享车牌号和路线（进行中订单）
const shareRouteInfo = (order) => {
  const shareContent = `
订单信息：
订单号：${order.id}
导游：${order.guideName || '未知'}
车牌号：${order.carNumber || '待分配'}
联系电话：${order.guidePhone || '待提供'}

路线信息：
日期：${order.startDate || '-'} 至 ${order.endDate || '-'}
起点：${order.startPoint || '待确认'}
终点：${order.endPoint || '待确认'}
路线：${order.routeName || '待确认'}
参与人数：${order.participants || 1}人
  `.trim()
  
  // 复制到剪贴板
  navigator.clipboard.writeText(shareContent).then(() => {
    alert('车牌号和路线信息已复制到剪贴板')
  }).catch(() => {
    alert('复制失败，请手动复制：\n' + shareContent)
  })
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24px 0;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  border-bottom: 2px solid #eee;
}

.tab {
  padding: 12px 24px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
}

.tab.active {
  color: #2563eb;
  border-bottom-color: #2563eb;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.order-left {
  flex: 1;
}

.order-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: flex-start;
}

.order-id {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.order-status-large {
  padding: 8px 16px;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  text-align: center;
  min-width: 100px;
}

.order-status-large.pending {
  background: #fef3c7;
  color: #d97706;
}

.order-status-large.confirmed {
  background: #dbeafe;
  color: #2563eb;
}

.order-status-large.in_progress {
  background: #d1fae5;
  color: #059669;
}

.order-status-large.completed {
  background: #e0e7ff;
  color: #6366f1;
}

.order-status-large.cancelled {
  background: #fee2e2;
  color: #dc2626;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-item {
  display: flex;
  gap: 8px;
  font-size: 14px;
}

.order-right .order-info {
  align-items: flex-end;
  text-align: right;
}

.order-right .info-item {
  justify-content: flex-end;
}

.label {
  color: #999;
  font-size: 14px;
}

.value {
  color: #333;
  font-size: 14px;
}

.value.price {
  color: #2563eb;
  font-weight: 600;
  font-size: 16px;
}

.order-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.btn-primary {
  background: #2563eb;
  color: #fff;
}

.btn-secondary {
  background: #f3f4f6;
  color: #333;
}

.btn-danger {
  background: #ef4444;
  color: #fff;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  width: 90%;
  max-width: 500px;
}

.review-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.rating-section,
.comment-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stars-input {
  display: flex;
  gap: 4px;
}

.star {
  font-size: 24px;
  color: #ddd;
  cursor: pointer;
}

.star.active {
  color: #ffc107;
}

.comment-input {
  width: 100%;
  min-height: 100px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  resize: vertical;
}

.modal-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.review-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.review-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

/* 订单详情弹窗样式 */
.order-detail {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item .label {
  color: #666;
  font-size: 14px;
  font-weight: 500;
  min-width: 80px;
}

.detail-item .value {
  color: #333;
  font-size: 14px;
  text-align: right;
  flex: 1;
}

.detail-item .value.status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.share-section {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.btn-share {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-share:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}
</style>
