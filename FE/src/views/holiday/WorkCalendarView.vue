<template>
  <div class="calendar-view-page">
    <h2 class="page-title">工作日历</h2>
    <p class="page-desc">查看全年工作日 / 节假日安排</p>

    <!-- 控制栏 -->
    <div class="control-bar">
      <div class="control-left">
        <span class="control-label">年份</span>
        <el-select v-model="selectedYear" style="width: 130px" @change="fetchData">
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
        </el-select>
      </div>
    </div>

    <!-- 图例 + 统计 -->
    <div v-if="hasData && !loading" class="info-bar">
      <div class="legend">
        <div class="legend-item" v-for="item in legendItems" :key="item.key">
          <span class="legend-color" :style="{ background: item.bg, borderColor: item.border }"></span>
          <span class="legend-text">{{ item.label }}</span>
          <span class="legend-count">{{ stats[item.key] || 0 }}天</span>
        </div>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="state-wrap">
      <el-icon class="is-loading" :size="36"><Loading /></el-icon>
      <p>加载中...</p>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && !hasData" class="state-wrap">
      <el-empty :description="errorMsg || `${selectedYear}年暂无日历数据`" />
    </div>

    <!-- 12 个月日历 -->
    <div v-if="hasData && !loading" class="months-grid">
      <div v-for="m in 12" :key="m" class="month-card">
        <div class="month-header">
          <span class="month-title">{{ selectedYear }}年{{ m }}月</span>
        </div>
        <div class="weekday-row">
          <span v-for="w in ['一','二','三','四','五','六','日']" :key="w" class="weekday-cell">{{ w }}</span>
        </div>
        <div class="days-grid">
          <span
            v-for="(cell, idx) in getMonthCells(m)"
            :key="idx"
            class="day-cell"
            :class="cell ? cell.category : 'empty'"
            :title="cell ? getCellTooltip(cell) : ''"
          >
            <template v-if="cell">
              <span class="day-num">{{ cell.day }}</span>
            </template>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getWorkCalendar } from '@/api/holiday'

const selectedYear = ref(new Date().getFullYear())
const loading = ref(false)
const errorMsg = ref('')
const dayData = ref({})

const yearOptions = [2026, 2025, 2024, 2023, 2022, 2021, 2020, 2019, 2018, 2017, 2016]

const hasData = computed(() => Object.keys(dayData.value).length > 0)

/* day_type → category 映射 */
const dayTypeMap = {
  0: 'workday',
  1: 'weekend',
  2: 'legal',
  3: 'adjusted'
}

const legendItems = [
  { key: 'workday',  label: '工作日',     bg: '#ffffff', border: '#d4d4d8' },
  { key: 'weekend',  label: '双休日',     bg: '#f1f5f9', border: '#cbd5e1' },
  { key: 'legal',    label: '法定节假日', bg: '#fef2f2', border: '#fca5a5' },
  { key: 'adjusted', label: '调休日',     bg: '#fffbeb', border: '#fcd34d' }
]

const stats = computed(() => {
  const s = { workday: 0, weekend: 0, legal: 0, adjusted: 0 }
  Object.values(dayData.value).forEach(d => {
    if (s[d.category] !== undefined) s[d.category]++
  })
  return s
})

async function fetchData() {
  if (!selectedYear.value) return
  loading.value = true
  errorMsg.value = ''
  dayData.value = {}
  try {
    const res = await getWorkCalendar(selectedYear.value)
    const data = res.data
    if (data.code === 200 && Array.isArray(data.data)) {
      const result = {}
      data.data.forEach(item => {
        const dateStr = typeof item.date === 'string' ? item.date : String(item.date)
        const parts = dateStr.split('-')
        const day = parseInt(parts[2])
        const month = parseInt(parts[1])
        result[dateStr] = {
          date: dateStr,
          day,
          month,
          dayType: item.dayType,
          category: dayTypeMap[item.dayType] || 'workday'
        }
      })
      dayData.value = result
      if (Object.keys(result).length === 0) {
        errorMsg.value = `${selectedYear.value}年暂无日历数据`
      }
    } else {
      errorMsg.value = data.message || '查询失败'
    }
  } catch (err) {
    console.error('Calendar API error:', err)
    errorMsg.value = err.response?.data?.message || err.message || '网络错误'
  } finally {
    loading.value = false
  }
}

function getMonthCells(month) {
  if (!selectedYear.value) return []
  const year = selectedYear.value
  const firstDay = new Date(year, month - 1, 1)
  const daysInMonth = new Date(year, month, 0).getDate()

  let firstDow = firstDay.getDay()
  firstDow = firstDow === 0 ? 6 : firstDow - 1

  const cells = []
  for (let i = 0; i < firstDow; i++) cells.push(null)
  for (let d = 1; d <= daysInMonth; d++) {
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    const data = dayData.value[dateStr]
    if (data) {
      cells.push(data)
    } else {
      const dow = new Date(year, month - 1, d).getDay()
      const isWeekend = dow === 0 || dow === 6
      cells.push({
        date: dateStr,
        day: d,
        month,
        category: isWeekend ? 'weekend' : 'workday'
      })
    }
  }
  return cells
}

function getCellTooltip(cell) {
  const labels = {
    workday: '工作日',
    weekend: '双休日',
    legal: '法定节假日',
    adjusted: '调休日'
  }
  return `${cell.date} — ${labels[cell.category] || ''}`
}

fetchData()
</script>

<style scoped>
.calendar-view-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 4px;
  color: #1e293b;
}

.page-desc {
  font-size: 13px;
  color: #64748b;
  margin: 0 0 20px;
}

.control-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.control-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.control-label {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.info-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  background: #fff;
  border-radius: 12px;
  padding: 14px 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}
.legend {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}
.legend-color {
  display: inline-block;
  width: 16px;
  height: 16px;
  border-radius: 4px;
  border: 1px solid;
}
.legend-text {
  font-size: 13px;
  color: #374151;
}
.legend-count {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 600;
}

.state-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #94a3b8;
}
.state-wrap p {
  margin-top: 16px;
  font-size: 14px;
}

.months-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.month-card {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}
.month-header {
  margin-bottom: 10px;
}
.month-title {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
}

.weekday-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
  margin-bottom: 4px;
}
.weekday-cell {
  text-align: center;
  font-size: 11px;
  font-weight: 600;
  color: #94a3b8;
  padding: 2px 0;
}

.days-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}
.day-cell {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 5px;
  border: 1px solid transparent;
  min-height: 30px;
  user-select: none;
}
.day-cell.empty {
  background: transparent;
  border: none;
}
.day-num {
  font-size: 12px;
  font-weight: 600;
  line-height: 1;
}

.day-cell.workday {
  background: #fff;
  border-color: #e4e4e7;
  color: #374151;
}
.day-cell.weekend {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #64748b;
}
.day-cell.legal {
  background: #fef2f2;
  border-color: #fca5a5;
  color: #dc2626;
}
.day-cell.adjusted {
  background: #fffbeb;
  border-color: #fcd34d;
  color: #d97706;
}

@media (max-width: 1200px) {
  .months-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 900px) {
  .months-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 600px) {
  .months-grid { grid-template-columns: 1fr; }
  .day-cell { min-height: 36px; }
  .day-num { font-size: 13px; }
}
</style>
