import { createI18n } from 'vue-i18n'
import zh from './locales/zh.js'
import en from './locales/en.js'

const messages = {
  zh,
  en
}

// 从 localStorage 获取保存的语言设置
const savedLocale = localStorage.getItem('locale') || 'zh'

const i18n = createI18n({
  locale: savedLocale,
  fallbackLocale: 'zh',
  messages
})

export default i18n
