<template>
  <section class="composer glass-card">
    <textarea
      v-model="content"
      placeholder="分享你的经验或问题..."
    ></textarea>

    <div class="actions">
      <Button size="sm" @click="submit">
        发布
      </Button>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import Button from '@/components/base/Button.vue'

const emit = defineEmits(['submit'])

const content = ref('')

const submit = () => {
  if (!content.value.trim()) return
  emit('submit', content.value)
  content.value = ''
}
</script>

<style scoped>
.composer {
  margin: 24px 0;
  padding: 16px;
  background: #f9fafb;
  border-radius: 12px;
}

textarea {
  width: 100%;
  min-height: 80px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 10px;
  resize: none;
}

.actions {
  margin-top: 8px;
  text-align: right;
}
</style>
