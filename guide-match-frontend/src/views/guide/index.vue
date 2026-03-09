<template>
  <PageContainer>
    <Hero />

    <Filter
      v-model="searchKeyword"
      @search="handleSearch"
    />

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <List
      v-else
      :guides="guides"
      @view="viewGuide"
    />
  </PageContainer>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import PageContainer from '@/components/layout/PageContainer.vue'
import Hero from './sections/Hero.vue'
import Filter from './sections/Filter.vue'
import List from './sections/List.vue'
import request from '@/api/request'

const router = useRouter()
const searchKeyword = ref('')
const guides = ref([])
const loading = ref(false)
const error = ref('')
const searchParams = ref({
  keyword: '',
  tags: [],
  page: 1,
  size: 20
})

const loadGuides = async (params = {}) => {
  loading.value = true
  error.value = ''
  
  try {
    const response = await request.get('/guides/list', {
      params: {
        ...searchParams.value,
        ...params
      }
    })
    
    // 处理返回的数据
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
      match: guide.match || 0,
      price: guide.dailyRate || guide.price || 0,
      period: '天'
    }))
  } catch (err) {
    console.error('加载向导列表失败:', err)
    error.value = '加载向导信息失败，请稍后重试'
    guides.value = []
  } finally {
    loading.value = false
  }
}

const handleSearch = (payload) => {
  console.log('guide search:', payload)
  
  // 更新搜索参数
  searchParams.value = {
    ...searchParams.value,
    keyword: payload.keyword || '',
    tags: payload.tags || [],
    page: 1
  }
  
  // 重新加载数据
  loadGuides()
}

const viewGuide = (guide) => {
  console.log('view guide:', guide)
  router.push(`/guide/${guide.id}`)
}

// 监听搜索关键词变化
watch(searchKeyword, (newKeyword) => {
  searchParams.value.keyword = newKeyword
})

onMounted(() => {
  loadGuides()
})
</script>

<style scoped>
.loading, .error {
  padding: 40px 20px;
  text-align: center;
  color: #666;
  font-size: 16px;
}

.error {
  color: #e74c3c;
}
</style>
