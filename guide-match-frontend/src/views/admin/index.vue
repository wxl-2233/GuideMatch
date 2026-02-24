<template>
  <div class="admin-page">
    <PageContainer>
      <h1 class="page-title">{{ t('admin.title') }}</h1>

      <!-- 数据统计 -->
      <div class="stats-section">
        <h2 class="section-title">{{ t('admin.stats') }}</h2>
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon">👥</div>
            <div class="stat-info">
              <div class="stat-label">{{ t('admin.totalUsers') }}</div>
              <div class="stat-value">{{ stats.totalUsers }}</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">🎯</div>
            <div class="stat-info">
              <div class="stat-label">{{ t('admin.totalGuides') }}</div>
              <div class="stat-value">{{ stats.totalGuides }}</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">📋</div>
            <div class="stat-info">
              <div class="stat-label">{{ t('admin.totalOrders') }}</div>
              <div class="stat-value">{{ stats.totalOrders }}</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">✅</div>
            <div class="stat-info">
              <div class="stat-label">{{ t('admin.completedOrders') }}</div>
              <div class="stat-value">{{ stats.completedOrders }}</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">💰</div>
            <div class="stat-info">
              <div class="stat-label">{{ t('admin.totalCommission') }}</div>
              <div class="stat-value">¥{{ stats.totalCommission }}</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">📝</div>
            <div class="stat-info">
              <div class="stat-label">{{ t('admin.pendingPosts') }}</div>
              <div class="stat-value">{{ stats.pendingPosts }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 数据可视化图表 -->
      <div class="charts-section">
        <h2 class="section-title">{{ t('admin.dataVisualization') }}</h2>
        <div class="charts-grid">
          <div class="chart-card">
            <h3>{{ t('admin.userGrowthTrend') }}</h3>
            <canvas ref="userGrowthChart" class="chart-canvas"></canvas>
          </div>
          <div class="chart-card">
            <h3>{{ t('admin.orderStatusDistribution') }}</h3>
            <canvas ref="orderStatusChart" class="chart-canvas"></canvas>
          </div>
          <div class="chart-card">
            <h3>{{ t('admin.userRoleDistribution') }}</h3>
            <canvas ref="userRolesChart" class="chart-canvas"></canvas>
          </div>
          <div class="chart-card">
            <h3>{{ t('admin.orderTrend') }}</h3>
            <canvas ref="orderTrendChart" class="chart-canvas"></canvas>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="quick-actions">
        <h2 class="section-title">{{ t('admin.quickActions') }}</h2>
        <div class="action-buttons">
          <button class="action-btn" @click="activeTab = 'users'">
            <span class="btn-icon">👥</span>
            <span>{{ t('admin.userManagement') }}</span>
          </button>
          <button class="action-btn" @click="activeTab = 'avatars'">
            <span class="btn-icon">🖼️</span>
            <span>{{ t('admin.avatarReview') }}</span>
          </button>
          <button class="action-btn" @click="activeTab = 'guides'">
            <span class="btn-icon">🎯</span>
            <span>{{ t('admin.guideReviewTitle') }}</span>
          </button>
          <button class="action-btn" @click="activeTab = 'posts'">
            <span class="btn-icon">📝</span>
            <span>{{ t('admin.postReviewTitle') }}</span>
          </button>
          <button class="action-btn" @click="activeTab = 'orders'">
            <span class="btn-icon">📋</span>
            <span>{{ t('admin.orderManagementTitle') }}</span>
          </button>
        </div>
      </div>

      <!-- 标签页内容 -->
      <div class="tabs-content">
        <!-- 用户管理 -->
        <div v-if="activeTab === 'users'" class="tab-panel">
          <div class="panel-header">
            <h3>{{ t('admin.userManagement') }}</h3>
            <div class="filter-controls">
              <select v-model="userFilter.role" class="filter-select" @change="loadUsers">
                <option value="">{{ t('admin.allRoles') }}</option>
                <option value="tourist">{{ t('role.tourist') }}</option>
                <option value="guide">{{ t('role.guide') }}</option>
                <option value="admin">{{ t('role.admin') }}</option>
              </select>
              <select v-model="userFilter.status" class="filter-select" @change="loadUsers">
                <option value="">{{ t('admin.allStatus') }}</option>
                <option value="active">{{ t('admin.active') }}</option>
                <option value="frozen">{{ t('admin.frozen') }}</option>
              </select>
            </div>
          </div>
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>昵称</th>
                <th>邮箱</th>
                <th>角色</th>
                <th>状态</th>
                <th>注册时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in users" :key="user.id">
                <td>{{ user.id }}</td>
                <td>
                  <div class="user-cell">
                    <img
                      :src="getAvatarFullUrl(user.avatarPath)"
                      :alt="user.nickname"
                      class="user-avatar-small"
                      @error="handleImageError"
                    />
                    <span>{{ user.nickname }}</span>
                  </div>
                </td>
                <td>{{ user.email }}</td>
                <td>{{ getRoleText(user.role) }}</td>
                <td>
                  <span class="status-badge" :class="user.status">
                    {{ getStatusText(user.status) }}
                  </span>
                </td>
                <td>{{ formatDate(user.createTime) }}</td>
                <td>
                  <button
                    v-if="user.status === 'active'"
                    class="btn btn-danger btn-sm"
                    @click="freezeUser(user.id)"
                  >
                    冻结
                  </button>
                  <button
                    v-else
                    class="btn btn-success btn-sm"
                    @click="unfreezeUser(user.id)"
                  >
                    解冻
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 头像审核 -->
        <div v-if="activeTab === 'avatars'" class="tab-panel">
          <div class="panel-header">
            <h3>{{ t('admin.avatarReview') }}</h3>
            <div class="filter-controls">
              <select v-model="avatarFilter.status" class="filter-select" @change="loadAvatarUsers">
                <option value="">全部状态</option>
                <option value="pending">待审核</option>
                <option value="approved">已通过</option>
                <option value="rejected">已拒绝</option>
              </select>
            </div>
          </div>
          <div v-if="avatarUsers.length === 0" class="empty">暂无需要处理的头像</div>
          <div v-else class="avatar-list">
            <div
              v-for="user in avatarUsers"
              :key="user.id"
              class="avatar-card"
            >
              <div class="avatar-preview">
                <img
                  :src="getAvatarFullUrl(user.avatarPath)"
                  alt="avatar"
                  class="avatar-image"
                  @error="handleImageError"
                />
                <span v-if="user.avatarStatus === 'pending'" class="status-tag pending">待审核</span>
                <span v-else-if="user.avatarStatus === 'approved'" class="status-tag approved">已通过</span>
                <span v-else-if="user.avatarStatus === 'rejected'" class="status-tag rejected">已拒绝</span>
              </div>
              <div class="avatar-info">
                <div class="avatar-main">
                  <span class="avatar-name">{{ user.nickname }}</span>
                  <span class="avatar-role">{{ getRoleText(user.role) }}</span>
                </div>
                <div class="avatar-meta">
                  <span>邮箱：{{ user.email || '未填写' }}</span>
                  <span>用户状态：{{ getStatusText(user.status) }}</span>
                  <span>注册时间：{{ formatDate(user.createTime) }}</span>
                </div>
              </div>
              <div class="avatar-actions">
                <button
                  v-if="user.avatarStatus === 'pending' || !user.avatarStatus"
                  class="btn btn-success btn-sm"
                  @click="approveAvatar(user.id)"
                >
                  审核通过
                </button>
                <button
                  v-if="user.avatarStatus === 'pending' || !user.avatarStatus"
                  class="btn btn-danger btn-sm"
                  @click="showRejectAvatarModal(user)"
                >
                  拒绝
                </button>
                <button
                  v-if="user.avatarStatus === 'approved'"
                  class="btn btn-danger btn-sm"
                  @click="removeAvatar(user.id)"
                >
                  下架头像
                </button>
                <button
                  v-if="user.status === 'active'"
                  class="btn btn-danger btn-sm"
                  @click="freezeUser(user.id)"
                >
                  冻结用户
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 向导审核 -->
        <div v-if="activeTab === 'guides'" class="tab-panel">
          <div class="panel-header">
            <h3>{{ t('admin.guideReviewTitle') }}</h3>
          </div>
          <div v-if="pendingGuides.length === 0" class="empty">暂无待审核向导</div>
          <div v-else class="guides-list">
            <div
              v-for="guide in pendingGuides"
              :key="guide.id"
              class="guide-review-card"
            >
              <div class="guide-avatar-section">
                <img
                  :src="getAvatarFullUrl(guide.avatarPath)"
                  :alt="guide.nickname || guide.userName"
                  class="guide-avatar-small"
                  @error="handleImageError"
                />
              </div>
              <div class="guide-info">
                <h4>{{ guide.nickname || guide.userName }}</h4>
                <p>{{ guide.bio }}</p>
                <div class="guide-details">
                  <span>语言：{{ formatArray(guide.languages) }}</span>
                  <span>城市：{{ formatArray(guide.cities) }}</span>
                  <span>日薪：¥{{ guide.dailyRate }}</span>
                </div>
              </div>
              <div class="review-actions">
                <button class="btn btn-success" @click="approveGuide(guide.id)">
                  通过
                </button>
                <button class="btn btn-danger" @click="showRejectGuideModal(guide)">
                  拒绝
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 内容审核 -->
        <div v-if="activeTab === 'posts'" class="tab-panel">
          <div class="panel-header">
            <h3>{{ t('admin.postReviewTitle') }}</h3>
          </div>
          <div v-if="pendingPosts.length === 0" class="empty">暂无待审核内容</div>
          <div v-else class="posts-list">
            <div
              v-for="post in pendingPosts"
              :key="post.id"
              class="post-review-card"
            >
              <div class="post-header">
                <span class="post-author">{{ post.userName }}</span>
                <span class="post-role">{{ t(`role.${post.userRole || 'tourist'}`) }}</span>
                <span class="post-time">{{ formatDate(post.createTime) }}</span>
              </div>
              <h4 class="post-title">{{ post.title }}</h4>
              <p class="post-content">{{ post.content }}</p>
              <div class="review-actions">
                <button class="btn btn-success" @click="approvePost(post.id)">
                  通过
                </button>
                <button class="btn btn-danger" @click="showRejectPostModal(post)">
                  拒绝
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 订单管理 -->
        <div v-if="activeTab === 'orders'" class="tab-panel">
          <div class="panel-header">
            <h3>{{ t('admin.orderManagementTitle') }}</h3>
            <button class="btn btn-primary" @click="exportOrders">
              📥 导出订单数据
            </button>
          </div>
          <div v-if="allOrders.length === 0" class="empty">暂无订单数据</div>
          <table v-else class="data-table">
            <thead>
              <tr>
                <th>订单号</th>
                <th>{{ t('role.tourist') }}</th>
                <th>{{ t('role.guide') }}</th>
                <th>日期</th>
                <th>金额</th>
                <th>状态</th>
                <th>创建时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in allOrders" :key="order.id">
                <td>{{ order.id || '-' }}</td>
                <td>{{ order.touristName || '未知用户' }}</td>
                <td>{{ order.guideName || '未知向导' }}</td>
                <td>{{ order.startDate ? `${order.startDate} 至 ${order.endDate || order.startDate}` : '-' }}</td>
                <td>¥{{ order.totalPrice || 0 }}</td>
                <td>
                  <span class="status-badge" :class="order.status">
                    {{ getOrderStatusText(order.status) }}
                  </span>
                </td>
                <td>{{ formatDate(order.createTime) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </PageContainer>

    <!-- 拒绝向导弹窗 -->
    <div v-if="showRejectGuide" class="modal-overlay" @click.self="showRejectGuide = false">
      <div class="modal-content">
        <h3>拒绝向导申请</h3>
        <textarea
          v-model="rejectReason"
          placeholder="请输入拒绝原因..."
          class="reject-input"
        ></textarea>
        <div class="modal-actions">
          <button class="btn btn-secondary" @click="showRejectGuide = false">取消</button>
          <button class="btn btn-danger" @click="confirmRejectGuide">确认拒绝</button>
        </div>
      </div>
    </div>

    <!-- 拒绝帖子弹窗 -->
    <div v-if="showRejectPost" class="modal-overlay" @click.self="showRejectPost = false">
      <div class="modal-content">
        <h3>拒绝帖子</h3>
        <textarea
          v-model="rejectReason"
          placeholder="请输入拒绝原因..."
          class="reject-input"
        ></textarea>
        <div class="modal-actions">
          <button class="btn btn-secondary" @click="showRejectPost = false">取消</button>
          <button class="btn btn-danger" @click="confirmRejectPost">确认拒绝</button>
        </div>
      </div>
    </div>

    <!-- 拒绝头像弹窗 -->
    <div v-if="showRejectAvatar" class="modal-overlay" @click.self="showRejectAvatar = false">
      <div class="modal-content">
        <h3>拒绝头像</h3>
        <textarea
          v-model="rejectReason"
          placeholder="请输入拒绝原因..."
          class="reject-input"
        ></textarea>
        <div class="modal-actions">
          <button class="btn btn-secondary" @click="showRejectAvatar = false">取消</button>
          <button class="btn btn-danger" @click="confirmRejectAvatar">确认拒绝</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { useI18n } from '@/composables/useI18n'
import request from '@/api/request'
import PageContainer from '@/components/layout/PageContainer.vue'

const { t } = useI18n()

const stats = ref({
  totalUsers: 0,
  totalGuides: 0,
  totalOrders: 0,
  completedOrders: 0,
  totalCommission: 0,
  pendingPosts: 0
})

const chartData = ref({
  userGrowth: [],
  orderStatus: {},
  userRoles: {},
  orderTrend: []
})

const userGrowthChart = ref(null)
const orderStatusChart = ref(null)
const userRolesChart = ref(null)
const orderTrendChart = ref(null)

const activeTab = ref('users')
const users = ref([])
const avatarUsers = ref([])
const pendingGuides = ref([])
const pendingPosts = ref([])
const allOrders = ref([])

const userFilter = ref({
  role: '',
  status: ''
})

const avatarFilter = ref({
  status: ''
})

const showRejectGuide = ref(false)
const showRejectPost = ref(false)
const showRejectAvatar = ref(false)
const rejectReason = ref('')
const currentRejectItem = ref(null)

const loadStats = async () => {
  try {
    const response = await request.get('/admin/stats')
    stats.value = response.data
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadUsers = async () => {
  try {
    const params = {}
    if (userFilter.value.role) params.role = userFilter.value.role
    if (userFilter.value.status) params.status = userFilter.value.status

    const response = await request.get('/admin/users', { params })
    users.value = response.data.list || []
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

const loadAvatarUsers = async () => {
  try {
    const params = {}
    if (avatarFilter.value.status) {
      params.status = avatarFilter.value.status
    }
    const response = await request.get('/admin/avatars', { params })
    avatarUsers.value = response.data.list || []
  } catch (error) {
    console.error('加载头像列表失败:', error)
  }
}

const getAvatarFullUrl = (avatarPath) => {
  if (!avatarPath) return '/default-avatar.png'
  if (avatarPath.startsWith('http://') || avatarPath.startsWith('https://')) {
    return avatarPath
  }
  if (avatarPath.startsWith('/img/avatar/')) {
    return `http://localhost:8080${avatarPath}`
  }
  if (avatarPath.startsWith('/img/')) {
    return `http://localhost:8080${avatarPath}`
  }
  if (avatarPath.startsWith('/static/')) {
    const filename = avatarPath.split('/').pop()
    return `http://localhost:8080/img/avatar/${filename}`
  }
  return `http://localhost:8080/img/avatar/${avatarPath}`
}

const handleImageError = (e) => {
  e.target.src = '/default-avatar.png'
}

const approveAvatar = async (userId) => {
  try {
    await request.post(`/admin/avatars/${userId}/approve`)
    alert('头像审核通过')
    loadAvatarUsers()
  } catch (error) {
    alert('操作失败: ' + (error.response?.data?.error || error.message))
  }
}

const showRejectAvatarModal = (user) => {
  currentRejectItem.value = user
  rejectReason.value = ''
  showRejectAvatar.value = true
}

const confirmRejectAvatar = async () => {
  if (!rejectReason.value.trim()) {
    alert('请输入拒绝原因')
    return
  }

  try {
    await request.post(`/admin/avatars/${currentRejectItem.value.id}/reject`, {
      reason: rejectReason.value
    })
    alert('头像已拒绝')
    showRejectAvatar.value = false
    loadAvatarUsers()
  } catch (error) {
    alert('操作失败: ' + (error.response?.data?.error || error.message))
  }
}

const loadPendingGuides = async () => {
  try {
    const response = await request.get('/admin/guides/pending')
    // 解析JSON字符串为数组
    pendingGuides.value = (response.data.list || []).map(guide => ({
      ...guide,
      languagesArray: guide.languages ? (Array.isArray(guide.languages) ? guide.languages : JSON.parse(guide.languages)) : [],
      citiesArray: guide.cities ? (Array.isArray(guide.cities) ? guide.cities : JSON.parse(guide.cities)) : [],
      tagsArray: guide.tags ? (Array.isArray(guide.tags) ? guide.tags : JSON.parse(guide.tags)) : []
    }))
  } catch (error) {
    console.error('加载待审核向导失败:', error)
  }
}

// 格式化数组显示（将JSON字符串或数组转换为逗号分隔的字符串）
const formatArray = (value) => {
  if (!value) return '暂无'
  if (Array.isArray(value)) {
    return value.join('、')
  }
  if (typeof value === 'string') {
    try {
      const parsed = JSON.parse(value)
      if (Array.isArray(parsed)) {
        return parsed.join('、')
      }
      return value
    } catch (e) {
      return value
    }
  }
  return String(value)
}

const loadPendingPosts = async () => {
  try {
    const response = await request.get('/admin/posts/pending')
    pendingPosts.value = response.data.list || []
  } catch (error) {
    console.error('加载待审核帖子失败:', error)
  }
}

const loadOrders = async () => {
  try {
    const response = await request.get('/admin/orders')
    allOrders.value = response.data.list || []
  } catch (error) {
    console.error('加载订单列表失败:', error)
  }
}

const freezeUser = async (userId) => {
  if (!confirm('确定要冻结该用户吗？')) return

  try {
    await request.post(`/admin/users/${userId}/freeze`)
    alert('用户已冻结')
    window.location.reload()
  } catch (error) {
    alert('操作失败')
  }
}

const unfreezeUser = async (userId) => {
  try {
    await request.post(`/admin/users/${userId}/unfreeze`)
    alert('用户已解冻')
    window.location.reload()
  } catch (error) {
    alert('操作失败')
  }
}

const removeAvatar = async (userId) => {
  if (!confirm('确定要下架该用户头像吗？')) return

  try {
    await request.post(`/admin/users/${userId}/avatar/remove`)
    alert('头像已下架')
    loadAvatarUsers()
    loadUsers()
  } catch (error) {
    alert('操作失败')
  }
}

const approveGuide = async (guideId) => {
  try {
    await request.post(`/admin/guides/${guideId}/approve`)
    alert('审核通过')
    window.location.reload()
  } catch (error) {
    alert('操作失败')
  }
}

const showRejectGuideModal = (guide) => {
  currentRejectItem.value = guide
  rejectReason.value = ''
  showRejectGuide.value = true
}

const confirmRejectGuide = async () => {
  if (!rejectReason.value.trim()) {
    alert('请输入拒绝原因')
    return
  }

  try {
    await request.post(`/admin/guides/${currentRejectItem.value.id}/reject`, {
      reason: rejectReason.value
    })
    alert('已拒绝')
    showRejectGuide.value = false
    window.location.reload()
  } catch (error) {
    alert('操作失败')
  }
}

const approvePost = async (postId) => {
  try {
    await request.post(`/admin/posts/${postId}/approve`)
    alert('审核通过')
    window.location.reload()
  } catch (error) {
    alert('操作失败')
  }
}

const showRejectPostModal = (post) => {
  currentRejectItem.value = post
  rejectReason.value = ''
  showRejectPost.value = true
}

const confirmRejectPost = async () => {
  if (!rejectReason.value.trim()) {
    alert('请输入拒绝原因')
    return
  }

  try {
    await request.post(`/admin/posts/${currentRejectItem.value.id}/reject`, {
      reason: rejectReason.value
    })
    alert('已拒绝')
    showRejectPost.value = false
    window.location.reload()
  } catch (error) {
    alert('操作失败')
  }
}

const getRoleText = (role) => {
  return t(`role.${role}`) || role
}

const getStatusText = (status) => {
  const map = {
    active: t('admin.active'),
    frozen: t('admin.frozen'),
    pending: t('admin.pending')
  }
  return map[status] || status
}

const getOrderStatusText = (status) => {
  const map = {
    pending: '待确认',
    confirmed: '已确认',
    in_progress: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}

const loadChartData = async () => {
  try {
    const response = await request.get('/admin/stats/charts')
    chartData.value = response.data
    await nextTick()
    drawCharts()
  } catch (error) {
    console.error('加载图表数据失败:', error)
  }
}

const drawCharts = () => {
  // 绘制用户增长趋势图
  if (userGrowthChart.value && chartData.value.userGrowth) {
    drawLineChart(userGrowthChart.value, chartData.value.userGrowth, '用户增长')
  }
  
  // 绘制订单状态分布图
  if (orderStatusChart.value && chartData.value.orderStatus) {
    drawPieChart(orderStatusChart.value, chartData.value.orderStatus, '订单状态')
  }
  
  // 绘制用户角色分布图
  if (userRolesChart.value && chartData.value.userRoles) {
    drawPieChart(userRolesChart.value, chartData.value.userRoles, '用户角色')
  }
  
  // 绘制订单趋势图
  if (orderTrendChart.value && chartData.value.orderTrend) {
    drawLineChart(orderTrendChart.value, chartData.value.orderTrend, '订单趋势')
  }
}

const drawLineChart = (canvas, data, title) => {
  const ctx = canvas.getContext('2d')
  const width = canvas.width = canvas.offsetWidth
  const height = canvas.height = canvas.offsetHeight
  const padding = 40
  const chartWidth = width - padding * 2
  const chartHeight = height - padding * 2

  // 清空画布
  ctx.clearRect(0, 0, width, height)

  // 绘制背景
  ctx.fillStyle = '#f9fafb'
  ctx.fillRect(0, 0, width, height)

  if (data.length === 0) return

  // 计算最大值
  const maxValue = Math.max(...data.map(d => d.count), 1)
  const stepX = chartWidth / (data.length - 1 || 1)
  const stepY = chartHeight / maxValue

  // 绘制坐标轴
  ctx.strokeStyle = '#e5e7eb'
  ctx.lineWidth = 1
  ctx.beginPath()
  ctx.moveTo(padding, padding)
  ctx.lineTo(padding, height - padding)
  ctx.lineTo(width - padding, height - padding)
  ctx.stroke()

  // 绘制数据点
  ctx.strokeStyle = '#2563eb'
  ctx.lineWidth = 2
  ctx.beginPath()
  data.forEach((item, index) => {
    const x = padding + index * stepX
    const y = height - padding - item.count * stepY
    if (index === 0) {
      ctx.moveTo(x, y)
    } else {
      ctx.lineTo(x, y)
    }
  })
  ctx.stroke()

  // 绘制数据点
  ctx.fillStyle = '#2563eb'
  data.forEach((item, index) => {
    const x = padding + index * stepX
    const y = height - padding - item.count * stepY
    ctx.beginPath()
    ctx.arc(x, y, 4, 0, Math.PI * 2)
    ctx.fill()
    
    // 绘制数值
    ctx.fillStyle = '#666'
    ctx.font = '12px Arial'
    ctx.textAlign = 'center'
    ctx.fillText(item.count, x, y - 10)
    ctx.fillStyle = '#2563eb'
  })

  // 绘制日期标签
  ctx.fillStyle = '#666'
  ctx.font = '10px Arial'
  ctx.textAlign = 'center'
  data.forEach((item, index) => {
    const x = padding + index * stepX
    const date = new Date(item.date).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
    ctx.fillText(date, x, height - padding + 20)
  })
}

const drawPieChart = (canvas, data, title) => {
  const ctx = canvas.getContext('2d')
  const width = canvas.width = canvas.offsetWidth
  const height = canvas.height = canvas.offsetHeight
  const centerX = width / 2
  const centerY = height / 2
  const radius = Math.min(width, height) / 2 - 40

  // 清空画布
  ctx.clearRect(0, 0, width, height)

  // 绘制背景
  ctx.fillStyle = '#f9fafb'
  ctx.fillRect(0, 0, width, height)

  const entries = Object.entries(data)
  if (entries.length === 0) return

  const total = entries.reduce((sum, [_, value]) => sum + value, 0)
  if (total === 0) return

  const colors = ['#2563eb', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899']
  let currentAngle = -Math.PI / 2

  entries.forEach(([label, value], index) => {
    const sliceAngle = (value / total) * Math.PI * 2
    const color = colors[index % colors.length]

    // 绘制扇形
    ctx.beginPath()
    ctx.moveTo(centerX, centerY)
    ctx.arc(centerX, centerY, radius, currentAngle, currentAngle + sliceAngle)
    ctx.closePath()
    ctx.fillStyle = color
    ctx.fill()
    ctx.strokeStyle = '#fff'
    ctx.lineWidth = 2
    ctx.stroke()

    // 绘制标签
    const labelAngle = currentAngle + sliceAngle / 2
    const labelX = centerX + Math.cos(labelAngle) * (radius * 0.7)
    const labelY = centerY + Math.sin(labelAngle) * (radius * 0.7)
    
    ctx.fillStyle = '#fff'
    ctx.font = 'bold 12px Arial'
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'
    ctx.fillText(value, labelX, labelY)

    // 绘制图例
    const legendX = 20
    const legendY = 20 + index * 25
    ctx.fillStyle = color
    ctx.fillRect(legendX, legendY - 8, 12, 12)
    ctx.fillStyle = '#333'
    ctx.font = '12px Arial'
    ctx.textAlign = 'left'
    ctx.fillText(`${label}: ${value}`, legendX + 18, legendY)

    currentAngle += sliceAngle
  })
}

const exportOrders = async () => {
  try {
    const response = await request.get('/admin/orders/export', {
      responseType: 'blob'
    })
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `orders_${new Date().toISOString().split('T')[0]}.xlsx`)
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
    
    alert('订单数据导出成功')
  } catch (error) {
    console.error('导出订单失败:', error)
    alert('导出订单失败，请重试')
  }
}

// 监听标签页切换，自动加载对应数据
watch(activeTab, (newTab) => {
  if (newTab === 'guides') {
    loadPendingGuides()
  } else if (newTab === 'posts') {
    loadPendingPosts()
  } else if (newTab === 'orders') {
    loadOrders()
  } else if (newTab === 'avatars') {
    // 默认加载待审核头像
    avatarFilter.value.status = 'pending'
    loadAvatarUsers()
  }
}, { immediate: true })

onMounted(() => {
  loadStats()
  loadUsers()
  loadChartData()
  // 如果默认标签页是 guides 或 posts，也要加载数据
  if (activeTab.value === 'guides') {
    loadPendingGuides()
  } else if (activeTab.value === 'posts') {
    loadPendingPosts()
  } else if (activeTab.value === 'orders') {
    loadOrders()
  } else if (activeTab.value === 'avatars') {
    avatarFilter.value.status = 'pending'
    loadAvatarUsers()
  }
})
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24px 0;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
}

.stats-section {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 16px 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
}

.stat-icon {
  font-size: 32px;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #2563eb;
}

.quick-actions {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn:hover {
  border-color: #2563eb;
  background: #f0f7ff;
}

.btn-icon {
  font-size: 32px;
}

.tabs-content {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.panel-subtitle {
  font-size: 13px;
  color: #6b7280;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.filter-controls {
  display: flex;
  gap: 12px;
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  display: table;
}

.data-table thead {
  display: table-header-group;
}

.data-table tbody {
  display: table-row-group;
}

.data-table th,
.data-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
  display: table-cell;
}

.data-table th {
  background: #f9fafb;
  font-weight: 600;
  color: #333;
}

.data-table tr {
  display: table-row;
}

.empty {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 14px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.status-badge.active {
  background: #d1fae5;
  color: #059669;
}

.status-badge.frozen {
  background: #fee2e2;
  color: #dc2626;
}

.btn {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  background: #6b7280;
  color: #fff;
}

.btn-sm {
  padding: 4px 8px;
  font-size: 12px;
}

.btn-success {
  background: #10b981;
  color: #fff;
}

.btn-danger {
  background: #ef4444;
  color: #fff;
}

.guides-list,
.posts-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.avatar-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.avatar-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  background: #f9fafb;
}

.avatar-preview {
  position: relative;
  width: 80px;
  height: 80px;
  flex-shrink: 0;
}

.avatar-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  background: #e5e7eb;
  border: 2px solid #e5e7eb;
}

.status-tag {
  position: absolute;
  top: -4px;
  right: -4px;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
  background: #fff;
  border: 1px solid #e5e7eb;
}

.status-tag.pending {
  background: #fef3c7;
  color: #d97706;
  border-color: #fbbf24;
}

.status-tag.approved {
  background: #d1fae5;
  color: #059669;
  border-color: #10b981;
}

.status-tag.rejected {
  background: #fee2e2;
  color: #dc2626;
  border-color: #ef4444;
}

.avatar-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.avatar-main {
  display: flex;
  align-items: center;
  gap: 8px;
}

.avatar-name {
  font-weight: 600;
}

.avatar-role {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
  background: #e5f0ff;
  color: #2563eb;
}

.avatar-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 12px;
  color: #6b7280;
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.guide-review-card,
.post-review-card {
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.guide-avatar-section {
  flex-shrink: 0;
}

.guide-avatar-small {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e5e7eb;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar-small {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e5e7eb;
}

.post-header {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #999;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.post-content {
  color: #666;
  line-height: 1.6;
  margin-bottom: 16px;
}

.review-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  width: 90%;
  max-width: 500px;
}

.reject-input {
  width: 100%;
  min-height: 100px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  margin: 16px 0;
  resize: vertical;
}

.modal-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.btn-secondary {
  background: #f3f4f6;
  color: #333;
}

.btn-primary {
  background: #2563eb;
  color: #fff;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary:hover {
  background: #1d4ed8;
}

/* ===== 图表区域 ===== */
.charts-section {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
  margin-top: 16px;
}

.chart-card {
  background: #fff;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  padding: 20px;
}

.chart-card h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.chart-canvas {
  width: 100%;
  height: 300px;
  max-height: 300px;
}
</style>