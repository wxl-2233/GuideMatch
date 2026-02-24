<template>
  <div class="guide-card  glass-card">
    <!-- 头像 -->
    <img :src="getAvatarUrl(guide?.avatar || guide?.avatarPath, guide?.avatarStatus)" class="avatar" />

    <!-- 信息 -->
    <div class="info">
      <div class="header">
        <h3 class="name">{{ guide?.name || guide?.nickname || '未知' }}</h3>
        <StarRate :rate="guide?.rating || guide?.score || 0" />
      </div>

      <div class="tags">
        <span
          v-for="tag in (guide?.tags || guide?.tagsArray || [])"
          :key="tag"
          class="tag"
        >
          {{ tag }}
        </span>
      </div>

      <div class="footer">
        <LevelProgress :percent="guide?.match || 0" />
        <Button size="sm" @click="$emit('view', guide)">
          查看详情
        </Button>
      </div>
    </div>
  </div>
</template>

<script setup>
import Button from '@/components/base/Button.vue'
import StarRate from '@/components/base/StarRate.vue'
import LevelProgress from '@/components/base/LevelProgress.vue'

defineProps({
  guide: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

defineEmits(['view'])

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
</script>

<style scoped>
.guide-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
}

.info {
  flex: 1;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.name {
  font-size: 16px;
  font-weight: 600;
}

.tags {
  margin: 8px 0;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  background: rgba(255, 255, 255, 0.22);
  border: 1px solid rgba(255, 255, 255, 0.28);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);

  color: #111827;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 999px;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
