<template>
  <section class="actions">
    <Button @click="$emit('order')">
      预约向导
    </Button>

    <Button type="secondary">
      私信咨询
    </Button>
  </section>
</template>

<script setup>
import Button from '@/components/base/Button.vue'

defineEmits(['order'])
</script>

<style scoped>
.actions {
  display: flex;
  gap: 16px;
}
</style>
