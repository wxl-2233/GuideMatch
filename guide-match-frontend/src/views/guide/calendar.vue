<template>
  <div class="calendar-page">
    <PageContainer>
      <h1 class="page-title">日历管理</h1>

      <div class="calendar-controls">
        <div class="control-group">
          <label>选择月份：</label>
          <input
            v-model="selectedMonth"
            type="month"
            class="month-input"
            @change="loadCalendar"
          />
        </div>
        <div class="status-legend">
          <div class="legend-item">
            <span class="legend-color available"></span>
            <span>可接单</span>
          </div>
          <div class="legend-item">
            <span class="legend-color booked"></span>
            <span>已约满</span>
          </div>
          <div class="legend-item">
            <span class="legend-color unavailable"></span>
            <span>请假</span>
          </div>
        </div>
      </div>

      <div class="calendar-grid">
        <div
          v-for="day in calendarDays"
          :key="day.date"
          class="calendar-day"
          :class="[day.status, { 'is-past': day.isPast, 'is-selected': selectedDays.includes(day.date) }]"
          @click="toggleDaySelection(day)"
          @dblclick="toggleDayStatus(day)"
        >
          <div class="day-header">
            <div class="day-number">{{ day.day }}</div>
            <div class="day-price" v-if="day.status === 'booked'">¥1200</div>
          </div>
          <div class="day-status">{{ getStatusText(day.status) }}</div>
          <div class="day-info" v-if="day.status === 'available'">
            <span class="info-text">可接单</span>
          </div>
        </div>
      </div>

      <div class="bulk-actions">
        <div class="actions-header">
          <h3>批量操作</h3>
          <p class="hint-text">提示：单击日期进行选择，双击日期切换状态</p>
        </div>
        <div class="action-buttons">
          <button class="btn btn-primary" @click="setBulkStatus('available')">
            批量设为可接单
          </button>
          <button class="btn btn-secondary" @click="setBulkStatus('unavailable')">
            批量设为请假
          </button>
          <button class="btn btn-danger" @click="clearSelected">
            清空选择 (已选 {{ selectedDays.length }} 个)
          </button>
        </div>
      </div>
    </PageContainer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '@/api/request'
import PageContainer from '@/components/layout/PageContainer.vue'

const selectedMonth = ref(new Date().toISOString().slice(0, 7))
const calendarDays = ref([])
const selectedDays = ref([])

const loadCalendar = async () => {
  try {
    const [year, month] = selectedMonth.value.split('-').map(Number)
    const daysInMonth = new Date(year, month, 0).getDate()
    
    // 获取该月的日历数据
    const response = await request.get('/guides/calendar', {
      params: {
        year,
        month
      }
    })

    const existingDates = (response.data.list || []).reduce((acc, item) => {
      acc[item.date] = item.status
      return acc
    }, {})

    // 生成日历数组
    calendarDays.value = []
    for (let day = 1; day <= daysInMonth; day++) {
      const date = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
      const dateObj = new Date(year, month - 1, day)
      const isPast = dateObj < new Date()
      
      calendarDays.value.push({
        date,
        day,
        status: existingDates[date] || (isPast ? 'unavailable' : 'available'),
        isPast
      })
    }
  } catch (error) {
    console.error('加载日历失败:', error)
  }
}

const toggleDayStatus = async (day) => {
  if (day.isPast) {
    alert('不能修改过去的日期')
    return
  }

  const statuses = ['available', 'booked', 'unavailable']
  const currentIndex = statuses.indexOf(day.status)
  const nextStatus = statuses[(currentIndex + 1) % statuses.length]

  try {
    await request.post('/guides/calendar', {
      date: day.date,
      status: nextStatus
    })
    // 只更新当前日期状态，不刷新整个页面，保持当前月份选择
    day.status = nextStatus
  } catch (error) {
    alert('更新失败: ' + (error.response?.data?.error || error.message))
  }
}

const setBulkStatus = async (status) => {
  if (selectedDays.value.length === 0) {
    alert('请先选择日期（单击日期进行选择，双击切换状态）')
    return
  }

  try {
    for (const date of selectedDays.value) {
      await request.post('/guides/calendar', {
        date,
        status
      })
      // 更新本地状态
      const day = calendarDays.value.find(d => d.date === date)
      if (day) {
        day.status = status
      }
    }
    alert(`批量设置成功，已更新 ${selectedDays.value.length} 个日期`)
    selectedDays.value = []
  } catch (error) {
    alert('批量设置失败: ' + (error.response?.data?.error || error.message))
  }
}

const toggleDaySelection = (day) => {
  if (day.isPast) return
  
  const index = selectedDays.value.indexOf(day.date)
  if (index > -1) {
    selectedDays.value.splice(index, 1)
  } else {
    selectedDays.value.push(day.date)
  }
}

const clearSelected = () => {
  selectedDays.value = []
}

const getStatusText = (status) => {
  const map = {
    available: '可接单',
    booked: '已约满',
    unavailable: '请假'
  }
  return map[status] || ''
}

onMounted(() => {
  loadCalendar()
})
</script>

<style scoped>
.calendar-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24px 0;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
}

.calendar-controls {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.control-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.month-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.status-legend {
  display: flex;
  gap: 24px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-color {
  width: 20px;
  height: 20px;
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

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.calendar-day {
  aspect-ratio: 1;
  min-height: 80px;
  border: 2px solid #e5e5e5;
  border-radius: 6px;
  padding: 8px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  font-size: 13px;
}

.calendar-day:hover {
  border-color: #2563eb;
  transform: translateY(-2px);
}

.calendar-day.available {
  background: #d1fae5;
  border-color: #10b981;
}

.calendar-day.booked {
  background: #fef3c7;
  border-color: #f59e0b;
}

.calendar-day.unavailable {
  background: #fee2e2;
  border-color: #ef4444;
  opacity: 0.6;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 4px;
}

.day-number {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.day-price {
  font-size: 11px;
  font-weight: 600;
  color: #d97706;
  background: rgba(245, 158, 11, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
}

.day-status {
  font-size: 11px;
  color: #666;
  margin-top: auto;
  font-weight: 500;
}

.day-info {
  margin-top: 4px;
  font-size: 10px;
  color: #059669;
}

.info-text {
  background: rgba(16, 185, 129, 0.1);
  padding: 2px 4px;
  border-radius: 3px;
}

.calendar-day.is-selected {
  border-color: #2563eb;
  border-width: 3px;
  background: rgba(37, 99, 235, 0.05);
}

.calendar-day.is-past {
  opacity: 0.5;
  cursor: not-allowed;
}

.bulk-actions {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  margin-top: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.actions-header {
  margin-bottom: 16px;
}

.actions-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
}

.hint-text {
  margin: 0;
  font-size: 13px;
  color: #666;
}

.action-buttons {
  display: flex;
  gap: 12px;
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
  background: #6b7280;
  color: #fff;
}

.btn-danger {
  background: #ef4444;
  color: #fff;
}
</style>
