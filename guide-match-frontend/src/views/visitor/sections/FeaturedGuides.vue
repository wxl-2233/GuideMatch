<template>
  <div class="guides">
    <GuideCard
      v-for="guide in guides"
      :key="guide.id"
      :guide="guide"
    />
  </div>
</template>

<script setup>
import GuideCard from '@/components/business/GuideCard.vue'

const guides = [
  { id: 1, name: 'Alice', score: 92 },
  { id: 2, name: 'Bob', score: 88 },
  { id: 3, name: 'Chris', score: 90 },
  { id: 4, name: 'Diana', score: 86 }
]
</script>

<style scoped>
.guides {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>
