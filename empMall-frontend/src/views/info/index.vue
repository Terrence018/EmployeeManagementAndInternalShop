<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Message, Iphone, Lock, Key } from '@element-plus/icons-vue' 
import request from '@/utils/request'

// --- 數據定義 ---
const userInfo = ref({
  id: '',
  username: '',
  name: '',
  deptName: '',
  job: '',
  gender: 1,
  salary: 0,
  phone: '',
  email: '',    
  password: ''  
})

// 驗證彈窗控制
const verifyDialogVisible = ref(false)
const verifyCode = ref('')
const countdown = ref(0)
let timer = null

// 方法 

// 1. 獲取個人資訊
const loadInfo = async () => {
  const uid = localStorage.getItem('uid')
  // 假設後端接口為 /emps/{id}
  const res = await request.get(`/emps/${uid}`)
  if (res.code === 1) {
    userInfo.value = res.data
    // 密碼欄位留空，避免誤操作
    userInfo.value.password = ''
    
    // 處理部門顯示 (若後端沒有回傳 deptName，這裡給個預設值)
    if (!userInfo.value.deptName) userInfo.value.deptName = '一般部門' 
  }
}

// 2. 點擊「確認修改」 -> 開啟驗證視窗
const handlePreSubmit = () => {
  if (!userInfo.value.email) {
    ElMessage.warning('請填寫電子信箱才能進行驗證')
    return
  }
  if (!userInfo.value.phone) {
    ElMessage.warning('手機號碼為必填')
    return
  }
  
  // 清空上次的輸入
  verifyCode.value = ''
  verifyDialogVisible.value = true
}

// 3. 發送驗證碼
const sendCode = async () => {
  const targetEmail = userInfo.value.email
  
  try {
    const res = await request.post(`/email/send?email=${targetEmail}`)
    if (res.code === 1) {
      ElMessage.success(`驗證碼已發送至 ${targetEmail}`)
      
      // 倒數計時 60秒
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

// 4. 最終提交 (驗證並保存)
const handleFinalSubmit = async () => {
  if (!verifyCode.value) return ElMessage.warning('請輸入驗證碼')

  try {
    const submitData = {
      id: userInfo.value.id,
      phone: userInfo.value.phone,
      email: userInfo.value.email,
      // 如果密碼為空，則傳 undefined 或不傳
      password: userInfo.value.password || undefined,
      code: verifyCode.value // ✅ 帶上驗證碼
    }

    // 呼叫後端更新接口
    const res = await request.put('/emps/updatePersonal', submitData)
    
    if (res.code === 1) {
      ElMessage.success('資料修改成功！')
      verifyDialogVisible.value = false
      loadInfo() // 重新整理資料
    } else {
      ElMessage.error(res.msg || '修改失敗，驗證碼可能錯誤')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('系統忙碌中')
  }
}

onMounted(() => {
  loadInfo()
})
</script>

<template>
  <div class="personal-container">
    <h2 class="page-title"><el-icon><User /></el-icon> 個人資料修改</h2>

    <div class="content-wrapper">
      <el-alert
        title="注意：僅可修改手機號碼、信箱與密碼，如需變更其他資訊請聯繫管理員。"
        type="info"
        show-icon
        :closable="false"
        style="margin-bottom: 20px;"
      />

      <el-form label-width="100px" class="info-form">
        
        <el-form-item label="員工帳號">
          <el-input v-model="userInfo.username" disabled />
        </el-form-item>

        <el-form-item label="真實姓名">
          <el-input v-model="userInfo.name" disabled />
        </el-form-item>

        <el-form-item label="所屬部門">
          <el-input v-model="userInfo.deptName" disabled />
        </el-form-item>

        <el-form-item label="職位/性別">
          <div style="display: flex; align-items: center; width: 100%;">
            <el-tag type="success" style="margin-right: 20px;">{{ userInfo.job || '員工' }}</el-tag>
            <span style="margin-right: 10px;">性別</span>
            <el-radio-group v-model="userInfo.gender" disabled>
              <el-radio :label="1">男</el-radio>
              <el-radio :label="2">女</el-radio>
            </el-radio-group>
          </div>
        </el-form-item>

        <el-form-item label="目前薪資">
          <el-input v-model="userInfo.salary" disabled>
             <template #append>元</template>
          </el-input>
        </el-form-item>

        <el-divider content-position="center">可修改區域</el-divider>

        <el-form-item label="電子信箱" required>
          <el-input v-model="userInfo.email" placeholder="請輸入常用信箱 (用於驗證)">
            <template #prefix><el-icon><Message /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item label="手機號碼" required>
          <el-input v-model="userInfo.phone" placeholder="請輸入手機號碼">
            <template #prefix><el-icon><Iphone /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item label="修改密碼">
          <el-input 
            v-model="userInfo.password" 
            type="password" 
            show-password 
            placeholder="若不修改請留空"
          >
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="submit-btn" @click="handlePreSubmit">
            確認修改
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-dialog v-model="verifyDialogVisible" title="🔐 安全驗證" width="420px" center>
      <div style="text-align: center; margin-bottom: 20px;">
        <p style="color: #606266;">為了保障您的帳號安全，我們需要驗證您的身分。</p>
        <p style="font-size: 13px; color: #909399;">驗證碼將發送至：{{ userInfo.email }}</p>
      </div>

      <el-form-item>
        <div style="display: flex; width: 100%; gap: 10px;">
          <el-input 
            v-model="verifyCode" 
            placeholder="請輸入6位數驗證碼" 
            :prefix-icon="Key"
          />
          <el-button 
            type="primary" 
            :disabled="countdown > 0" 
            @click="sendCode"
            style="width: 140px;"
          >
            {{ countdown > 0 ? `${countdown}s 重發` : '發送驗證碼' }}
          </el-button>
        </div>
      </el-form-item>

      <template #footer>
        <el-button @click="verifyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleFinalSubmit">驗證並保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped>
.personal-container {
  padding: 20px;
  background-color: #fff; /* 或 #f5f7fa */
}
.page-title {
  font-size: 20px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.content-wrapper {
  max-width: 800px;
  margin: 0 auto;
  border: 1px solid #ebeef5;
  padding: 30px;
  border-radius: 8px;
  background-color: #fff;
}
.info-form {
  margin-top: 20px;
}
.submit-btn {
  width: 100%;
  height: 40px;
  font-size: 16px;
  margin-top: 10px;
  background-color: #409eff; /* 配合您的截圖顏色 */
}
</style>