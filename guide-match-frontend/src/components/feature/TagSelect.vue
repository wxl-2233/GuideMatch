<template>
  <div class="tag-select">
    <span
      v-for="tag in options"
      :key="tag"
      class="tag"
      :class="{ active: modelValue.includes(tag) }"
      @click="toggle(tag)"
    >
      {{ tag }}
    </span>
  </div>
</template>

<script setup>
const props = defineProps({
  options: {
    type: Array,
    default: () => [],
  },
  modelValue: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['update:modelValue'])

const toggle = (tag) => {
  const set = new Set(props.modelValue)
  set.has(tag) ? set.delete(tag) : set.add(tag)
  emit('update:modelValue', [...set])
}
</script>

<style scoped>
.tag-select {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  padding: 4px 10px;
  font-size: 12px;
  border-radius: 999px;
  background: #f3f4f6;
  cursor: pointer;
}

.tag.active {
  background: #2563eb;
  color: #fff;
}
</style>
