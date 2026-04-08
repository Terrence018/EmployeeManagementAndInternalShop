<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// 引入圖示與 request 工具
import { Message, Key, Lock } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { loginApi } from '@/api/login'

const router = useRouter()

// 定義登入表單數據
const loginForm = ref({ username: '', password: '' })

// 登入邏輯
const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('請輸入帳號和密碼')
    return
  }

  try {
    const res = await loginApi(loginForm.value)
    if (res.code === 1) {
      const data = res.data
      localStorage.setItem('token', data.token)
      localStorage.setItem('uid', data.id)
      localStorage.setItem('name', data.name)
      localStorage.setItem('role', data.role)
      localStorage.setItem('points', data.points)

      ElMessage.success('登入成功')
      router.push('/') 
    } else {
      ElMessage.error(res.msg || '登入失敗')
    }
  } catch (error) {
    console.error(error)
  }
}

const handleResetForm = () => {
  loginForm.value = { username: '', password: '' }
}

// 忘記密碼 / 重置密碼
const forgotVisible = ref(false)
const resetForm = ref({
  email: '',
  code: '',
  newPassword: ''
})
const countdown = ref(0)
let timer = null

// 1. 發送驗證碼
const sendCode = async () => {
  if (!resetForm.value.email) return ElMessage.warning('請輸入 Email')
  
  try {
    // 呼叫後端發信接口
    const res = await request.post(`/email/send?email=${resetForm.value.email}`)
    if (res.code === 1) {
      ElMessage.success('驗證碼已發送，請查收信箱')
      
      // 開始倒數 60 秒
      countdown.value = 60
      timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    } else {
      ElMessage.error(res.msg || '發送失敗')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('發送失敗，請稍後再試')
  }
}

// 2. 確認重置密碼
const handleResetPassword = async () => {
  if (!resetForm.value.email || !resetForm.value.code || !resetForm.value.newPassword) {
    return ElMessage.warning('請填寫完整資訊')
  }
  
  try {
    // 呼叫後端重置接口
    const res = await request.post('/emps/reset-password', resetForm.value)
    if (res.code === 1) {
      ElMessage.success('密碼重置成功！請使用新密碼登入')
      forgotVisible.value = false
      // 清空表單與倒數
      resetForm.value = { email: '', code: '', newPassword: '' }
      clearInterval(timer)
      countdown.value = 0
    } else {
      ElMessage.error(res.msg || '重置失敗')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('系統忙碌中')
  }
}
</script>

<template>
  <div id="container">
    <div class="login-form">
      <el-form label-width="80px">
        <p class="title">員工管理與內部商城系統</p>
        
        
        <el-form-item label="帳號" prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="請輸入帳號"
            @keyup.enter="handleLogin"
          ></el-input>
        </el-form-item>

        <el-form-item label="密碼" prop="password">
          <el-input 
            type="password" 
            v-model="loginForm.password" 
            placeholder="請輸入密碼"
            @keyup.enter="handleLogin"
            show-password
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button class="button" type="primary" @click="handleLogin">登 入</el-button>
          <el-button class="button" type="info" @click="handleResetForm">重 設</el-button>
        </el-form-item>

        <div style="text-align: right; margin-top: -10px;">
          <el-button type="primary" link @click="forgotVisible = true">忘記密碼？</el-button>
        </div>
      </el-form>
    </div>

    <el-dialog v-model="forgotVisible" title="重置密碼" width="420px" center append-to-body>
      <el-form :model="resetForm" label-position="top">
        
        <el-form-item label="註冊信箱">
          <el-input 
            v-model="resetForm.email" 
            placeholder="請輸入員工 Email" 
            :prefix-icon="Message" 
          />
        </el-form-item>

        <el-form-item label="驗證碼">
          <div style="display: flex; gap: 10px; width: 100%;">
            <el-input 
              v-model="resetForm.code" 
              placeholder="6位數驗證碼" 
              :prefix-icon="Key" 
            />
            <el-button 
              type="primary" 
              :disabled="countdown > 0" 
              @click="sendCode"
              style="width: 120px;"
            >
              {{ countdown > 0 ? `${countdown}s 重發` : '發送驗證碼' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="新密碼">
          <el-input 
            v-model="resetForm.newPassword" 
            type="password" 
            placeholder="請輸入新密碼" 
            show-password 
            :prefix-icon="Lock" 
          />
        </el-form-item>

      </el-form>

      <template #footer>
        <el-button @click="forgotVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPassword">確認重置</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped>
#container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('../../assets/bg1.jpg');
  background-repeat: no-repeat;
  background-size: cover;
}

.login-form {
  width: 400px;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.3);
  background-color: rgba(255, 255, 255, 0.95); /* 稍微調高不透明度，讓彈窗背景更清晰 */
}

.title {
  font-size: 28px;
  text-align: center;
  margin-bottom: 30px;
  font-weight: bold;
  color: #333;
}

.button {
  width: 45%;
}
</style>