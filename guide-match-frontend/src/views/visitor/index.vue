<template>
  <div class="visitor-page">
    <!-- 顶部滚动风景 -->
    <ScenicHero />

    <!-- 登录注册区域（在轮播图和推荐内容之间展开） -->
    <div class="login-section-wrapper">
      <InlineLogin :visible="showLogin" @close="showLogin = false" @submit="handleLogin" />
    </div>

    <!-- 精选内容区 -->
    <Highlights />
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import ScenicHero from './sections/ScenicHero.vue'
import Highlights from './sections/Highlights.vue'
import InlineLogin from '@/components/feature/InlineLogin.vue'

const showLogin = ref(false)

const handleLogin = () => {
  showLogin.value = false
}

// 监听来自 AppHeader 的登录事件
const handleLoginEvent = () => {
  showLogin.value = true
}

onMounted(() => {
  // 监听自定义事件
  window.addEventListener('show-login', handleLoginEvent)
})

onBeforeUnmount(() => {
  window.removeEventListener('show-login', handleLoginEvent)
})
</script>

<style scoped>
.visitor-page {
  min-height: 100vh;
  background-color: var(--bg-main);
  display: flex;
  flex-direction: column;
  gap: 0;
  padding-bottom: 80px;
}

.login-section-wrapper {
  width: 100%;
  margin: 0 auto;
  padding: 0;
  position: relative;
  z-index: 10;
  overflow: hidden;
}
</style>
