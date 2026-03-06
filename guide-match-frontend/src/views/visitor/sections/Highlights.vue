<template>
  <section class="highlights">
    <!-- 推荐向导 -->
    <div class="highlight-card">
      <header class="card-header">
        <h3>{{ t('home.recommendedGuides') }}</h3>
        <RouterLink to="/tourist">{{ t('home.viewAllGuides') }} →</RouterLink>
      </header>

      <div v-if="guidesLoading" class="loading">加载中...</div>
      <div v-else-if="guides.length === 0" class="empty">暂无推荐向导</div>
      <div v-else class="guide-list">
        <div
          v-for="guide in guides"
          :key="guide.id"
          class="guide-item"
          @click="goToGuide(guide.id)"
        >
          <img
            :src="getAvatarUrl(guide.avatarPath, guide.avatarStatus)"
            :alt="guide.name"
            class="guide-avatar"
          />
          <div class="guide-info">
            <h4 class="guide-name">{{ guide.name }}</h4>
            <div class="guide-rating">
              <span class="stars">{{ formatRating(guide.rating) }}</span>
              <span class="rating-text">{{ guide.rating }}分</span>
            </div>
            <div class="guide-price">¥{{ guide.price }}/{{ guide.period }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 社区讨论 -->
    <div class="highlight-card">
      <header class="card-header">
        <h3>{{ t('home.discussions') }}</h3>
        <RouterLink to="/community">{{ t('home.enterCommunity') }} →</RouterLink>
      </header>

      <div v-if="postsLoading" class="loading">加载中...</div>
      <div v-else-if="posts.length === 0" class="empty">暂无讨论</div>
      <div v-else>
        <PostItem
          v-for="p in posts"
          :key="p.id"
          :title="p.title"
          :author="p.nickname || p.author || '未知用户'"
          :likes="p.likesCount || p.likes || 0"
          :comments="0"
        />
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PostItem from '@/components/business/PostItem.vue'
import { useI18n } from '@/composables/useI18n'
import { getAvatarUrl } from '@/utils/avatar'
import request from '@/api/request'

const { t } = useI18n()
const router = useRouter()

const guides = ref([])
const guidesLoading = ref(false)
const posts = ref([])
const postsLoading = ref(false)

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

// 加载推荐向导（按评分排序，取前4个）
const loadRecommendedGuides = async () => {
  guidesLoading.value = true
  try {
    const response = await request.get('/guides/list', {
      params: {
        page: 1,
        size: 4
      }
    })
    
    const guidesList = response.data.list || []
    // 按评分排序，取前4个
    guides.value = guidesList
      .sort((a, b) => (b.rating || 0) - (a.rating || 0))
      .slice(0, 4)
      .map(guide => ({
        id: guide.id,
        name: guide.name,
        rating: guide.rating || 5.0,
        price: guide.price || 0,
        period: guide.period || '天',
        avatarPath: guide.avatarPath,
        avatarStatus: guide.avatarStatus
      }))
  } catch (error) {
    console.error('加载推荐向导失败:', error)
    guides.value = []
  } finally {
    guidesLoading.value = false
  }
}

// 加载热门帖子（按点赞数排序，取前4个）
const loadHotPosts = async () => {
  postsLoading.value = true
  try {
    const response = await request.get('/posts/list', {
      params: {
        page: 1,
        size: 4
      }
    })
    
    const postsList = response.data.list || []
    // 按点赞数排序
    posts.value = postsList
      .sort((a, b) => (b.likesCount || 0) - (a.likesCount || 0))
      .slice(0, 4)
      .map(post => ({
        id: post.id,
        title: post.title,
        nickname: post.nickname,
        author: post.nickname, // PostItem组件使用author字段
        likesCount: post.likesCount || 0,
        likes: post.likesCount || 0 // PostItem组件使用likes字段
      }))
  } catch (error) {
    console.error('加载热门帖子失败:', error)
    posts.value = []
  } finally {
    postsLoading.value = false
  }
}

// 跳转到向导详情页
const goToGuide = (guideId) => {
  router.push(`/guide/${guideId}`)
}

// 格式化评分显示
const formatRating = (rating) => {
  const fullStars = Math.floor(rating)
  const hasHalfStar = rating % 1 >= 0.5
  let stars = ''
  
  for (let i = 0; i < fullStars; i++) {
    stars += '⭐'
  }
  if (hasHalfStar) {
    stars += '✨'
  }
  
  return stars || '⭐'
}

onMounted(() => {
  loadRecommendedGuides()
  loadHotPosts()
})
</script>

<style scoped>
.highlights {
  max-width: 1200px;
  margin: 80px auto 0 auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
}

.highlight-card {
  background: var(--card-bg);
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(139, 92, 246, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-main);
}

.card-header a {
  font-size: 14px;
  color: var(--primary);
  text-decoration: none;
}

.card-header a:hover {
  text-decoration: underline;
}

.guide-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.guide-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.guide-item:hover {
  background: rgba(139, 92, 246, 0.05);
  border-color: var(--primary);
  transform: translateY(-2px);
}

.guide-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.guide-info {
  flex: 1;
  min-width: 0;
}

.guide-name {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.guide-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.stars {
  font-size: 12px;
}

.rating-text {
  font-size: 12px;
  color: var(--text-muted);
}

.guide-price {
  font-size: 14px;
  font-weight: 600;
  color: var(--primary);
}

.loading,
.empty {
  text-align: center;
  padding: 40px 20px;
  color: var(--text-muted);
  font-size: 14px;
}

@media (max-width: 768px) {
  .highlights {
    grid-template-columns: 1fr;
    gap: 24px;
    padding: 0 16px;
  }
  
  .guide-list {
    grid-template-columns: 1fr;
  }
  
  .guide-item {
    gap: 16px;
  }
  
  .guide-avatar {
    width: 56px;
    height: 56px;
  }
}
</style>
