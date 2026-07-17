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

    // 非登录接口的 401 → 清 token 跳登录
    if (status === 401) {
      localStorage.removeItem('token');
      ElMessage.error(msg);
      if (window.location.pathname !== '/login') {
        setTimeout(() => {
          window.location.href = '/login';
        }, 2000);
      }
      return Promise.reject(error);
    }

    // 全局：显示后端 msg
    ElMessage.error(msg);
    return Promise.reject(error);
  }
)

export default request
