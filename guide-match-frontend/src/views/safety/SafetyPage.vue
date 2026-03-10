<template>
  <div class="safety-page">
    <PageContainer>
      <div class="page-header">
        <h1>安全保障中心</h1>
        <p>实时位置追踪 · 紧急求助 · 行程分享</p>
      </div>
      
      <SafetyTracking />
    </PageContainer>
  </div>
</template>

<script setup>
import SafetyTracking from '@/components/safety/SafetyTracking.vue'
import PageContainer from '@/components/layout/PageContainer.vue'
</script>

<style scoped>
.safety-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 0;
}

.page-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 10px 0;
}

.page-header p {
  font-size: 16px;
  color: #666;
  margin: 0;
}
</style>
