import request from '@/utils/request'

/**
 * 新增员工
 * 对应后端 POST /api/employee/add
 * 字段对应 EmployeeSaveDTO（不含 id/empNo/password/timestamps）
 * @param {Object} data 员工信息
 */
export function addEmployee(data) {
  return request.post('/employee/add', data)
}

/**
 * 查询所有 PM（新增员工下拉框用）
 * 对应后端 GET /api/employee/pm-list
 * @returns {Promise<{data: {data: Array<{id:number, empNo:string, realName:string}>}}>}
 */
export function getPmList() {
  return request.get('/employee/pm-list')
}

/**
 * 根据 pmId 查询该 PM 手下所有 PL（下拉框联动用）
 * 对应后端 GET /api/employee/pl-list?pmId=
 * @param {number} pmId
 */
export function getPlList(pmId) {
  return request.get('/employee/pl-list', { params: { pmId } })
}
