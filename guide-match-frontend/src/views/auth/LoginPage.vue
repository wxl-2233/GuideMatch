<template>
  <div class="login-page">
    <!-- 语言选择器 -->
    <div class="language-selector">
      <select v-model="currentLanguage" @change="changeLanguage" class="lang-select">
        <option value="zh">中文</option>
        <option value="en">English</option>
        <option value="ja">日本語</option>
        <option value="ko">한국어</option>
      </select>
    </div>

    <!-- 居中卡片容器 -->
    <div class="auth-card">
      <!-- 左侧表单区域 -->
      <div class="form-section">
        <div class="auth-container">
          <!-- 标题 -->
          <div class="auth-header">
            <h1 class="auth-title">GuideMatch</h1>
            <p class="auth-subtitle">连接向导与探索者的智能平台</p>
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

            <!-- 第一步：账号信息 -->
            <template v-if="registerStep === 1">
              <div class="form-group">
                <label>{{ t('login.email') }} <span class="required">*</span></label>
                <input
                  v-model="registerForm.email"
                  type="email"
                  :placeholder="t('login.emailPlaceholder')"
                  class="form-input"
                  required
                />
              </div>
              <div class="form-group">
                <label>{{ t('login.selectRole') }} <span class="required">*</span></label>
                <select v-model="registerForm.role" class="form-select">
                  <option value="tourist">{{ t('role.tourist') }}</option>
                  <option value="guide">{{ t('role.guide') }}</option>
                </select>
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
            </template>

            <!-- 第二步：个人资料 -->
            <template v-else>
              <!-- 返回按钮 -->
              <button class="back-btn" @click="goToPreviousStep" type="button">
                ← 返回上一步
              </button>
              
              <div class="form-group">
                <label>上传头像</label>
                <div class="avatar-upload">
                  <div class="avatar-preview" :style="{ backgroundImage: avatarPreview ? `url(${avatarPreview})` : 'none' }">
                    <span v-if="!avatarPreview" class="avatar-placeholder">📷</span>
                  </div>
                  <input
                    type="file"
                    ref="avatarInput"
                    @change="handleAvatarChange"
                    accept="image/*"
                    class="avatar-input"
                  />
                  <button type="button" class="avatar-btn" @click="$refs.avatarInput.click()">
                    选择图片
                  </button>
                </div>
              </div>
              <div class="form-group">
                <label>{{ t('login.phonenumber') }}</label>
                <input
                  v-model="registerForm.phonenumber"
                  type="tel"
                  :placeholder="t('login.phonenumberPlaceholder')"
                  class="form-input"
                />
              </div>
              <p class="step-hint">"个人资料"步骤里的信息后续可在个人中心修改</p>
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

      <!-- 右侧图片区域 -->
      <div class="image-section">
        <div class="image-container">
          <img 
            :src="loginImage" 
            alt="Login Background"
            class="auth-image"
          />
          <div class="image-overlay">
            <h2 class="image-title">Discover More<br/>with the Right Companion</h2>
            <p class="image-subtitle">让每一次探索，都遇见对的人</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from '@/composables/useI18n'
import request from '@/api/request'

const { t } = useI18n()
const router = useRouter()

// 使用单张登录背景图片
const loginImage = new URL('@/assets/scenic/5.jpg', import.meta.url).href

// 语言切换
const currentLanguage = ref('zh')

const changeLanguage = (event) => {
  const lang = event.target.value
  // 这里可以集成实际的语言切换逻辑
  console.log('切换语言到:', lang)
  // 可以调用 i18n 或其他国际化方案
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
  role: 'tourist',
  bio: '',
  city: '',
  languages: {
    chinese: true,
    english: false,
    japanese: false,
    korean: false
  }
})

const avatarPreview = ref('')
const avatarFile = ref(null)

const resetForm = ref({
  email: '',
  nickname: '',
  newPassword: '',
  confirmPassword: ''
})

const getSubtitle = () => {
  if (mode.value === 'login') return t('login.title')
  if (mode.value === 'register') return t('login.registerSubtitle')
  return t('login.resetPasswordTitle')
}

const getRegisterButtonText = () => {
  if (registerStep.value === 1) return t('login.continue')
  return t('login.register')
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

const handleAvatarChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    avatarFile.value = file
    const reader = new FileReader()
    reader.onload = (e) => {
      avatarPreview.value = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

const goToPreviousStep = () => {
  registerStep.value = 1
}

const resetForms = () => {
  loginForm.value = { email: '', password: '' }
  registerForm.value = {
    email: '',
    nickname: '',
    password: '',
    fullName: '',
    phonenumber: '',
    role: 'tourist',
    bio: '',
    city: '',
    languages: {
      chinese: true,
      english: false,
      japanese: false,
      korean: false
    }
  }
  avatarPreview.value = ''
  avatarFile.value = null
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
      
      // 登录成功后根据角色跳转到不同页面
      const role = response.data.role || 'tourist'
      const targetRoute = role === 'admin'
        ? '/admin'
        : role === 'guide'
          ? '/guide/dashboard'
          : '/visitor'
      
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
      
      // 注册成功后根据角色跳转
      const role = response.data.role || 'tourist'
      const targetRoute = role === 'admin'
        ? '/admin'
        : role === 'guide'
          ? '/guide/dashboard'
          : '/visitor'
      
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
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  box-sizing: border-box;
  position: relative;
}

/* 语言选择器 */
.language-selector {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 100;
}

.lang-select {
  padding: 8px 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  color: #000;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.lang-select:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
}

/* 居中卡片容器 */
.auth-card {
  width: 100%;
  max-width: 1200px;
  min-height: 600px;
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  display: flex;
  overflow: hidden;
  margin: 0 auto;
}

/* 左侧表单区域 */
.form-section {
  flex: 0 0 45%;
  padding: 60px 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
}

/* 右侧图片区域 */
.image-section {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.image-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.auth-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

.image-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.8) 0%, rgba(118, 75, 162, 0.8) 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  text-align: center;
  padding: 40px;
}

.image-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 16px 0;
  line-height: 1.2;
}

.image-subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
  line-height: 1.5;
}

/* 表单容器 */
.auth-container {
  width: 100%;
  max-width: 400px;
}

.auth-header {
  text-align: center;
  margin-bottom: 40px;
}

.auth-title {
  font-size: 32px;
  font-weight: 700;
  color: #37352f;
  margin: 0 0 12px 0;
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
  margin-bottom: 24px;
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
.form-select,
.form-textarea {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e5e5e5;
  border-radius: 12px;
  font-size: 14px;
  transition: all 0.2s;
  box-sizing: border-box;
  background: #fff;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
  font-family: inherit;
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

.forgot-password {
  text-align: right;
  margin-bottom: 24px;
}

.step-hint {
  font-size: 12px;
  color: #787774;
  margin: 16px 0 0 0;
  line-height: 1.5;
}

.auth-btn {
  width: 100%;
  padding: 16px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
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
  margin-bottom: 32px;
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

/* 返回按钮样式 */
.back-btn {
  background: none;
  border: none;
  color: #667eea;
  font-size: 14px;
  cursor: pointer;
  padding: 8px 0;
  margin-bottom: 20px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.back-btn:hover {
  color: #764ba2;
  transform: translateX(-4px);
}

/* 头像上传样式 */
.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 2px solid #e5e5e5;
  background-color: #f9f9f9;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  margin: 0 auto;
}

.avatar-placeholder {
  font-size: 32px;
  color: #999;
}

.avatar-input {
  display: none;
}

.avatar-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  margin: 0 auto;
}

.avatar-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .auth-card {
    flex-direction: column;
    max-width: 500px;
    min-height: auto;
  }
  
  .form-section {
    flex: 1;
    padding: 40px 30px;
  }
  
  .image-section {
    flex: 0 0 300px;
  }
  
  .image-overlay {
    padding: 20px;
  }
  
  .image-title {
    font-size: 24px;
  }
  
  .image-subtitle {
    font-size: 14px;
  }
}

@media (max-width: 640px) {
  .login-page {
    padding: 10px;
  }
  
  .auth-card {
    border-radius: 16px;
  }
  
  .form-section {
    padding: 30px 20px;
  }
  
  .image-section {
    flex: 0 0 200px;
  }
  
  .auth-title {
    font-size: 28px;
  }
  
  .image-title {
    font-size: 20px;
  }
  
  .language-selector {
    top: 10px;
    right: 10px;
  }
}
</style>
