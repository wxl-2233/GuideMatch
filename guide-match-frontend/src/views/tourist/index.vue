<template>
  <div class="tourist-page">
    <PageContainer>
      <div class="filter-section">
        <div class="search-bar">
          <input
            v-model="searchKeyword"
            type="text"
            :placeholder="t('guide.search')"
            class="search-input"
            @keyup.enter="handleSearch"
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
              <option v-for="city in ['北京', '上海', '深圳', '广州', '杭州']" :key="city" :value="city">
                {{ city }}
              </option>
            </select>
          </div>

          <div class="filter-group">
            <label>{{ t('guide.language') }}：</label>
            <select v-model="filters.language" class="filter-select" @change="loadGuides">
              <option value="">{{ t('guide.all') }}</option>
              <option value="中文">中文</option>
              <option value="英文">英文</option>
            </select>
          </div>

          <div class="filter-group">
            <label>{{ t('guide.price') }}：</label>
            <input
              v-model.number="filters.minPrice"
              type="number"
              placeholder="最低"
              class="price-input"
              @change="loadGuides"
            />
            <span>-</span>
            <input
              v-model.number="filters.maxPrice"
              type="number"
              placeholder="最高"
              class="price-input"
              @change="loadGuides"
            />
          </div>

          <div class="filter-group">
            <label>{{ t('guide.sort') }}：</label>
            <select v-model="filters.sortBy" class="filter-select" @change="loadGuides">
              <option value="rating">{{ t('guide.rating') }}</option>
              <option value="price">{{ t('guide.priceSort') }}</option>
              <option value="orders">{{ t('guide.orders') }}</option>
            </select>
          </div>
        </div>
      </div>

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
                    {{ i <= Math.floor(guide.rating || 0) ? '⭐' : '☆' }}
                  </span>
                </div>
                <span class="rating-text">{{ formatRating(guide.rating) }}</span>
                <span class="review-count">({{ guide.reviewCount || 0 }}评价)</span>
              </div>
              
              <div class="guide-location">📍 {{ guide.city || '未知城市' }}</div>
              
              <div class="guide-tags">
                <span v-for="tag in guide.tags" :key="tag" class="tag">
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

const isLogin = computed(() => !!localStorage.getItem('token'))

const filters = ref({
  showFavoritesOnly: false,
  city: '',
  language: '',
  minPrice: null,
  maxPrice: null,
  sortBy: 'rating'
})

const formatRating = (rating) => {
  const val = parseFloat(rating)
  return isNaN(val) ? '0.0' : val.toFixed(1)
}

const loadGuides = async () => {
  loading.value = true
  try {
    const params = {
      keyword: searchKeyword.value || undefined,
      city: filters.value.city || undefined,
      language: filters.value.language || undefined,
      minPrice: filters.value.minPrice || undefined,
      maxPrice: filters.value.maxPrice || undefined,
      sortBy: filters.value.sortBy
    }

    const response = await request.get('/guides/list', { params })
    let list = (response.data.list || []).map(guide => {
      // 核心修复：确保 tags 始终为数组
      let tagsArr = []
      try {
        tagsArr = typeof guide.tags === 'string' ? JSON.parse(guide.tags) : (guide.tags || [])
      } catch (e) { tagsArr = [] }
      return { ...guide, tags: tagsArr }
    })

    if (filters.value.showFavoritesOnly) {
      list = list.filter(g => favorites.value.includes(g.id))
    }
    guides.value = list
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => loadGuides()

const toggleFavorite = async (guideId) => {
  try {
    const isFav = favorites.value.includes(guideId)
    if (isFav) {
      await request.delete(`/favorites/${guideId}`)
      favorites.value = favorites.value.filter(id => id !== guideId)
    } else {
      await request.post('/favorites', { guideId })
      favorites.value.push(guideId)
    }
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const isFavorite = (id) => favorites.value.includes(id)

const loadFavorites = async () => {
  if (!isLogin.value) return
  try {
    const response = await request.get('/favorites')
    favorites.value = (response.data.list || []).map(f => f.guideId)
  } catch (e) {}
}

const viewGuide = (id) => router.push(`/guide/${id}`)
const contactGuide = (id) => isLogin.value ? console.log('联系:', id) : alert('请先登录')

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

/* 筛选区 */
.filter-section {
  background: var(--card-bg);
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: var(--card-shadow);
}

.search-input {
  width: 100%;
  height: 44px;
  padding: 0 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  outline: none;
  transition: border-color 0.2s;
}

.search-input:focus { border-color: var(--primary); }

.filters {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  margin-top: 16px;
  align-items: center;
}

.filter-group { display: flex; align-items: center; gap: 8px; font-size: 14px; }

.filter-select, .price-input {
  height: 36px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 0 8px;
  outline: none;
}

/* 列表展示区 */
.guides-section {
  background: var(--card-bg);
  padding: 24px;
  border-radius: 12px;
  box-shadow: var(--card-shadow);
}

.guides-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.guide-card {
  border: 1px solid rgba(0,0,0,0.05);
  border-radius: 16px;
  background: #fff;
  transition: all 0.3s ease;
  overflow: hidden;
}

.guide-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 30px rgba(139, 92, 246, 0.15);
}

.guide-header {
  position: relative;
  padding-top: 24px;
  text-align: center;
}

.guide-avatar {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #f3f4f6;
}

.guide-status {
  position: absolute;
  top: 20px;
  right: 20px;
  padding: 2px 8px;
  border-radius: 20px;
  font-size: 11px;
  color: #fff;
}
.online { background: #10b981; }
.offline { background: #9ca3af; }

.guide-info { padding: 16px 20px 20px; text-align: center; }

.guide-name { font-size: 18px; margin: 0; color: #111827; }
.guide-title { font-size: 13px; color: #6b7280; margin: 4px 0 12px; }

.guide-rating { display: flex; align-items: center; justify-content: center; gap: 6px; margin-bottom: 12px; }
.stars { color: #fbbf24; }
.rating-text { font-weight: bold; color: #374151; }

.guide-tags { display: flex; flex-wrap: wrap; gap: 6px; justify-content: center; margin-bottom: 15px; }
.tag { background: #f3f0ff; color: #7c3aed; padding: 2px 10px; border-radius: 10px; font-size: 12px; }

.guide-description {
  font-size: 13px;
  color: #6b7280;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 15px;
  height: 40px;
}

.guide-stats {
  display: flex;
  border-top: 1px solid #f3f4f6;
  padding: 12px 0;
  margin-bottom: 15px;
}
.stat-item { flex: 1; }
.stat-label { font-size: 11px; color: #9ca3af; display: block; }
.stat-value { font-weight: 600; color: #1f2937; }

.guide-price { margin-bottom: 20px; }
.price { font-size: 22px; font-weight: 800; color: var(--primary); }

.guide-actions { display: flex; gap: 8px; }
.btn-primary { flex: 2; background: var(--primary); color: #fff; border: none; border-radius: 8px; cursor: pointer; height: 40px; }
.btn-contact { flex: 1; background: #10b981; color: #fff; border: none; border-radius: 8px; cursor: pointer; }
.btn-favorite { width: 40px; background: #fff; border: 1px solid #e5e7eb; border-radius: 8px; cursor: pointer; }
.btn-favorite.active { border-color: #ef4444; background: #fff1f2; }

@media (max-width: 768px) {
  .guides-grid { grid-template-columns: 1fr; }
  .filters { flex-direction: column; align-items: flex-start; }
}
</style>