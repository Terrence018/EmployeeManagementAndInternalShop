<script setup>
import { ref, onMounted, onUnmounted } from 'vue' // ✅ 引入 onUnmounted
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  HomeFilled, 
  Tools, 
  HelpFilled, 
  Avatar, 
  Histogram, 
  InfoFilled, 
  Document, 
  Shop, 
  Goods, 
  List, 
  SwitchButton, 
  User, 
  Timer,
  ShoppingBag,
  CreditCard,
  TrendCharts,
  Wallet 
} from '@element-plus/icons-vue' 

const router = useRouter()
const userName = ref('')
const userRole = ref(null)
const userPoints = ref(0)

// ✅ 新增：更新點數的方法
const updatePointsFromStorage = () => {
  const points = localStorage.getItem('points')
  if (points !== null) {
    userPoints.value = parseInt(points)
  }
}

onMounted(() => {
  const name = localStorage.getItem('name')
  const role = localStorage.getItem('role')

  if (name) userName.value = name
  if (role) userRole.value = parseInt(role)
  
  // ✅ 初始載入點數
  updatePointsFromStorage()

  // ✅ 監聽自定義事件 "update-points"
  // 當購物車結帳或單品兌換成功後，會觸發此事件
  window.addEventListener('update-points', updatePointsFromStorage)
})

// ✅ 新增：組件銷毀時移除監聽，防止記憶體洩漏
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
    <el-container>
      <el-header class="header">
        <span class="title">員工管理與商城系統</span>
        <span class="right_tool">
          <span v-if="userRole === 2" class="points-area">
            💰 剩餘點數: {{ userPoints }} &nbsp;|&nbsp;
          </span>
          <span style="font-size: 14px; color: #eee; margin-right: 15px;">
            歡迎, {{ userName }} {{ userRole === 1 ? '(管理員)' : '(員工)' }}
          </span>
          <a href="javascript:void(0)" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon> 登出
          </a>
        </span>
      </el-header>
      
      <el-container>
        <el-aside width="220px" class="aside">
          <el-menu router :default-active="$route.path">
            
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
              <el-menu-item index="/points/overview">
                <el-icon><User /></el-icon>員工點數查看
              </el-menu-item>
              <el-menu-item index="/points/manage">
                <el-icon><CreditCard /></el-icon>點數發放與紀錄
              </el-menu-item>
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
            
            <el-menu-item index="/profile" v-if="userRole === 2">
              <el-icon><User /></el-icon>個人中心
            </el-menu-item>

          </el-menu>
        </el-aside>
        
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.header { background-image: linear-gradient(to right, #243949 0%, #517fa4 100%); }
.title { color: white; font-size: 30px; font-family: "PingFang SC", "Microsoft YaHei", "Heiti SC", sans-serif; line-height: 60px; font-weight: bolder; }
.right_tool{ float: right; line-height: 60px; color: white; }
.points-area { color: #FFD700; font-weight: bold; font-size: 18px; font-family: Arial, sans-serif; }
a { color: white; text-decoration: none; cursor: pointer; }
a:hover { color: #FFD700; }
.aside { width: 220px; border-right: 1px solid #ccc; height: 90vh; }
</style>