<template>
  <div class="employee-add-page">
    <h2 class="page-title">添加员工</h2>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="110px"
      class="emp-form"
    >
      <!-- 基本信息 -->
      <h3 class="section-title">基本信息</h3>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="角色" prop="role">
            <el-select v-model="form.role" placeholder="请选择角色" @change="onRoleChange" style="width:100%">
              <el-option :value="0" label="员工 (PG)" />
              <el-option :value="1" label="PL" />
              <el-option :value="2" label="PM" />
              <el-option :value="3" label="HR/管理部" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="11 位手机号" maxlength="11" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 上级关系（动态显示） -->
      <h3 class="section-title">上级关系</h3>
      <el-row :gutter="20" v-if="needPm">
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="所属 PM" prop="pmId">
            <el-select
              v-model="form.pmId"
              placeholder="请选择 PM"
              filterable
              clearable
              style="width:100%"
              @change="onPmChange"
            >
              <el-option
                v-for="pm in pmList"
                :key="pm.id"
                :value="pm.id"
                :label="pm.pmname"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" v-if="needPl">
          <el-form-item label="所属 PL" prop="plId">
            <el-select
              v-model="form.plId"
              :placeholder="form.pmId ? '请选择 PL' : '请先选择 PM'"
              :disabled="!form.pmId"
              filterable
              clearable
              style="width:100%"
            >
              <el-option
                v-for="pl in plList"
                :key="pl.id"
                :value="pl.id"
                :label="pl.plname"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <p v-else class="hint">PM / HR 无需设置上级</p>

      <!-- 薪资组成 -->
      <h3 class="section-title">薪资组成</h3>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="基本工资" prop="baseSalary">
            <el-input-number v-model="form.baseSalary" :min="0" :precision="2" controls-position="right" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="岗位工资" prop="positionSalary">
            <el-input-number v-model="form.positionSalary" :min="0" :precision="2" controls-position="right" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="住房补贴">
            <el-input-number v-model="form.housingSubsidy" :min="0" :precision="2" controls-position="right" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="交通补贴">
            <el-input-number v-model="form.carSubsidy" :min="0" :precision="2" controls-position="right" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="餐补">
            <el-input-number v-model="form.mealSubsidy" :min="0" :precision="2" controls-position="right" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="其他补贴">
            <el-input-number v-model="form.otherSubsidy" :min="0" :precision="2" controls-position="right" style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 社保 / 公积金 -->
      <h3 class="section-title">社保 / 公积金</h3>
      <p class="hint">
        基数留空或填 0 → 系统自动使用「基本工资」；比例留空 → 使用南京法定默认比例
      </p>

      <!-- 基数 -->
      <h4 class="sub-title">缴费基数</h4>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="社保基数">
            <el-input-number v-model="form.socialInsuranceConfig.socialInsuranceBase" :min="0" :precision="2" controls-position="right" style="width:100%" placeholder="留空用基本工资" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="公积金基数">
            <el-input-number v-model="form.socialInsuranceConfig.housingFundBase" :min="0" :precision="2" controls-position="right" style="width:100%" placeholder="留空用基本工资" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 养老保险 -->
      <h4 class="sub-title">养老保险</h4>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="个人比例(%)">
            <el-input-number v-model="form.socialInsuranceConfig.pensionPersonal" :min="0" :max="100" :precision="2" controls-position="right" style="width:100%" placeholder="默认 8%" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="公司比例(%)">
            <el-input-number v-model="form.socialInsuranceConfig.pensionCompany" :min="0" :max="100" :precision="2" controls-position="right" style="width:100%" placeholder="默认 16%" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 医疗保险 -->
      <h4 class="sub-title">医疗保险</h4>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="个人比例(%)">
            <el-input-number v-model="form.socialInsuranceConfig.medicalPersonal" :min="0" :max="100" :precision="2" controls-position="right" style="width:100%" placeholder="默认 2%" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="公司比例(%)">
            <el-input-number v-model="form.socialInsuranceConfig.medicalCompany" :min="0" :max="100" :precision="2" controls-position="right" style="width:100%" placeholder="默认 8%" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 失业保险 -->
      <h4 class="sub-title">失业保险</h4>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="个人比例(%)">
            <el-input-number v-model="form.socialInsuranceConfig.unemploymentPersonal" :min="0" :max="100" :precision="2" controls-position="right" style="width:100%" placeholder="默认 0.5%" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="公司比例(%)">
            <el-input-number v-model="form.socialInsuranceConfig.unemploymentCompany" :min="0" :max="100" :precision="2" controls-position="right" style="width:100%" placeholder="默认 0.5%" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 工伤保险 -->
      <h4 class="sub-title">工伤保险（仅公司缴纳）</h4>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="公司比例(%)">
            <el-input-number v-model="form.socialInsuranceConfig.injuryCompany" :min="0" :max="100" :precision="2" controls-position="right" style="width:100%" placeholder="默认 0.4%" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 生育保险 -->
      <h4 class="sub-title">生育保险（仅公司缴纳）</h4>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="公司比例(%)">
            <el-input-number v-model="form.socialInsuranceConfig.maternityCompany" :min="0" :max="100" :precision="2" controls-position="right" style="width:100%" placeholder="默认 0.8%" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 住房公积金 -->
      <h4 class="sub-title">住房公积金</h4>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="个人比例(%)">
            <el-input-number v-model="form.socialInsuranceConfig.housingFundPersonal" :min="0" :max="100" :precision="2" controls-position="right" style="width:100%" placeholder="默认 8%" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-form-item label="公司比例(%)">
            <el-input-number v-model="form.socialInsuranceConfig.housingFundCompany" :min="0" :max="100" :precision="2" controls-position="right" style="width:100%" placeholder="默认 8%" />
          </el-form-item>
        </el-col>
      </el-row>

      <p class="hint">
        初始密码为 <code>123456</code>，工号由系统自动生成。
      </p>

      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSubmit">提交</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { addEmployee, getPmList, getPlList } from '@/api/employee'

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  realName: '',
  role: null,
  phone: '',
  pmId: null,
  plId: null,
  baseSalary: null,
  positionSalary: null,
  housingSubsidy: null,
  carSubsidy: null,
  mealSubsidy: null,
  otherSubsidy: null,
  socialInsuranceConfig: {
    socialInsuranceBase: null,
    housingFundBase: null,
    pensionPersonal: null,
    pensionCompany: null,
    medicalPersonal: null,
    medicalCompany: null,
    unemploymentPersonal: null,
    unemploymentCompany: null,
    injuryCompany: null,
    maternityCompany: null,
    housingFundPersonal: null,
    housingFundCompany: null
  }
})

const rules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  baseSalary: [{ required: true, message: '请输入基本工资', trigger: 'blur' }],
  positionSalary: [{ required: true, message: '请输入岗位工资', trigger: 'blur' }],
  pmId: [{ required: true, message: '请选择 PM', trigger: 'change' }],
  plId: [{ required: true, message: '请选择 PL', trigger: 'change' }]
}

const pmList = ref([])
const plList = ref([])

// 角色是否需要 PM（PG/PL 需要）
const needPm = computed(() => form.role === 0 || form.role === 1)
// 角色是否需要 PL（仅 PG 需要）
const needPl = computed(() => form.role === 0)

function onRoleChange() {
  // 切换角色时清空上级选择
  form.pmId = null
  form.plId = null
  plList.value = []
  // 按需加载 PM 列表：仅 PG/PL 需要，HR/PM 不调接口
  if (needPm.value && pmList.value.length === 0) {
    loadPmList()
  }
}

async function onPmChange() {
  form.plId = null
  plList.value = []
  if (!form.pmId) return
  // 只有 PG 才需要 PL 上级；PL/PM/HR 不需要，不调 /pl-list
  if (!needPl.value) return
  try {
    const res = await getPlList(form.pmId)
    plList.value = res.data.data || []
  } catch {
    // 错误已在 request.js 拦截器统一处理
  }
}

async function loadPmList() {
  try {
    const res = await getPmList()
    pmList.value = res.data.data || []
  } catch {
    // 错误已在 request.js 拦截器统一处理
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  // 组装提交数据：null 字段保留（后端兜底），非 needPm/needPl 时删除上级字段
  const payload = { ...form }
  if (!needPm.value) {
    delete payload.pmId
    delete payload.plId
  } else if (!needPl.value) {
    delete payload.plId
  }

  loading.value = true
  try {
    const res = await addEmployee(payload)
    ElMessage.success(res.data.message || '新增员工成功')
    handleReset()
  } catch {
    // 错误已在 request.js 拦截器统一处理
  } finally {
    loading.value = false
  }
}

function handleReset() {
  formRef.value?.resetFields()
  Object.assign(form, {
    realName: '', role: null, phone: '',
    pmId: null, plId: null,
    baseSalary: null, positionSalary: null,
    housingSubsidy: null, carSubsidy: null, mealSubsidy: null, otherSubsidy: null
  })
  Object.assign(form.socialInsuranceConfig, {
    socialInsuranceBase: null,
    housingFundBase: null,
    pensionPersonal: null,
    pensionCompany: null,
    medicalPersonal: null,
    medicalCompany: null,
    unemploymentPersonal: null,
    unemploymentCompany: null,
    injuryCompany: null,
    maternityCompany: null,
    housingFundPersonal: null,
    housingFundCompany: null
  })
  plList.value = []
}
</script>

<style scoped>
.employee-add-page {
  max-width: 1100px;
  margin: 0 auto;
}

.page-title {
  margin: 0 0 24px;
  font-size: 22px;
  font-weight: 600;
}

.section-title {
  margin: 24px 0 16px;
  padding-left: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  border-left: 3px solid #6366f1;
}

.sub-title {
  margin: 16px 0 8px;
  padding-left: 10px;
  font-size: 13px;
  font-weight: 500;
  color: #475569;
  border-left: 2px solid #94a3b8;
}

.hint {
  margin: 0 0 16px;
  font-size: 12px;
  color: #94a3b8;
}
.hint code {
  padding: 1px 6px;
  background: #f1f5f9;
  border-radius: 4px;
  color: #6366f1;
  font-family: Consolas, Monaco, monospace;
}

.emp-form :deep(.el-input-number) {
  width: 100%;
}

/* 移动端表单适配 */
@media (max-width: 768px) {
  .page-title {
    font-size: 18px;
    margin-bottom: 16px;
  }
  .emp-form {
    --el-form-label-width: 90px;
  }
  .section-title {
    margin: 16px 0 12px;
    font-size: 14px;
  }
}
</style>
