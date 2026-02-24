<template>
  <div class="app-shell">
    <AppHeader v-if="!isLoginPage" />
    <main class="app-main" :class="{ 'full-height': isLoginPage }">
      <router-view :key="route.path" />
    </main>
    <AppFooter v-if="!isLoginPage" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'

const route = useRoute()

const isLoginPage = computed(() => route.path === '/login')
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-main {
  flex: 1;
}

.app-main.full-height {
  flex: none;
  height: 100vh;
}
</style>
