import axios from 'axios'
const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 5000
})
// 请求拦截
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    // 确保 token 格式正确（Bearer token）
    if (token.startsWith('Bearer ')) {
      config.headers.Authorization = token
    } else {
      config.headers.Authorization = `Bearer ${token}`
    }
  }
  return config
})

// 响应拦截 - 处理 401 错误
request.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // 如果是未登录错误，且不是查看帖子列表的请求，可以忽略
      const url = error.config?.url || ''
      if (!url.includes('/posts/list') && !url.includes('/posts/') || url.includes('/posts/') && !url.includes('/view')) {
        // 需要登录的接口，但用户未登录
        console.warn('需要登录:', url)
      }
    }
    return Promise.reject(error)
  }
)

export default request