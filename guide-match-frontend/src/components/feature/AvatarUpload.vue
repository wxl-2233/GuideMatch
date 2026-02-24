<template>
  <div class="avatar-upload">
    <img
      v-if="preview"
      :src="preview"
      class="avatar"
    />
    <div v-else class="placeholder">
      上传头像
    </div>

    <input
      type="file"
      accept="image/*"
      class="file-input"
      @change="onChange"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['change'])

const preview = ref('')

const onChange = (e) => {
  const file = e.target.files[0]
  if (!file) return

  preview.value = URL.createObjectURL(file)
  emit('change', file)
}
</script>

<style scoped>
.avatar-upload {
  position: relative;
  width: 96px;
  height: 96px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder {
  width: 100%;
  height: 100%;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #6b7280;
}

.file-input {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
}
</style>
