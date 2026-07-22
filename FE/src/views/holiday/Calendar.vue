<template>
  <div class="holiday-calendar-page">
    <h2 class="page-title">节假日日历管理</h2>
    <p class="page-desc">系统管理员确认全年工作日 / 节假日配置，数据来源：timor.tech 节假日 API</p>

    <!-- 顶部控制栏 -->
    <div class="control-bar">
      <div class="control-left">
        <span class="control-label">选择年份</span>
        <el-select
          v-model="selectedYear"
          placeholder="请选择年份"
          style="width: 130px"
          @change="onYearChange"
        >
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
        </el-select>
        <el-button
          type="primary"
          @click="fetchData"
          :loading="loading"
          :disabled="!selectedYear"
        >
          获取节假日数据
        </el-button>
        <el-button
          v-if="hasData"
          @click="confirmAllMonths"
          type="success"
          plain
        >
          全部确认
        </el-button>
        <el-button
          v-if="canSubmit"
          @click="submitToBackend"
          type="danger"
          :loading="submitting"
        >
          {{ submitting ? '提交中...' : '提交到后端' }}
        </el-button>
      </div>
    </div>

    <!-- 图例 + 统计 + 确认进度 -->
    <div v-if="hasData && !loading" class="info-bar">
      <div class="legend">
        <div class="legend-item" v-for="item in legendItems" :key="item.key">
          <span
            class="legend-color"
            :style="{ background: item.bg, borderColor: item.border }"
          ></span>
          <span class="legend-text">{{ item.label }}</span>
          <span class="legend-count">{{ stats[item.key] || 0 }}天</span>
        </div>
      </div>
      <div class="confirm-progress">
        <span class="progress-label">确认进度</span>
        <el-progress
          :percentage="confirmProgress"
          :stroke-width="10"
          style="width: 160px"
        />
        <span class="progress-text">{{ confirmedCount }}/12 月</span>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="state-wrap">
      <el-icon class="is-loading" :size="36"><Loading /></el-icon>
      <p>正在获取 {{ selectedYear }} 年节假日数据...</p>
    </div>

    <!-- 错误提示 -->
    <el-alert
      v-if="errorMsg"
      :title="errorMsg"
      type="error"
      show-icon
      :closable="false"
      style="margin-bottom: 16px"
    />

    <!-- 空状态 -->
    <div v-if="!loading && !hasData && !errorMsg" class="state-wrap">
      <el-empty description="请先选择年份，然后点击「获取节假日数据」" />
    </div>

    <!-- 12 个月日历 -->
    <div v-if="hasData && !loading" class="months-grid">
      <div
        v-for="m in 12"
        :key="m"
        class="month-card"
        :class="{ confirmed: isMonthConfirmed(m) }"
      >
        <!-- 月份标题 -->
        <div class="month-header">
          <span class="month-title">{{ selectedYear }}年{{ m }}月</span>
          <el-tag
            :type="isMonthConfirmed(m) ? 'success' : 'info'"
            size="small"
            effect="plain"
          >
            {{ isMonthConfirmed(m) ? '已确认' : '待确认' }}
          </el-tag>
        </div>

        <!-- 星期表头 -->
        <div class="weekday-row">
          <span
            v-for="w in ['一','二','三','四','五','六','日']"
            :key="w"
            class="weekday-cell"
          >{{ w }}</span>
        </div>

        <!-- 日期格子 -->
        <div class="days-grid">
          <span
            v-for="(cell, idx) in getMonthCells(m)"
            :key="idx"
            class="day-cell"
            :class="[cell ? cell.category : '', isMonthConfirmed(m) ? 'readonly' : 'editable']"
            :title="cell ? getCellTooltip(cell) : ''"
            @click="cell && !isMonthConfirmed(m) && toggleDayType(cell)"
          >
            <template v-if="cell">
              <span class="day-num">{{ cell.day }}</span>
              <span v-if="cell.name" class="day-name">{{ cell.name }}</span>
            </template>
          </span>
        </div>

        <!-- 月度操作栏 -->
        <div class="month-footer">
          <el-button
            size="small"
            :type="isMonthConfirmed(m) ? 'success' : 'primary'"
            :plain="!isMonthConfirmed(m)"
            @click="toggleConfirm(m)"
          >
            {{ isMonthConfirmed(m) ? '取消确认' : '确认本月' }}
          </el-button>
          <span class="month-stats">{{ getMonthStats(m) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getYearHoliday, submitHolidayConfig } from '@/api/holiday'
import { useHolidayStore } from '@/stores/holiday'

/* ── Store ── */
const holidayStore = useHolidayStore()

/* ── 响应式状态 ── */
const selectedYear = ref(null)
const loading = ref(false)
const errorMsg = ref('')
const dayData = ref({})                    // 键: "YYYY-MM-DD" → 日期详情
const submitting = ref(false)

/* ── 年份选项（API 支持 2013~2026）── */
const yearOptions = [2026, 2025, 2024, 2023, 2022, 2021, 2020, 2019, 2018, 2017, 2016, 2015, 2014, 2013]

const hasData = computed(() => Object.keys(dayData.value).length > 0)

/* ── 图例定义 ── */
const legendItems = [
  { key: 'workday',  label: '工作日',     bg: '#ffffff', border: '#d4d4d8' },
  { key: 'weekend',  label: '双休日',     bg: '#f1f5f9', border: '#cbd5e1' },
  { key: 'legal',    label: '法定节假日', bg: '#fef2f2', border: '#fca5a5' },
  { key: 'adjusted', label: '调休日',     bg: '#fffbeb', border: '#fcd34d' },
  { key: 'makeup',   label: '调休补班',   bg: '#eff6ff', border: '#93c5fd' }
]

/* ── 类型切换顺序 ── */
const typeCycle = ['workday', 'adjusted', 'weekend', 'legal', 'makeup']
const nextTypeMap = Object.fromEntries(typeCycle.map((t, i) => [t, typeCycle[(i + 1) % typeCycle.length]]))

/* ── 统计 ── */
const stats = computed(() => {
  const s = { workday: 0, weekend: 0, legal: 0, adjusted: 0, makeup: 0 }
  Object.values(dayData.value).forEach(d => {
    if (s[d.category] !== undefined) s[d.category]++
  })
  return s
})

const confirmedCount = computed(() => {
  if (!selectedYear.value) return 0
  return holidayStore.getAllConfirmedMonths(selectedYear.value).length
})
const confirmProgress = computed(() => Math.round(confirmedCount.value / 12 * 100))
const canSubmit = computed(() => selectedYear.value && holidayStore.isAllConfirmed(selectedYear.value))

/* ── 辅助：判断某月是否已确认 ── */
function isMonthConfirmed(month) {
  if (!selectedYear.value) return false
  return holidayStore.isMonthConfirmed(selectedYear.value, month)
}

/* ── 年份切换 ── */
function onYearChange() {
  dayData.value = {}
  errorMsg.value = ''
  // 从 store 加载已确认的月份数据
  if (selectedYear.value) {
    holidayStore.loadFromStorage(selectedYear.value)
  }
}

/* ── 拉取 API 数据 ── */
async function fetchData() {
  if (!selectedYear.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await getYearHoliday(selectedYear.value)
    const data = res.data
    if (data.code !== 0) {
      errorMsg.value = `API 返回错误: ${data.msg || JSON.stringify(data)}`
      return
    }
    parseApiData(data, selectedYear.value)
    // 加载 store 中已确认的数据覆盖当前
    holidayStore.loadFromStorage(selectedYear.value)
    applyStoreDataToCalendar()
    ElMessage.success(`成功获取 ${selectedYear.value} 年节假日数据（${Object.keys(dayData.value).length} 天）`)
  } catch (err) {
    console.error('Holiday API error:', err)
    errorMsg.value = `获取数据失败: ${err.message || '网络错误'}。timor.tech API 可能暂时不可用，请稍后重试。`
  } finally {
    loading.value = false
  }
}

/* ── 解析 API 返回数据 ── */
function parseApiData(data, year) {
  const typeObj = data.type || {}
  const holidayObj = data.holiday || {}
  const result = {}

  const isLeap = (year % 4 === 0 && year % 100 !== 0) || (year % 400 === 0)
  const totalDays = isLeap ? 366 : 365

  for (let i = 0; i < totalDays; i++) {
    const date = new Date(year, 0, 1 + i)
    const month = date.getMonth() + 1
    const day = date.getDate()
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
    const mmdd = `${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`

    const dow = date.getDay()
    const week = dow === 0 ? 7 : dow

    const typeInfo = typeObj[dateStr]
    const holidayInfo = holidayObj[mmdd]

    let type, wage, name, category

    if (typeInfo) {
      type = typeInfo.type
      wage = holidayInfo?.wage
      name = holidayInfo?.name || ''
      if (name === '周六' || name === '周日') name = ''

      if (type === 0) category = 'workday'
      else if (type === 1) category = 'weekend'
      else if (type === 2) category = (wage === 3) ? 'legal' : 'adjusted'
      else if (type === 3) category = 'makeup'
      else category = 'workday'
    } else {
      type = 0
      wage = 1
      name = ''
      category = 'workday'
    }

    result[dateStr] = {
      date: dateStr,
      day,
      month,
      type,
      wage: wage || 1,
      name,
      week,
      category
    }
  }

  dayData.value = result
}

/* ── 将 store 中已确认的数据应用到当前日历 ── */
function applyStoreDataToCalendar() {
  if (!selectedYear.value) return
  const yd = holidayStore.getYearData(selectedYear.value)
  for (let m = 1; m <= 12; m++) {
    const md = yd[m]
    if (md && md.days) {
      md.days.forEach(d => {
        if (dayData.value[d.date]) {
          dayData.value[d.date].category = d.category
          if (d.name) dayData.value[d.date].name = d.name
        }
      })
    }
  }
}

/* ── 生成月份日历格子 ── */
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
        type: isWeekend ? 1 : 0,
        name: '',
        week: dow === 0 ? 7 : dow,
        category: isWeekend ? 'weekend' : 'workday'
      })
    }
  }
  return cells
}

/* ── Tooltip ── */
function getCellTooltip(cell) {
  const typeLabels = {
    workday: '工作日',
    weekend: '双休日',
    legal: '法定节假日（3倍工资）',
    adjusted: '调休日（2倍工资）',
    makeup: '调休补班（正常上班）'
  }
  const weekLabels = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五', 6: '周六', 7: '周日' }
  let tip = `${cell.date} ${weekLabels[cell.week] || ''} — ${typeLabels[cell.category] || ''}`
  if (cell.name) tip += `\n节日: ${cell.name}`
  if (cell.wage) tip += ` | 工资倍数: ${cell.wage}倍`
  if (!isMonthConfirmed(cell.month)) {
    tip += '\n【点击切换类型】'
  }
  return tip
}

/* ── 月度统计摘要 ── */
function getMonthStats(month) {
  const cells = getMonthCells(month).filter(c => c)
  const counts = { workday: 0, weekend: 0, legal: 0, adjusted: 0, makeup: 0 }
  cells.forEach(c => { if (counts[c.category] !== undefined) counts[c.category]++ })
  const parts = []
  if (counts.legal) parts.push(`法定${counts.legal}`)
  if (counts.adjusted) parts.push(`调休${counts.adjusted}`)
  if (counts.makeup) parts.push(`补班${counts.makeup}`)
  parts.push(`上班${counts.workday + counts.makeup}`)
  parts.push(`休息${counts.weekend + counts.legal + counts.adjusted}`)
  return parts.join(' · ')
}

/* ── 点击日期切换类型 ── */
function toggleDayType(cell) {
  if (!cell || !cell.date) return
  const current = cell.category
  const next = nextTypeMap[current] || 'workday'
  cell.category = next
  // 同步到响应式对象
  if (dayData.value[cell.date]) {
    dayData.value[cell.date].category = next
  }
  ElMessage.info(`${cell.date} 已改为「${getCategoryLabel(next)}」`)
}

function getCategoryLabel(cat) {
  const map = { workday: '工作日', weekend: '双休日', legal: '法定节假日', adjusted: '调休日', makeup: '调休补班' }
  return map[cat] || cat
}

/* ── 确认 / 取消确认 ── */
function toggleConfirm(month) {
  if (isMonthConfirmed(month)) {
    ElMessageBox.confirm(
      `确定取消 ${selectedYear.value}年${month}月 的确认状态吗？`,
      '取消确认',
      { type: 'warning' }
    ).then(() => {
      holidayStore.unconfirmMonth(selectedYear.value, month)
      ElMessage.info(`已取消 ${month}月 确认`)
    }).catch(() => {})
  } else {
    // 保存当月数据到 store
    const cells = getMonthCells(month).filter(c => c)
    holidayStore.saveMonth(selectedYear.value, month, cells)
    ElMessage.success(`${selectedYear.value}年${month}月 已确认并保存`)
  }
}

/* ── 全部确认 ── */
function confirmAllMonths() {
  ElMessageBox.confirm(
    `确定确认 ${selectedYear.value} 年全部 12 个月的节假日配置吗？`,
    '全部确认',
    { type: 'warning' }
  ).then(() => {
    for (let m = 1; m <= 12; m++) {
      const cells = getMonthCells(m).filter(c => c)
      holidayStore.saveMonth(selectedYear.value, m, cells)
    }
    ElMessage.success(`${selectedYear.value}年全年节假日已确认`)
  }).catch(() => {})
}

/* ── 提交到后端 ── */
async function submitToBackend() {
  const payload = holidayStore.getSubmitPayload(selectedYear.value)
  if (!payload) {
    ElMessage.warning('尚有月份未确认，无法提交')
    return
  }
  submitting.value = true
  try {
    const res = await submitHolidayConfig(payload)
    const data = res.data
    if (data.code === 200) {
      ElMessage.success(data.data || `${selectedYear.value}年节假日配置已提交成功`)
    } else {
      ElMessage.error(data.message || '提交失败')
    }
  } catch (err) {
    console.error('Submit error:', err)
    ElMessage.error(`提交失败: ${err.response?.data?.message || err.message || '网络错误'}`)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.holiday-calendar-page {
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

/* ── 控制栏 ── */
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
  flex-wrap: wrap;
}
.control-label {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  white-space: nowrap;
}

/* ── 图例 + 进度 ── */
.info-bar {
  display: flex;
  justify-content: space-between;
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
  white-space: nowrap;
}
.legend-count {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 600;
}
.confirm-progress {
  display: flex;
  align-items: center;
  gap: 8px;
}
.progress-label {
  font-size: 13px;
  color: #64748b;
  white-space: nowrap;
}
.progress-text {
  font-size: 13px;
  font-weight: 600;
  color: #4f46e5;
  white-space: nowrap;
}

/* ── 加载 / 空状态 ── */
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

/* ── 12 个月网格 ── */
.months-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

/* ── 月份卡片 ── */
.month-card {
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  border: 2px solid transparent;
  transition: border-color 0.2s;
}
.month-card.confirmed {
  border-color: #22c55e;
}
.month-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.month-title {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
}

/* ── 星期表头 ── */
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

/* ── 日期格子 ── */
.days-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}
.day-cell {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 5px;
  border: 1px solid transparent;
  overflow: hidden;
  min-height: 30px;
  user-select: none;
}
.day-cell.editable {
  cursor: pointer;
}
.day-cell.editable:hover {
  filter: brightness(0.92);
  transform: scale(1.05);
  transition: all 0.15s;
}
.day-cell.readonly {
  cursor: not-allowed;
  opacity: 0.85;
}
.day-num {
  font-size: 12px;
  font-weight: 600;
  line-height: 1;
}
.day-name {
  font-size: 8px;
  line-height: 1;
  margin-top: 1px;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 0 1px;
}

/* ── 日期类型颜色 ── */
.day-cell.workday {
  background: #ffffff;
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
.day-cell.legal .day-name {
  color: #dc2626;
}
.day-cell.adjusted {
  background: #fffbeb;
  border-color: #fcd34d;
  color: #d97706;
}
.day-cell.adjusted .day-name {
  color: #d97706;
}
.day-cell.makeup {
  background: #eff6ff;
  border-color: #93c5fd;
  color: #2563eb;
}
.day-cell.makeup .day-name {
  color: #2563eb;
}

/* ── 月度操作栏 ── */
.month-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  padding-top: 8px;
  border-top: 1px solid #f1f5f9;
}
.month-stats {
  font-size: 11px;
  color: #94a3b8;
  text-align: right;
  line-height: 1.4;
}

/* ── 响应式 ── */
@media (max-width: 1200px) {
  .months-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 900px) {
  .months-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 600px) {
  .months-grid {
    grid-template-columns: 1fr;
  }
  .info-bar {
    flex-direction: column;
    align-items: flex-start;
  }
  .day-cell {
    min-height: 36px;
  }
  .day-num {
    font-size: 13px;
  }
  .day-name {
    font-size: 9px;
  }
}
</style>
