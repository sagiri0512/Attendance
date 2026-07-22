import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 节假日日历 Store
 * 管理每年每月的节假日确认数据
 * 数据结构: { [year]: { [month]: { confirmed: bool, days: [{date,category,name}] } } }
 */
export const useHolidayStore = defineStore('holiday', () => {
  /* ── State ── */
  const yearData = ref({})

  /* ── Getters ── */
  const getYearData = computed(() => (year) => yearData.value[year] || {})
  const getMonthData = computed(() => (year, month) => {
    const yd = yearData.value[year]
    return yd ? yd[month] : null
  })
  const isMonthConfirmed = computed(() => (year, month) => {
    const md = getMonthData.value(year, month)
    return md ? md.confirmed : false
  })
  const getAllConfirmedMonths = computed(() => (year) => {
    const yd = yearData.value[year]
    if (!yd) return []
    return Object.entries(yd)
      .filter(([_, v]) => v.confirmed)
      .map(([k, _]) => Number(k))
      .sort((a, b) => a - b)
  })
  const isAllConfirmed = computed(() => (year) => {
    return getAllConfirmedMonths.value(year).length === 12
  })

  /* ── Actions ── */

  /** 保存某月数据 */
  function saveMonth(year, month, days) {
    if (!yearData.value[year]) yearData.value[year] = {}
    yearData.value[year][month] = {
      confirmed: true,
      days: days.map(d => ({
        date: d.date,
        category: d.category,
        name: d.name || ''
      }))
    }
    persist()
  }

  /** 取消某月确认 */
  function unconfirmMonth(year, month) {
    const yd = yearData.value[year]
    if (yd && yd[month]) {
      yd[month].confirmed = false
      persist()
    }
  }

  /** 确认某月（仅改状态，days 已存在） */
  function confirmMonthOnly(year, month) {
    if (!yearData.value[year]) yearData.value[year] = {}
    if (!yearData.value[year][month]) yearData.value[year][month] = { confirmed: false, days: [] }
    yearData.value[year][month].confirmed = true
    persist()
  }

  /** 获取全年确认后可提交的数据 */
  function getSubmitPayload(year) {
    const yd = yearData.value[year]
    if (!yd) return null
    const payload = []
    for (let m = 1; m <= 12; m++) {
      const md = yd[m]
      if (!md || !md.confirmed) return null // 有未确认的月份
      payload.push(...md.days)
    }
    return {
      year,
      total: payload.length,
      days: payload
    }
  }

  /** 加载某年的 localStorage 缓存 */
  function loadFromStorage(year) {
    try {
      const key = `holiday_store_${year}`
      const raw = localStorage.getItem(key)
      if (raw) {
        const parsed = JSON.parse(raw)
        if (!yearData.value[year]) yearData.value[year] = {}
        Object.assign(yearData.value[year], parsed)
      }
    } catch (e) {
      console.warn('load holiday store failed:', e)
    }
  }

  /** 清除某年数据 */
  function clearYear(year) {
    if (yearData.value[year]) {
      delete yearData.value[year]
      persist()
    }
    localStorage.removeItem(`holiday_store_${year}`)
  }

  /* ── 持久化 ── */
  function persist() {
    for (const year of Object.keys(yearData.value)) {
      const key = `holiday_store_${year}`
      localStorage.setItem(key, JSON.stringify(yearData.value[year]))
    }
  }

  return {
    yearData,
    getYearData,
    getMonthData,
    isMonthConfirmed,
    getAllConfirmedMonths,
    isAllConfirmed,
    saveMonth,
    unconfirmMonth,
    confirmMonthOnly,
    getSubmitPayload,
    loadFromStorage,
    clearYear
  }
})
