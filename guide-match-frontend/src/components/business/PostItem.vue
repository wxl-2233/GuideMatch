<template>
  <div class="post-item">
    <div class="title">
      {{ title }}
    </div>

    <div class="meta">
      <span class="author">{{ author }}</span>
      <span class="dot">·</span>
      <span class="stats">👍 {{ likes }} · 💬 {{ comments }}</span>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: String,
  author: String,
  likes: {
    type: Number,
    default: 0
  },
  comments: {
    type: Number,
    default: 0
  },
})
</script>

<style scoped>
.post-item {
  padding: 14px 0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.post-item:hover {
  background-color: rgba(0, 0, 0, 0.02);
  border-radius: 4px;
  padding-left: 8px;
  padding-right: 8px;
  margin-left: -8px;
  margin-right: -8px;
}

.post-item + .post-item {
  border-top: 1px solid rgba(0, 0, 0, 0.08);
}

.title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  line-height: 1.6;
  margin-bottom: 8px;
  /* 确保文字清晰可读 */
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.meta {
  margin-top: 8px;
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
}

.author {
  color: #555;
  font-weight: 500;
}

.dot {
  margin: 0 4px;
  color: #999;
}

.stats {
  color: #777;
}
</style>
