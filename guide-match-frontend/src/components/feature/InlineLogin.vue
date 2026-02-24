<template>
  <Transition name="door-open">
    <div v-if="isVisible" class="inline-login-section">
      <div class="login-wrapper">
        <button class="close-btn" @click="close">×</button>
        
        <div class="login-content">
          <!-- 标题 -->
          <div class="login-header">
            <h2 class="login-title">
              {{ mode === 'login' ? t('login.loginSubtitle') 
                : mode === 'register' ? t('login.registerSubtitle')
                : t('login.resetPasswordTitle') }}
            </h2>
            <p class="login-subtitle">
              {{ mode === 'forgot-password' ? t('login.resetPasswordSubtitle') : t('login.title') }}
            </p>
          </div>

          <!-- 邮件登录（登录和注册模式显示） -->
          <div v-if="mode !== 'forgot-password'" class="email-section">
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
          <div v-if="mode === 'login'" class="password-field">
            <input
              v-model="password"
              type="password"
              class="email-input"
              :placeholder="t('login.passwordPlaceholder')"
            />
            <p class="forgot-password-link">
              <span class="switch-link" @click="mode = 'forgot-password'">
                {{ t('login.forgotPassword') }}
              </span>
            </p>
          </div>

          <!-- 忘记密码模式 -->
          <div v-if="mode === 'forgot-password'" class="forgot-password-fields">
            <div class="field-group">
              <label class="field-label">{{ t('login.email') }}</label>
              <input
                v-model="resetEmail"
                type="email"
                class="email-input"
                :placeholder="t('login.emailPlaceholder')"
              />
            </div>
            <div class="field-group">
              <label class="field-label">{{ t('login.nickname') }}</label>
              <input
                v-model="resetNickname"
                type="text"
                class="email-input"
                :placeholder="t('login.nickname')"
              />
            </div>
            <div class="field-group">
              <label class="field-label">{{ t('login.newPassword') }}</label>
              <input
                v-model="newPassword"
                type="password"
                class="email-input"
                :placeholder="t('login.newPasswordPlaceholder')"
              />
            </div>
            <div class="field-group">
              <label class="field-label">{{ t('login.confirmPassword') }}</label>
              <input
                v-model="confirmPassword"
                type="password"
                class="email-input"
                :placeholder="t('login.confirmPasswordPlaceholder')"
              />
            </div>
            <button class="continue-btn" @click.stop="resetPassword" :disabled="loading">
              {{ loading ? t('common.loading') : t('login.resetPassword') }}
            </button>
          </div>

          <!-- 继续按钮（非忘记密码模式） -->
          <button 
            v-if="mode !== 'forgot-password'" 
            class="continue-btn" 
            @click.stop="handleSubmit" 
            :disabled="loading"
          >
            {{ loading ? t('common.loading') : getSubmitText }}
          </button>

          <!-- 切换模式 -->
          <p class="switch-mode">
            <span v-if="mode === 'login'">{{ t('login.switchToRegister') }}</span>
            <span v-else-if="mode === 'register'">{{ t('login.switchToLogin') }}</span>
            <span v-else-if="mode === 'forgot-password'">
              <span class="switch-link" @click="mode = 'login'">
                {{ t('login.backToLogin') }}
              </span>
            </span>
            <span v-if="mode !== 'forgot-password'">
              <span class="switch-link" @click="toggleMode">
                {{ mode === 'login' ? t('login.register') : t('common.login') }}
              </span>
            </span>
          </p>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from '@/composables/useI18n'
import request from '@/api/request'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'submit'])

const { t, locale } = useI18n()
const router = useRouter()

const isVisible = ref(props.visible)
const mode = ref('login') // 'login'、'register' 或 'forgot-password'
const email = ref('')
const password = ref('')
const loading = ref(false)
const registerStep = ref(1) // 注册步骤：1 或 2
const registerForm = ref({
  nickname: '',
  password: '',
  fullName: '',
  phonenumber: '',
  role: 'tourist'
})

// 忘记密码相关
const resetEmail = ref('')
const resetNickname = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const currentLang = computed(() => locale.value)

const getSubmitText = computed(() => {
  if (mode.value === 'login') {
    return t('common.login')
  } else {
    if (registerStep.value === 1) {
      return t('login.continue')
    } else {
      return t('login.register')
    }
  }
})

const toggleMode = () => {
  mode.value = mode.value === 'login' ? 'register' : 'login'
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
  // 重置忘记密码状态
  resetEmail.value = ''
  resetNickname.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
}

const close = () => {
  isVisible.value = false
  emit('close')
}

const handleSubmit = async () => {
  loading.value = true

  try {
    if (mode.value === 'login') {
      // 登录逻辑
      if (!email.value || !password.value) {
        alert(t('message.fillCompleteInfo'))
        loading.value = false
        return
      }

      const response = await request.post('/auth/login', {
        email: email.value,
        password: password.value
      })

      if (response.data && response.data.token) {
        localStorage.setItem('token', 'Bearer ' + response.data.token)
        localStorage.setItem('user', JSON.stringify(response.data))
        
        emit('submit')
        close()
        
        // 根据角色跳转
        const role = response.data.role || 'tourist'
        const targetRoute = role === 'admin'
          ? '/admin'
          : role === 'guide'
            ? '/guide/dashboard'
            : '/tourist'
        
        setTimeout(() => {
          window.location.href = targetRoute
        }, 100)
      }
    } else {
      // 注册分步逻辑
      if (registerStep.value === 1) {
        // 校验第一步：账号信息
        if (!registerForm.value.nickname || !registerForm.value.password || !email.value) {
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
        
        emit('submit')
        close()
        
        // 根据角色跳转
        const role = response.data.role || 'tourist'
        const targetRoute = role === 'admin'
          ? '/admin'
          : role === 'guide'
            ? '/guide/dashboard'
            : '/tourist'
        
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

// 重置密码
const resetPassword = async () => {
  if (!resetEmail.value || !resetEmail.value.trim()) {
    alert('请输入邮箱')
    return
  }
  if (!resetNickname.value || !resetNickname.value.trim()) {
    alert('请输入用户名')
    return
  }
  if (!newPassword.value || newPassword.value.length < 6) {
    alert(t('login.newPasswordPlaceholder'))
    return
  }
  if (newPassword.value !== confirmPassword.value) {
    alert(t('login.passwordMismatch'))
    return
  }

  loading.value = true
  try {
    const response = await request.post('/auth/reset-password', {
      email: resetEmail.value,
      nickname: resetNickname.value,
      newPassword: newPassword.value
    })

    if (response.data) {
      alert(t('login.resetSuccess'))
      // 重置状态并返回登录
      mode.value = 'login'
      resetEmail.value = ''
      resetNickname.value = ''
      newPassword.value = ''
      confirmPassword.value = ''
    }
  } catch (error) {
    console.error('重置密码失败:', error)
    alert(error.response?.data?.error || error.message || '重置密码失败，请重试')
  } finally {
    loading.value = false
  }
}

// 监听 visible prop 变化
import { watch } from 'vue'
watch(() => props.visible, (newVal) => {
  isVisible.value = newVal
  if (newVal) {
    mode.value = 'login'
    registerStep.value = 1
    // 重置忘记密码状态
    resetEmail.value = ''
    resetNickname.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
  }
})
</script>

<style scoped>
.inline-login-section {
  width: 100%;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  margin: 0;
  overflow: hidden;
  transform-origin: center;
}

.login-wrapper {
  position: relative;
  padding: 60px 24px;
  max-width: 600px;
  margin: 0 auto;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(0, 0, 0, 0.05);
  border-radius: 50%;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  transition: all 0.2s;
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.1);
  transform: rotate(90deg);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #37352f;
  margin: 0 0 8px 0;
}

.login-subtitle {
  font-size: 16px;
  color: #787774;
  margin: 0;
}

.email-section {
  margin-bottom: 20px;
}

.email-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e5e5e5;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s;
  box-sizing: border-box;
}

.email-input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.password-field {
  margin-bottom: 20px;
}

.register-fields {
  margin-bottom: 20px;
}

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
  background: #e0f2fe;
  color: #2563eb;
}

.step-index {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  color: #666;
}

.step.active .step-index {
  background: #2563eb;
  color: #fff;
}

.step-label {
  font-size: 12px;
  font-weight: 500;
}

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
  margin-left: 4px;
}

.optional {
  color: #787774;
  font-size: 12px;
  font-weight: normal;
}

.role-select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e5e5e5;
  border-radius: 8px;
  font-size: 14px;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s;
}

.role-select:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.step-hint {
  font-size: 12px;
  color: #787774;
  margin: 12px 0 0 0;
  line-height: 1.5;
}

.continue-btn {
  width: 100%;
  padding: 14px 24px;
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 16px;
}

.continue-btn:hover:not(:disabled) {
  background: #1d4ed8;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.continue-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

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
  font-weight: 500;
  text-decoration: underline;
}

.switch-link:hover {
  color: #1d4ed8;
}

.forgot-password-link {
  text-align: right;
  margin-top: 8px;
  margin-bottom: 0;
  font-size: 14px;
}

.forgot-password-fields {
  margin-bottom: 20px;
}

.forgot-step-1,
.forgot-step-2 {
  display: flex;
  flex-direction: column;
  gap: 16px;
}


/* 门式展开动画效果 */
.door-open-enter-active,
.door-open-leave-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: center;
  overflow: hidden;
}

.door-open-enter-from {
  opacity: 0;
  transform: scaleY(0);
  max-height: 0;
}

.door-open-enter-to {
  opacity: 1;
  transform: scaleY(1);
  max-height: 1000px;
}

.door-open-leave-from {
  opacity: 1;
  transform: scaleY(1);
  max-height: 1000px;
}

.door-open-leave-to {
  opacity: 0;
  transform: scaleY(0);
  max-height: 0;
}
</style>

