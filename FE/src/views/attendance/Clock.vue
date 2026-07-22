<template>
  <div class="clock-page">
    <!-- 当前时间 -->
    <div class="time-display">
      <div class="time">{{ currentTime }}</div>
      <div class="date">{{ currentDate }}</div>
    </div>

    <!-- 打卡按钮 -->
    <div class="clock-btn-wrapper" @click="handleClock">
      <div class="clock-btn" :class="{ active: isClocking }">
        <span class="btn-icon">⏰</span>
        <span class="btn-text">{{ isClocking ? '打卡中...' : '点击打卡' }}</span>
      </div>
    </div>

    <!-- 打卡结果 -->
    <div v-if="resultMsg" class="result-card" :class="{ success: resultSuccess }">
      <span class="result-icon">{{ resultSuccess ? '✅' : '❌' }}</span>
      <span>{{ resultMsg }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { clock } from '@/api/attendance'

const currentTime = ref('')
const currentDate = ref('')
const isClocking = ref(false)
const resultMsg = ref('')
const resultSuccess = ref(false)
let timer = null

function updateTime() {
  const d = new Date()
  const pad = n => String(n).padStart(2, '0')
  currentTime.value = `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
  currentDate.value = `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  clearInterval(timer)
})

async function handleClock() {
  if (isClocking.value) return
  isClocking.value = true
  resultMsg.value = ''
  try {
    const res = await clock()
    resultMsg.value = res.data.data || '打卡成功'
    resultSuccess.value = true
  } catch {
    // 错误信息已在 request.js 拦截器中处理
  } finally {
    isClocking.value = false
    setTimeout(() => { resultMsg.value = '' }, 3000)
  }
}
</script>

<style scoped>
.clock-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 24px;
  max-width: 500px;
  margin: 0 auto;
}

.time-display {
  text-align: center;
  margin-bottom: 48px;
}
.time {
  font-size: 64px;
  font-weight: 300;
  color: #1a1a2e;
  letter-spacing: 4px;
  font-variant-numeric: tabular-nums;
}
.date {
  margin-top: 8px;
  font-size: 16px;
  color: #94a3b8;
}

.clock-btn-wrapper {
  cursor: pointer;
  margin-bottom: 32px;
}
.clock-btn {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  transition: all 0.3s;
  box-shadow: 0 8px 32px rgba(99, 102, 241, 0.35);
}
.clock-btn:hover {
  transform: scale(1.04);
  box-shadow: 0 12px 40px rgba(99, 102, 241, 0.45);
}
.clock-btn:active {
  transform: scale(0.96);
}
.clock-btn.active {
  opacity: 0.7;
  pointer-events: none;
}
.btn-icon {
  font-size: 36px;
  margin-bottom: 8px;
}
.btn-text {
  font-size: 16px;
  font-weight: 500;
}

.result-card {
  padding: 14px 28px;
  border-radius: 12px;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
  animation: fadeIn 0.3s;
}
.result-card.success {
  background: #f0fdf4;
  color: #16a34a;
}
.result-card:not(.success) {
  background: #fef2f2;
  color: #dc2626;
}
.result-icon {
  font-size: 18px;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* 小屏适配 */
@media (max-width: 420px) {
  .clock-page {
    padding: 40px 16px;
  }
  .time {
    font-size: 44px;
    letter-spacing: 2px;
  }
  .date {
    font-size: 14px;
  }
  .clock-btn {
    width: 140px;
    height: 140px;
  }
  .btn-icon {
    font-size: 28px;
  }
  .btn-text {
    font-size: 14px;
  }
  .time-display {
    margin-bottom: 36px;
  }
}
</style>
