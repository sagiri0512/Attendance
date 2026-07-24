<template>
  <div class="record-page">
    <!-- 数据表格 -->
    <div class="table-wrapper">
      <el-table
        :data="records"
        stripe
        v-loading="loading"
        empty-text="暂无考勤记录"
        style="width: 100%"
        :header-cell-style="{ background: '#1a1a2e', color: '#e2e8f0', fontWeight: 600 }"
        :row-class-name="rowClassName"
      >
        <el-table-column label="日期" width="170" fixed>
          <template #default="{ row }">
            <div class="date-cell">
              <span class="date-main">{{ formatDate(row.wDate || row.aDate) }}</span>
              <el-tag
                v-if="row.wDayType && row.wDayType !== 0"
                :type="dayTypeTag(row.wDayType)"
                size="small"
                effect="light"
                round
                class="date-type-tag"
              >
                {{ dateExtraLabel(row) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="上班打卡" width="140">
          <template #default="{ row }">
            <span v-if="row.aClockIn">{{ formatTime(row.aClockIn) }}</span>
            <span v-else class="null-text">--</span>
          </template>
        </el-table-column>

        <el-table-column label="下班打卡" width="140">
          <template #default="{ row }">
            <span v-if="row.aClockOut">{{ formatTime(row.aClockOut, row.aDate) }}</span>
            <span v-else class="null-text">--</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="140">
          <template #default="{ row }">
            <el-tag :type="statusType(row)" size="small" effect="plain" round>
              {{ displayStatus(row) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="缺勤(h)" width="90" align="center">
          <template #default="{ row }">
            <span v-if="row.aAbsentHours > 0" class="absent-hours">{{ row.aAbsentHours }}</span>
            <span v-else class="zero-text">0</span>
          </template>
        </el-table-column>

        <el-table-column label="请假信息" min-width="200">
          <template #default="{ row }">
            <div v-if="row.aLeaveId" class="leave-info">
              <span class="leave-type">{{ leaveTypeLabel(row.lType) }}</span>
              <span class="leave-days">{{ row.lDays }}天</span>
              <el-tag
                :type="leaveFinalType(row.lFinal)"
                size="small"
                effect="light"
                round
                class="leave-status-tag"
              >
                {{ leaveStatusLabel(row.lFinal) }}
              </el-tag>
              <div class="leave-detail" v-if="row.lReason">
                <span class="reason-text">{{ row.lReason }}</span>
              </div>
              <div class="leave-range" v-if="row.lStart">
                {{ formatTime(row.lStart) }} ~ {{ formatTime(row.lEnd) }}
              </div>
            </div>
            <span v-else class="null-text">无</span>
          </template>
        </el-table-column>

        <el-table-column label="加班信息" min-width="180">
          <template #default="{ row }">
            <div v-if="row.oHours != null" class="overtime-info">
              <span class="overtime-hours">{{ row.oHours }}h</span>
              <span class="overtime-wage">¥{{ row.oWage != null ? row.oWage : '--' }}</span>
              <el-tag
                :type="overtimeTag(row.oStatus)"
                size="small"
                effect="light"
                round
              >
                {{ overtimeLabel(row.oStatus) }}
              </el-tag>
            </div>
            <span v-else class="null-text">无</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        background
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyRecords } from '@/api/attendance'

const records = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 格式化日期 2026-07-20 → 2026年7月20日 周六
function formatDate(str) {
  if (!str) return '--'
  const d = new Date(str)
  const y = d.getFullYear()
  const m = d.getMonth() + 1
  const day = d.getDate()
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  return `${y}年${m}月${day}日 周${weekdays[d.getDay()]}`
}

// 日期列附加标签：优先显示节假日名称（如"国庆节"），否则显示类型名称
function dateExtraLabel(row) {
  if (row.wHolidayName) return row.wHolidayName
  return dayTypeLabel(row.wDayType)
}

// 格式化时间 2026-07-20T08:55:00 → 08:55
// 如果时间跨天（00:00~05:00 且日期 != record_date），显示"次日 HH:mm"
function formatTime(str, recordDate) {
  if (!str) return '--'
  const d = new Date(str)
  const h = d.getHours()
  const m = String(d.getMinutes()).padStart(2, '0')
  const hh = String(h).padStart(2, '0')
  // 跨天判断：凌晨 0~4 点 且 时间日期和 record_date 不是同一天
  if (h < 5 && recordDate) {
    const rd = new Date(recordDate)
    if (d.toDateString() !== rd.toDateString()) {
      return `次日 ${hh}:${m}`
    }
  }
  return `${hh}:${m}`
}

// 状态显示文字
function displayStatus(row) {
  const s = row.aStatus
  if (s === 0) return '正常'
  if (s === 1) {
    if (row.aLeaveId && row.lFinal === 0) return '缺勤(审批中)'
    return '缺勤'
  }
  if (s === 2) return '请假'
  if (s === 3) return '请假缺勤'
  return '未知'
}

// 状态 tag 类型
function statusType(row) {
  const s = row.aStatus
  if (s === 0) return 'success'
  if (s === 1) return 'danger'
  if (s === 2) return 'warning'
  if (s === 3) return 'danger'
  return 'info'
}

// 请假类型标签
function leaveTypeLabel(t) {
  const map = { 1: '事假', 2: '病假', 3: '年假', 4: '调休' }
  return map[t] || '未知'
}

// 请假审批状态标签
function leaveStatusLabel(f) {
  const map = { 0: '审批中', 1: '已通过', 2: '已驳回', 3: '已取消' }
  return map[f] || '--'
}

// 请假审批状态 tag 类型
function leaveFinalType(f) {
  const map = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }
  return map[f] || 'info'
}

// 日期类型标签
function dayTypeLabel(t) {
  const map = { 0: '工作日', 1: '休息日', 2: '节假日', 3: '调休' }
  return map[t] || '--'
}
function dayTypeTag(t) {
  const map = { 0: '', 1: 'warning', 2: 'danger', 3: 'warning' }
  return map[t] || 'info'
}

// 加班信息
function overtimeLabel(s) {
  const map = { 0: '审批中', 1: '已通过', 2: '已驳回' }
  return map[s] || '--'
}
function overtimeTag(s) {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[s] || 'info'
}

// 行样式
function rowClassName({ row }) {
  if (row.aStatus === 0) return 'row-normal'
  if (row.aStatus === 1) return 'row-absent'
  if (row.aStatus === 2) return 'row-leave'
  return ''
}

// 兼容 Jackson 对单字母前缀字段(wDate/aDate 等)的序列化差异
// 统一映射成驼峰写法，后续代码只用规范名
function normalizeRow(r) {
  return {
    ...r,
    wDate:         r.wDate         ?? r.WDate         ?? r.wdate,
    wDayType:      r.wDayType      ?? r.WDayType      ?? r.wdayType      ?? r.wDaytype,
    wHolidayName:  r.wHolidayName  ?? r.WHolidayName  ?? r.wholidayName,
    aId:           r.aId           ?? r.AId           ?? r.aid,
    aDate:         r.aDate         ?? r.ADate         ?? r.adate,
    aClockIn:      r.aClockIn      ?? r.AClockIn      ?? r.aclockIn,
    aClockOut:     r.aClockOut     ?? r.AClockOut     ?? r.aclockOut,
    aStatus:       r.aStatus       ?? r.AStatus       ?? r.astatus,
    aAbsentHours:  r.aAbsentHours  ?? r.AAbsentHours  ?? r.aabsentHours,
    aLeaveId:      r.aLeaveId      ?? r.ALeaveId      ?? r.aleaveId,
    lType:         r.lType         ?? r.LType         ?? r.ltype,
    lStart:        r.lStart        ?? r.LStart        ?? r.lstart,
    lEnd:          r.lEnd          ?? r.LEnd          ?? r.lend,
    lDays:         r.lDays         ?? r.LDays         ?? r.ldays,
    lReason:       r.lReason       ?? r.LReason       ?? r.lreason,
    lFinal:        r.lFinal        ?? r.LFinal        ?? r.lfinal,
    oHours:        r.oHours        ?? r.OHours        ?? r.ohours,
    oWage:         r.oWage         ?? r.OWage         ?? r.owage,
    oStatus:       r.oStatus       ?? r.OStatus       ?? r.ostatus,
  }
}

async function fetchData() {
  loading.value = true
  try {
    // 后端用 start(偏移量) 和 size(每页条数)，前端 pageNum 从1开始
    const start = (pageNum.value - 1) * pageSize.value
    const size = pageSize.value
    const res = await getMyRecords(start, size)
    const data = res.data?.data
    if (data) {
      records.value = (data.records || []).map(normalizeRow)
      total.value = data.total || 0
    }
  } catch {
    // request.js 已处理全局错误
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.record-page {
  padding: 0;
}

/* 表格容器 */
.table-wrapper {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

/* 移动端表格横滚 */
@media (max-width: 768px) {
  .table-wrapper {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }
  .table-wrapper :deep(.el-table) {
    min-width: 1060px;
  }
}

/* 日期列 */
.date-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.date-main {
  font-weight: 600;
  color: #1a1a2e;
  white-space: nowrap;
}
.date-type-tag {
  align-self: flex-start;
}

/* 空值文本 */
.null-text {
  color: #cbd5e1;
}

/* 缺勤小时高亮 */
.absent-hours {
  color: #f56c6c;
  font-weight: 600;
}
.zero-text {
  color: #67c23a;
}

/* 请假信息 */
.leave-info {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
  line-height: 1.5;
}
.leave-type {
  background: rgba(139, 92, 246, 0.12);
  color: #8b5cf6;
  padding: 2px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}
.leave-days {
  color: #64748b;
  font-size: 13px;
}
.leave-status-tag {
  margin-left: 2px;
}
.leave-detail {
  width: 100%;
}
.reason-text {
  color: #475569;
  font-size: 12px;
}
.leave-range {
  color: #94a3b8;
  font-size: 12px;
  width: 100%;
}

/* 加班信息 */
.overtime-info {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
  line-height: 1.5;
}
.overtime-hours {
  background: rgba(37, 99, 235, 0.12);
  color: #2563eb;
  padding: 2px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}
.overtime-wage {
  color: #64748b;
  font-size: 13px;
}

/* 行状态高亮 */
:deep(.row-normal) { background: #f0fdf4 !important; }
:deep(.row-absent) { background: #fef2f2 !important; }
:deep(.row-leave) { background: #fefce8 !important; }

/* 分页 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .pagination-wrapper {
    justify-content: center;
    margin-top: 12px;
  }
  .pagination-wrapper :deep(.el-pagination) {
    --el-pagination-font-size: 12px;
    --el-pagination-button-width: 28px;
    --el-pagination-button-height: 28px;
  }
}
</style>
