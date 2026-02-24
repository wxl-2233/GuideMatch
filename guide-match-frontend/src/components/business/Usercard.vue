<template>
  <div class="user-card">
    <img :src="user.avatar" class="avatar" />

    <div class="info">
      <div class="name">{{ user.name }}</div>
      <div class="desc">{{ user.desc }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  user: {
    type: Object,
    required: true,
  },
})
</script>

<style scoped>
.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.info {
  display: flex;
  flex-direction: column;
}

.name {
  font-weight: 600;
  font-size: 14px;
}

.desc {
  font-size: 12px;
  color: #6b7280;
}
</style>
