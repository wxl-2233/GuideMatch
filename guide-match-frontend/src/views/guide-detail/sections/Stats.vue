<template>
  <section class="stats">
    <div class="item">
      <span class="label">评分</span>
      <StarRate :rate="guide.rating" />
    </div>

    <div class="item">
      <span class="label">匹配度</span>
      <LevelProgress :percent="guide.match" />
    </div>
  </section>
</template>

<script setup>
import StarRate from '@/components/base/StarRate.vue'
import LevelProgress from '@/components/base/LevelProgress.vue'

defineProps({
  guide: {
    type: Object,
    required: true,
  },
})
</script>

<style scoped>
.stats {
  display: flex;
  gap: 40px;
  margin-bottom: 32px;
}

.item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.label {
  font-size: 14px;
  color: #6b7280;
}
</style>
