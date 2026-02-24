<template>
  <span class="status" :class="status">
    {{ textMap[status] }}
  </span>
</template>

<script setup>
const props = defineProps({
  status: {
    type: String,
    required: true, // pending | done | canceled
  },
})

const textMap = {
  pending: '进行中',
  done: '已完成',
  canceled: '已取消',
}
</script>

<style scoped>
.status {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
}

.pending {
  background: #fef3c7;
  color: #92400e;
}

.done {
  background: #dcfce7;
  color: #166534;
}

.canceled {
  background: #fee2e2;
  color: #991b1b;
}
</style>
