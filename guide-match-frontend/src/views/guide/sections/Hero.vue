<template>
  <section class="hero">
    <h1 class="title">{{ t('guide.allGuides') }}</h1>
    <p class="subtitle">
      {{ t('guide.subtitle') }}
    </p>
  </section>
</template>

<script setup>
import { useI18n } from '@/composables/useI18n'

const { t } = useI18n()
</script>

<style scoped>
.hero {
  padding: 40px 0 24px;
}

.title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
}

.subtitle {
  color: #6b7280;
}
</style>
