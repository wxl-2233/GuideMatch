<template>
  <button
    class="base-button"
    :class="[typeClass, sizeClass]"
    :disabled="disabled"
  >
    <slot />
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: {
    type: String,
    default: 'primary', // primary | secondary | text
  },
  size: {
    type: String,
    default: 'md', // sm | md | lg
  },
  disabled: {
    type: Boolean,
    default: false,
  },
})

const typeClass = computed(() => `btn-${props.type}`)
const sizeClass = computed(() => `btn-${props.size}`)
</script>

<style scoped>
.base-button {
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
}

/* size */
.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
}

.btn-md {
  padding: 10px 16px;
  font-size: 14px;
}

.btn-lg {
  padding: 14px 20px;
  font-size: 16px;
}

/* type */
.btn-primary {
  background-color: #2563eb;
  color: #fff;
}

.btn-primary:hover {
  background-color: #1d4ed8;
}

.btn-secondary {
  background-color: #f3f4f6;
  color: #111827;
}

.btn-secondary:hover {
  background-color: #e5e7eb;
}

.btn-text {
  background: transparent;
  color: #2563eb;
}

/* disabled */
.base-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
