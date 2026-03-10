<template>
  <div class="member-center">
    <!-- 会员信息卡片 -->
    <div class="member-card">
      <div class="member-header">
        <div class="member-avatar">
          <div class="avatar-circle" :class="memberInfo.memberLevel.toLowerCase()">
            {{ getMemberLevelIcon() }}
          </div>
        </div>
        <div class="member-info">
          <h2 class="member-name">{{ userInfo.nickname || '用户' }}</h2>
          <div class="member-level">
            <span class="level-badge" :class="memberInfo.memberLevel.toLowerCase()">
              {{ getMemberLevelName() }}
            </span>
            <span class="level-points">{{ memberInfo.totalPoints }} 积分</span>
          </div>
        </div>
      </div>
      
      <div class="member-stats">
        <div class="stat-item">
          <span class="stat-label">当前积分</span>
          <span class="stat-value">{{ memberInfo.points }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">历史总积分</span>
          <span class="stat-value">{{ memberInfo.totalPoints }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">信誉状态</span>
          <span class="stat-value" :class="memberInfo.hasGoodReputation ? 'good' : 'bad'">
            {{ memberInfo.hasGoodReputation ? '良好' : '待改善' }}
          </span>
        </div>
      </div>
    </div>

    <!-- 等级进度 -->
    <div class="level-progress">
      <h3>等级进度</h3>
      <div class="progress-bar">
        <div 
          class="progress-fill" 
          :style="{ width: getLevelProgress() + '%' }"
          :class="memberInfo.memberLevel.toLowerCase()"
        ></div>
      </div>
      <div class="progress-info">
        <span>当前: {{ getMemberLevelName() }}</span>
        <span>距离下一级: {{ getNextLevelPoints() - memberInfo.totalPoints }} 积分</span>
      </div>
    </div>

    <!-- 积分获得方式 -->
    <div class="points-ways">
      <h3>如何获得积分</h3>
      <div class="ways-grid">
        <div class="way-card">
          <div class="way-icon">💰</div>
          <h4>消费获得</h4>
          <p>购买定制旅程、预定酒店等服务，消费金额的50%转为积分</p>
          <div class="way-example">消费1000元 = 获得500积分</div>
        </div>
        
        <div class="way-card">
          <div class="way-icon">⭐</div>
          <h4>评价导游</h4>
          <p>旅程结束后对导游进行评价和反馈</p>
          <div class="way-points">+50积分</div>
        </div>
        
        <div class="way-card">
          <div class="way-icon">📝</div>
          <h4>社区分享</h4>
          <p>在内容社区分享您的旅行经历</p>
          <div class="way-points">+50积分</div>
        </div>
        
        <div class="way-card">
          <div class="way-icon">📱</div>
          <h4>社交媒体分享</h4>
          <p>在社交媒体分享旅行内容并标记"GuideMatch"</p>
          <div class="way-points">+50积分</div>
        </div>
      </div>
    </div>

    <!-- 会员权益 -->
    <div class="member-benefits">
      <h3>会员权益</h3>
      <div class="benefits-grid">
        <div class="benefit-card basic">
          <div class="benefit-header">
            <span class="benefit-icon">🎯</span>
            <h4>基础权益</h4>
          </div>
          <ul class="benefit-list">
            <li>专属客服服务</li>
            <li>紧急求助优先处理</li>
          </ul>
        </div>
        
        <div class="benefit-card" :class="memberInfo.memberLevel.toLowerCase()">
          <div class="benefit-header">
            <span class="benefit-icon">⚡</span>
            <h4>等级权益</h4>
          </div>
          <ul class="benefit-list">
            <li v-for="benefit in getCurrentLevelBenefits()" :key="benefit">
              {{ benefit }}
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 积分记录 -->
    <div class="points-history">
      <h3>积分记录</h3>
      <div class="history-list">
        <div 
          v-for="transaction in transactions" 
          :key="transaction.id"
          class="history-item"
        >
          <div class="transaction-info">
            <span class="transaction-type">{{ getTransactionTypeName(transaction.transactionType) }}</span>
            <span class="transaction-desc">{{ transaction.description }}</span>
          </div>
          <div class="transaction-points" :class="transaction.points > 0 ? 'positive' : 'negative'">
            {{ transaction.points > 0 ? '+' : '' }}{{ transaction.points }}
          </div>
          <div class="transaction-time">{{ formatTime(transaction.createdAt) }}</div>
        </div>
      </div>
      
      <div v-if="hasMoreTransactions" class="load-more">
        <button @click="loadMoreTransactions" :disabled="isLoading">
          {{ isLoading ? '加载中...' : '加载更多' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '@/api/request'

const memberInfo = ref({})
const transactions = ref([])
const userInfo = ref({})
const isLoading = ref(false)
const currentPage = ref(1)
const hasMoreTransactions = ref(false)

onMounted(() => {
  loadMemberInfo()
  loadTransactions()
})

const loadMemberInfo = async () => {
  try {
    const response = await request.get('/member/info')
    memberInfo.value = response.data || {}
    
    // 获取用户信息
    const token = localStorage.getItem('token')
    if (token) {
      // 解析token获取用户信息（简化版本）
      const payload = JSON.parse(atob(token.split('.')[1]))
      userInfo.value = payload || {}
    }
  } catch (error) {
    console.error('加载会员信息失败:', error)
  }
}

const loadTransactions = async () => {
  if (isLoading.value) return
  
  isLoading.value = true
  try {
    const response = await request.get(`/member/points-transactions?page=${currentPage.value}&size=10`)
    const newTransactions = response.data || []
    
    if (currentPage.value === 1) {
      transactions.value = newTransactions
    } else {
      transactions.value.push(...newTransactions)
    }
    
    hasMoreTransactions.value = newTransactions.length === 10
  } catch (error) {
    console.error('加载积分记录失败:', error)
  } finally {
    isLoading.value = false
  }
}

const loadMoreTransactions = () => {
  currentPage.value++
  loadTransactions()
}

const getMemberLevelName = () => {
  const levels = {
    'NORMAL': '普通会员',
    'V1_GOLD': 'V1黄金会员',
    'V2_DIAMOND': 'V2钻石会员',
    'V3_BLACK_DIAMOND': 'V3黑钻会员'
  }
  return levels[memberInfo.value.memberLevel] || '普通会员'
}

const getMemberLevelIcon = () => {
  const icons = {
    'NORMAL': '👤',
    'V1_GOLD': '🥇',
    'V2_DIAMOND': '💎',
    'V3_BLACK_DIAMOND': '⭐'
  }
  return icons[memberInfo.value.memberLevel] || '👤'
}

const getLevelProgress = () => {
  const currentPoints = memberInfo.value.totalPoints || 0
  const nextLevelPoints = getNextLevelPoints()
  
  if (!nextLevelPoints) return 100
  
  const prevLevelPoints = getPrevLevelPoints()
  const levelRange = nextLevelPoints - prevLevelPoints
  const progressInLevel = currentPoints - prevLevelPoints
  
  return Math.min(100, Math.max(0, (progressInLevel / levelRange) * 100))
}

const getNextLevelPoints = () => {
  const currentPoints = memberInfo.value.totalPoints || 0
  const currentLevel = memberInfo.value.memberLevel || 'NORMAL'
  
  if (currentPoints >= 30000) return null
  if (currentPoints >= 10000) return 30000
  if (currentPoints >= 1000) return 10000
  return 1000
}

const getPrevLevelPoints = () => {
  const currentLevel = memberInfo.value.memberLevel || 'NORMAL'
  
  switch (currentLevel) {
    case 'V1_GOLD': return 1000
    case 'V2_DIAMOND': return 10000
    case 'V3_BLACK_DIAMOND': return 30000
    default: return 0
  }
}

const getCurrentLevelBenefits = () => {
  const benefits = {
    'NORMAL': [],
    'V1_GOLD': ['匹配速度提升20%', '匹配精度提升15%'],
    'V2_DIAMOND': ['匹配速度提升50%', '匹配精度提升40%'],
    'V3_BLACK_DIAMOND': ['匹配速度提升100%', '匹配精度提升80%']
  }
  return benefits[memberInfo.value.memberLevel] || []
}

const getTransactionTypeName = (type) => {
  const types = {
    'CONSUMPTION': '消费获得',
    'REVIEW': '评价获得',
    'COMMUNITY_POST': '社区分享',
    'SOCIAL_SHARE': '社交媒体分享',
    'LEVEL_UPGRADE': '等级升级'
  }
  return types[type] || '其他'
}

const formatTime = (date) => {
  return new Date(date).toLocaleString('zh-CN')
}
</script>

<style scoped>
.member-center {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.member-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.member-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 25px;
}

.member-avatar {
  flex-shrink: 0;
}

.avatar-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.avatar-circle.v1_gold {
  border-color: #ffd700;
  box-shadow: 0 0 20px rgba(255, 215, 0, 0.3);
}

.avatar-circle.v2_diamond {
  border-color: #b9f2ff;
  box-shadow: 0 0 20px rgba(185, 242, 255, 0.3);
}

.avatar-circle.v3_black_diamond {
  border-color: #ff6bff;
  box-shadow: 0 0 20px rgba(255, 107, 255, 0.3);
}

.member-info h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
}

.member-level {
  display: flex;
  align-items: center;
  gap: 12px;
}

.level-badge {
  background: rgba(255, 255, 255, 0.2);
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.level-points {
  font-size: 14px;
  opacity: 0.9;
}

.member-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
}

.stat-label {
  display: block;
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 5px;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: bold;
}

.stat-value.good {
  color: #27ae60;
}

.stat-value.bad {
  color: #e74c3c;
}

.level-progress {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.level-progress h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
}

.progress-bar {
  height: 8px;
  background: #ecf0f1;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-fill {
  height: 100%;
  background: #3498db;
  transition: width 0.3s ease;
}

.progress-fill.v1_gold {
  background: linear-gradient(90deg, #ffd700, #ffed4e);
}

.progress-fill.v2_diamond {
  background: linear-gradient(90deg, #b9f2ff, #c084fc);
}

.progress-fill.v3_black_diamond {
  background: linear-gradient(90deg, #ff6bff, #c73ffc);
}

.progress-info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #666;
}

.points-ways, .member-benefits {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.points-ways h3, .member-benefits h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
}

.ways-grid, .benefits-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.way-card, .benefit-card {
  border: 1px solid #e1e8ed;
  border-radius: 8px;
  padding: 20px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.way-card:hover, .benefit-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.way-icon, .benefit-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.way-card h4, .benefit-card h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
}

.way-card p {
  margin: 0 0 10px 0;
  color: #666;
  line-height: 1.5;
}

.way-example {
  background: #f8f9fa;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 12px;
  color: #495057;
  border-left: 3px solid #3498db;
}

.way-points {
  background: #27ae60;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: 600;
  display: inline-block;
}

.benefit-card.basic {
  border-color: #3498db;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
}

.benefit-card.v1_gold {
  border-color: #ffd700;
  background: linear-gradient(135deg, #fff9e6, #fef3c7);
}

.benefit-card.v2_diamond {
  border-color: #b9f2ff;
  background: linear-gradient(135deg, #f0f4ff, #e8f4fd);
}

.benefit-card.v3_black_diamond {
  border-color: #ff6bff;
  background: linear-gradient(135deg, #ffefff, #f0f4ff);
}

.benefit-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.benefit-card h4 {
  margin: 0;
  color: #2c3e50;
}

.benefit-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.benefit-list li {
  padding: 5px 0;
  color: #495057;
  position: relative;
  padding-left: 20px;
}

.benefit-list li:before {
  content: "✓";
  position: absolute;
  left: 0;
  color: #27ae60;
  font-weight: bold;
}

.points-history {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.points-history h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
}

.history-list {
  max-height: 400px;
  overflow-y: auto;
}

.history-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 15px;
  padding: 15px;
  border-bottom: 1px solid #eee;
  align-items: center;
}

.transaction-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.transaction-type {
  font-weight: 600;
  color: #2c3e50;
}

.transaction-desc {
  font-size: 14px;
  color: #666;
}

.transaction-points {
  text-align: center;
  font-weight: bold;
  font-size: 16px;
}

.transaction-points.positive {
  color: #27ae60;
}

.transaction-points.negative {
  color: #e74c3c;
}

.transaction-time {
  text-align: right;
  font-size: 12px;
  color: #999;
}

.load-more {
  text-align: center;
  margin-top: 20px;
}

.load-more button {
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 10px 20px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.load-more button:hover:not(:disabled) {
  background: #2980b9;
}

.load-more button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .member-stats {
    grid-template-columns: 1fr;
  }
  
  .ways-grid, .benefits-grid {
    grid-template-columns: 1fr;
  }
  
  .history-item {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .transaction-info {
    text-align: left;
  }
  
  .transaction-points,
  .transaction-time {
    text-align: left;
  }
}
</style>
