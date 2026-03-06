<template>
  <div class="membership-page">
    <PageContainer>
      <div class="membership-container">
        <div class="membership-header">
          <h1 class="title">{{ t('membership.title') }}</h1>
          <p class="subtitle">{{ t('membership.subtitle') }}</p>
        </div>

        <div class="membership-plans">
          <div class="plan-card" :class="{ featured: plan.featured, selected: selectedPlan === plan.id }" v-for="plan in plans" :key="plan.id">
            <div class="plan-header">
              <h3 class="plan-name">{{ plan.name }}</h3>
              <div class="plan-price">
                <span class="price">¥{{ plan.price }}</span>
                <span class="period">/{{ plan.period }}</span>
              </div>
              <div v-if="plan.featured" class="featured-badge">{{ t('membership.recommended') }}</div>
            </div>
            
            <div class="plan-features">
              <div class="feature" v-for="feature in plan.features" :key="feature">
                <span class="feature-icon">{{ feature.icon }}</span>
                <span class="feature-text">{{ feature.text }}</span>
              </div>
            </div>
            
            <div class="plan-footer">
              <button 
                class="btn-select" 
                :class="{ selected: selectedPlan === plan.id, owned: selectedPlan === plan.id }"
                @click="selectPlan(plan)"
                :disabled="selectedPlan === plan.id"
              >
                {{ selectedPlan === plan.id ? t('membership.owned') : t('membership.select') }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </PageContainer>
    
    <!-- 支付方式选择弹窗 -->
    <div v-if="showPaymentModal" class="payment-modal-overlay" @click="showPaymentModal = false">
      <div class="payment-modal" @click.stop>
        <div class="payment-header">
          <h3>{{ t('membership.selectPaymentMethod') }}</h3>
          <button class="close-btn" @click="showPaymentModal = false">×</button>
        </div>
        <div class="payment-content">
          <div class="payment-methods">
            <div class="payment-method" @click="confirmPayment">
              <div class="payment-icon">💳</div>
              <div class="payment-info">
                <h4>{{ t('membership.creditCard') }}</h4>
                <p>{{ t('membership.creditCardDesc') }}</p>
              </div>
            </div>
            <div class="payment-method" @click="confirmPayment">
              <div class="payment-icon">📱</div>
              <div class="payment-info">
                <h4>{{ t('membership.alipay') }}</h4>
                <p>{{ t('membership.alipayDesc') }}</p>
              </div>
            </div>
            <div class="payment-method" @click="confirmPayment">
              <div class="payment-icon">💰</div>
              <div class="payment-info">
                <h4>{{ t('membership.wechatPay') }}</h4>
                <p>{{ t('membership.wechatPayDesc') }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from '@/composables/useI18n'
import request from '@/api/request'
import PageContainer from '@/components/layout/PageContainer.vue'

const { t } = useI18n()
const router = useRouter()

const selectedPlan = ref(null)
const showPaymentModal = ref(false)
const currentPlan = ref(null)

const plans = ref([
  {
    id: 'basic',
    name: '基础会员',
    price: 29,
    period: '月',
    featured: false,
    features: [
      { icon: '✓', text: '浏览所有向导' },
      { icon: '✓', text: '发布社区帖子' },
      { icon: '✓', text: '收藏向导' },
      { icon: '✓', text: '基础客服支持' }
    ]
  },
  {
    id: 'premium',
    name: '高级会员',
    price: 58,
    period: '月',
    featured: true,
    features: [
      { icon: '✓', text: '浏览所有向导' },
      { icon: '✓', text: '发布社区帖子' },
      { icon: '✓', text: '收藏向导' },
      { icon: '✓', text: '优先客服支持' },
      { icon: '✓', text: '专属标识' },
      { icon: '✓', text: '月度优惠券' }
    ]
  },
  {
    id: 'vip',
    name: 'VIP会员',
    price: 98,
    period: '月',
    featured: false,
    features: [
      { icon: '✓', text: '浏览所有向导' },
      { icon: '✓', text: '发布社区帖子' },
      { icon: '✓', text: '收藏向导' },
      { icon: '✓', text: '24/7专属客服' },
      { icon: '✓', text: 'VIP专属标识' },
      { icon: '✓', text: '月度优惠券' },
      { icon: '✓', text: '免费向导推荐' }
    ]
  }
])

const selectPlan = (plan) => {
  if (selectedPlan.value === plan.id) {
    // 如果已经是当前计划，不进行任何操作
    return
  }
  
  // 立即设置选中状态
  selectedPlan.value = plan.id
  
  // 显示支付方式选择弹窗
  showPaymentModal.value = true
  currentPlan.value = plan
}

const confirmPayment = () => {
  selectedPlan.value = currentPlan.value.id
  showPaymentModal.value = false
  
  // 这里可以集成实际的支付逻辑
  alert(`${t('membership.paymentSuccess')} - ${currentPlan.value.name}`)
  
  // 更新用户信息
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const user = JSON.parse(userStr)
    user.membershipPlan = currentPlan.value.id
    localStorage.setItem('user', JSON.stringify(user))
  }
}

onMounted(() => {
  // 检查用户当前会员状态
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      selectedPlan.value = user.membershipPlan || null
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
})
</script>

<style scoped>
.membership-page {
  min-height: 100vh;
  background: var(--bg-main);
  padding: 24px 0;
}

.membership-container {
  max-width: 1200px;
  margin: 0 auto;
}

.membership-header {
  text-align: center;
  margin-bottom: 48px;
}

.title {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-main);
  margin-bottom: 16px;
}

.subtitle {
  font-size: 18px;
  color: var(--text-muted);
  margin-bottom: 0;
}

.membership-plans {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 48px;
}

.plan-card {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 24px;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
  position: relative;
}

.plan-card:hover {
  border-color: var(--primary) !important;
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(139, 92, 246, 0.15);
}

.plan-card.selected {
  border-color: var(--primary) !important;
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.2);
}

.plan-card.featured {
  border: 1px solid var(--border-color);
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.2);
}

.plan-card.featured:hover {
  border-color: var(--primary) !important;
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(139, 92, 246, 0.15);
}

.plan-header {
  text-align: center;
  margin-bottom: 24px;
  position: relative;
}

.plan-name {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.plan-price {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 4px;
}

.price {
  font-size: 32px;
  font-weight: 700;
  color: var(--primary);
}

.period {
  font-size: 16px;
  color: #6b7280;
}

.featured-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: var(--primary);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.plan-features {
  margin-bottom: 24px;
}

.feature {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.feature-icon {
  color: var(--primary);
  font-size: 16px;
  font-weight: 600;
}

.feature-text {
  color: #1f2937;
  font-size: 14px;
}

.plan-footer {
  text-align: center;
}

.btn-select {
  width: 100%;
  padding: 12px 24px;
  border: 2px solid var(--primary);
  border-radius: 8px;
  background: transparent;
  color: var(--primary);
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-select:hover {
  background: var(--primary);
  color: white;
}

.btn-select.selected {
  background: var(--primary);
  color: white;
}

.btn-select:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-select.owned {
  background: var(--primary);
  color: white;
  cursor: default;
}

/* 支付弹窗样式 */
.payment-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.payment-modal {
  background: var(--card-bg);
  border-radius: 16px;
  padding: 32px;
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.payment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.payment-header h3 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-main);
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: var(--text-muted);
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: var(--header-pill-bg);
  color: var(--text-main);
}

.payment-content {
  margin-bottom: 24px;
}

.payment-methods {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

.payment-method {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border: 2px solid var(--border-color);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.payment-method:hover {
  border-color: var(--primary);
  background: var(--header-pill-bg);
}

.payment-icon {
  font-size: 32px;
  line-height: 1;
}

.payment-info h4 {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.payment-info p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .payment-methods {
    grid-template-columns: 1fr;
  }
}
</style>
