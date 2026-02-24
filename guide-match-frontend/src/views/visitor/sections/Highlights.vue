<template>
  <section class="highlights">
    <!-- 向导推荐 -->
    <div class="highlight-card">
      <header class="card-header">
        <h3>{{ t('home.recommendedGuides') }}</h3>
        <RouterLink to="/guide">{{ t('home.viewAll') }} →</RouterLink>
      </header>

      <div v-if="guidesLoading" class="loading">加载中...</div>
      <div v-else-if="guides.length === 0" class="empty">暂无推荐向导</div>
      <div v-else class="guide-list">
        <GuideCard
          v-for="g in guides"
          :key="g.id"
          :guide="g"
          @view="viewGuide"
        />
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
import GuideCard from '@/components/business/GuideCard.vue'
import PostItem from '@/components/business/PostItem.vue'
import { useI18n } from '@/composables/useI18n'
import request from '@/api/request'

const { t } = useI18n()
const router = useRouter()

const guides = ref([])
const posts = ref([])
const guidesLoading = ref(false)
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
        sortBy: 'rating',
        page: 1,
        size: 4
      }
    })
    
    const guidesList = response.data.list || []
    guides.value = guidesList.map(guide => ({
      id: guide.id,
      userId: guide.userId,
      nickname: guide.nickname,
      name: guide.nickname, // GuideCard组件可能使用name字段
      avatarPath: guide.avatarPath,
      avatar: guide.avatarPath, // GuideCard组件可能使用avatar字段
      rating: guide.rating || 0,
      score: guide.rating || 0, // GuideCard组件可能使用score字段
      tagsArray: parseJsonSafely(guide.tags),
      tags: parseJsonSafely(guide.tags), // GuideCard组件可能使用tags字段
      citiesArray: parseJsonSafely(guide.cities),
      dailyRate: guide.dailyRate || 0,
      totalReviews: guide.totalReviews || 0,
      match: 85 // 匹配度可以基于其他因素计算，这里先给个默认值
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

// 查看向导详情
const viewGuide = (guide) => {
  if (guide && guide.id) {
    router.push(`/guide/${guide.id}`)
  }
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
  background: #ffffff;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  /* 确保背景是白色，文字清晰可见 */
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
}

.card-header a {
  font-size: 14px;
  color: var(--primary);
}

.guide-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.loading,
.empty {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 14px;
}
</style>
