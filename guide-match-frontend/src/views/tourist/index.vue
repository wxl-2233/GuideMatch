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
            <img
              :src="getAvatarUrl(guide.avatarPath, guide.avatarStatus)"
              :alt="guide.nickname"
              class="guide-avatar"
            />
            <div class="guide-info">
              <h3 class="guide-name">{{ guide.nickname }}</h3>
              <div class="guide-rating">
                <span class="stars">★★★★★</span>
                <span class="rating-value">{{ formatRating(guide.rating) }}</span>
                <span class="reviews">({{ guide.totalReviews || 0 }}{{ t('guide.reviews') }})</span>
              </div>
              <div class="guide-tags">
                <span
                  v-for="tag in guide.tagsArray"
                  :key="tag"
                  class="tag"
                >
                  {{ tag }}
                </span>
              </div>
              <div class="guide-cities">
                <span v-for="city in guide.citiesArray" :key="city" class="city">
                  {{ city }}
                </span>
              </div>
              <div class="guide-price">
                <span class="price-label">{{ t('guide.dailyRate') }}：</span>
                <span class="price-value">¥{{ guide.dailyRate || 0 }}</span>
              </div>
            </div>
            <div class="guide-actions">
              <button class="btn-primary" @click.stop="viewGuide(guide.id)">
                {{ t('guide.viewDetails') }}
              </button>
              <button
                class="btn-favorite"
                :class="{ active: isFavorite(guide.id) }"
                @click.stop="toggleFavorite(guide.id)"
              >
                {{ isFavorite(guide.id) ? '❤️' : '🤍' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </PageContainer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/request'
import PageContainer from '@/components/layout/PageContainer.vue'
import { useI18n } from '@/composables/useI18n'

const { t } = useI18n()

const router = useRouter()

const guides = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const favorites = ref([])

const filters = ref({
  showFavoritesOnly: false,
  city: '',
  language: '',
  minPrice: null,
  maxPrice: null,
  tag: '',
  sortBy: 'rating'
})

const getAvatarUrl = (avatarPath, avatarStatus) => {
  if (!avatarPath) {
    return '/default-avatar.png'
  }
  if (avatarStatus && avatarStatus !== 'approved') {
    return '/default-avatar.png'
  }
  if (avatarPath.startsWith('http://') || avatarPath.startsWith('https://')) {
    return avatarPath
  }
  if (avatarPath.startsWith('/img/avatar/')) {
    return `http://localhost:8080${avatarPath}`
  }
  if (avatarPath.startsWith('/img/')) {
    return `http://localhost:8080${avatarPath}`
  }
  if (avatarPath.startsWith('/static/')) {
    const filename = avatarPath.split('/').pop()
    return `http://localhost:8080/img/avatar/${filename}`
  }
  return `http://localhost:8080/img/avatar/${avatarPath}`
}

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

// 查看向导详情
const viewGuide = (guideId) => {
  router.push(`/guide/${guideId}`)
}

// 收藏/取消收藏
const toggleFavorite = async (guideId) => {
  try {
    const isFav = favorites.value.includes(guideId)
    if (isFav) {
      await request.delete(`/favorites/${guideId}`)
    } else {
      await request.post('/favorites', { guideId })
    }
    window.location.reload()
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
  background: #f5f5f5;
  padding: 24px 0;
}

.filter-section {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
  border: 1px solid #e5e5e5;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
}

.guide-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.guide-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 16px;
}

.guide-info {
  margin-bottom: 16px;
}

.guide-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #333;
}

.guide-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.stars {
  color: #ffc107;
  font-size: 14px;
}

.rating-value {
  font-weight: 600;
  color: #333;
}

.reviews {
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