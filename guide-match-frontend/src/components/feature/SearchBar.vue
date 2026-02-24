<template>
  <div class="search-bar">
    <input
      class="input"
      type="text"
      :placeholder="placeholder"
      v-model="keyword"
      @keyup.enter="onSearch"
    />
    <Button size="sm" @click="onSearch">
      搜索
    </Button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import Button from '@/components/base/Button.vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
  placeholder: {
    type: String,
    default: '请输入关键词',
  },
})

const emit = defineEmits(['update:modelValue', 'search'])

const keyword = ref(props.modelValue)

watch(
  () => props.modelValue,
  (val) => {
    keyword.value = val
  }
)

const onSearch = () => {
  emit('update:modelValue', keyword.value)
  emit('search', keyword.value)
}
</script>

<style scoped>
.search-bar {
  display: flex;
  gap: 8px;
  align-items: center;
}

.input {
  flex: 1;
  padding: 10px 12px;
  border-radius: 10px;
  
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.22);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  color: #111827;
  outline: none;
}

.input::placeholder {
  color: rgba(17, 24, 39, 0.55);
}

</style>
