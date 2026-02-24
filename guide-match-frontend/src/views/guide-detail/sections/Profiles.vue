<template>
  <section class="profile glass-card">
    <img :src="guide.avatar" class="avatar" />

    <div class="info">
      <h1 class="name">{{ guide.name }}</h1>
      <p class="desc">{{ guide.desc }}</p>

      <div class="tags">
        <span v-for="tag in guide.tags" :key="tag" class="tag">
          {{ tag }}
        </span>
      </div>
    </div>
  </section>
</template>

<script setup>
defineProps({
  guide: {
    type: Object,
    required: true,
  },
})
</script>

<style scoped>
.profile {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
}

.info {
  flex: 1;
}

.name {
  font-size: 24px;
  font-weight: 700;
}

.desc {
  margin: 8px 0;
  color: #6b7280;
}

.tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  padding: 4px 10px;
  background: #eef2ff;
  color: #4338ca;
  border-radius: 999px;
  font-size: 12px;
}
</style>
