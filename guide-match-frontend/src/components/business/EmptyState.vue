<template>
  <div class="empty-state">
    <div class="icon">📭</div>
    <p class="text">{{ text }}</p>
    <Button
      v-if="actionText"
      size="sm"
      @click="$emit('action')"
    >
      {{ actionText }}
    </Button>
  </div>
</template>

<script setup>
import Button from '@/components/base/Button.vue'

defineProps({
  text: {
    type: String,
    default: '暂无数据',
  },
  actionText: {
    type: String,
    default: '',
  },
})

defineEmits(['action'])
</script>

<style scoped>
.empty-state {
  padding: 40px 0;
  text-align: center;
  color: #6b7280;
}

.icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.text {
  margin-bottom: 12px;
}
</style>
