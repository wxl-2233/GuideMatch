<template>
  <div class="login-page">
    <!-- 顶部导航 -->
    <div class="top-bar">
      <div class="logo">
        <div class="logo-icon">G</div>
        <span class="logo-text">GuideMatch</span>
      </div>
      <div class="lang-selector" @click.stop>
        <select v-model="currentLang" @change="changeLang" class="lang-select">
          <option value="zh">简体中文</option>
          <option value="en">English</option>
        </select>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="login-container">
      <div class="login-card">
        <!-- 标题 -->
        <h1 class="main-title">{{ t('login.title') }}</h1>
        <p class="subtitle">{{ mode === 'login' ? t('login.loginSubtitle') : t('login.registerSubtitle') }}</p>

        <!-- 第三方登录按钮 -->
        <div class="social-buttons">
          <button class="social-btn google" @click.stop="handleSocialLogin('google')">
            <svg width="18" height="18" viewBox="0 0 18 18">
              <path fill="#4285F4" d="M17.64 9.2c0-.637-.057-1.251-.164-1.84H9v3.481h4.844c-.209 1.125-.843 2.078-1.796 2.717v2.258h2.908c1.702-1.567 2.684-3.874 2.684-6.615z"/>
              <path fill="#34A853" d="M9 18c2.43 0 4.467-.806 5.965-2.184l-2.908-2.258c-.806.54-1.837.86-3.057.86-2.351 0-4.34-1.587-5.053-3.72H.957v2.332C2.438 15.983 5.482 18 9 18z"/>
              <path fill="#FBBC05" d="M3.952 10.698c-.18-.54-.282-1.117-.282-1.698s.102-1.158.282-1.698V4.97H.957C.348 6.175 0 7.55 0 9s.348 2.825.957 4.03l2.995-2.332z"/>
              <path fill="#EA4335" d="M9 3.58c1.321 0 2.508.454 3.44 1.345l2.582-2.58C13.463.891 11.426 0 9 0 5.482 0 2.438 2.017.957 4.97L3.952 7.302C4.665 5.167 6.654 3.58 9 3.58z"/>
            </svg>
            <span>{{ t('social.google') }}</span>
          </button>
          <button class="social-btn apple" @click.stop="handleSocialLogin('apple')">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="currentColor">
              <path d="M13.5 1.5c-.75 0-1.5.375-2.25.75-.75.375-1.5.75-2.25.75s-1.5-.375-2.25-.75C6.375 1.875 5.625 1.5 4.875 1.5c-1.5 0-3 1.125-3 3 0 3 2.25 5.25 4.5 7.5 1.125 1.125 2.25 2.25 3.375 3.375.375.375.75.75 1.125.75.375 0 .75-.375 1.125-.75 1.125-1.125 2.25-2.25 3.375-3.375 2.25-2.25 4.5-4.5 4.5-7.5 0-1.875-1.5-3-3-3z"/>
            </svg>
            <span>{{ t('social.apple') }}</span>
          </button>
          <button class="social-btn microsoft" @click.stop="handleSocialLogin('microsoft')">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="currentColor">
              <rect x="1" y="1" width="7" height="7" fill="#F25022"/>
              <rect x="10" y="1" width="7" height="7" fill="#7FBA00"/>
              <rect x="1" y="10" width="7" height="7" fill="#00A4EF"/>
              <rect x="10" y="10" width="7" height="7" fill="#FFB900"/>
            </svg>
            <span>{{ t('social.microsoft') }}</span>
          </button>
          <button class="social-btn passkey" @click.stop="handleSocialLogin('passkey')">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="9" cy="7" r="3"/>
              <path d="M3 16c0-3.314 2.686-6 6-6s6 2.686 6 6"/>
            </svg>
            <span>{{ t('social.passkey') }}</span>
          </button>
          <button class="social-btn sso" @click.stop="handleSocialLogin('sso')">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="2" y="4" width="14" height="10" rx="1"/>
              <path d="M6 8h6M6 11h4"/>
            </svg>
            <span>{{ t('social.sso') }}</span>
          </button>
        </div>

        <!-- 邮件登录 -->
        <div class="email-section">
          <label class="email-label">{{ t('login.email') }}</label>
          <input
            v-model="email"
            type="email"
            class="email-input"
            :placeholder="t('login.emailPlaceholder')"
          />
        </div>

        <!-- 注册模式下的分步表单 -->
        <div v-if="mode === 'register'" class="register-fields">
          <!-- 步骤指示器 -->
          <div class="steps-indicator">
            <div
              class="step"
              :class="{ active: registerStep === 1 }"
            >
              <span class="step-index">1</span>
              <span class="step-label">账号信息</span>
            </div>
            <div
              class="step"
              :class="{ active: registerStep === 2 }"
            >
              <span class="step-index">2</span>
              <span class="step-label">个人资料</span>
            </div>
          </div>

          <!-- 第一步：账号信息（必填） -->
          <template v-if="registerStep === 1">
            <div class="field-group">
              <label class="field-label">
                {{ t('login.nickname') }} <span class="required">*</span>
              </label>
              <input
                v-model="registerForm.nickname"
                type="text"
                class="email-input"
                :placeholder="t('login.nickname')"
                required
              />
            </div>
            <div class="field-group">
              <label class="field-label">
                {{ t('login.password') }} <span class="required">*</span>
              </label>
              <input
                v-model="registerForm.password"
                type="password"
                class="email-input"
                :placeholder="t('login.passwordPlaceholder')"
                required
              />
            </div>
            <p class="step-hint">
              {{ t('login.step1Hint') }}
            </p>
          </template>

          <!-- 第二步：个人资料（选填） -->
          <template v-else>
            <div class="field-group">
              <label class="field-label">
                {{ t('login.selectRole') }} <span class="required">*</span>
              </label>
              <select v-model="registerForm.role" class="role-select">
                <option value="tourist">{{ t('role.tourist') }}</option>
                <option value="guide">{{ t('role.guide') }}</option>
              </select>
            </div>
            <div class="field-group">
              <label class="field-label">
                {{ t('login.fullName') }} <span class="optional">（{{ t('login.optional') }}）</span>
              </label>
              <input
                v-model="registerForm.fullName"
                type="text"
                class="email-input"
                :placeholder="t('login.fullNamePlaceholder')"
              />
            </div>
            <div class="field-group">
              <label class="field-label">
                {{ t('login.phonenumber') }} <span class="optional">（{{ t('login.optional') }}）</span>
              </label>
              <input
                v-model="registerForm.phonenumber"
                type="tel"
                class="email-input"
                :placeholder="t('login.phonenumberPlaceholder')"
              />
            </div>
            <p class="step-hint">
              {{ t('login.step2Hint') }}
            </p>
          </template>
        </div>

        <!-- 登录模式下的密码字段 -->
        <div v-if="mode === 'login' && showPassword" class="password-field">
          <input
            v-model="password"
            type="password"
            class="email-input"
            :placeholder="t('login.passwordPlaceholder')"
          />
        </div>

        <!-- 继续按钮 -->
        <button class="continue-btn" @click.stop="handleSubmit" :disabled="loading">
          {{ loading ? t('common.loading') : getSubmitText }}
        </button>

        <!-- 切换模式 -->
        <p class="switch-mode">
          <span v-if="mode === 'login'">{{ t('login.switchToRegister') }}</span>
          <span v-else>{{ t('login.switchToLogin') }}</span>
          <span class="switch-link" @click="toggleMode">
            {{ mode === 'login' ? t('login.register') : t('common.login') }}
          </span>
        </p>
      </div>

      <!-- 底部条款 -->
      <p class="terms">
        {{ t('login.terms') }}
        <a href="#" class="terms-link" @click.prevent>{{ t('login.termsLink') }}</a>
        {{ t('common.and') }}
        <a href="#" class="terms-link" @click.prevent>{{ t('login.privacyLink') }}</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/request'
import { useI18n } from '@/composables/useI18n'

const { t, setLocale, locale } = useI18n()

const router = useRouter()

// 定义 emits
const emit = defineEmits(['close', 'submit'])

const mode = ref('login') // login | register
const registerStep = ref(1) // 注册分步：1 基本信息，2 个人信息
const email = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)

const registerForm = ref({
  nickname: '',
  password: '',
  fullName: '',
  phonenumber: '',
  role: 'tourist'
})

const toggleMode = (e) => {
  if (e) {
    e.preventDefault()
    e.stopPropagation()
  }
  mode.value = mode.value === 'login' ? 'register' : 'login'
  showPassword.value = false
  registerStep.value = 1
  email.value = ''
  password.value = ''
  registerForm.value = {
    nickname: '',
    password: '',
    fullName: '',
    phonenumber: '',
    role: 'tourist'
  }
}

const currentLang = computed({
  get: () => locale.value,
  set: (val) => setLocale(val)
})

const changeLang = () => {
  setLocale(currentLang.value)
}

const handleSocialLogin = (provider) => {
  console.log('Social login:', provider)
  alert(t('message.socialLoginNotImplemented', { provider }))
  // TODO: 实现第三方登录
}

const getSubmitText = computed(() => {
  if (mode.value === 'login') {
    return t('login.continue')
  }
  // 注册模式
  if (registerStep.value === 1) {
    return '下一步'
  }
  return t('login.register')
})

const handleSubmit = async () => {
  if (!email.value) {
    alert(t('message.enterEmail'))
    return
  }

  loading.value = true

  try {
    if (mode.value === 'login') {
      if (!showPassword.value) {
        showPassword.value = true
        loading.value = false
        return
      }

      // 登录
      const response = await request.post('/auth/login', {
        email: email.value,
        password: password.value
      })

      if (response.data && response.data.token) {
        localStorage.setItem('token', 'Bearer ' + response.data.token)
        localStorage.setItem('user', JSON.stringify(response.data))
        
        // 触发提交事件（用于弹窗模式）
        emit('submit')
        
        // 重置状态
        loading.value = false
        
        // 根据角色跳转
        const role = response.data.role || 'tourist'
        const targetRoute = role === 'admin' ? '/admin' 
                          : role === 'guide' ? '/guide/dashboard' 
                          : '/tourist'
        
        // 使用 window.location 直接跳转，避免 Vue Router 的组件更新问题
        setTimeout(() => {
          window.location.href = targetRoute
        }, 100)
      }
    } else {
      // 注册分步逻辑
      if (registerStep.value === 1) {
        // 校验第一步：账号信息
        if (!registerForm.value.nickname || !registerForm.value.password) {
          alert(t('message.fillCompleteInfo'))
          loading.value = false
          return
        }
        // 进入第二步
        registerStep.value = 2
        loading.value = false
        return
      }

      // 第二步：提交注册
      const response = await request.post('/auth/register', {
        email: email.value,
        nickname: registerForm.value.nickname,
        password: registerForm.value.password,
        fullName: registerForm.value.fullName,
        phonenumber: registerForm.value.phonenumber,
        role: registerForm.value.role
      })

      if (response.data && response.data.token) {
        localStorage.setItem('token', 'Bearer ' + response.data.token)
        localStorage.setItem('user', JSON.stringify(response.data))
        
        // 触发提交事件（用于弹窗模式）
        emit('submit')
        
        // 重置状态
        loading.value = false
        
        // 根据角色跳转到对应主界面
        const role = response.data.role || 'tourist'
        const targetRoute = role === 'admin'
          ? '/admin'
          : role === 'guide'
            ? '/guide/dashboard'
            : '/tourist'
        
        // 使用 window.location 直接跳转，避免 Vue Router 的组件更新问题
        setTimeout(() => {
          window.location.href = targetRoute
        }, 100)
      }
    }
  } catch (error) {
    console.error('登录/注册错误:', error)
    alert(error.response?.data?.error || error.message || '操作失败，请重试')
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  z-index: 1000;
  overflow-y: auto;
}

/* 顶部导航栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 10;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  width: 24px;
  height: 24px;
  background: #37352f;
  color: #fff;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.logo-text {
  font-size: 14px;
  font-weight: 500;
  color: #37352f;
}

.lang-selector {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #37352f;
  padding: 0;
}

.lang-select {
  padding: 4px 8px;
  border: 1px solid rgba(0, 0, 0, 0.09);
  border-radius: 4px;
  background: #fff;
  font-size: 14px;
  color: #37352f;
  cursor: pointer;
  outline: none;
}

.lang-select:hover {
  border-color: #2563eb;
}

/* 主内容区 */
.login-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  min-height: 100%;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 8px;
  padding: 32px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  margin: auto;
}

.main-title {
  font-size: 40px;
  font-weight: 700;
  color: #37352f;
  margin: 0 0 8px 0;
  text-align: center;
}

.subtitle {
  font-size: 16px;
  color: #787774;
  margin: 0 0 32px 0;
  text-align: center;
}

/* 第三方登录按钮 */
.social-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 24px;
}

.social-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  width: 100%;
  height: 36px;
  border: 1px solid rgba(0, 0, 0, 0.09);
  border-radius: 4px;
  background: #fff;
  color: #37352f;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
  user-select: none;
  pointer-events: auto;
}

.social-btn:hover {
  background: rgba(0, 0, 0, 0.02);
}

.social-btn:active {
  background: rgba(0, 0, 0, 0.05);
}

.social-btn svg {
  flex-shrink: 0;
}

/* 邮件登录 */
.email-section {
  margin-bottom: 24px;
}

.email-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #37352f;
  margin-bottom: 6px;
}

.email-input {
  width: 100%;
  height: 36px;
  padding: 0 12px;
  border: 1px solid rgba(0, 0, 0, 0.09);
  border-radius: 4px;
  font-size: 14px;
  color: #37352f;
  background: #fff;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.email-input:focus {
  border-color: #2563eb;
}

.email-input::placeholder {
  color: #9b9a97;
}

.email-hint {
  font-size: 12px;
  color: #787774;
  margin: 6px 0 0 0;
}

.register-fields {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.steps-indicator {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  gap: 8px;
}

.step {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  border-radius: 999px;
  background: #f3f4f6;
  color: #6b7280;
  font-size: 12px;
}

.step.active {
  background: #2563eb;
  color: #fff;
}

.step-index {
  width: 18px;
  height: 18px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
}

.step-label {
  white-space: nowrap;
}

.step-hint {
  font-size: 12px;
  color: #9b9a97;
}

.role-selector {
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-selector label {
  font-size: 14px;
  color: #37352f;
}

.role-select {
  flex: 1;
  height: 36px;
  padding: 0 12px;
  border: 1px solid rgba(0, 0, 0, 0.09);
  border-radius: 4px;
  font-size: 14px;
  color: #37352f;
  background: #fff;
  outline: none;
}

.password-field {
  margin-bottom: 24px;
}

/* 继续按钮 */
.continue-btn {
  width: 100%;
  height: 36px;
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
  margin-bottom: 16px;
  user-select: none;
  pointer-events: auto;
}

.continue-btn:hover:not(:disabled) {
  background: #1d4ed8;
}

.continue-btn:active:not(:disabled) {
  background: #1e40af;
}

.continue-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  pointer-events: none;
}

/* 切换模式 */
.switch-mode {
  text-align: center;
  font-size: 14px;
  color: #787774;
  margin: 0;
}

.switch-link {
  color: #2563eb;
  cursor: pointer;
  margin-left: 4px;
  user-select: none;
  pointer-events: auto;
}

.switch-link:hover {
  text-decoration: underline;
}

/* 字段组 */
.field-group {
  margin-bottom: 16px;
}

.field-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #37352f;
  margin-bottom: 6px;
}

.required {
  color: #ef4444;
}

.optional {
  color: #787774;
  font-size: 12px;
  font-weight: normal;
}

.step-hint {
  font-size: 12px;
  color: #787774;
  margin: 12px 0 0 0;
  line-height: 1.5;
}

/* 底部条款 */
.terms {
  margin-top: 24px;
  font-size: 12px;
  color: #787774;
  text-align: center;
}

.terms-link {
  color: #2563eb;
  text-decoration: none;
}

.terms-link:hover {
  text-decoration: underline;
}
</style>