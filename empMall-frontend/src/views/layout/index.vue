<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue' 
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request' 
import { 
  HomeFilled, Tools, HelpFilled, Avatar, Histogram, InfoFilled, 
  Document, Shop, Goods, List, SwitchButton, User, Timer,
  ShoppingBag, CreditCard, TrendCharts, Wallet,
  Platform, Present 
} from '@element-plus/icons-vue' 

import ChatWidget from '@/components/ChatWidget.vue'

const router = useRouter()
const userName = ref('')
const userRole = ref(2) 
const userPoints = ref(0)
const currentUid = ref('')

// 主題配色設定
const currentTheme = computed(() => {
  if (userRole.value === 1) {
    return {
      bgStyle: { backgroundImage: 'linear-gradient(to right, #243949 0%, #517fa4 100%)' },
      mainTextColor: '#FFFFFF', 
      pointsColor: '#FFD700',
      hoverColor: '#FFD700',
      textShadow: '1px 1px 2px rgba(0,0,0,0.3)'
    }
  } else {
    return {
      bgStyle: { backgroundImage: 'linear-gradient(to right, #FFECD2 0%, #FCB69F 100%)' },
      mainTextColor: '#543310',
      pointsColor: '#003366',
      hoverColor: '#003366',
      textShadow: '0px 1px 1px rgba(255,255,255,0.6)'
    }
  }
})

const systemTitle = computed(() => {
  if (userRole.value === 1) return 'EMP MALL 企業管理後台' 
  if (userRole.value === 2) return 'EMP MALL 員工福利商城' 
  return 'EMP MALL 系統'
})

const updatePointsFromStorage = () => {
  const points = localStorage.getItem('points')
  if (points !== null) {
    userPoints.value = parseInt(points)
  }
}

const refreshUserInfo = async () => {
  const uid = localStorage.getItem('uid')
  if (!uid) return 

  try {
    const res = await request.get(`/emps/${uid}`)
    if (res.code === 1) {
      const latestEmp = res.data
      if (latestEmp.points != null) {
        userPoints.value = latestEmp.points
        localStorage.setItem('points', latestEmp.points)
      }
      if (latestEmp.name) {
        userName.value = latestEmp.name
        localStorage.setItem('name', latestEmp.name)
      }
    }
  } catch (error) {
    console.error("同步使用者資訊失敗", error)
  }
}

onMounted(() => {
  const name = localStorage.getItem('name')
  const role = localStorage.getItem('role')
  const uid = localStorage.getItem('uid')
  if (uid) currentUid.value = uid

  console.log('🔍 目前登入資訊 - Name:', name, 'Role:', role) 

  if (name) userName.value = name
  
  if (role) {
    const roleNum = parseInt(role, 10)
    if (!isNaN(roleNum)) {
      userRole.value = roleNum
    } else {
      userRole.value = 2
    }
  } else {
    userRole.value = 2
  }
  
  updatePointsFromStorage()
  refreshUserInfo()
  window.addEventListener('update-points', updatePointsFromStorage)
})

onUnmounted(() => {
  window.removeEventListener('update-points', updatePointsFromStorage)
})

const handleLogout = () => {
  ElMessageBox.confirm('確定要登出系統嗎?', '提示', {
    confirmButtonText: '確定', type: 'warning'
  }).then(() => {
    localStorage.clear()
    router.push('/login')
    ElMessage.success('已登出')
  }).catch(() => {})
}
</script>

<template>
  <div class="common-layout">
    <el-container class="full-height-container">
      
      <el-header class="header-base" :style="currentTheme.bgStyle">
        <span class="title" :style="{ color: currentTheme.mainTextColor, textShadow: currentTheme.textShadow }">
          <el-icon style="margin-right: 10px; vertical-align: middle;" v-if="userRole === 1"><Platform /></el-icon>
          <el-icon style="margin-right: 10px; vertical-align: middle;" v-else-if="userRole === 2"><Present /></el-icon>
          {{ systemTitle }}
        </span>
        
        <span class="right_tool" :style="{ color: currentTheme.mainTextColor }">
          <span v-if="userRole === 2" class="points-area" :style="{ color: currentTheme.pointsColor }">
             剩餘點數: {{ userPoints }} &nbsp;|&nbsp;
          </span>
          <span style="font-size: 14px; margin-right: 15px; opacity: 0.95;">
            歡迎, {{ userName }} {{ userRole === 1 ? '(管理員)' : '(員工)' }}
          </span>
          <a href="javascript:void(0)" @click="handleLogout" class="logout-link" :style="{ '--hover-color': currentTheme.hoverColor }">
            <el-icon><SwitchButton /></el-icon> 登出
          </a>
        </span>
      </el-header>
      
      <el-container class="body-container">
        <el-aside width="220px" class="aside">
          <el-menu router :default-active="$route.path" class="el-menu-vertical">
            
            <el-menu-item index="/home"><el-icon><HomeFilled /></el-icon> 首頁</el-menu-item>
            
            <el-sub-menu index="/system" v-if="userRole === 1">
              <template #title><el-icon><Tools /></el-icon>部門及員工管理</template>
              <el-menu-item index="/dept"><el-icon><HelpFilled /></el-icon>部門管理</el-menu-item>
              <el-menu-item index="/emp"><el-icon><Avatar /></el-icon>員工管理</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="/mall-admin" v-if="userRole === 1">
              <template #title><el-icon><ShoppingBag /></el-icon>商城營運管理</template>
              <el-menu-item index="/mall/manage"><el-icon><Goods /></el-icon>商品上架與管理</el-menu-item>
              <el-menu-item index="/order/manage"><el-icon><List /></el-icon>訂單發貨管理</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="/points" v-if="userRole === 1">
              <template #title><el-icon><Wallet /></el-icon>點數管理</template>
              <el-menu-item index="/points/overview"><el-icon><User /></el-icon>員工點數查看</el-menu-item>
              <el-menu-item index="/points/manage"><el-icon><CreditCard /></el-icon>點數發放與紀錄</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="/report" v-if="userRole === 1">
              <template #title><el-icon><Histogram /></el-icon>資訊統計管理</template>
              <el-menu-item index="/report/emp"><el-icon><InfoFilled /></el-icon>員工資訊統計</el-menu-item>
              <el-menu-item index="/log"><el-icon><Document /></el-icon>日誌記錄資訊統計</el-menu-item>
              <el-menu-item index="/stats/sales"><el-icon><TrendCharts /></el-icon>商品銷售總紀錄</el-menu-item>
            </el-sub-menu>

            <el-menu-item index="/mall" v-if="userRole === 2"><el-icon><Shop /></el-icon>內部積分商城</el-menu-item>
            <el-menu-item index="/order/my" v-if="userRole === 2"><el-icon><Goods /></el-icon>我的兌換商品</el-menu-item>
            <el-menu-item index="/points/history" v-if="userRole === 2"><el-icon><Timer /></el-icon>點數兌換紀錄</el-menu-item>
            <el-menu-item index="/profile" v-if="userRole === 2"><el-icon><User /></el-icon>個人中心</el-menu-item>

          </el-menu>
        </el-aside>
        
        <el-main class="main-scroll-container">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>

    <ChatWidget 
      v-if="currentUid && userRole" 
      :userRole="userRole" 
      :userName="userName" 
      :uid="currentUid"
    />
  </div>
</template>

<style scoped>
.common-layout {
  height: 100vh;
  width: 100vw;
  overflow: hidden; /* 鎖定最外層，防止雙重捲軸 */
}

.full-height-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.header-base {
  transition: all 0.5s ease;
  padding: 0 20px;
  height: 60px;
  flex-shrink: 0; /* Header 高度固定，不壓縮 */
}

/* 身體部分：佔滿剩餘高度 */
.body-container {
  flex: 1; 
  overflow: hidden; /* 確保內容不會撐開整個頁面，而是限制在這裡 */
  display: flex;
}

.aside { 
  width: 220px; 
  border-right: 1px solid #ccc; 
  height: 100%; 
  background-color: #fff; 
  overflow-y: auto; /* 側邊欄自己可以捲動 */
}

/* 主內容區域樣式 */
.main-scroll-container {
  flex: 1; /* 佔滿右側剩餘空間 */
  height: 100%; /* 確保高度被限制 */
  overflow-y: auto; /* 開啟垂直捲動 */
  padding: 20px;
  background-color: #fff; /* 視情況調整背景色 */
}

.el-menu-vertical { border-right: none; }

.title { 
  font-size: 26px;
  font-family: "PingFang SC", "Microsoft YaHei", "Heiti SC", sans-serif; 
  line-height: 60px; 
  font-weight: bolder; 
  transition: color 0.3s ease;
}

.right_tool{ 
  float: right; 
  line-height: 60px; 
  transition: color 0.3s ease; 
}

.points-area { 
  font-weight: bold; 
  font-size: 18px; 
  font-family: Arial, sans-serif; 
  text-shadow: 0px 0px 1px rgba(255,255,255,0.8); 
}

.logout-link {
  color: inherit;
  text-decoration: none;
  cursor: pointer;
  transition: color 0.2s;
}

.logout-link:hover {
  color: var(--hover-color) !important; 
}
</style>