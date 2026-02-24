<template>
  <div class="income-page">
    <PageContainer>
      <h1 class="page-title">{{ t('income.title') }}</h1>

      <div class="income-overview">
        <div class="overview-card">
          <div class="card-label">{{ t('income.totalIncome') }}</div>
          <div class="card-value">¥{{ incomeStats.totalIncome }}</div>
        </div>
        <div class="overview-card">
          <div class="card-label">{{ t('income.monthlyIncome') }}</div>
          <div class="card-value">¥{{ incomeStats.monthlyIncome }}</div>
        </div>
        <div class="overview-card">
          <div class="card-label">{{ t('income.availableBalance') }}</div>
          <div class="card-value">¥{{ incomeStats.availableBalance }}</div>
        </div>
        <div class="overview-card">
          <div class="card-label">{{ t('income.completedOrders') }}</div>
          <div class="card-value">{{ incomeStats.completedOrders }}</div>
        </div>
      </div>

      <div class="filter-section">
        <div class="filter-group">
          <label>{{ t('income.dateRange') }}：</label>
          <input
            v-model="filters.startDate"
            type="date"
            class="date-input"
          />
          <span>{{ t('income.to') }}</span>
          <input
            v-model="filters.endDate"
            type="date"
            class="date-input"
          />
          <button class="btn btn-primary" @click="loadIncomeData">{{ t('income.query') }}</button>
        </div>
      </div>

      <div class="income-details">
        <h2 class="section-title">{{ t('income.details') }}</h2>
        <div v-if="incomeList.length === 0" class="empty">{{ t('income.noRecords') }}</div>
        <table v-else class="income-table">
          <thead>
            <tr>
              <th>{{ t('income.orderId') }}</th>
              <th>{{ t('income.date') }}</th>
              <th>{{ t('income.orderAmount') }}</th>
              <th>{{ t('income.commission') }}</th>
              <th>{{ t('income.actualIncome') }}</th>
              <th>{{ t('income.status') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in incomeList" :key="item.id">
              <td>{{ item.orderId || '-' }}</td>
              <td>{{ item.date || '-' }}</td>
              <td>¥{{ item.totalPrice || 0 }}</td>
              <td>¥{{ item.commissionAmount || 0 }}</td>
              <td class="income-amount">¥{{ item.guideIncome || 0 }}</td>
              <td>
                <span class="status-badge" :class="item.status">
                  {{ getStatusText(item.status) }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="withdrawals-section">
        <div class="section-header">
          <h2 class="section-title">{{ t('income.withdrawals') }}</h2>
          <button class="btn btn-primary" @click="showWithdrawModal = true">
            {{ t('income.applyWithdrawal') }}
          </button>
        </div>
        <div v-if="withdrawals.length === 0" class="empty">{{ t('income.noWithdrawals') }}</div>
        <div v-else class="withdrawals-list">
          <div
            v-for="withdrawal in withdrawals"
            :key="withdrawal.id"
            class="withdrawal-card"
          >
            <div class="withdrawal-info">
              <div class="info-row">
                <span class="label">{{ t('income.withdrawalAmount') }}：</span>
                <span class="value">¥{{ withdrawal.amount }}</span>
              </div>
              <div class="info-row">
                <span class="label">{{ t('income.bankName') }}：</span>
                <span class="value">{{ withdrawal.bankName || '-' }}</span>
              </div>
              <div class="info-row">
                <span class="label">{{ t('income.bankAccount') }}：</span>
                <span class="value">{{ withdrawal.bankAccount || '-' }}</span>
              </div>
              <div class="info-row">
                <span class="label">{{ t('income.accountHolder') }}：</span>
                <span class="value">{{ withdrawal.accountHolder || '-' }}</span>
              </div>
              <div class="info-row">
                <span class="label">{{ t('income.applyTime') }}：</span>
                <span class="value">{{ formatDateTime(withdrawal.createTime) }}</span>
              </div>
              <div class="info-row" v-if="withdrawal.processTime">
                <span class="label">{{ t('income.processTime') }}：</span>
                <span class="value">{{ formatDateTime(withdrawal.processTime) }}</span>
              </div>
              <div class="info-row">
                <span class="label">{{ t('income.withdrawalStatus') }}：</span>
                <span class="status-badge" :class="withdrawal.status">
                  {{ getWithdrawalStatusText(withdrawal.status) }}
                </span>
              </div>
              <div class="info-row" v-if="withdrawal.rejectionReason">
                <span class="label">{{ t('income.rejectionReason') }}：</span>
                <span class="value reject-reason">{{ withdrawal.rejectionReason }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </PageContainer>

    <div v-if="showWithdrawModal" class="modal-overlay" @click.self="showWithdrawModal = false">
      <div class="modal-content">
        <h3>{{ t('income.applyWithdrawal') }}</h3>
        <div class="withdraw-form">
          <div class="form-group">
            <label>{{ t('income.withdrawalAmount') }}：</label>
            <input
              v-model.number="withdrawForm.amount"
              type="number"
              class="input"
              :max="incomeStats.availableBalance"
            />
            <span class="hint">{{ t('income.availableAmount') }}：¥{{ incomeStats.availableBalance }}</span>
          </div>
          <div class="form-group">
            <label>{{ t('income.bankName') }}：</label>
            <input v-model="withdrawForm.bankName" type="text" class="input" />
          </div>
          <div class="form-group">
            <label>{{ t('income.bankAccount') }}：</label>
            <input v-model="withdrawForm.bankAccount" type="text" class="input" />
          </div>
          <div class="form-group">
            <label>{{ t('income.accountHolder') }}：</label>
            <input v-model="withdrawForm.accountHolder" type="text" class="input" />
          </div>
          <div class="modal-actions">
            <button class="btn btn-secondary" @click="showWithdrawModal = false">{{ t('common.cancel') }}</button>
            <button class="btn btn-primary" @click="submitWithdraw">{{ t('common.submit') }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import PageContainer from '@/components/layout/PageContainer.vue'
import { useI18n } from '@/composables/useI18n'

const { t } = useI18n()

const incomeStats = ref({
  totalIncome: 0,
  monthlyIncome: 0,
  availableBalance: 0,
  completedOrders: 0
})

const incomeList = ref([])
const withdrawals = ref([])
const showWithdrawModal = ref(false)

const filters = ref({
  startDate: '',
  endDate: ''
})

const withdrawForm = ref({
  amount: 0,
  bankName: '',
  bankAccount: '',
  accountHolder: ''
})

const loadIncomeData = async () => {
  try {
    const params = {}
    if (filters.value.startDate) params.startDate = filters.value.startDate
    if (filters.value.endDate) params.endDate = filters.value.endDate

    const response = await request.get('/guides/income', { params })
    
    // 如果指定了时间范围，只显示该范围内的数据
    let list = response.data.list || []
    
    // 前端过滤（如果后端没有过滤）
    if (filters.value.startDate || filters.value.endDate) {
      list = list.filter(item => {
        const itemDate = item.date || item.createTime
        if (!itemDate) return false
        
        const date = new Date(itemDate).toISOString().split('T')[0]
        if (filters.value.startDate && date < filters.value.startDate) return false
        if (filters.value.endDate && date > filters.value.endDate) return false
        return true
      })
    }
    
    incomeStats.value = response.data.stats || incomeStats.value
    incomeList.value = list.map(item => ({
      ...item,
      orderId: item.orderId || item.id || '-',
      date: item.date || item.createTime || '-'
    }))
    
    if (filters.value.startDate || filters.value.endDate) {
      alert(`已筛选 ${incomeList.value.length} 条记录`)
    }
  } catch (error) {
    console.error('加载收入数据失败:', error)
    alert('加载收入数据失败: ' + (error.response?.data?.error || error.message))
  }
}

const loadWithdrawals = async () => {
  try {
    const response = await request.get('/withdrawals')
    withdrawals.value = response.data.list || []
  } catch (error) {
    console.error('加载提现记录失败:', error)
  }
}

const submitWithdraw = async () => {
  if (!withdrawForm.value.amount || withdrawForm.value.amount <= 0) {
    alert(t('income.enterAmount'))
    return
  }

  if (withdrawForm.value.amount > incomeStats.value.availableBalance) {
    alert(t('income.amountExceeded'))
    return
  }

  try {
    await request.post('/withdrawals', withdrawForm.value)
    alert(t('income.withdrawalSubmitted'))
    showWithdrawModal.value = false
    window.location.reload()
  } catch (error) {
    alert(error.response?.data?.error || t('income.withdrawalFailed'))
  }
}

const getStatusText = (status) => {
  return t(`order.status.${status}`) || status
}

const getWithdrawalStatusText = (status) => {
  return t(`order.status.${status}`) || status
}

const getWithdrawalStatusClass = (status) => {
  const map = {
    pending: 'pending',
    approved: 'approved',
    rejected: 'rejected',
    completed: 'completed'
  }
  return map[status] || ''
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '-'
  try {
    const date = new Date(dateTimeStr)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (e) {
    return dateTimeStr
  }
}

onMounted(() => {
  loadIncomeData()
  loadWithdrawals()
})
</script>

<style scoped>
.income-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24px 0;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
}

.income-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.overview-card {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-label {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.card-value {
  font-size: 32px;
  font-weight: 600;
  color: #2563eb;
}

.filter-section {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.date-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.income-details,
.withdrawals-section {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.income-table {
  width: 100%;
  border-collapse: collapse;
}

.income-table th,
.income-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.income-table th {
  background: #f9fafb;
  font-weight: 600;
}

.income-amount {
  color: #10b981;
  font-weight: 600;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.status-badge.completed {
  background: #d1fae5;
  color: #059669;
}

.status-badge.pending {
  background: #fef3c7;
  color: #d97706;
}

.withdrawals-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.withdrawal-card {
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  padding: 16px;
}

.info-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.info-row .label {
  font-weight: 600;
  color: #666;
  min-width: 100px;
}

.info-row .value {
  color: #333;
  flex: 1;
}

.reject-reason {
  color: #ef4444;
  font-style: italic;
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

.withdraw-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.hint {
  font-size: 12px;
  color: #999;
}

.modal-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 16px;
}

.btn {
  padding: 10px 20px;
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
</style>
