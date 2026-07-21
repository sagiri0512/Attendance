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
        <el-table-column label="日期" width="120" fixed>
          <template #default="{ row }">
            <span class="date-cell">{{ formatDate(row.adate) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="上班打卡" width="140">
          <template #default="{ row }">
            <span v-if="row.aclockIn">{{ formatTime(row.aclockIn) }}</span>
            <span v-else class="null-text">--</span>
          </template>
        </el-table-column>

        <el-table-column label="下班打卡" width="140">
          <template #default="{ row }">
            <span v-if="row.aclockOut">{{ formatTime(row.aclockOut) }}</span>
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
            <span v-if="row.aabsentHours > 0" class="absent-hours">{{ row.aabsentHours }}</span>
            <span v-else class="zero-text">0</span>
          </template>
        </el-table-column>

        <el-table-column label="请假信息" min-width="200">
          <template #default="{ row }">
            <div v-if="row.aleaveId" class="leave-info">
              <span class="leave-type">{{ leaveTypeLabel(row.ltype) }}</span>
              <span class="leave-days">{{ row.ldays }}天</span>
              <el-tag
                :type="leaveFinalType(row.lfinal)"
                size="small"
                effect="light"
                round
                class="leave-status-tag"
              >
                {{ leaveStatusLabel(row.lfinal) }}
              </el-tag>
              <div class="leave-detail" v-if="row.lreason">
                <span class="reason-text">{{ row.lreason }}</span>
              </div>
              <div class="leave-range" v-if="row.lstart">
                {{ formatTime(row.lstart) }} ~ {{ formatTime(row.lend) }}
              </div>
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

// 格式化日期 2026-07-20 → 7/20
function formatDate(str) {
  if (!str) return '--'
  const d = new Date(str)
  const m = d.getMonth() + 1
  const day = d.getDate()
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  return `${m}/${day} 周${weekdays[d.getDay()]}`
}

// 格式化时间 2026-07-20T08:55:00 → 08:55
function formatTime(str) {
  if (!str) return '--'
  const d = new Date(str)
  const h = String(d.getHours()).padStart(2, '0')
  const m = String(d.getMinutes()).padStart(2, '0')
  return `${h}:${m}`
}

// 状态显示文字
function displayStatus(row) {
  const s = row.astatus
  if (s === 0) return '正常'
  if (s === 1) {
    if (row.aleaveId && row.lfinal === 0) return '缺勤(审批中)'
    return '缺勤'
  }
  if (s === 2) return '请假'
  if (s === 3) return '请假缺勤'
  return '未知'
}

// 状态 tag 类型
function statusType(row) {
  const s = row.astatus
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

// 行样式
function rowClassName({ row }) {
  if (row.astatus === 0) return 'row-normal'
  if (row.astatus === 1) return 'row-absent'
  if (row.astatus === 2) return 'row-leave'
  return ''
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
      records.value = data.records || []
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

/* 日期列 */
.date-cell {
  font-weight: 600;
  color: #1a1a2e;
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
</style>
