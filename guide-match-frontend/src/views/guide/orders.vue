<template>
  <div class="guide-orders-page">
    <PageContainer>
      <h1 class="page-title">{{ t('order.title') }}</h1>

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
          <div class="order-header">
            <span class="order-id">{{ t('order.orderId') }}：{{ order.id }}</span>
            <span class="order-status" :class="order.status">
              {{ getStatusText(order.status) }}
            </span>
          </div>

          <div class="order-info">
            <div class="info-item">
              <span class="label">{{ t('order.tourist') }}：</span>
              <span class="value">{{ order.touristName || t('common.unknown') }}</span>
            </div>
            <div class="info-item">
              <span class="label">{{ t('order.date') }}：</span>
              <span class="value">{{ order.startDate }} {{ t('common.to') }} {{ order.endDate }}</span>
            </div>
            <div class="info-item">
              <span class="label">{{ t('order.participants') }}：</span>
              <span class="value">{{ order.participants }}{{ t('common.people') }}</span>
            </div>
            <div class="info-item">
              <span class="label">{{ t('order.totalPrice') }}：</span>
              <span class="value price">¥{{ order.totalPrice }}</span>
            </div>
            <div v-if="order.status === 'completed'" class="info-item">
              <span class="label">{{ t('order.guideIncome') }}：</span>
              <span class="value income">¥{{ order.guideIncome }}</span>
            </div>
            <div v-if="order.specialRequirements" class="info-item">
              <span class="label">{{ t('order.specialRequirements') }}：</span>
              <span class="value">{{ order.specialRequirements }}</span>
            </div>
            <div v-if="order.rejectReason" class="info-item">
              <span class="label">{{ t('order.rejectReason') }}：</span>
              <span class="value reject-reason">{{ order.rejectReason }}</span>
            </div>
          </div>

          <div class="order-actions">
            <template v-if="order.status === 'pending'">
              <button class="btn btn-primary" @click="acceptOrder(order.id)">
                {{ t('order.accept') }}
              </button>
              <button class="btn btn-danger" @click="showRejectModal(order)">
                {{ t('order.reject') }}
              </button>
            </template>
            <template v-else-if="order.status === 'confirmed'">
              <button class="btn btn-success" @click="startOrder(order.id)">
                {{ t('order.startService') }}
              </button>
            </template>
            <template v-else-if="order.status === 'in_progress'">
              <button class="btn btn-success" @click="completeOrder(order.id)">
                {{ t('order.completeOrder') }}
              </button>
            </template>
            <template v-else-if="order.status === 'completed'">
              <span class="completed-badge">{{ t('order.completed') }}</span>
            </template>
            <template v-else-if="order.status === 'cancelled'">
              <span class="cancelled-badge">{{ t('order.cancelled') }}</span>
            </template>
          </div>

          <!-- 评价展示 -->
          <div v-if="order.review && order.reviewed" class="review-section">
            <h4 class="review-title">{{ t('order.touristReview') }}</h4>
            <ReviewCard :review="order.review" />
          </div>
        </div>
      </div>
    </PageContainer>

    <!-- 拒单弹窗 -->
    <div v-if="showReject" class="modal-overlay" @click.self="showReject = false">
      <div class="modal-content">
        <h3>{{ t('order.rejectReason') }}</h3>
        <textarea
          v-model="rejectReason"
          :placeholder="t('order.enterRejectReason')"
          class="reject-input"
        ></textarea>
        <div class="modal-actions">
          <button class="btn btn-secondary" @click="showReject = false">{{ t('order.cancel') }}</button>
          <button class="btn btn-danger" @click="confirmReject">{{ t('order.confirmReject') }}</button>
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
const showReject = ref(false)
const rejectReason = ref('')
const currentRejectOrder = ref(null)

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

const acceptOrder = async (orderId) => {
  if (!confirm('确定接单吗？')) return

  try {
    await request.post(`/orders/${orderId}/accept`)
    alert('接单成功')
    loadOrders()
  } catch (error) {
    alert(error.response?.data?.error || '接单失败')
  }
}

const showRejectModal = (order) => {
  currentRejectOrder.value = order
  rejectReason.value = ''
  showReject.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    alert(t('order.enterRejectReason'))
    return
  }

  try {
    await request.post(`/orders/${currentRejectOrder.value.id}/reject`, {
      reason: rejectReason.value
    })
    alert(t('order.reject') + t('common.success'))
    showReject.value = false
    loadOrders()
  } catch (error) {
    alert(error.response?.data?.error || t('message.operationFailed'))
  }
}

const startOrder = async (orderId) => {
  if (!confirm(t('order.startService') + '？' + t('order.status.inProgress'))) return

  try {
    await request.post(`/orders/${orderId}/start`)
    alert(t('order.startService') + t('common.success'))
    loadOrders()
  } catch (error) {
    if (error.response?.status === 404) {
      alert(t('message.developing'))
    } else {
      alert(error.response?.data?.error || t('message.operationFailed'))
    }
  }
}

const completeOrder = async (orderId) => {
  if (!confirm(t('order.confirmComplete'))) return

  try {
    await request.post(`/orders/${orderId}/complete`)
    
    try {
      const userResponse = await request.get('/auth/me')
      if (userResponse.data) {
        localStorage.setItem('user', JSON.stringify(userResponse.data))
        window.dispatchEvent(new CustomEvent('userInfoUpdated', { detail: userResponse.data }))
      }
    } catch (e) {
      console.error('刷新用户信息失败:', e)
    }
    
    alert(t('order.orderCompleted'))
    loadOrders()
  } catch (error) {
    alert(error.response?.data?.error || t('message.operationFailed'))
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.guide-orders-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24px 0;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  background: #fff;
  padding: 8px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tab {
  padding: 8px 16px;
  border: none;
  background: transparent;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.tab:hover {
  background: #f3f4f6;
}

.tab.active {
  background: #2563eb;
  color: #fff;
}

.loading,
.empty {
  text-align: center;
  padding: 40px;
  color: #999;
  background: #fff;
  border-radius: 8px;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e5e5;
}

.order-id {
  font-size: 14px;
  color: #999;
}

.order-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.order-status.pending {
  background: #fef3c7;
  color: #d97706;
}

.order-status.confirmed {
  background: #dbeafe;
  color: #2563eb;
}

.order-status.in_progress {
  background: #d1fae5;
  color: #059669;
}

.order-status.completed {
  background: #d1fae5;
  color: #059669;
}

.order-status.cancelled {
  background: #fee2e2;
  color: #dc2626;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  gap: 8px;
}

.label {
  color: #999;
  min-width: 80px;
}

.value {
  color: #333;
}

.value.price {
  color: #2563eb;
  font-weight: 600;
}

.value.income {
  color: #10b981;
  font-weight: 600;
}

.value.reject-reason {
  color: #dc2626;
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
  transition: all 0.3s;
}

.btn-primary {
  background: #2563eb;
  color: #fff;
}

.btn-primary:hover {
  background: #1d4ed8;
}

.btn-danger {
  background: #ef4444;
  color: #fff;
}

.btn-danger:hover {
  background: #dc2626;
}

.btn-success {
  background: #10b981;
  color: #fff;
}

.btn-success:hover {
  background: #059669;
}

.btn-secondary {
  background: #f3f4f6;
  color: #333;
}

.btn-secondary:hover {
  background: #e5e7eb;
}

.completed-badge,
.cancelled-badge {
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
}

.completed-badge {
  background: #d1fae5;
  color: #059669;
}

.cancelled-badge {
  background: #fee2e2;
  color: #dc2626;
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

.modal-content h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
}

.reject-input {
  width: 100%;
  min-height: 100px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  margin: 16px 0;
  resize: vertical;
}

.modal-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 16px;
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
</style>

