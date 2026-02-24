<template>
  <div class="modal-mask" @click.self="$emit('cancel')">
    <div class="modal">
      <h3 class="title">{{ title }}</h3>
      <p class="message">{{ message }}</p>

      <div class="actions">
        <Button type="secondary" @click="$emit('cancel')">
          取消
        </Button>
        <Button @click="$emit('confirm')">
          确认
        </Button>
      </div>
    </div>
  </div>
</template>

<script setup>
import Button from '@/components/base/Button.vue'

defineProps({
  title: {
    type: String,
    default: '提示',
  },
  message: {
    type: String,
    default: '确定要继续吗？',
  },
})

defineEmits(['confirm', 'cancel'])
</script>

<style scoped>
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  width: 320px;
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
}

.title {
  margin-bottom: 8px;
  font-size: 16px;
}

.message {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 16px;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
