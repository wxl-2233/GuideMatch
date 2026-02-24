<template>
  <section class="feed glass-card">
    <div
      v-for="post in posts"
      :key="post.id"
      class="post"
    >
      <Usercard :user="post.user" />
      <p class="content">{{ post.content }}</p>
    </div>

    <EmptyState
      v-if="!posts.length"
      text="暂无帖子"
    />
  </section>
</template>

<script setup>
import Usercard from '@/components/business/Usercard.vue'
import EmptyState from '@/components/business/EmptyState.vue'

defineProps({
  posts: {
    type: Array,
    default: () => [],
  },
})
</script>

<style scoped>
.feed {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post {
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.content {
  margin-top: 8px;
  font-size: 14px;
  color: #374151;
}
</style>
