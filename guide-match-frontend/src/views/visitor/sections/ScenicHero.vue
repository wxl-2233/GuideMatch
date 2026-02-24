<template>
  <section class="scenic-hero">
    <div class="carousel-container">
      <div
        v-for="(image, index) in images"
        :key="index"
        class="carousel-slide"
        :class="{ active: currentIndex === index }"
        :style="{ backgroundImage: `url(${image})` }"
      />
    </div>
    <div class="overlay" />

    <div class="content">
      <h1 class="title">{{ t('scenic.title') }}</h1>
      <p class="subtitle">
        {{ t('scenic.subtitle') }}
      </p>
    </div>

    <!-- 轮播指示器 -->
    <div class="carousel-indicators">
      <button
        v-for="(image, index) in images"
        :key="index"
        class="indicator"
        :class="{ active: currentIndex === index }"
        @click="goToSlide(index)"
      />
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useI18n } from '@/composables/useI18n'

const { t } = useI18n()

const images = [
  new URL('@/assets/scenic/1.jpg', import.meta.url).href,
  new URL('@/assets/scenic/2.jpg', import.meta.url).href,
  new URL('@/assets/scenic/3.jpg', import.meta.url).href,
  new URL('@/assets/scenic/4.jpg', import.meta.url).href
]

const currentIndex = ref(0)
let intervalId = null
let isMounted = false

const goToSlide = (index) => {
  if (!isMounted) return
  currentIndex.value = index
  resetInterval()
}

const nextSlide = () => {
  // 只在组件仍然挂载时更新
  if (isMounted) {
    currentIndex.value = (currentIndex.value + 1) % images.length
  }
}

const resetInterval = () => {
  if (intervalId) {
    clearInterval(intervalId)
    intervalId = null
  }
  if (isMounted) {
    intervalId = setInterval(nextSlide, 5000) // 每5秒切换
  }
}

onMounted(() => {
  isMounted = true
  resetInterval()
})

onBeforeUnmount(() => {
  isMounted = false
  if (intervalId) {
    clearInterval(intervalId)
    intervalId = null
  }
})
</script>

<style scoped>
.scenic-hero {
  position: relative;
  width: 100%;
  height: 60vh;
  min-height: 420px;
  max-height: 560px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.carousel-container {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.carousel-slide {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  opacity: 0;
  transition: opacity 1s ease-in-out;
  /* 防止图片变形，保持宽高比 */
  image-rendering: -webkit-optimize-contrast;
  image-rendering: crisp-edges;
}

.carousel-slide.active {
  opacity: 1;
}

/* 轻遮罩：让字清楚 */
.overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.25),
    rgba(0, 0, 0, 0.35)
  );
  z-index: 1;
}

.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 2;
}

.indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.indicator.active {
  background: rgba(255, 255, 255, 0.9);
  width: 12px;
  height: 12px;
}

/* 文案区 */
.content {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 0 24px;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.title {
  font-size: clamp(28px, 4vw, 40px);
  font-weight: 700;
  letter-spacing: 0.04em;
  margin-bottom: 12px;
  color: #ffffff;
}

.subtitle {
  font-size: 15px;
  opacity: 0.9;
  color: #ffffff;
}
</style>
