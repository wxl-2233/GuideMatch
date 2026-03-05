<template>
  <div class="membership-page">
    <PageContainer>
      <div class="membership-container">
        <div class="membership-header">
          <h1 class="title">{{ t('membership.title') }}</h1>
          <p class="subtitle">{{ t('membership.subtitle') }}</p>
        </div>

        <div class="membership-plans">
          <div class="plan-card" :class="{ featured: plan.featured }" v-for="plan in plans" :key="plan.id">
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
                :class="{ selected: selectedPlan === plan.id }"
                @click="selectPlan(plan)"
                :disabled="selectedPlan === plan.id"
              >
                {{ selectedPlan === plan.id ? t('membership.selected') : t('membership.select') }}
              </button>
            </div>
          </div>
        </div>

        <div class="membership-benefits">
          <h2>{{ t('membership.benefits') }}</h2>
          <div class="benefits-grid">
            <div class="benefit-item" v-for="benefit in benefits" :key="benefit.title">
              <div class="benefit-icon">{{ benefit.icon }}</div>
              <div class="benefit-content">
                <h4>{{ benefit.title }}</h4>
                <p>{{ benefit.description }}</p>
              </div>
            </div>
          </div>
        </div>

        <div class="membership-action">
          <button 
            class="btn-upgrade" 
            @click="handleUpgrade"
            :disabled="!selectedPlan || processing"
          >
            {{ processing ? t('membership.processing') : t('membership.upgradeNow') }}
          </button>
        </div>
      </div>
    </PageContainer>
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
const processing = ref(false)

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

const benefits = ref([
  {
    icon: '🎯',
    title: '精准匹配',
    description: '根据您的需求智能推荐最合适的向导'
  },
  {
    icon: '💰',
    title: '优惠价格',
    description: '会员专享折扣，预订向导更划算'
  },
  {
    icon: '🚀',
    title: '优先服务',
    description: '会员优先获得向导响应，享受更快服务'
  },
  {
    icon: '🏆',
    title: '专属标识',
    description: '在社区中显示会员专属标识，彰显身份'
  },
  {
    icon: '🎁',
    title: '生日福利',
    description: '会员生日当月享受特别优惠和礼物'
  },
  {
    icon: '📞',
    title: '专属客服',
    description: '24/7在线客服，随时为您解答疑问'
  }
])

const selectPlan = (plan) => {
  selectedPlan.value = plan.id
}

const handleUpgrade = async () => {
  if (!selectedPlan.value) {
    alert(t('membership.selectPlanFirst'))
    return
  }

  processing.value = true
  
  try {
    const response = await request.post('/membership/upgrade', {
      planId: selectedPlan.value
    })
    
    if (response.data.success) {
      alert(t('membership.upgradeSuccess'))
      // 更新用户信息
      const userStr = localStorage.getItem('user')
      if (userStr) {
        const user = JSON.parse(userStr)
        user.membershipPlan = selectedPlan.value
        localStorage.setItem('user', JSON.stringify(user))
      }
      router.push('/profile')
    } else {
      alert(response.data.message || t('membership.upgradeFailed'))
    }
  } catch (error) {
    console.error('升级会员失败:', error)
    alert(t('membership.upgradeFailed'))
  } finally {
    processing.value = false
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
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(139, 92, 246, 0.15);
}

.plan-card.featured {
  border-color: var(--primary);
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.2);
}

.plan-header {
  text-align: center;
  margin-bottom: 24px;
  position: relative;
}

.plan-name {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-main);
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
  color: var(--text-muted);
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
  color: var(--text-main);
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

.membership-benefits {
  margin-bottom: 48px;
}

.membership-benefits h2 {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-main);
  text-align: center;
  margin-bottom: 32px;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.benefit-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 24px;
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
}

.benefit-icon {
  font-size: 32px;
  line-height: 1;
}

.benefit-content h4 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 8px;
}

.benefit-content p {
  font-size: 14px;
  color: var(--text-muted);
  margin: 0;
  line-height: 1.5;
}

.membership-action {
  text-align: center;
}

.btn-upgrade {
  padding: 16px 48px;
  border: none;
  border-radius: 12px;
  background: var(--primary);
  color: white;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-upgrade:hover:not(:disabled) {
  background: var(--primary);
  opacity: 0.9;
  transform: translateY(-2px);
}

.btn-upgrade:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .membership-plans {
    grid-template-columns: 1fr;
  }
  
  .benefits-grid {
    grid-template-columns: 1fr;
  }
  
  .benefit-item {
    flex-direction: column;
    text-align: center;
  }
}
</style>
