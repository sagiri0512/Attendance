import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => response,
  error => {
    const status = error.response?.status;
    const msg = error.response?.data?.message || '请求失败';

    // 非登录接口的 403 → 清 token 跳登录
    if (status === 403) {
      localStorage.removeItem('token');
      ElMessage.error('登录已失效，请重新登录');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
      return Promise.reject(error);
    }

    // 全局：显示后端 msg
    ElMessage.error(msg);
    return Promise.reject(error);
  }
)

export default request
