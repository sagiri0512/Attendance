import request from '@/utils/request'
import { useUserStore } from '@/store/user'
import { getUserIdFromToken } from '@/utils/jwt'

export function clock() {
  return request.post('/attendance/clock')
}

export function getMyRecords(start = 0, size = 10) {
  const userStore = useUserStore()
  // 优先用 userStore 已获取的用户信息，否则从 JWT 解析 sub（即 eid）
  const eid = userStore.user?.eid || getUserIdFromToken(userStore.token) || 0
  return request.get(`/attendance/my/${eid}`, { params: { start, size } })
}
