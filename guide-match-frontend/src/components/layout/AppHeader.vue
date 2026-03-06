<template>
  <header class="app-header">
    <div class="inner">
      <!-- 左侧 -->
      <div class="left">
        <div class="logo" @click="goHome">
          GuideMatch
        </div>

        <nav class="nav" v-if="!isAdmin">
          <RouterLink to="/visitor" class="nav-item">{{ t('common.home') }}</RouterLink>
          <RouterLink to="/guide" class="nav-item">{{ t('common.guide') }}</RouterLink>
          <RouterLink to="/community" class="nav-item">{{ t('common.community') }}</RouterLink>
        </nav>
      </div>

      <!-- 右侧 -->
      <div class="right">
        <!-- 语言切换 -->
        <div class="lang-dropdown" ref="langRoot">
          <button class="lang-trigger" @click="toggleLang">
            {{ currentLang }}
            <span class="arrow">▾</span>
          </button>

          <div v-if="langOpen" class="lang-menu">
            <button
              v-for="lang in langs"
              :key="lang.code"
              class="lang-item"
              :class="{ active: locale === lang.code }"
              @click="selectLang(lang.code)"
            >
              {{ lang.label }}
            </button>
          </div>
        </div>

        <!-- 通知（登录后显示） -->
        <div v-if="isLogin" class="notification-dropdown" ref="notificationRoot">
          <button class="notification-btn" @click="toggleNotification">
            🔔
            <span v-if="unreadCount > 0" class="notification-badge">{{ unreadCount }}</span>
          </button>
          <div v-if="notificationOpen" class="notification-menu">
            <div v-if="notifications.length === 0" class="notification-empty">
              {{ t('header.noNotifications') }}
            </div>
            <div
              v-for="notification in notifications"
              :key="notification.id"
              class="notification-item"
              :class="{ unread: !notification.read }"
              @click="markAsRead(notification.id)"
            >
              <div class="notification-content">{{ notification.content }}</div>
              <div class="notification-time">{{ formatTime(notification.createTime) }}</div>
            </div>
          </div>
        </div>

        <!-- 用户菜单（登录后显示） -->
        <div v-if="isLogin" class="user-menu-dropdown" ref="userMenuRoot">
          <button class="user-menu-btn" @click="toggleUserMenu">
            <img
              :src="getAvatarUrl(userInfo?.avatarPath, userInfo?.avatarStatus)"
              :alt="userInfo?.nickname"
              class="user-avatar"
            />
            <span class="user-name">{{ userInfo?.nickname }}</span>
            <span class="arrow">▾</span>
          </button>
          <div v-if="userMenuOpen" class="user-menu">
            <div class="user-info">
              <div class="user-level">Lv.{{ userInfo?.lv || 1 }}</div>
              <div class="user-exp">经验: {{ userInfo?.exp || 0 }}</div>
            </div>
            <button class="user-menu-item" @click="goToProfile">
              {{ t('header.profile') }}
            </button>
            <!-- 根据角色显示不同的快速入口 -->
            <template v-if="userInfo?.role === 'tourist'">
              <button class="user-menu-item" @click="goToOrders">
                {{ t('header.myOrders') }}
              </button>
              <button class="user-menu-item" @click="goToFavorites">
                {{ t('header.myFavorites') }}
              </button>
              <button class="user-menu-item" @click="goToMembership">
                {{ t('header.becomeMember') }}
              </button>
            </template>
            <template v-else-if="userInfo?.role === 'guide'">
              <button class="user-menu-item" @click="goToGuideDashboard">
                {{ t('header.guideDashboard') }}
              </button>
              <button class="user-menu-item" @click="goToGuideOrders">
                {{ t('header.orderManagement') }}
              </button>
              <button class="user-menu-item" @click="goToGuideCalendar">
                {{ t('header.calendarManagement') }}
              </button>
              <button class="user-menu-item" @click="goToGuideIncome">
                {{ t('header.incomeStats') }}
              </button>
              <button class="user-menu-item" @click="goToMembership">
                {{ t('header.becomeMember') }}
              </button>
            </template>
            <template v-else-if="userInfo?.role === 'admin'">
              <button class="user-menu-item" @click="goToAdmin">
                {{ t('header.adminPanel') }}
              </button>
            </template>
            <button class="user-menu-item" @click="logout">
              {{ t('header.logout') }}
            </button>
          </div>
        </div>

        <!-- 登录按钮（未登录时显示） -->
        <button v-else class="login-btn" @click="goToLogin">
          {{ t('header.loginRegister') }}
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from '@/composables/useI18n'
import { getAvatarUrl } from '@/utils/avatar'
import request from '@/api/request'

defineEmits(['login'])

const { t, setLocale, locale } = useI18n()

/* ===== 路由 ===== */
const router = useRouter()
const goHome = () => {
  // 根据用户角色跳转到不同首页
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      if (user.role === 'admin') {
        router.push('/admin')
        return
      } else if (user.role === 'guide') {
        router.push('/guide/dashboard')
        return
      } else if (user.role === 'tourist') {
        router.push('/tourist')
        return
      }
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
  // 未登录或解析失败，跳转到访客首页
  router.push('/visitor')
}

const goToLogin = () => {
  // 跳转到独立的登录页面
  router.push('/login')
}

const triggerLogin = () => {
  // 如果不在首页，先跳转到首页
  if (router.currentRoute.value.path !== '/visitor') {
    router.push('/visitor').then(() => {
      // 等待路由切换完成后再触发登录显示
      setTimeout(() => {
        window.dispatchEvent(new CustomEvent('show-login'))
      }, 100)
    })
  } else {
    // 已经在首页，直接触发登录显示
    window.dispatchEvent(new CustomEvent('show-login'))
  }
}

/* ===== 语言切换 ===== */

const langs = [
  { code: 'zh', label: '中文' },
  { code: 'en', label: 'English' },
  { code: 'ja', label: '日本語' }
]
const currentLang = computed(() => {
  const lang = langs.find(l => l.code === locale.value)
  return lang ? lang.label : '中文'
})
const langOpen = ref(false)
const langRoot = ref(null)

const toggleLang = () => {
  langOpen.value = !langOpen.value
}

const selectLang = (langCode) => {
  setLocale(langCode)
  langOpen.value = false
}

const handleClickOutside = (e) => {
  // 添加 null 检查，避免访问已销毁的 DOM 元素
  if (langRoot.value && langRoot.value.contains && !langRoot.value.contains(e.target)) {
    langOpen.value = false
  }
  if (notificationRoot.value && notificationRoot.value.contains && !notificationRoot.value.contains(e.target)) {
    notificationOpen.value = false
  }
  if (userMenuRoot.value && userMenuRoot.value.contains && !userMenuRoot.value.contains(e.target)) {
    userMenuOpen.value = false
  }
}

/* ===== 登录状态 ===== */
const isLogin = computed(() => {
  return !!localStorage.getItem('token')
})

const isAdmin = computed(() => {
  return userInfo.value?.role === 'admin'
})

const userInfo = ref(null)

const loadUserInfo = () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      userInfo.value = JSON.parse(userStr)
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
}

// 监听用户信息更新事件
const handleUserInfoUpdate = (event) => {
  if (event.detail) {
    userInfo.value = event.detail
  }
}

/* ===== 通知功能 ===== */
const notificationRoot = ref(null)
const notificationOpen = ref(false)
const notifications = ref([])
const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.read).length
})

const toggleNotification = () => {
  notificationOpen.value = !notificationOpen.value
  if (notificationOpen.value) {
    loadNotifications()
  }
}

const loadNotifications = async () => {
  try {
    // 检查是否仍然登录，避免在组件卸载后更新状态
    if (!isLogin.value) {
      return
    }
    const response = await request.get('/notifications/list')
    // 再次检查，确保组件仍然挂载
    if (isLogin.value) {
      notifications.value = response.data.list || []
    }
  } catch (error) {
    // 只在组件仍然挂载时记录错误
    if (isLogin.value) {
      console.error('加载通知失败:', error)
    }
  }
}

const markAsRead = async (id) => {
  try {
    await request.post(`/notifications/${id}/read`)
    const notification = notifications.value.find(n => n.id === id)
    if (notification) {
      notification.read = true
    }
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

/* ===== 用户菜单 ===== */
const userMenuRoot = ref(null)
const userMenuOpen = ref(false)

const toggleUserMenu = () => {
  userMenuOpen.value = !userMenuOpen.value
}

const goToProfile = () => {
  userMenuOpen.value = false
  router.push('/profile')
}

const goToOrders = () => {
  userMenuOpen.value = false
  router.push('/tourist/orders')
}

const goToFavorites = () => {
  userMenuOpen.value = false
  router.push('/tourist/favorites')
}

const goToMembership = () => {
  userMenuOpen.value = false
  router.push('/membership')
}

const goToGuideDashboard = () => {
  userMenuOpen.value = false
  router.push('/guide')
}

const goToGuideOrders = () => {
  userMenuOpen.value = false
  router.push('/guide/orders')
}

const goToGuideCalendar = () => {
  userMenuOpen.value = false
  router.push('/guide/calendar')
}

const goToGuideIncome = () => {
  userMenuOpen.value = false
  router.push('/guide/income')
}

const goToAdmin = () => {
  userMenuOpen.value = false
  router.push('/admin')
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  userInfo.value = null
  userMenuOpen.value = false
  window.location.href = '/visitor'
}

let notificationInterval = null

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  // 默认使用亮色模式
  document.documentElement.setAttribute('data-theme', 'light')
  loadUserInfo()
  
  // 监听用户信息更新事件
  window.addEventListener('userInfoUpdated', handleUserInfoUpdate)
  
  if (isLogin.value) {
    loadNotifications()
    // 定期刷新通知
    notificationInterval = setInterval(() => {
      // 检查组件是否仍然挂载
      if (isLogin.value) {
        loadNotifications()
      }
    }, 60000) // 每分钟刷新一次
  }
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  window.removeEventListener('userInfoUpdated', handleUserInfoUpdate)
  // 清理定时器
  if (notificationInterval) {
    clearInterval(notificationInterval)
    notificationInterval = null
  }
})
</script>

<style scoped>
/* ===== Header 外层 ===== */
.app-header {
  position: sticky;
  top: 0;
  z-index: 1000;

  width: 100%;
  height: 64px;

  /* ❗ 关键：固定不透明背景，不随滚动改变 */
  background-color: var(--header-bg) !important;
  backdrop-filter: none;
  -webkit-backdrop-filter: none;

  border-bottom: 1px solid var(--header-border);
  
  /* 确保背景始终可见，不透明 */
  transition: none;
}

/* ===== 内容层 ===== */
.inner {
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;

  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* ===== 左侧 ===== */
.left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-main);
  cursor: pointer;
}

.nav {
  display: flex;
  gap: 16px;
}

.nav-item {
  font-size: 14px;
  color: var(--text-muted);
  transition: color 0.2s;
}

.nav-item:hover {
  color: var(--primary);
}

.nav-item.router-link-active {
  color: var(--primary);
  font-weight: 600;
}

/* ===== 右侧 ===== */
.right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* ===== 语言下拉 ===== */
.lang-dropdown {
  position: relative;
}

.lang-trigger {
  padding: 6px 12px;
  border-radius: 10px;
  border: none;

  background: var(--header-pill-bg);
  color: var(--text-main);

  font-size: 14px;
  cursor: pointer;

  display: flex;
  align-items: center;
  gap: 6px;
}

.arrow {
  font-size: 12px;
  opacity: 0.8;
}

.lang-menu {
  position: absolute;
  right: 0;
  top: calc(100% + 8px);

  min-width: 120px;
  padding: 6px;

  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid var(--header-border);
  border-radius: 12px;

  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.lang-item {
  width: 100%;
  padding: 8px 10px;
  border: none;
  background: transparent;

  text-align: left;
  border-radius: 8px;
  cursor: pointer;

  color: var(--text-main);
  font-size: 14px;
}

.lang-item:hover {
  background: rgba(139, 92, 246, 0.1);
}

.lang-item.active {
  background: rgba(139, 92, 246, 0.1);
  color: var(--primary);
}

/* ===== 登录按钮 ===== */
.login-btn {
  padding: 6px 14px;
  border-radius: 10px;
  border: none;

  background: var(--primary);
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.login-btn:hover {
  background: var(--primary);
  opacity: 0.9;
}

/* ===== 通知下拉 ===== */
.notification-dropdown {
  position: relative;
}

.notification-btn {
  position: relative;
  padding: 6px 10px;
  border-radius: 10px;
  border: none;
  background: var(--header-pill-bg);
  color: var(--text-main);
  font-size: 18px;
  cursor: pointer;
}

.notification-badge {
  position: absolute;
  top: -2px;
  right: -2px;
  background: var(--primary);
  color: #fff;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.notification-menu {
  position: absolute;
  right: 0;
  top: calc(100% + 8px);
  width: 320px;
  max-height: 400px;
  overflow-y: auto;
  padding: 8px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid var(--header-border);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

.notification-empty {
  padding: 24px;
  text-align: center;
  color: var(--text-muted);
  font-size: 14px;
}

.notification-item {
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 4px;
  transition: background 0.2s;
}

.notification-item:hover {
  background: rgba(0, 0, 0, 0.06);
}

.notification-item.unread {
  background: rgba(139, 92, 246, 0.1);
}

.notification-content {
  font-size: 14px;
  color: var(--text-main);
  margin-bottom: 4px;
}

.notification-time {
  font-size: 12px;
  color: var(--text-muted);
}

/* ===== 用户菜单下拉 ===== */
.user-menu-dropdown {
  position: relative;
}

.user-menu-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 10px;
  border: none;
  background: var(--header-pill-bg);
  color: var(--text-main);
  font-size: 14px;
  cursor: pointer;
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
}

.user-name {
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-menu {
  position: absolute;
  right: 0;
  top: calc(100% + 8px);
  min-width: 200px;
  padding: 8px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid var(--header-border);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

.user-info {
  padding: 12px;
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 4px;
}

.user-level {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary);
  margin-bottom: 4px;
}

.user-exp {
  font-size: 12px;
  color: var(--text-muted);
}

.user-menu-item {
  width: 100%;
  padding: 10px 12px;
  border: none;
  background: transparent;
  text-align: left;
  border-radius: 8px;
  cursor: pointer;
  color: var(--text-main);
  font-size: 14px;
  transition: background 0.2s;
}

.user-menu-item:hover {
  background: rgba(0, 0, 0, 0.06);
}
</style>
