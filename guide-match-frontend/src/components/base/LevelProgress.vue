<template>
  <div class="level-progress">
    <div class="bar">
      <div
        class="fill"
        :style="{ width: percent + '%' }"
      ></div>
    </div>
    <span class="label">{{ percent }}%</span>
  </div>
</template>

<script setup>
defineProps({
  percent: {
    type: Number,
    required: true,
  },
})
</script>

<style scoped>
.level-progress {
  display: flex;
  align-items: center;
  gap: 8px;
}

.bar {
  width: 100px;
  height: 8px;
  background-color: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.fill {
  height: 100%;
  background-color: #22c55e;
  transition: width 0.3s ease;
}

.label {
  font-size: 12px;
  color: #374151;
}
</style>
