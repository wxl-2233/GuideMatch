<template>
  <div class="review-card">
    <div class="review-header">
      <div class="reviewer-info">
        <img
          :src="getAvatarUrl(review.avatarPath, review.avatarStatus)"
          :alt="review.nickname || '用户'"
          class="reviewer-avatar"
        />
        <div class="reviewer-details">
          <div class="reviewer-name">{{ review.nickname || '匿名用户' }}</div>
          <div class="review-time">{{ formatDate(review.createTime) }}</div>
        </div>
      </div>
      <div class="review-rating">
        <span
          v-for="i in 5"
          :key="i"
          class="star"
          :class="{ active: i <= (review.rating || 0) }"
        >
          ★
        </span>
      </div>
    </div>
    <div v-if="review.comment" class="review-content">
      {{ review.comment }}
    </div>
    <div v-else class="review-content empty">
      该用户未填写评价内容
    </div>
  </div>
</template>

<script setup>
defineProps({
  review: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

const getAvatarUrl = (avatarPath, avatarStatus) => {
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

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.review-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.review-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.reviewer-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e5e7eb;
}

.reviewer-details {
  flex: 1;
}

.reviewer-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.review-time {
  font-size: 12px;
  color: #999;
}

.review-rating {
  display: flex;
  gap: 2px;
}

.star {
  font-size: 18px;
  color: #e5e7eb;
}

.star.active {
  color: #fbbf24;
}

.review-content {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  white-space: pre-wrap;
  word-break: break-word;
}

.review-content.empty {
  color: #999;
  font-style: italic;
  text-align: center;
}
</style>

