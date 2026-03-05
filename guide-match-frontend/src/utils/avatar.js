// 统一的头像URL处理工具
export const getAvatarUrl = (avatarPath, avatarStatus) => {
  // 只有审核通过的头像才显示，否则显示默认头像
  if (!avatarPath || avatarStatus !== 'approved') {
    return '/default-avatar.png'
  }
  
  // 如果已经是完整URL，直接返回
  if (avatarPath.startsWith('http://') || avatarPath.startsWith('https://')) {
    return avatarPath
  }
  
  // 获取API基础URL
  const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
  const serverUrl = baseURL.replace('/api', '') // 移除 /api 后缀
  
  // 处理不同格式的路径
  if (avatarPath.startsWith('/img/avatar/')) {
    return `${serverUrl}${avatarPath}`
  }
  if (avatarPath.startsWith('/img/')) {
    return `${serverUrl}${avatarPath}`
  }
  if (avatarPath.startsWith('/static/')) {
    const filename = avatarPath.split('/').pop()
    return `${serverUrl}/img/avatar/${filename}`
  }
  
  // 默认情况，假设是相对路径
  return `${serverUrl}/img/avatar/${avatarPath}`
}
