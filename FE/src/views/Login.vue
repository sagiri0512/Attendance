<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-shapes">
      <div class="shape shape-1" />
      <div class="shape shape-2" />
      <div class="shape shape-3" />
    </div>

    <div class="login-card">
      <!-- 头部 -->
      <div class="card-header">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10" />
            <polyline points="12 6 12 12 16 14" />
          </svg>
        </div>
        <h1>考勤面板</h1>
        <p>员工登录</p>
      </div>

      <!-- 表单 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="empNo">
          <el-input
            v-model="form.empNo"
            placeholder="请输入工号"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 底部 -->
    <p class="footer-text">Attendance System</p>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  empNo: '',
  password: ''
})

const rules = {
  empNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login({
      empNo: form.empNo,
      password: form.password
    })
    userStore.setToken(res.data)
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
    // 错误信息已在 request.js 拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f172a 100%);
  overflow: hidden;
  position: relative;
}

/* ── 背景浮动装饰 ── */
.bg-shapes {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}
.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
  animation: float 20s ease-in-out infinite;
}
.shape-1 {
  width: 400px;
  height: 400px;
  background: #6366f1;
  top: -10%;
  left: -5%;
  animation-delay: 0s;
}
.shape-2 {
  width: 300px;
  height: 300px;
  background: #8b5cf6;
  bottom: -12%;
  right: -8%;
  animation-delay: -7s;
}
.shape-3 {
  width: 250px;
  height: 250px;
  background: #3b82f6;
  top: 50%;
  left: 55%;
  animation-delay: -14s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33%  { transform: translate(30px, -40px) scale(1.08); }
  66%  { transform: translate(-20px, 30px) scale(0.94); }
}

/* ── 登录卡片 ── */
.login-card {
  position: relative;
  z-index: 1;
  width: 400px;
  padding: 44px 40px 36px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
}

/* ── 头部 ── */
.card-header {
  text-align: center;
  margin-bottom: 32px;
}
.logo-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 16px;
  color: #818cf8;
  background: rgba(129, 140, 248, 0.1);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.logo-icon svg {
  width: 32px;
  height: 32px;
}
.card-header h1 {
  font-size: 22px;
  font-weight: 600;
  color: #f1f5f9;
  margin: 0 0 4px;
  letter-spacing: 1px;
}
.card-header p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* ── 表单 ── */
.login-card :deep(.el-form-item) {
  margin-bottom: 20px;
}
.login-card :deep(.el-form-item__label) {
  display: none;
}
.login-card :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.04);
  border-radius: 10px;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.1);
  transition: box-shadow 0.25s, background 0.25s;
}
.login-card :deep(.el-input__wrapper:hover) {
  background: rgba(255, 255, 255, 0.06);
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.18);
}
.login-card :deep(.el-input__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.08);
  box-shadow: 0 0 0 2px rgba(129, 140, 248, 0.5);
}
.login-card :deep(.el-input__inner) {
  color: #e2e8f0;
}
.login-card :deep(.el-input__inner::placeholder) {
  color: #475569;
}
.login-card :deep(.el-input__prefix) {
  color: #64748b;
}

.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  letter-spacing: 4px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border: none;
  transition: opacity 0.25s, transform 0.2s;
}
.login-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}
.login-btn:active {
  transform: translateY(0);
}

/* ── 底部文字 ── */
.footer-text {
  position: relative;
  z-index: 1;
  margin-top: 24px;
  font-size: 12px;
  color: #334155;
  letter-spacing: 2px;
}
</style>
