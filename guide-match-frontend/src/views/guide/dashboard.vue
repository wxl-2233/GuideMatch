<template>
  <div class="guide-dashboard">
    <PageContainer>
      <h1 class="page-title">{{ t('dashboard.title') }}</h1>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-label">{{ t('dashboard.pendingOrders') }}</div>
          <div class="stat-value">{{ stats.pendingOrders }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-label">{{ t('dashboard.inProgressOrders') }}</div>
          <div class="stat-value">{{ stats.inProgressOrders }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-label">{{ t('dashboard.totalIncome') }}</div>
          <div class="stat-value">¥{{ stats.totalIncome }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-label">{{ t('dashboard.rating') }}</div>
          <div class="stat-value">{{ stats.rating }}</div>
        </div>
      </div>

      <!-- 待接订单列表 -->
      <div class="section">
        <h2 class="section-title">{{ t('dashboard.pendingOrders') }}</h2>
        <div v-if="pendingOrders.length === 0" class="empty">{{ t('dashboard.noPendingOrders') }}</div>
        <div v-else class="orders-list">
          <div
            v-for="order in pendingOrders"
            :key="order.id"
            class="order-card"
          >
            <div class="order-info">
              <div class="order-header">
                <span class="order-id">{{ t('order.orderId') }}：{{ order.id }}</span>
                <span class="order-date">{{ order.startDate }} {{ t('common.to') }} {{ order.endDate }}</span>
              </div>
              <div class="order-details">
                <div class="detail-item">
                  <span class="label">{{ t('dashboard.tourist') }}：</span>
                  <span class="value">{{ order.touristName }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">{{ t('dashboard.participants') }}：</span>
                  <span class="value">{{ order.participants }}{{ t('common.people') }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">{{ t('dashboard.totalPrice') }}：</span>
                  <span class="value price">¥{{ order.totalPrice }}</span>
                </div>
                <div v-if="order.specialRequirements" class="detail-item">
                  <span class="label">{{ t('dashboard.specialRequirements') }}：</span>
                  <span class="value">{{ order.specialRequirements }}</span>
                </div>
              </div>
            </div>
            <div class="order-actions">
              <button class="btn btn-primary" @click="acceptOrder(order.id)">
                {{ t('dashboard.accept') }}
              </button>
              <button class="btn btn-danger" @click="showRejectModal(order)">
                {{ t('dashboard.reject') }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 进行中订单 -->
      <div class="section">
        <h2 class="section-title">{{ t('dashboard.inProgressOrders') }}</h2>
        <div v-if="inProgressOrders.length === 0" class="empty">{{ t('dashboard.noInProgressOrders') }}</div>
        <div v-else class="orders-list">
          <div
            v-for="order in inProgressOrders"
            :key="order.id"
            class="order-card"
          >
            <div class="order-info">
              <div class="order-header">
                <span class="order-id">{{ t('order.orderId') }}：{{ order.id }}</span>
                <span class="order-date">{{ order.startDate }} {{ t('common.to') }} {{ order.endDate }}</span>
              </div>
              <div class="order-details">
                <div class="detail-item">
                  <span class="label">{{ t('dashboard.tourist') }}：</span>
                  <span class="value">{{ order.touristName || t('common.unknown') }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">{{ t('dashboard.participants') }}：</span>
                  <span class="value">{{ order.participants }}{{ t('common.people') }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">{{ t('dashboard.totalPrice') }}：</span>
                  <span class="value price">¥{{ order.totalPrice }}</span>
                </div>
              </div>
            </div>
            <div class="order-actions">
              <button class="btn btn-success" @click="completeOrder(order.id)">
                {{ t('dashboard.completeOrder') }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 历史订单 -->
      <div class="section">
        <h2 class="section-title">{{ t('dashboard.historyOrders') }}</h2>
        <div v-if="historyOrders.length === 0" class="empty">{{ t('dashboard.noHistoryOrders') }}</div>
        <div v-else class="orders-list">
          <div
            v-for="order in historyOrders"
            :key="order.id"
            class="order-card"
          >
            <div class="order-info">
              <div class="order-header">
                <span class="order-id">{{ t('order.orderId') }}：{{ order.id }}</span>
                <span class="order-status" :class="order.status">
                  {{ getStatusText(order.status) }}
                </span>
              </div>
              <div class="order-details">
                <div class="detail-item">
                  <span class="label">{{ t('dashboard.tourist') }}：</span>
                  <span class="value">{{ order.touristName || t('common.unknown') }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">{{ t('order.date') }}：</span>
                  <span class="value">{{ order.startDate }} {{ t('common.to') }} {{ order.endDate }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">{{ t('dashboard.participants') }}：</span>
                  <span class="value">{{ order.participants }}{{ t('common.people') }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">{{ t('dashboard.totalPrice') }}：</span>
                  <span class="value price">¥{{ order.totalPrice }}</span>
                </div>
                <div v-if="order.status === 'completed' && order.guideIncome" class="detail-item">
                  <span class="label">{{ t('order.guideIncome') }}：</span>
                  <span class="value income">¥{{ order.guideIncome }}</span>
                </div>
              </div>
            </div>
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

const { t } = useI18n()

const stats = ref({
  pendingOrders: 0,
  inProgressOrders: 0,
  totalIncome: 0,
  rating: 0
})

const pendingOrders = ref([])
const inProgressOrders = ref([])
const historyOrders = ref([])
const showReject = ref(false)
const rejectReason = ref('')
const currentRejectOrder = ref(null)

const loadDashboard = async () => {
  try {
    // 加载统计数据
    const statsRes = await request.get('/guides/stats')
    stats.value = statsRes.data

    // 加载待接订单
    const pendingRes = await request.get('/orders/list', {
      params: { status: 'pending' }
    })
    pendingOrders.value = pendingRes.data.list || []

    // 加载进行中订单
    const progressRes = await request.get('/orders/list', {
      params: { status: 'in_progress' }
    })
    inProgressOrders.value = progressRes.data.list || []

    // 加载历史订单（已完成和已取消）
    const historyRes = await request.get('/orders/list')
    const allOrders = historyRes.data.list || []
    historyOrders.value = allOrders.filter(order => 
      order.status === 'completed' || order.status === 'cancelled'
    ).slice(0, 10) // 只显示最近10条
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

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

const acceptOrder = async (orderId) => {
  try {
    await request.post(`/orders/${orderId}/accept`)
    alert(t('dashboard.accept') + t('common.success'))
    window.location.reload()
  } catch (error) {
    alert(error.response?.data?.error || t('dashboard.accept') + t('message.operationFailed'))
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
    window.location.reload()
  } catch (error) {
    alert(error.response?.data?.error || t('message.operationFailed'))
  }
}

const completeOrder = async (orderId) => {
  if (!confirm(t('dashboard.confirmComplete'))) return

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
    
    alert(t('dashboard.orderCompleted'))
    loadDashboard()
  } catch (error) {
    alert(error.response?.data?.error || t('message.operationFailed'))
  }
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped>
.guide-dashboard {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24px 0;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 32px;
}

.stat-card {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: #2563eb;
}

.section {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 16px 0;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.order-id {
  font-size: 14px;
  color: #999;
}

.order-date {
  font-size: 14px;
  color: #666;
}

.order-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  gap: 8px;
}

.label {
  color: #999;
}

.value {
  color: #333;
}

.value.price {
  color: #2563eb;
  font-weight: 600;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.order-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.order-status.completed {
  background: #d1fae5;
  color: #059669;
}

.order-status.cancelled {
  background: #fee2e2;
  color: #dc2626;
}

.value.income {
  color: #10b981;
  font-weight: 600;
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

.btn-danger {
  background: #ef4444;
  color: #fff;
}

.btn-success {
  background: #10b981;
  color: #fff;
}

.btn-secondary {
  background: #f3f4f6;
  color: #333;
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
}
</style>