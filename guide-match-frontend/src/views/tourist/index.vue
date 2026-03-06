<template>
  <div class="tourist-page">
    <PageContainer>
      <!-- 搜索和筛选区 -->
      <div class="filter-section">
        <div class="search-bar">
          <input
            v-model="searchKeyword"
            type="text"
            :placeholder="t('guide.search')"
            class="search-input"
            @input="handleSearch"
          />
        </div>

        <div class="filters">
          <div class="filter-group">
            <label>
              <input
                v-model="filters.showFavoritesOnly"
                type="checkbox"
                class="checkbox-input"
                @change="loadGuides"
              />
              只看收藏
            </label>
          </div>
          <div class="filter-group">
            <label>{{ t('guide.city') }}：</label>
            <select v-model="filters.city" class="filter-select" @change="loadGuides">
              <option value="">{{ t('guide.all') }}</option>
              <option value="北京">北京</option>
              <option value="上海">上海</option>
              <option value="深圳">深圳</option>
              <option value="广州">广州</option>
              <option value="杭州">杭州</option>
            </select>
          </div>

          <div class="filter-group">
            <label>{{ t('guide.language') }}：</label>
            <select v-model="filters.language" class="filter-select" @change="loadGuides">
              <option value="">{{ t('guide.all') }}</option>
              <option value="中文">中文</option>
              <option value="英文">英文</option>
              <option value="日文">日文</option>
              <option value="韩文">韩文</option>
            </select>
          </div>

          <div class="filter-group">
            <label>{{ t('guide.price') }}：</label>
            <input
              v-model.number="filters.minPrice"
              type="number"
              :placeholder="t('guide.minPrice')"
              class="price-input"
              @input="loadGuides"
            />
            <span>-</span>
            <input
              v-model.number="filters.maxPrice"
              type="number"
              :placeholder="t('guide.maxPrice')"
              class="price-input"
              @input="loadGuides"
            />
          </div>

          <div class="filter-group">
            <label>标签：</label>
            <select v-model="filters.tag" class="filter-select" @change="loadGuides">
              <option value="">{{ t('guide.all') }}</option>
              <option value="留学">留学</option>
              <option value="考研">考研</option>
              <option value="计算机">计算机</option>
              <option value="数学">数学</option>
              <option value="经验">经验</option>
              <option value="旅游">旅游</option>
              <option value="美食">美食</option>
              <option value="文化">文化</option>
            </select>
          </div>

          <div class="filter-group">
            <label>{{ t('guide.sort') }}：</label>
            <select v-model="filters.sortBy" class="filter-select" @change="loadGuides">
              <option value="rating">{{ t('guide.rating') }}</option>
              <option value="price">{{ t('guide.priceSort') }}</option>
              <option value="orders">{{ t('guide.orders') }}</option>
              <option value="available_date">最近可约时间</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 向导列表 -->
      <div class="guides-section">
        <div v-if="loading" class="loading">{{ t('common.loading') }}</div>
        <div v-else-if="guides.length === 0" class="empty">{{ t('guide.noGuides') }}</div>
        <div v-else class="guides-grid">
          <div
            v-for="guide in guides"
            :key="guide.id"
            class="guide-card"
            @click="viewGuide(guide.id)"
          >
            <div class="guide-header">
              <img
                :src="getAvatarUrl(guide.avatarPath, guide.avatarStatus)"
                :alt="guide.name"
                class="guide-avatar"
              />
              <div class="guide-status" :class="{ online: guide.isOnline, offline: !guide.isOnline }">
                {{ guide.isOnline ? '在线' : '离线' }}
              </div>
            </div>
            
            <div class="guide-info">
              <h3 class="guide-name">{{ guide.name }}</h3>
              <div class="guide-title">{{ guide.title || '专业向导' }}</div>
              
              <div class="guide-rating">
                <div class="stars">
                  <span v-for="i in 5" :key="i" class="star">
                    {{ i <= Math.floor(guide.rating) ? '⭐' : '☆' }}
                  </span>
                </div>
                <span class="rating-text">{{ formatRating(guide.rating) }}</span>
                <span class="review-count">({{ guide.reviewCount || 0 }}条评价)</span>
              </div>
              
              <div class="guide-location">
                📍 {{ guide.city || '未知城市' }}
              </div>
              
              <div class="guide-tags">
                <span v-for="tag in (guide.tags || [])" :key="tag" class="tag">
                  {{ tag }}
                </span>
              </div>
              
              <div class="guide-description">
                {{ guide.description || '暂无介绍' }}
              </div>
              
              <div class="guide-stats">
                <div class="stat-item">
                  <span class="stat-label">服务次数</span>
                  <span class="stat-value">{{ guide.serviceCount || 0 }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">响应率</span>
                  <span class="stat-value">{{ guide.responseRate || 100 }}%</span>
                </div>
              </div>
              
              <div class="guide-price">
                <span class="price">¥{{ guide.price || 0 }}</span>
                <span class="period">/{{ guide.period || '天' }}</span>
                <span class="price-note" v-if="guide.priceNote">{{ guide.priceNote }}</span>
              </div>
              
              <div class="guide-actions">
                <button class="btn-primary" @click.stop="viewGuide(guide.id)">
                  {{ t('guide.viewDetails') }}
                </button>
                <button
                  class="btn-favorite"
                  :class="{ active: isFavorite(guide.id) }"
                  @click.stop="toggleFavorite(guide.id)"
                  :disabled="!isLogin"
                >
                  {{ isFavorite(guide.id) ? '❤️' : '🤍' }}
                </button>
                <button class="btn-contact" @click.stop="contactGuide(guide.id)">
                  💬 联系
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </PageContainer>
    
    <!-- AI悬浮球按钮 -->
    <AIFloatingButton />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/request'
import PageContainer from '@/components/layout/PageContainer.vue'
import AIFloatingButton from '@/components/AIFloatingButton.vue'
import { useI18n } from '@/composables/useI18n'
import { getAvatarUrl } from '@/utils/avatar'

const { t } = useI18n()

const router = useRouter()

const guides = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const favorites = ref([])

// 检查登录状态
const isLogin = computed(() => {
  return !!localStorage.getItem('token')
})

const filters = ref({
  showFavoritesOnly: false,
  city: '',
  language: '',
  minPrice: null,
  maxPrice: null,
  tag: '',
  sortBy: 'rating'
})

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

// 联系向导
const contactGuide = (guideId) => {
  if (!isLogin.value) {
    alert('请先登录后联系向导')
    return
  }
  // 这里可以跳转到聊天页面或者打开联系对话框
  console.log('联系向导:', guideId)
}

// 查看向导详情
const viewGuide = (guideId) => {
  router.push(`/guide/${guideId}`)
}

// 加载向导列表
const loadGuides = async () => {
  loading.value = true
  try {
    const params = {
      city: filters.value.city || undefined,
      language: filters.value.language || undefined,
      minPrice: filters.value.minPrice || undefined,
      maxPrice: filters.value.maxPrice || undefined,
      tag: filters.value.tag || undefined,
      sortBy: filters.value.sortBy
    }

    const response = await request.get('/guides/list', { params })
    let guidesList = (response.data.list || []).map(guide => ({
      ...guide,
      tagsArray: guide.tags ? JSON.parse(guide.tags) : [],
      citiesArray: guide.cities ? JSON.parse(guide.cities) : [],
      languagesArray: guide.languages ? JSON.parse(guide.languages) : []
    }))

    // 如果选择了"只看收藏"，则过滤出收藏的向导
    if (filters.value.showFavoritesOnly) {
      guidesList = guidesList.filter(guide => favorites.value.includes(guide.id))
    }

    guides.value = guidesList
  } catch (error) {
    console.error('加载向导列表失败:', error)
    guides.value = []
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  loadGuides()
}

// 收藏/取消收藏
const toggleFavorite = async (guideId) => {
  try {
    const isFav = favorites.value.includes(guideId)
    if (isFav) {
      await request.delete(`/favorites/${guideId}`)
      // 立即从收藏列表中移除
      favorites.value = favorites.value.filter(id => id !== guideId)
    } else {
      await request.post('/favorites', { guideId })
      // 立即添加到收藏列表
      favorites.value.push(guideId)
    }
    
    // 重新加载导游列表以更新收藏状态显示
    loadGuides()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 检查是否已收藏
const isFavorite = (guideId) => {
  return favorites.value.includes(guideId)
}

// 加载收藏列表
const loadFavorites = async () => {
  try {
    const response = await request.get('/favorites')
    favorites.value = (response.data.list || []).map(fav => fav.guideId)
  } catch (error) {
    console.error('加载收藏失败:', error)
  }
}

onMounted(() => {
  loadGuides()
  loadFavorites()
})
</script>

<style scoped>
.tourist-page {
  min-height: 100vh;
  background: var(--bg-main);
  padding: 24px 0;
}

.filter-section {
  background: var(--card-bg);
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.08);
}

.search-bar {
  margin-bottom: 16px;
}

.search-input {
  width: 100%;
  height: 40px;
  padding: 0 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

.search-input:focus {
  border-color: #2563eb;
}

.filters {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-group label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

.filter-select {
  height: 36px;
  padding: 0 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  cursor: pointer;
}

.price-input {
  width: 100px;
  height: 36px;
  padding: 0 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
}

.guides-section {
  background: var(--card-bg);
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.08);
}

.loading,
.empty {
  text-align: center;
  padding: 48px;
  color: #999;
}

.guides-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.guide-card {
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 0;
  cursor: pointer;
  transition: all 0.3s;
  background: var(--card-bg);
  overflow: hidden;
}

.guide-card:hover {
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.15);
  transform: translateY(-4px);
}

.guide-header {
  position: relative;
  padding: 20px 20px 0 20px;
  text-align: center;
}

.guide-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 8px;
}

.guide-status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.guide-status.online {
  background: #10b981;
  color: white;
}

.guide-status.offline {
  background: #6b7280;
  color: white;
}

.guide-info {
  padding: 0 20px 20px 20px;
}

.guide-name {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 4px 0;
  color: var(--text-main);
  text-align: center;
}

.guide-title {
  font-size: 14px;
  color: var(--text-muted);
  margin-bottom: 12px;
  text-align: center;
}

.guide-rating {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 12px;
}

.stars {
  display: flex;
  gap: 2px;
}

.star {
  font-size: 14px;
}

.rating-text {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
}

.review-count {
  font-size: 12px;
  color: var(--text-muted);
}

.guide-location {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-bottom: 12px;
  font-size: 14px;
  color: var(--text-muted);
}

.guide-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
  justify-content: center;
}

.tag {
  padding: 4px 8px;
  background: rgba(139, 92, 246, 0.1);
  color: var(--primary);
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.guide-description {
  font-size: 14px;
  color: var(--text-muted);
  line-height: 1.5;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-align: center;
}

.guide-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 16px;
  padding: 12px 0;
  border-top: 1px solid var(--border-color);
  border-bottom: 1px solid var(--border-color);
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 4px;
}

.stat-value {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-main);
}

.guide-price {
  text-align: center;
  margin-bottom: 16px;
}

.price {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary);
}

.period {
  font-size: 14px;
  color: var(--text-muted);
}

.price-note {
  display: block;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
}

.guide-actions {
  display: flex;
  gap: 8px;
}

.btn-primary {
  flex: 1;
  padding: 12px 16px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  background: #7c3aed;
}

.btn-favorite {
  padding: 12px 16px;
  background: white;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-favorite:hover {
  border-color: var(--primary);
}

.btn-favorite.active {
  background: rgba(239, 68, 68, 0.1);
  border-color: #ef4444;
}

.btn-favorite:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-contact {
  padding: 12px 16px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-contact:hover {
  background: #059669;
}

@media (max-width: 768px) {
  .tourist-page {
    padding: 16px 0;
  }
  
  .guides-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .guide-card {
    border-radius: 12px;
  }
  
  .guide-header {
    padding: 16px 16px 0 16px;
  }
  
  .guide-avatar {
    width: 64px;
    height: 64px;
  }
  
  .guide-info {
    padding: 0 16px 16px 16px;
  }
  
  .guide-name {
    font-size: 18px;
  }
  
  .guide-stats {
    flex-direction: column;
    gap: 8px;
  }
  
  .stat-item {
    display: flex;
    justify-content: space-between;
    text-align: left;
  }
  
  .guide-actions {
    flex-direction: column;
  }
  
  .btn-primary,
  .btn-favorite,
  .btn-contact {
    width: 100%;
  }
}
  color: #999;
  font-size: 12px;
}

.guide-tags,
.guide-cities {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 8px;
}

.tag,
.city {
  padding: 4px 10px;
  background: #f0f0f0;
  border-radius: 12px;
  font-size: 12px;
  color: #666;
}

.guide-price {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.price-label {
  color: #999;
  font-size: 14px;
}

.price-value {
  color: #2563eb;
  font-size: 20px;
  font-weight: 600;
  margin-left: 8px;
}

.guide-actions {
  display: flex;
  gap: 8px;
}

.btn-primary {
  flex: 1;
  height: 36px;
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-primary:hover {
  background: #1d4ed8;
}

.btn-favorite {
  width: 36px;
  height: 36px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-favorite.active {
  border-color: #ef4444;
  background: #fef2f2;
}
</style>