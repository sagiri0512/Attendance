import axios from 'axios'

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
