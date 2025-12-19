import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus' // 引入消息提示（可選）

// --- 引入 View 組件 ---
import LayoutView from '@/views/layout/index.vue'
import LoginView from '@/views/login/index.vue'
import IndexView from '@/views/index/index.vue'

// 1. 部門與員工管理
import DeptView from '@/views/dept/index.vue'
import EmpView from '@/views/emp/index.vue'

// 2. 商城營運管理
import MallManageView from '@/views/mall/manage.vue'
import OrderManageView from '@/views/order/manage.vue'

// 3. 點數管理 (管理員)
import PointsManageView from '@/views/points/manage.vue'
import PointsOverviewView from '@/views/points/overview.vue'

// 4. 資訊統計管理
import EmpReportView from '@/views/report/emp/index.vue'
import LogView from '@/views/log/index.vue'
import SalesView from '@/views/stats/sales.vue'

// 5. 員工專區
import MallView from '@/views/mall/index.vue'
import OrderView from '@/views/order/my.vue'
import PointsHistoryView from '@/views/points/history.vue'
import InfoView from '@/views/info/index.vue'
import CartView from '@/views/mall/CartView.vue'



const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {  
      path: '/',
      name: 'layout',
      component: LayoutView,
      redirect: '/home',
      children: [
        // --- 首頁 (大家都能看) ---
        { path: 'home', name: 'home', component: IndexView },

        // --- 🔒 1. 部門及員工管理 (管理員專用) ---
        { path: 'dept', name: 'dept', component: DeptView, meta: { requiresAdmin: true } },
        { path: 'emp', name: 'emp', component: EmpView, meta: { requiresAdmin: true } },

        // --- 🔒 2. 商城營運管理 (管理員專用) ---
        { path: 'mall/manage', name: 'MallManage', component: MallManageView, meta: { requiresAdmin: true } },
        { path: 'order/manage', name: 'OrderManage', component: OrderManageView, meta: { requiresAdmin: true } },

        // --- 🔒 3. 點數管理 (管理員專用) ---
        { path: 'points/overview', name: 'PointsOverview', component: PointsOverviewView, meta: { requiresAdmin: true } },
        { path: 'points/manage', name: 'PointsManage', component: PointsManageView, meta: { requiresAdmin: true } },

        // --- 🔒 4. 資訊統計管理 (管理員專用) ---
        { path: 'report/emp', name: 'EmpReport', component: EmpReportView, meta: { requiresAdmin: true } },
        { path: 'log', name: 'log', component: LogView, meta: { requiresAdmin: true } },
        { path: 'stats/sales', name: 'Sales', component: SalesView, meta: { requiresAdmin: true } },
        
        // --- 🔓 5. 員工專用功能 (一般員工及管理員都能看) ---
        { path: 'mall', name: 'mall', component: MallView },
        { path: 'order/my', name: 'MyOrders', component: OrderView },
        { path: 'points/history', name: 'PointsHistory', component: PointsHistoryView },
        { path: 'profile', name: 'UserProfile', component: InfoView },
        { path: 'mall/cart', name: 'Cart', component: CartView }
      ]
    },
    { path: '/login', name: 'login', component: LoginView },
  ]
})

/**
 * 負責攔截「普通員工」非法訪問「管理頁面」
 */
router.beforeEach((to, from, next) => {
  // 1. 從本地存儲獲取角色信息 (1=管理員, 2=員工)
  const userRole = localStorage.getItem('role')
  const token = localStorage.getItem('token')

  // 2. 如果去的地方不是登錄頁，且沒有 Token，強制回登錄
  if (to.path !== '/login' && !token) {
    return next('/login')
  }

  // 3. 核心權限校驗：判斷目標路由是否需要管理員權限
  if (to.meta.requiresAdmin) {
    // 如果角色不是 1 (管理員)，則攔截並導回首頁
    if (userRole !== '1') {
      ElMessage.error('權限不足，無法訪問該頁面')
      return next('/home') // 或者是你指定的頁面
    }
  }

  // 4. 校驗通過，放行
  next()
})

export default router