<template>
  <section class="list">
    <GuideList
      v-if="guides.length"
      :guides="guides"
      @view="$emit('view', $event)"
    />

    <EmptyState
      v-else
      :text="t('guide.noGuides')"
    />
  </section>
</template>

<script setup>
import GuideList from '@/components/business/GuideList.vue'
import EmptyState from '@/components/business/EmptyState.vue'
import { useI18n } from '@/composables/useI18n'

const { t } = useI18n()

defineProps({
  guides: {
    type: Array,
    default: () => [],
  },
})

defineEmits(['view'])
</script>

<style scoped>
.list {
  margin-top: 32px;
}
</style>
