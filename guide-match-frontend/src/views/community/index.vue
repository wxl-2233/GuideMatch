<template>
  <div class="community-page">
    <PageContainer>
      <div class="hero-section">
        <h1 class="hero-title">{{ t('community.title') }}</h1>
        <p class="hero-subtitle">{{ t('community.subtitle') }}</p>
      </div>

      <!-- 发帖区域 -->
      <div v-if="isLogin" class="composer-section">
        <div class="composer-card">
          <h3>{{ t('community.publish') }}</h3>
          <div class="composer-form">
            <input
              v-model="newPost.title"
              type="text"
              :placeholder="t('community.titlePlaceholder')"
              class="title-input"
            />
            <textarea
              v-model="newPost.content"
              :placeholder="t('community.contentPlaceholder')"
              class="content-input"
            ></textarea>
            <div class="composer-actions">
              <button class="btn btn-primary" @click="submitPost" :disabled="posting">
                {{ posting ? t('community.publishing') : t('community.publishBtn') }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 帖子列表 -->
      <div class="posts-section">
        <div v-if="loading" class="loading">{{ t('common.loading') }}</div>
        <div v-else-if="posts.length === 0" class="empty">{{ t('message.noPosts') }}</div>
        <div v-else class="posts-list">
          <div
            v-for="post in posts"
            :key="post.id"
            class="post-card"
          >
            <div class="post-header">
              <img
                :src="getAvatarUrl(post.avatarPath, post.avatarStatus)"
                :alt="post.nickname"
                class="post-avatar"
              />
              <div class="post-author-info">
                <div class="author-name">{{ post.nickname }}</div>
                <div class="author-role">
                  {{ post.userRole === 'tourist' ? t('role.tourist') : t('role.guide') }}
                </div>
              </div>
              <div class="post-time">{{ formatDate(post.createTime) }}</div>
            </div>

            <h3 class="post-title">{{ post.title }}</h3>
            <p class="post-content">{{ post.content }}</p>

            <div v-if="post.imagesArray && post.imagesArray.length > 0" class="post-images">
              <img
                v-for="(img, index) in post.imagesArray"
                :key="index"
                :src="img"
                alt="图片"
                class="post-image"
              />
            </div>

            <div class="post-footer">
              <button
                class="like-btn"
                :class="{ active: post.isLiked }"
                @click="toggleLike(post)"
              >
                <span class="like-icon" :class="{ liked: post.isLiked }">❤️</span>
                <span class="like-count">{{ post.likesCount || 0 }}</span>
              </button>
              <span class="views-count">👁️ {{ post.viewsCount || 0 }}</span>
            </div>
          </div>
        </div>
      </div>
    </PageContainer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '@/api/request'
import PageContainer from '@/components/layout/PageContainer.vue'
import { useI18n } from '@/composables/useI18n'

const { t, locale } = useI18n()

const isLogin = computed(() => {
  return !!localStorage.getItem('token')
})

const posts = ref([])
const loading = ref(false)
const posting = ref(false)

const newPost = ref({
  title: '',
  content: ''
})

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

const loadPosts = async () => {
  loading.value = true
  try {
    const response = await request.get('/posts/list')
    posts.value = (response.data.list || []).map(post => {
      // 确保 isLiked 是布尔值，处理各种可能的返回值
      let isLiked = false
      if (post.isLiked === true || post.isLiked === 'true' || post.isLiked === 1) {
        isLiked = true
      } else if (post.isLiked === false || post.isLiked === 'false' || post.isLiked === 0 || post.isLiked === null || post.isLiked === undefined) {
        isLiked = false
      }
      return {
        ...post,
        imagesArray: post.images ? JSON.parse(post.images) : [],
        isLiked: isLiked,
        avatarStatus: post.avatarStatus || null
      }
    })
    console.log('加载的帖子数据:', posts.value.map(p => ({ id: p.id, isLiked: p.isLiked, likesCount: p.likesCount })))
  } catch (error) {
    console.error('加载帖子失败:', error)
    posts.value = []
  } finally {
    loading.value = false
  }
}

const submitPost = async () => {
  if (!newPost.value.title.trim() || !newPost.value.content.trim()) {
    alert(t('message.fillTitleAndContent'))
    return
  }

  posting.value = true
  try {
    await request.post('/posts/create', {
      title: newPost.value.title,
      content: newPost.value.content
    })
    alert(t('message.publishSuccess'))
    newPost.value = { title: '', content: '' }
    window.location.reload()
  } catch (error) {
    alert(error.response?.data?.error || t('message.publishFailed'))
  } finally {
    posting.value = false
  }
}

const toggleLike = async (post) => {
  if (!isLogin.value) {
    alert(t('message.pleaseLogin') || '请先登录')
    return
  }
  
  try {
    const response = await request.post(`/posts/${post.id}/like`)
    // 确保 liked 是布尔值
    post.isLiked = response.data.liked === true || response.data.liked === 'true' || response.data.liked === 1
    post.likesCount = response.data.likesCount || post.likesCount || 0
    console.log('点赞后状态:', { postId: post.id, isLiked: post.isLiked, likesCount: post.likesCount })
  } catch (error) {
    console.error('点赞失败:', error)
    if (error.response?.status === 401) {
      alert(t('message.pleaseLogin') || '请先登录')
    } else {
      alert(error.response?.data?.error || t('message.operationFailed'))
    }
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return t('time.justNow')
  if (minutes < 60) return t('time.minutesAgo', { n: minutes })
  if (hours < 24) return t('time.hoursAgo', { n: hours })
  if (days < 7) return t('time.daysAgo', { n: days })
  return date.toLocaleDateString(locale.value === 'zh' ? 'zh-CN' : 'en-US')
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.community-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24px 0;
}

.hero-section {
  text-align: center;
  margin-bottom: 32px;
}

.hero-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #333;
}

.hero-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.composer-section {
  margin-bottom: 24px;
}

.composer-card {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.composer-card h3 {
  margin: 0 0 16px 0;
  font-size: 20px;
}

.composer-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.title-input {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
  outline: none;
}

.title-input:focus {
  border-color: #2563eb;
}

.content-input {
  min-height: 120px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  resize: vertical;
  outline: none;
  font-family: inherit;
}

.content-input:focus {
  border-color: #2563eb;
}

.composer-actions {
  display: flex;
  justify-content: flex-end;
}

.posts-section {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.post-card {
  border-bottom: 1px solid #eee;
  padding-bottom: 24px;
}

.post-card:last-child {
  border-bottom: none;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.post-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.post-author-info {
  flex: 1;
}

.author-name {
  font-weight: 600;
  color: #333;
}

.author-role {
  font-size: 12px;
  color: #999;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 12px 0;
  color: #333;
}

.post-content {
  line-height: 1.6;
  color: #666;
  margin: 0 0 16px 0;
  white-space: pre-wrap;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 8px;
  margin-bottom: 16px;
}

.post-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 6px;
}

.post-footer {
  display: flex;
  align-items: center;
  gap: 24px;
}

.like-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.like-btn:hover {
  border-color: #2563eb;
}

.like-btn.active {
  border-color: #ef4444;
  background: #fef2f2;
}

.like-icon {
  font-size: 18px;
  filter: grayscale(100%);
  opacity: 0.6;
  transition: all 0.3s;
}

.like-icon.liked {
  filter: grayscale(0%);
  opacity: 1;
  transform: scale(1.1);
}

.views-count {
  color: #999;
  font-size: 14px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.btn-primary {
  background: #2563eb;
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: #1d4ed8;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>