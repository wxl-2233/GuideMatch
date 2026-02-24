<template>
  <PageContainer>
    <Hero />

    <Filter
      v-model="keyword"
      @search="handleSearch"
    />

    <List
      :guides="guides"
      @view="viewGuide"
    />
  </PageContainer>
</template>

<script setup>
import { ref } from 'vue'

import PageContainer from '@/components/layout/PageContainer.vue'

import Hero from './sections/Hero.vue'
import Filter from './sections/Filter.vue'
import List from './sections/List.vue'

const keyword = ref('')

const guides = ref([
  {
    id: 1,
    name: 'Alice',
    avatar: '/default-avatar.png',
    rating: 5,
    tags: ['留学', '计算机'],
    match: 92,
  },
  {
    id: 2,
    name: 'Bob',
    avatar: '/default-avatar.png',
    rating: 4,
    tags: ['考研', '数学'],
    match: 85,
  },
])

const handleSearch = (payload) => {
  console.log('guide search:', payload)
  // TODO: 接口请求
}

const viewGuide = (guide) => {
  console.log('view guide:', guide)
  // TODO: router.push(`/guide/${guide.id}`)
}
</script>
