<template>
  <div class="guide-list">
    <GuideCard
      v-for="guide in guides"
      :key="guide.id"
      :guide="guide"
      @view="onView"
    />
  </div>
</template>

<script setup>
import GuideCard from './GuideCard.vue'

const props = defineProps({
  guides: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['view'])

const onView = (guide) => {
  emit('view', guide)
}
</script>

<style scoped>
.guide-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>
