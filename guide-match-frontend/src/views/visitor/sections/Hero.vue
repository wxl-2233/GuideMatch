<template>
  <section class="hero">
    <div class="hero-overlay">
      <h1 class="hero-title">探索，不止一种方式</h1>
    </div>
  </section>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const goGuide = () => {
  router.push('/guide')
}
</script>

<style scoped>
.hero {
  height: 70vh;
  background-image: url('/src/assets/bg.png');
  background-size: cover;
  background-position: center;
  position: relative;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-title {
  font-size: 36px;
  font-weight: 700;
  color: rgba(255,255,255,0.95);
  text-shadow: 0 4px 12px rgba(0,0,0,0.45);
}
</style>
