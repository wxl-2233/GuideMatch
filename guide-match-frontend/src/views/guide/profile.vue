<template>
  <div class="guide-profile-page">
    <PageContainer>
      <h1 class="page-title">{{ t('guideProfile.title') }}</h1>

      <div v-if="loading" class="loading">{{ t('guideProfile.loading') }}</div>
      <div v-else-if="!guideInfo && !isCreating" class="empty">
        <p>{{ t('guideProfile.noProfile') }}</p>
        <button class="btn btn-primary" @click="startCreate">{{ t('guideProfile.createProfile') }}</button>
      </div>
      <div v-else class="profile-form">
        <div class="form-card">
          <h2 class="card-title">{{ t('guideProfile.basicInfo') }}</h2>
          
          <div class="form-group">
            <label>{{ t('guideProfile.languages') }} <span class="required">*</span></label>
            <div class="tags-input">
              <input
                v-model="tagInput"
                type="text"
                :placeholder="t('guideProfile.languagesPlaceholder')"
                class="input"
                @keyup.enter="addTag('languages')"
              />
              <div class="tags-list">
                <span
                  v-for="(lang, index) in formData.languagesArray"
                  :key="index"
                  class="tag"
                  @click="editTag('languages', index, lang)"
                >
                  {{ lang }}
                  <span class="tag-remove" @click.stop="removeTag('languages', index)">×</span>
                </span>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>{{ t('guideProfile.cities') }} <span class="required">*</span></label>
            <div class="tags-input">
              <input
                v-model="cityInput"
                type="text"
                :placeholder="t('guideProfile.citiesPlaceholder')"
                class="input"
                @keyup.enter="addTag('cities')"
              />
              <div class="tags-list">
                <span
                  v-for="(city, index) in formData.citiesArray"
                  :key="index"
                  class="tag"
                  @click="editTag('cities', index, city)"
                >
                  {{ city }}
                  <span class="tag-remove" @click.stop="removeTag('cities', index)">×</span>
                </span>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>{{ t('guideProfile.tags') }}</label>
            <div class="tags-input">
              <input
                v-model="labelInput"
                type="text"
                :placeholder="t('guideProfile.tagsPlaceholder')"
                class="input"
                @keyup.enter="addTag('tags')"
              />
              <div class="tags-list">
                <span
                  v-for="(tag, index) in formData.tagsArray"
                  :key="index"
                  class="tag"
                  @click="editTag('tags', index, tag)"
                >
                  {{ tag }}
                  <span class="tag-remove" @click.stop="removeTag('tags', index)">×</span>
                </span>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label>{{ t('guideProfile.bio') }} <span class="required">*</span></label>
            <textarea
              v-model="formData.bio"
              rows="5"
              :placeholder="t('guideProfile.bioPlaceholder')"
              class="textarea"
            ></textarea>
          </div>

          <div class="form-group">
            <label>{{ t('guideProfile.hourlyRate') }}</label>
            <input
              v-model.number="formData.hourlyRate"
              type="number"
              min="0"
              step="0.01"
              class="input"
            />
          </div>

          <div class="form-group">
            <label>{{ t('guideProfile.dailyRate') }} <span class="required">*</span></label>
            <input
              v-model.number="formData.dailyRate"
              type="number"
              min="0"
              step="0.01"
              class="input"
            />
          </div>

          <div class="form-group">
            <label>{{ t('guideProfile.certificates') }}</label>
            <div class="tags-input">
              <input
                v-model="certificateInput"
                type="text"
                :placeholder="t('guideProfile.certificatesPlaceholder')"
                class="input"
                @keyup.enter="addTag('certificates')"
              />
              <div class="tags-list">
                <span
                  v-for="(cert, index) in formData.certificatesArray"
                  :key="index"
                  class="tag"
                  @click="editTag('certificates', index, cert)"
                >
                  {{ cert }}
                  <span class="tag-remove" @click.stop="removeTag('certificates', index)">×</span>
                </span>
              </div>
            </div>
          </div>

          <div v-if="guideInfo" class="info-section">
            <h3>{{ t('guideProfile.verificationStatus') }}</h3>
            <div class="status-info">
              <span :class="['status-badge', getStatusClass()]">
                {{ getStatusText() }}
              </span>
              <span v-if="guideInfo.rejectionReason" class="rejection-reason">
                {{ t('guideProfile.rejectionReason') }}：{{ guideInfo.rejectionReason }}
              </span>
            </div>
          </div>

          <div class="form-actions">
            <button class="btn btn-primary" @click="saveProfile" :disabled="saving">
              {{ saving ? t('guideProfile.saving') : t('guideProfile.saveProfile') }}
            </button>
            <button v-if="guideInfo" class="btn btn-danger" @click="deleteProfile" :disabled="deleting">
              {{ deleting ? t('guideProfile.deleting') : t('guideProfile.deleteProfile') }}
            </button>
          </div>
        </div>
      </div>
    </PageContainer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from '@/composables/useI18n'
import request from '@/api/request'
import PageContainer from '@/components/layout/PageContainer.vue'

const { t } = useI18n()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const isCreating = ref(false)
const guideInfo = ref(null)

const tagInput = ref('')
const cityInput = ref('')
const labelInput = ref('')
const certificateInput = ref('')

const formData = ref({
  languagesArray: [],
  citiesArray: [],
  tagsArray: [],
  certificatesArray: [],
  bio: '',
  hourlyRate: 0,
  dailyRate: 0
})

const loadGuideInfo = async () => {
  loading.value = true
  try {
    const response = await request.get('/guides/profile')
    guideInfo.value = response.data
    
    // 调试：打印原始数据
    console.log('原始数据:', {
      languages: guideInfo.value.languages,
      cities: guideInfo.value.cities,
      tags: guideInfo.value.tags,
      certificates: guideInfo.value.certificates,
      bio: guideInfo.value.bio
    })
    
    // 填充表单数据，添加错误处理避免乱码
    try {
      if (guideInfo.value.languages) {
        // 如果已经是数组，直接使用；否则尝试解析 JSON
        if (Array.isArray(guideInfo.value.languages)) {
          formData.value.languagesArray = guideInfo.value.languages
        } else {
          formData.value.languagesArray = JSON.parse(guideInfo.value.languages)
        }
      } else {
        formData.value.languagesArray = []
      }
    } catch (e) {
      console.error('解析语言数据失败:', e, '原始数据:', guideInfo.value.languages)
      formData.value.languagesArray = []
    }
    
    try {
      if (guideInfo.value.cities) {
        if (Array.isArray(guideInfo.value.cities)) {
          formData.value.citiesArray = guideInfo.value.cities
        } else {
          formData.value.citiesArray = JSON.parse(guideInfo.value.cities)
        }
      } else {
        formData.value.citiesArray = []
      }
    } catch (e) {
      console.error('解析城市数据失败:', e, '原始数据:', guideInfo.value.cities)
      formData.value.citiesArray = []
    }
    
    try {
      if (guideInfo.value.tags) {
        if (Array.isArray(guideInfo.value.tags)) {
          formData.value.tagsArray = guideInfo.value.tags
        } else {
          formData.value.tagsArray = JSON.parse(guideInfo.value.tags)
        }
      } else {
        formData.value.tagsArray = []
      }
    } catch (e) {
      console.error('解析标签数据失败:', e, '原始数据:', guideInfo.value.tags)
      formData.value.tagsArray = []
    }
    
    try {
      if (guideInfo.value.certificates) {
        if (Array.isArray(guideInfo.value.certificates)) {
          formData.value.certificatesArray = guideInfo.value.certificates
        } else {
          formData.value.certificatesArray = JSON.parse(guideInfo.value.certificates)
        }
      } else {
        formData.value.certificatesArray = []
      }
    } catch (e) {
      console.error('解析证书数据失败:', e, '原始数据:', guideInfo.value.certificates)
      formData.value.certificatesArray = []
    }
    
    formData.value.bio = guideInfo.value.bio || ''
    formData.value.hourlyRate = guideInfo.value.hourlyRate || 0
    formData.value.dailyRate = guideInfo.value.dailyRate || 0
  } catch (error) {
    if (error.response?.status === 400) {
      // 没有向导资料，可以创建
      isCreating.value = true
    } else {
      console.error('加载向导信息失败:', error)
      alert(t('guideProfile.errors.loadFailed'))
    }
  } finally {
    loading.value = false
  }
}

const startCreate = () => {
  isCreating.value = true
  formData.value = {
    languagesArray: [],
    citiesArray: [],
    tagsArray: [],
    certificatesArray: [],
    bio: '',
    hourlyRate: 0,
    dailyRate: 0
  }
}

const addTag = (type) => {
  let input = ''
  let targetArray = null
  let inputRef = null
  
  if (type === 'languages') {
    input = tagInput.value ? tagInput.value.trim() : ''
    targetArray = formData.value.languagesArray
    inputRef = tagInput
  } else if (type === 'cities') {
    input = cityInput.value ? cityInput.value.trim() : ''
    targetArray = formData.value.citiesArray
    inputRef = cityInput
  } else if (type === 'tags') {
    input = labelInput.value ? labelInput.value.trim() : ''
    targetArray = formData.value.tagsArray
    inputRef = labelInput
  } else if (type === 'certificates') {
    input = certificateInput.value ? certificateInput.value.trim() : ''
    targetArray = formData.value.certificatesArray
    inputRef = certificateInput
  }
  
  // 如果输入为空，直接返回
  if (!input) {
    return
  }
  
  // 检查是否已存在（不区分大小写）
  const exists = targetArray.some(
    item => item && item.toLowerCase() === input.toLowerCase()
  )
  
  if (!exists) {
    targetArray.push(input)
    inputRef.value = ''
  } else {
    // 如果已存在，清空输入框
    inputRef.value = ''
  }
}

const removeTag = (type, index) => {
  if (type === 'languages') {
    formData.value.languagesArray.splice(index, 1)
  } else if (type === 'cities') {
    formData.value.citiesArray.splice(index, 1)
  } else if (type === 'tags') {
    formData.value.tagsArray.splice(index, 1)
  } else if (type === 'certificates') {
    formData.value.certificatesArray.splice(index, 1)
  }
}

const editTag = (type, index, value) => {
  // 点击标签时，将标签内容放入对应的输入框，并删除该标签
  if (type === 'languages') {
    tagInput.value = value
    formData.value.languagesArray.splice(index, 1)
  } else if (type === 'cities') {
    cityInput.value = value
    formData.value.citiesArray.splice(index, 1)
  } else if (type === 'tags') {
    labelInput.value = value
    formData.value.tagsArray.splice(index, 1)
  } else if (type === 'certificates') {
    certificateInput.value = value
    formData.value.certificatesArray.splice(index, 1)
  }
}

const saveProfile = async () => {
  // 在保存前，自动将输入框中的内容添加到数组中（如果输入框不为空）
  // 但只添加一次，避免重复
  if (tagInput.value.trim()) {
    addTag('languages')
  }
  if (cityInput.value.trim()) {
    addTag('cities')
  }
  if (labelInput.value.trim()) {
    addTag('tags')
  }
  if (certificateInput.value.trim()) {
    addTag('certificates')
  }
  
  // 调试：打印保存前的数据
  console.log('保存前的数据:', {
    languagesArray: formData.value.languagesArray,
    citiesArray: formData.value.citiesArray,
    tagsArray: formData.value.tagsArray,
    certificatesArray: formData.value.certificatesArray,
    tagInput: tagInput.value,
    cityInput: cityInput.value,
    labelInput: labelInput.value,
    certificateInput: certificateInput.value
  })
  
  // 验证必填字段
  if (formData.value.languagesArray.length === 0) {
    alert(t('guideProfile.validation.languagesRequired'))
    return
  }
  if (formData.value.citiesArray.length === 0) {
    alert(t('guideProfile.validation.citiesRequired'))
    return
  }
  if (!formData.value.bio.trim()) {
    alert(t('guideProfile.validation.bioRequired'))
    return
  }
  if (!formData.value.dailyRate || formData.value.dailyRate <= 0) {
    alert(t('guideProfile.validation.dailyRateRequired'))
    return
  }

  saving.value = true
  try {
    // 确保数组中没有重复项（不区分大小写）
    const uniqueLanguages = []
    const seenLanguages = new Set()
    for (const lang of formData.value.languagesArray) {
      const lower = lang.toLowerCase()
      if (!seenLanguages.has(lower)) {
        seenLanguages.add(lower)
        uniqueLanguages.push(lang)
      }
    }
    
    const uniqueCities = []
    const seenCities = new Set()
    for (const city of formData.value.citiesArray) {
      const lower = city.toLowerCase()
      if (!seenCities.has(lower)) {
        seenCities.add(lower)
        uniqueCities.push(city)
      }
    }
    
    const uniqueTags = []
    const seenTags = new Set()
    for (const tag of formData.value.tagsArray) {
      const lower = tag.toLowerCase()
      if (!seenTags.has(lower)) {
        seenTags.add(lower)
        uniqueTags.push(tag)
      }
    }
    
    const uniqueCertificates = []
    const seenCertificates = new Set()
    for (const cert of formData.value.certificatesArray) {
      const lower = cert.toLowerCase()
      if (!seenCertificates.has(lower)) {
        seenCertificates.add(lower)
        uniqueCertificates.push(cert)
      }
    }
    
    const data = {
      languages: JSON.stringify(uniqueLanguages),
      cities: JSON.stringify(uniqueCities),
      tags: JSON.stringify(uniqueTags),
      bio: formData.value.bio,
      hourlyRate: formData.value.hourlyRate,
      dailyRate: formData.value.dailyRate,
      certificates: JSON.stringify(uniqueCertificates)
    }
    
    // 调试：打印即将保存的数据
    console.log('即将保存的数据:', data)

    await request.post('/guides/profile', data)
    alert(t('guideProfile.saveSuccess'))
    
    // 清空所有输入框
    tagInput.value = ''
    cityInput.value = ''
    labelInput.value = ''
    certificateInput.value = ''
    
    await loadGuideInfo()
    isCreating.value = false
  } catch (error) {
    console.error('保存失败:', error)
    alert(t('guideProfile.errors.saveFailed') + ': ' + (error.response?.data?.error || error.message))
  } finally {
    saving.value = false
  }
}

const deleteProfile = async () => {
  if (!confirm(t('guideProfile.deleteConfirm'))) {
    return
  }

  deleting.value = true
  try {
    await request.delete('/guides/profile')
    alert(t('guideProfile.deleteSuccess'))
    guideInfo.value = null
    isCreating.value = false
    formData.value = {
      languagesArray: [],
      citiesArray: [],
      tagsArray: [],
      certificatesArray: [],
      bio: '',
      hourlyRate: 0,
      dailyRate: 0
    }
  } catch (error) {
    console.error('删除失败:', error)
    alert(t('guideProfile.errors.deleteFailed') + ': ' + (error.response?.data?.error || error.message))
  } finally {
    deleting.value = false
  }
}

const getStatusText = () => {
  if (!guideInfo.value) return ''
  const status = guideInfo.value.verificationStatus || 'pending'
  const map = {
    pending: t('profile.pending'),
    approved: t('profile.approved'),
    rejected: t('profile.rejected')
  }
  return map[status] || status
}

const getStatusClass = () => {
  if (!guideInfo.value) return 'pending'
  return guideInfo.value.verificationStatus || 'pending'
}

onMounted(() => {
  loadGuideInfo()
})
</script>

<style scoped>
.guide-profile-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24px 0;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #333;
}

.loading,
.empty {
  text-align: center;
  padding: 48px;
  color: #999;
}

.profile-form {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.required {
  color: #ef4444;
}

.input,
.textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.input:focus,
.textarea:focus {
  border-color: #2563eb;
}

.textarea {
  resize: vertical;
}

.tags-input {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: #f0f0f0;
  border-radius: 16px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: background-color 0.2s;
}

.tag:hover {
  background: #e0e0e0;
}

.tag-remove {
  cursor: pointer;
  color: #999;
  font-size: 18px;
  line-height: 1;
}

.tag-remove:hover {
  color: #ef4444;
}

.info-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #eee;
}

.info-section h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #333;
}

.status-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.status-badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  width: fit-content;
}

.status-badge.pending {
  background: #fef3c7;
  color: #d97706;
}

.status-badge.approved {
  background: #d1fae5;
  color: #059669;
}

.status-badge.rejected {
  background: #fee2e2;
  color: #dc2626;
}

.rejection-reason {
  font-size: 14px;
  color: #dc2626;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #eee;
}

.btn {
  padding: 10px 24px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: #2563eb;
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: #1d4ed8;
}

.btn-danger {
  background: #ef4444;
  color: #fff;
}

.btn-danger:hover:not(:disabled) {
  background: #dc2626;
}
</style>
