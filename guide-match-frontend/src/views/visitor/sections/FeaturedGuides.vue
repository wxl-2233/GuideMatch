<template>
  <div class="guides">
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <template v-else>
      <GuideCard
        v-for="guide in guides"
        :key="guide.id"
        :guide="guide"
        @view="$emit('view', guide)"
      />
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import GuideCard from '@/components/business/GuideCard.vue'
import request from '@/api/request'

defineEmits(['view'])

const guides = ref([])
const loading = ref(false)
const error = ref('')

const loadFeaturedGuides = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await request.get('/guides/list', {
      params: {
        page: 1,
        size: 4,
        sortBy: 'rating',
        sortOrder: 'desc'
      }
    })
    
    // 处理返回的数据，确保字段映射正确
    guides.value = (response.data.list || []).map(guide => ({
      id: guide.id,
      name: guide.nickname || guide.name,
      nickname: guide.nickname,
      rating: guide.rating || guide.score || 0,
      score: guide.score || guide.rating || 0,
      avatar: guide.avatarPath,
      avatarStatus: guide.avatarStatus,
      tags: guide.tags,
      tagsArray: Array.isArray(guide.tags) ? guide.tags : (guide.tags ? guide.tags.split(',') : []),
      cities: guide.cities,
      citiesArray: Array.isArray(guide.cities) ? guide.cities : (guide.cities ? guide.cities.split(',') : []),
      languages: guide.languages,
      languagesArray: Array.isArray(guide.languages) ? guide.languages : (guide.languages ? guide.languages.split(',') : []),
      match: guide.match || 0
    }))
  } catch (err) {
    console.error('加载推荐向导失败:', err)
    error.value = '加载向导信息失败，请稍后重试'
    // 如果API失败，使用备用数据
    guides.value = [
      { id: 1, name: 'Alice', nickname: 'Alice', rating: 4.6, score: 92, tagsArray: ['英语', '日语'], match: 85 },
      { id: 2, name: 'Bob', nickname: 'Bob', rating: 4.4, score: 88, tagsArray: ['中文', '韩语'], match: 78 },
      { id: 3, name: 'Chris', nickname: 'Chris', rating: 4.5, score: 90, tagsArray: ['英语', '法语'], match: 82 },
      { id: 4, name: 'Diana', nickname: 'Diana', rating: 4.3, score: 86, tagsArray: ['西班牙语', '葡萄牙语'], match: 75 }
    ]
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadFeaturedGuides()
})
</script>

<style scoped>
.guides {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.loading, .error {
  padding: 20px;
  text-align: center;
  color: #666;
  font-size: 14px;
}

.error {
  color: #e74c3c;
}
</style>
