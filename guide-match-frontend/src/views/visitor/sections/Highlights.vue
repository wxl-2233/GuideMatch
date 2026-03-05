<template>
  <section class="highlights">
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
import request from '@/api/request'

const { t } = useI18n()
const router = useRouter()

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

onMounted(() => {
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
