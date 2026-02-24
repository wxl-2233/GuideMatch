import { ref, computed } from 'vue'
import zh from '../i18n/locales/zh.js'
import en from '../i18n/locales/en.js'

const locales = {
  zh: zh.default || zh,
  en: en.default || en
}

// 当前语言
const currentLocale = ref(localStorage.getItem('locale') || 'zh')

// 切换语言
export function useI18n() {
  const t = (key, params = {}) => {
    try {
      if (!key) return ''
      
      const keys = key.split('.')
      let value = locales[currentLocale.value]
      
      if (!value) {
        console.warn(`Locale ${currentLocale.value} not found, falling back to zh`)
        value = locales.zh
      }
      
      for (const k of keys) {
        if (value && typeof value === 'object') {
          value = value[k]
        } else {
          value = undefined
          break
        }
      }
      
      if (!value || typeof value !== 'string') {
        console.warn(`Translation key "${key}" not found`)
        return key
      }
      
      // 支持参数替换，例如 {n} 会被替换为 params.n
      if (Object.keys(params).length > 0) {
        return value.replace(/\{(\w+)\}/g, (match, paramKey) => {
          return params[paramKey] !== undefined ? params[paramKey] : match
        })
      }
      
      return value
    } catch (error) {
      console.error('Translation error:', error, 'key:', key)
      return key
    }
  }
  
  const setLocale = (locale) => {
    if (locales[locale]) {
      currentLocale.value = locale
      localStorage.setItem('locale', locale)
    }
  }
  
  const locale = computed(() => currentLocale.value)
  
  return {
    t,
    setLocale,
    locale
  }
}
