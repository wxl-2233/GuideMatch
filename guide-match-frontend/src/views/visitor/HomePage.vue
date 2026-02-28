<template>
  <div class="home-page">
    <!-- 左侧图片区域 -->
    <div class="left-section">
      <div class="image-carousel">
        <div
          v-for="(image, index) in images"
          :key="index"
          class="carousel-slide"
          :class="{ active: currentIndex === index }"
          :style="{ backgroundImage: `url(${image})` }"
        />
        <!-- 轮播指示器 -->
        <div class="carousel-indicators">
          <button
            v-for="(image, index) in images"
            :key="index"
            class="indicator"
            :class="{ active: currentIndex === index }"
            @click="goToSlide(index)"
          />
        </div>
      </div>
    </div>

    <!-- 右侧登录注册区域 -->
    <div class="right-section">
      <div class="auth-container">
        <!-- 标题 -->
        <div class="auth-header">
          <h1 class="auth-title">{{ t('home.title') }}</h1>
          <p class="auth-subtitle">{{ t('home.subtitle') }}</p>
        </div>

        <!-- 登录模式 -->
        <div v-if="mode === 'login'" class="auth-form">
          <div class="form-group">
            <label>{{ t('login.email') }}</label>
            <input
              v-model="loginForm.email"
              type="email"
              :placeholder="t('login.emailPlaceholder')"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label>{{ t('login.password') }}</label>
            <input
              v-model="loginForm.password"
              type="password"
              :placeholder="t('login.passwordPlaceholder')"
              class="form-input"
            />
          </div>
          <div class="forgot-password">
            <span class="link" @click="mode = 'forgot-password'">
              {{ t('login.forgotPassword') }}
            </span>
          </div>
          <button class="auth-btn" @click="handleLogin" :disabled="loading">
            {{ loading ? t('common.loading') : t('common.login') }}
          </button>
        </div>

        <!-- 注册模式 -->
        <div v-else-if="mode === 'register'" class="auth-form">
          <!-- 步骤指示器 -->
          <div class="steps-indicator">
            <div
              class="step"
              :class="{ active: registerStep === 1 }"
            >
              <span class="step-index">1</span>
              <span class="step-label">{{ t('home.accountInfo') }}</span>
            </div>
            <div
              class="step"
              :class="{ active: registerStep === 2 }"
            >
              <span class="step-index">2</span>
              <span class="step-label">{{ t('home.personalProfile') }}</span>
            </div>
          </div>

          <!-- 第一步：账号信息 -->
          <template v-if="registerStep === 1">
            <div class="form-group">
              <label>{{ t('login.email') }}</label>
              <input
                v-model="registerForm.email"
                type="email"
                :placeholder="t('login.emailPlaceholder')"
                class="form-input"
              />
            </div>
            <div class="form-group">
              <label>{{ t('login.nickname') }} <span class="required">*</span></label>
              <input
                v-model="registerForm.nickname"
                type="text"
                :placeholder="t('login.nickname')"
                class="form-input"
                required
              />
            </div>
            <div class="form-group">
              <label>{{ t('login.password') }} <span class="required">*</span></label>
              <input
                v-model="registerForm.password"
                type="password"
                :placeholder="t('login.passwordPlaceholder')"
                class="form-input"
                required
              />
            </div>
            <p class="step-hint">{{ t('home.step1Hint') }}</p>
          </template>

          <!-- 第二步：个人资料 -->
          <template v-else>
            <div class="form-group">
              <label>{{ t('login.selectRole') }} <span class="required">*</span></label>
              <select v-model="registerForm.role" class="form-select">
                <option value="tourist">{{ t('role.tourist') }}</option>
                <option value="guide">{{ t('role.guide') }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>{{ t('login.fullName') }} <span class="optional">（{{ t('login.optional') }}）</span></label>
              <input
                v-model="registerForm.fullName"
                type="text"
                :placeholder="t('login.fullNamePlaceholder')"
                class="form-input"
              />
            </div>
            <div class="form-group">
              <label>{{ t('login.phonenumber') }} <span class="optional">（{{ t('login.optional') }}）</span></label>
              <input
                v-model="registerForm.phonenumber"
                type="tel"
                :placeholder="t('login.phonenumberPlaceholder')"
                class="form-input"
              />
            </div>
            <p class="step-hint">{{ t('home.step2Hint') }}</p>
          </template>

          <button class="auth-btn" @click="handleRegister" :disabled="loading">
            {{ loading ? t('common.loading') : getRegisterButtonText() }}
          </button>
        </div>

        <!-- 忘记密码模式 -->
        <div v-else-if="mode === 'forgot-password'" class="auth-form">
          <div class="form-group">
            <label>{{ t('login.email') }}</label>
            <input
              v-model="resetForm.email"
              type="email"
              :placeholder="t('login.emailPlaceholder')"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label>{{ t('login.nickname') }}</label>
            <input
              v-model="resetForm.nickname"
              type="text"
              :placeholder="t('login.nickname')"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label>{{ t('login.newPassword') }}</label>
            <input
              v-model="resetForm.newPassword"
              type="password"
              :placeholder="t('login.newPasswordPlaceholder')"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label>{{ t('login.confirmPassword') }}</label>
            <input
              v-model="resetForm.confirmPassword"
              type="password"
              :placeholder="t('login.confirmPasswordPlaceholder')"
              class="form-input"
            />
          </div>
          <button class="auth-btn" @click="handleResetPassword" :disabled="loading">
            {{ loading ? t('common.loading') : t('login.resetPassword') }}
          </button>
        </div>

        <!-- 切换模式 -->
        <div class="auth-switch">
          <span v-if="mode === 'login'">
            {{ t('login.switchToRegister') }}
            <span class="link" @click="switchToRegister">{{ t('login.register') }}</span>
          </span>
          <span v-else-if="mode === 'register'">
            {{ t('login.switchToLogin') }}
            <span class="link" @click="switchToLogin">{{ t('common.login') }}</span>
          </span>
          <span v-else-if="mode === 'forgot-password'">
            <span class="link" @click="switchToLogin">{{ t('login.backToLogin') }}</span>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from '@/composables/useI18n'
import request from '@/api/request'

const { t } = useI18n()
const router = useRouter()

// 轮播图相关
const images = [
  new URL('@/assets/scenic/1.jpg', import.meta.url).href,
  new URL('@/assets/scenic/2.jpg', import.meta.url).href,
  new URL('@/assets/scenic/3.jpg', import.meta.url).href,
  new URL('@/assets/scenic/4.jpg', import.meta.url).href
]

const currentIndex = ref(0)
let intervalId = null
let isMounted = false

const goToSlide = (index) => {
  if (!isMounted) return
  currentIndex.value = index
  resetInterval()
}

const nextSlide = () => {
  if (isMounted) {
    currentIndex.value = (currentIndex.value + 1) % images.length
  }
}

const resetInterval = () => {
  if (intervalId) {
    clearInterval(intervalId)
    intervalId = null
  }
  if (isMounted) {
    intervalId = setInterval(nextSlide, 5000)
  }
}

// 认证相关
const mode = ref('login') // 'login', 'register', 'forgot-password'
const loading = ref(false)
const registerStep = ref(1)

const loginForm = ref({
  email: '',
  password: ''
})

const registerForm = ref({
  email: '',
  nickname: '',
  password: '',
  fullName: '',
  phonenumber: '',
  role: 'tourist'
})

const resetForm = ref({
  email: '',
  nickname: '',
  newPassword: '',
  confirmPassword: ''
})

const getRegisterButtonText = () => {
  if (registerStep.value === 1) return t('login.continue')
  return t('common.register')
}

const switchToLogin = () => {
  mode.value = 'login'
  resetForms()
}

const switchToRegister = () => {
  mode.value = 'register'
  registerStep.value = 1
  resetForms()
}

const resetForms = () => {
  loginForm.value = { email: '', password: '' }
  registerForm.value = {
    email: '',
    nickname: '',
    password: '',
    fullName: '',
    phonenumber: '',
    role: 'tourist'
  }
  resetForm.value = {
    email: '',
    nickname: '',
    newPassword: '',
    confirmPassword: ''
  }
}

const handleLogin = async () => {
  if (!loginForm.value.email || !loginForm.value.password) {
    alert(t('message.fillCompleteInfo'))
    return
  }

  loading.value = true
  try {
    const response = await request.post('/auth/login', {
      email: loginForm.value.email,
      password: loginForm.value.password
    })

    if (response.data && response.data.token) {
      localStorage.setItem('token', 'Bearer ' + response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data))
      
      // 根据角色跳转到不同页面
      const role = response.data.role || 'tourist'
      const targetRoute = role === 'admin'
        ? '/admin'
        : role === 'guide'
          ? '/guide/dashboard'
          : '/tourist'
      
      router.push(targetRoute)
    }
  } catch (error) {
    console.error('登录失败:', error)
    alert(error.response?.data?.error || error.message || '登录失败，请重试')
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (registerStep.value === 1) {
    // 校验第一步
    if (!registerForm.value.email || !registerForm.value.nickname || !registerForm.value.password) {
      alert(t('message.fillCompleteInfo'))
      return
    }
    registerStep.value = 2
    return
  }

  // 提交注册
  loading.value = true
  try {
    const response = await request.post('/auth/register', {
      email: registerForm.value.email,
      nickname: registerForm.value.nickname,
      password: registerForm.value.password,
      fullName: registerForm.value.fullName,
      phonenumber: registerForm.value.phonenumber,
      role: registerForm.value.role
    })

    if (response.data && response.data.token) {
      localStorage.setItem('token', 'Bearer ' + response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data))
      
      // 根据角色跳转
      const role = response.data.role || 'tourist'
      const targetRoute = role === 'admin'
        ? '/admin'
        : role === 'guide'
          ? '/guide/dashboard'
          : '/tourist'
      
      router.push(targetRoute)
    }
  } catch (error) {
    console.error('注册失败:', error)
    alert(error.response?.data?.error || error.message || '注册失败，请重试')
  } finally {
    loading.value = false
  }
}

const handleResetPassword = async () => {
  if (!resetForm.value.email || !resetForm.value.email.trim()) {
    alert('请输入邮箱')
    return
  }
  if (!resetForm.value.nickname || !resetForm.value.nickname.trim()) {
    alert('请输入用户名')
    return
  }
  if (!resetForm.value.newPassword || resetForm.value.newPassword.length < 6) {
    alert(t('login.newPasswordPlaceholder'))
    return
  }
  if (resetForm.value.newPassword !== resetForm.value.confirmPassword) {
    alert(t('login.passwordMismatch'))
    return
  }

  loading.value = true
  try {
    const response = await request.post('/auth/reset-password', {
      email: resetForm.value.email,
      nickname: resetForm.value.nickname,
      newPassword: resetForm.value.newPassword
    })

    if (response.data) {
      alert(t('login.resetSuccess'))
      switchToLogin()
    }
  } catch (error) {
    console.error('重置密码失败:', error)
    alert(error.response?.data?.error || error.message || '重置密码失败，请重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  isMounted = true
  resetInterval()
})

onBeforeUnmount(() => {
  isMounted = false
  if (intervalId) {
    clearInterval(intervalId)
    intervalId = null
  }
})
</script>

<style scoped>
.home-page {
  display: flex;
  height: 100vh;
  width: 100%;
  overflow: hidden;
}

/* 左侧图片区域 */
.left-section {
  flex: 0 0 45%;
  position: relative;
  overflow: hidden;
}

.image-carousel {
  position: relative;
  width: 100%;
  height: 100%;
}

.carousel-slide {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  opacity: 0;
  transition: opacity 1s ease-in-out;
}

.carousel-slide.active {
  opacity: 1;
}

.carousel-indicators {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 2;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.indicator.active {
  background: rgba(255, 255, 255, 0.9);
  width: 14px;
  height: 14px;
}

/* 右侧登录区域 */
.right-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px;
}

.auth-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  width: 100%;
  max-width: 450px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.auth-header {
  text-align: center;
  margin-bottom: 30px;
}

.auth-title {
  font-size: 32px;
  font-weight: 700;
  color: #37352f;
  margin: 0 0 10px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.auth-subtitle {
  font-size: 16px;
  color: #787774;
  margin: 0;
}

.auth-form {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #37352f;
  margin-bottom: 8px;
}

.form-input,
.form-select {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e5e5e5;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.2s;
  box-sizing: border-box;
  background: #fff;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.required {
  color: #ef4444;
  margin-left: 4px;
}

.optional {
  color: #787774;
  font-size: 12px;
  font-weight: normal;
}

.forgot-password {
  text-align: right;
  margin-bottom: 20px;
}

.step-hint {
  font-size: 12px;
  color: #787774;
  margin: 12px 0 0 0;
  line-height: 1.5;
}

.auth-btn {
  width: 100%;
  padding: 16px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.auth-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.auth-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.auth-switch {
  text-align: center;
  font-size: 14px;
  color: #787774;
}

.link {
  color: #667eea;
  cursor: pointer;
  margin-left: 4px;
  font-weight: 500;
  text-decoration: underline;
}

.link:hover {
  color: #764ba2;
}

/* 步骤指示器 */
.steps-indicator {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
  gap: 8px;
}

.step {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border-radius: 8px;
  background: #f5f5f5;
  transition: all 0.3s;
}

.step.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.step-index {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 12px;
  color: #666;
}

.step.active .step-index {
  background: #fff;
  color: #667eea;
}

.step-label {
  font-size: 11px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .home-page {
    flex-direction: column;
  }
  
  .left-section {
    flex: 0 0 40%;
    height: 40vh;
  }
  
  .right-section {
    flex: 1;
    padding: 20px;
  }
  
  .auth-container {
    padding: 30px 20px;
  }
}
</style>
