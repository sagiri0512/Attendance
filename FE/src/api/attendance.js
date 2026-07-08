import request from '@/utils/request'

export function clock() {
  return request.post('/attendance/clock')
}
