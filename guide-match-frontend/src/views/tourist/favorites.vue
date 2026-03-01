<template>
  <div class="favorites-page">
    <PageContainer>
      <h1 class="page-title">我的收藏</h1>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="favoriteGuides.length === 0" class="empty">
        <div class="empty-icon">❤️</div>
        <div class="empty-text">暂无收藏的向导</div>
        <button class="btn btn-primary" @click="goToGuides">去浏览向导</button>
      </div>
      <div v-else class="guides-grid">
        <div
          v-for="guide in favoriteGuides"
          :key="guide.id"
          class="guide-card"
          @click="viewGuide(guide.id)"
        >
          <img
            :src="getAvatarUrl(guide.avatarPath, guide.avatarStatus)"
            :alt="guide.nickname || '向导'"
            class="guide-avatar"
            @error="handleImageError"
          />
          <div class="guide-info">
            <h3 class="guide-name">{{ guide.nickname || '未知向导' }}</h3>
            <div class="guide-rating">
              <span class="stars">★★★★★</span>
              <span class="rating-value">{{ formatRating(guide.rating) }}</span>
              <span class="reviews">({{ guide.totalReviews || 0 }}评价)</span>
            </div>
            <div v-if="guide.tagsArray && guide.tagsArray.length > 0" class="guide-tags">
              <span v-for="tag in guide.tagsArray" :key="tag" class="tag">
                {{ tag }}
              </span>
            </div>
            <div v-else class="guide-tags-empty">
              <span class="tag-empty">暂无标签</span>
            </div>
            <div v-if="guide.citiesArray && guide.citiesArray.length > 0" class="guide-location">
              <span v-for="city in guide.citiesArray" :key="city" class="city">
                {{ city }}
              </span>
            </div>
            <div v-else class="guide-location-empty">
              <span class="city-empty">暂无服务城市</span>
            </div>
            <div class="guide-price">
              <span class="price-label">日薪：</span>
              <span class="price-value">¥{{ guide.dailyRate || 0 }}/天</span>
            </div>
            <div v-if="guide.hourlyRate" class="guide-price-hourly">
              <span class="price-label">时薪：</span>
              <span class="price-value">¥{{ guide.hourlyRate }}/小时</span>
            </div>
          </div>
          <div class="guide-actions">
            <button class="btn btn-primary" @click.stop="viewGuide(guide.id)">
              查看详情
            </button>
            <button
              class="btn btn-favorite active"
              @click.stop="removeFavorite(guide.id)"
              title="取消收藏"
            >
              ❤️
            </button>
          </div>
        </div>
      </div>
    </PageContainer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/request'
import PageContainer from '@/components/layout/PageContainer.vue'

const router = useRouter()

const favoriteGuides = ref([])
const loading = ref(false)

const getAvatarUrl = (avatarPath, avatarStatus) => {
  // 只有审核通过的头像才显示，否则显示默认头像
  if (!avatarPath || avatarStatus !== 'approved') {
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

// 处理图片加载错误
const handleImageError = (event) => {
  event.target.src = '/default-avatar.png'
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

// 解析JSON字符串
const parseJsonSafely = (jsonStr) => {
  if (!jsonStr) return []
  if (Array.isArray(jsonStr)) return jsonStr
  try {
    return JSON.parse(jsonStr)
  } catch (e) {
    return []
  }
}

// 加载收藏的向导列表
const loadFavorites = async () => {
  loading.value = true
  try {
    // 获取收藏列表（后端已返回 guideId 和完整信息）
    const favoritesResponse = await request.get('/favorites')
    const favorites = favoritesResponse.data.list || []
    
    if (favorites.length === 0) {
      favoriteGuides.value = []
      loading.value = false
      return
    }

    // 获取所有收藏的向导ID（guide 表的 id）
    const guideIds = favorites.map(fav => fav.guideId).filter(id => id != null)
    
    // 批量获取向导详细信息
    const guidesPromises = guideIds.map(async (guideId) => {
      try {
        const guideResponse = await request.get(`/guides/${guideId}`)
        const data = guideResponse.data
        
        // 后端返回的数据结构是 { guide: {...}, user: {...}, calendar: [...] }
        const guideData = data.guide || {}
        const userData = data.user || {}
        
        return {
          ...guideData,
          ...userData, // user 数据会覆盖 guide 中同名的字段（如 nickname, avatarPath）
          tagsArray: parseJsonSafely(guideData.tags),
          citiesArray: parseJsonSafely(guideData.cities),
          languagesArray: parseJsonSafely(guideData.languages),
          certificatesArray: parseJsonSafely(guideData.certificates)
        }
      } catch (error) {
        console.error(`加载向导 ${guideId} 失败:`, error)
        return null
      }
    })

    const guides = await Promise.all(guidesPromises)
    favoriteGuides.value = guides.filter(guide => guide !== null)
  } catch (error) {
    console.error('加载收藏失败:', error)
    favoriteGuides.value = []
  } finally {
    loading.value = false
  }
}

// 查看向导详情
const viewGuide = (guideId) => {
  router.push(`/guide/${guideId}`)
}

// 取消收藏
const removeFavorite = async (guideId) => {
  console.log('尝试取消收藏，guideId:', guideId)
  
  if (!confirm('确定要取消收藏吗？')) {
    console.log('用户取消了操作')
    return
  }

  try {
    console.log('发送取消收藏请求...')
    const response = await request.delete(`/favorites/${guideId}`)
    console.log('取消收藏响应:', response)
    
    // 立即从列表中移除，无需等待重新加载
    favoriteGuides.value = favoriteGuides.value.filter(guide => guide.id !== guideId)
    
    alert('已取消收藏')
  } catch (error) {
    console.error('取消收藏失败:', error)
    console.error('错误详情:', error.response?.data)
    alert(error.response?.data?.error || error.message || '取消收藏失败')
  }
}

// 去浏览向导
const goToGuides = () => {
  router.push('/tourist')
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24px 0;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
}

.loading,
.empty {
  text-align: center;
  padding: 60px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: #999;
  margin-bottom: 24px;
}

.guides-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.guide-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
}

.guide-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.guide-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  margin: 0 auto 16px;
  border: 3px solid #e5e5e5;
}

.guide-info {
  flex: 1;
  margin-bottom: 16px;
}

.guide-name {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px 0;
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
  color: #fbbf24;
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

.guide-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: center;
  margin-bottom: 12px;
}

.tag {
  padding: 4px 10px;
  background: #f3f4f6;
  border-radius: 12px;
  font-size: 12px;
  color: #666;
}

.guide-location {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: center;
  margin-bottom: 12px;
}

.city {
  padding: 4px 10px;
  background: #e0f2fe;
  border-radius: 12px;
  font-size: 12px;
  color: #0369a1;
}

.guide-price {
  text-align: center;
  margin-bottom: 16px;
}

.price-label {
  color: #999;
  font-size: 14px;
}

.price-value {
  color: #2563eb;
  font-weight: 600;
  font-size: 16px;
}

.guide-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
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

.btn-favorite {
  background: #fee2e2;
  color: #dc2626;
  font-size: 18px;
  padding: 8px 12px;
}

.btn-favorite:hover {
  background: #fecaca;
}

.btn-favorite.active {
  background: #fee2e2;
  color: #dc2626;
}
</style>

