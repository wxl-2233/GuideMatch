<template>
  <div class="member-page">
    <PageContainer>
      <div class="page-header">
        <h1>会员中心</h1>
        <p>积分等级 · 会员权益 · 专属服务</p>
      </div>
      
      <MemberCenter />
    </PageContainer>
  </div>
</template>

<script setup>
import MemberCenter from '@/components/member/MemberCenter.vue'
import PageContainer from '@/components/layout/PageContainer.vue'
</script>

<style scoped>
.member-page {
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
