import axios from 'axios'
import request from '@/utils/request'

// 独立 axios 实例，不走后端 /api 前缀
const timorRequest = axios.create({
  baseURL: '/timor-api',
  timeout: 15000
})

/**
 * 获取全年节假日数据
 * API 文档: https://timor.tech/api/holiday/year/{year}/?type=Y&week=Y
 * @param {number} year 年份（2013~2026）
 * @returns {Promise} axios response，res.data 包含 { code, holiday, type }
 */
export function getYearHoliday(year) {
  return timorRequest.get(`/api/holiday/year/${year}/`, {
    params: { type: 'Y', week: 'Y' }
  })
}

/**
 * 提交确认后的节假日数据到后端
 * @param {{ year: number, total: number, days: Array<{date:string,category:string,name:string}> }} payload
 */
export function submitHolidayConfig(payload) {
  return request.post('/holiday/confirm', payload)
}

/**
 * 从后端查询某年工作日历数据（所有员工可调用）
 * @param {number} year 年份
 * @returns {Promise} res.data.data 为 [{id, date, dayType}, ...]
 */
export function getWorkCalendar(year) {
  return request.get('/holiday/getHoliday', { params: { year } })
}
