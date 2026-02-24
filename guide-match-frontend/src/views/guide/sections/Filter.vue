<template>
  <section class="filter glass-panel">
    <!-- v-model 绑定本地 ref -->
    <SearchBar
      v-model="keyword"
      :placeholder="t('guide.search')"
      @search="onSearch"
    />

    <TagSelect
      v-model="tags"
      :options="tagOptions"
    />
  </section>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useI18n } from '@/composables/useI18n'

import SearchBar from '@/components/feature/SearchBar.vue'
import TagSelect from '@/components/feature/TagSelect.vue'

const { t } = useI18n()

const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:modelValue', 'search'])

/* 本地可写状态 */
const keyword = ref(props.modelValue)

watch(
  () => props.modelValue,
  (val) => {
    keyword.value = val
  }
)

watch(keyword, (val) => {
  emit('update:modelValue', val)
})

const tags = ref([])
const tagOptions = ['留学', '考研', '计算机', '数学', '经验']

const onSearch = (value) => {
  emit('search', {
    keyword: value,
    tags: tags.value,
  })
}
</script>

<style scoped>
.filter {
  margin: 24px 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>
