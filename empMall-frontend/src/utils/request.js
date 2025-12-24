import axios from 'axios'

//創建axios實例物件
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
      config.headers.token = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

//axios的響應 response攔截器
request.interceptors.response.use(
  (response) => { //成功回調
    return response.data
  },
  (error) => { //失败回調
    return Promise.reject(error)
  }
)

export default request