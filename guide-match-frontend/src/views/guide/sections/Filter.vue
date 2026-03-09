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
      :placeholder="t('guide.tags') || '标签'"
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

// 使用更通用的标签选项，这些可以从后端获取或使用常见的分类
const tags = ref([])
const tagOptions = ref([
  '英语', '中文', '日语', '韩语', '法语', '德语', '西班牙语',
  '留学', '旅游', '商务', '文化', '历史', '美食', '购物',
  '摄影', '徒步', '驾驶', '翻译', '导游', '陪同'
])

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
