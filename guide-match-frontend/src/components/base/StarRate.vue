<template>
  <div class="star-rate">
    <span
      v-for="i in max"
      :key="i"
      class="star"
      :class="{ active: i <= rate }"
    >
      ★
    </span>
  </div>
</template>

<script setup>
defineProps({
  rate: {
    type: Number,
    required: true,
  },
  max: {
    type: Number,
    default: 5,
  },
})
</script>

<style scoped>
.star-rate {
  display: inline-flex;
  gap: 4px;
}

.star {
  font-size: 14px;
  color: #d1d5db;
}

.star.active {
  color: #f59e0b;
}
</style>
