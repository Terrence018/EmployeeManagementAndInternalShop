import axios from 'axios'

//创建axios实例对象
const request = axios.create({
  baseURL: '/api',
  timeout: 600000
})

//請求攔截器，作用：在請求發出去之前，自動把 Token 塞進 Header
request.interceptors.request.use(
  (config) => {
    // 從 localStorage 拿出 token
    const token = localStorage.getItem('token')
    
    // 如果有 token，就放入請求頭中
    if (token) {
      // 注意：後端接收的 Header 名稱通常是 'token' 或 'Authorization'
      // 根據你的 Tlias 課程習慣，通常是 'token'
      config.headers.token = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

//axios的响应 response 拦截器
request.interceptors.response.use(
  (response) => { //成功回调
    return response.data
  },
  (error) => { //失败回调
    return Promise.reject(error)
  }
)

export default request