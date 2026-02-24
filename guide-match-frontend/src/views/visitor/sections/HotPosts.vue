<template>
  <div class="posts">
    <PostItem
      v-for="post in posts"
      :key="post.id"
      :post="post"
    />
  </div>
</template>

<script setup>
import PostItem from '@/components/business/PostItem.vue'

const posts = [
  { id: 1, title: '分享我申请计算机方向的经验', author: 'Alice', likes: 23 },
  { id: 2, title: '最近在准备考研数学，有推荐资料吗？', author: 'Bob', likes: 18 },
  { id: 3, title: '第一次找向导，有什么需要注意的？', author: 'Chris', likes: 12 },
  { id: 4, title: '向导一般能在哪些方面帮到你？', author: 'Diana', likes: 9 }
]
</script>

<style scoped>
.posts {
  display: flex;
  flex-direction: column;
}

.posts :deep(.post-item + .post-item) {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid rgba(255,255,255,0.15);
}
</style>
