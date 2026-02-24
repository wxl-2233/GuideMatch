<template>
  <footer class="app-footer">
    <div class="inner">
      <span>© 2025 GuideMatch</span>
      <span class="divider">|</span>
      <span>All rights reserved</span>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.app-footer {
  width: 100%;
  height: 56px;

  background: #ffffff;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}

.inner {
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;

  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;

  font-size: 12px;
  color: rgba(17, 24, 39, 0.75);
}

.divider {
  opacity: 0.5;
}
</style>
