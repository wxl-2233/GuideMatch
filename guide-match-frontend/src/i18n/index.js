import { createI18n } from 'vue-i18n'
import zh from './locales/zh.js'
import en from './locales/en.js'
import ko from './locales/ko.js'
import id from './locales/id.js'
import th from './locales/th.js'

const messages = {
  zh,
  en,
  ko,
  id,
  th
}

// 从 localStorage 获取保存的语言设置
const savedLocale = localStorage.getItem('locale') || 'zh'

const i18n = createI18n({
  locale: savedLocale,
  fallbackLocale: 'zh',
  messages
})

export default i18n
