<template>
  <div class="dd" ref="root">
    <button class="trigger glass-panel" type="button" @click="open = !open">
      <span class="label">{{ modelValue }}</span>
      <span class="chev">▾</span>
    </button>

    <div v-if="open" class="menu glass-card">
      <button
        v-for="opt in options"
        :key="opt"
        class="item"
        type="button"
        @click="select(opt)"
      >
        {{ opt }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  modelValue: { type: String, required: true },
  options: { type: Array, default: () => [] },
})

const emit = defineEmits(['update:modelValue'])

const open = ref(false)
const root = ref(null)

const select = (opt) => {
  emit('update:modelValue', opt)
  open.value = false
}

const onClickOutside = (e) => {
  if (!root.value) return
  if (!root.value.contains(e.target)) open.value = false
}

onMounted(() => document.addEventListener('click', onClickOutside))
onBeforeUnmount(() => document.removeEventListener('click', onClickOutside))
</script>

<style scoped>
.dd { position: relative; display: inline-block; }

.trigger {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 10px;
  border: 1px solid rgba(255,255,255,0.25);
  cursor: pointer;
  color: #111827;
}

.menu {
  position: absolute;
  right: 0;
  top: calc(100% + 8px);
  min-width: 160px;
  padding: 6px;
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.25);
}

.item {
  width: 100%;
  text-align: left;
  padding: 10px 10px;
  border: none;
  background: transparent;
  color: #111827;
  border-radius: 10px;
  cursor: pointer;
}

.item:hover {
  background: rgba(255,255,255,0.22);
}

.chev { opacity: 0.8; }
</style>
