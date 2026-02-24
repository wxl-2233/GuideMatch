import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginPage.vue'),
  },
  {
    path: '/visitor',
    name: 'Visitor',
    component: () => import('@/views/visitor/index.vue'),
  },
  {
    path: '/tourist',
    name: 'Tourist',
    component: () => import('@/views/tourist/index.vue'),
  },
  {
    path: '/guide',
    name: 'Guide',
    component: () => import('@/views/tourist/index.vue'),
  },
  {
    path: '/tourist/orders',
    name: 'TouristOrders',
    component: () => import('@/views/tourist/orders.vue'),
  },
  {
    path: '/tourist/favorites',
    name: 'TouristFavorites',
    component: () => import('@/views/tourist/favorites.vue'),
  },
  {
    path: '/guide/:id',
    name: 'GuideDetail',
    component: () => import('@/views/guide-detail/index.vue'),
  },
  {
    path: '/guide/dashboard',
    name: 'GuideDashboard',
    component: () => import('@/views/guide/dashboard.vue'),
  },
  {
    path: '/guide/profile',
    name: 'GuideProfile',
    component: () => import('@/views/guide/profile.vue'),
  },
  {
    path: '/guide/calendar',
    name: 'GuideCalendar',
    component: () => import('@/views/guide/calendar.vue'),
  },
  {
    path: '/guide/income',
    name: 'GuideIncome',
    component: () => import('@/views/guide/income.vue'),
  },
  {
    path: '/guide/orders',
    name: 'GuideOrders',
    component: () => import('@/views/guide/orders.vue'),
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/index.vue'),
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import('@/views/community/index.vue'),
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/profile/index.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫：限制未登录用户访问
router.beforeEach((to, from, next) => {
  // 获取用户信息
  const userStr = localStorage.getItem('user')
  let user = null
  if (userStr) {
    try {
      user = JSON.parse(userStr)
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }

  // 不需要登录的页面
  const publicPages = ['/login']
  const isPublicPage = publicPages.includes(to.path)

  // 如果未登录且访问的不是公共页面，重定向到登录页
  if (!user && !isPublicPage) {
    next('/login')
    return
  }

  // 如果是管理员，限制访问游客页面
  if (user && user.role === 'admin') {
    const restrictedPaths = ['/visitor', '/guide', '/community']
    if (restrictedPaths.includes(to.path)) {
      next('/admin')
      return
    }
  }

  next()
})

export default router
