<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '@/utils/request'
import { Search, Van, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 數據定義
const loading = ref(false)
const orderList = ref([])
const filterStatus = ref('') // 篩選狀態：空字串為全部
const searchQuery = ref('')  // 搜尋員工或商品

// 狀態映射
const statusMap = {
  1: { text: '⏳ 待發貨', type: 'warning' },
  2: { text: '🚚 已發貨', type: 'primary' },
  3: { text: '✅ 已完成', type: 'success' },
  4: { text: '❌ 已取消', type: 'info' }
}

const statusOptions = [
  { label: '全部狀態', value: '' },
  { label: '待發貨', value: 1 },
  { label: '已發貨', value: 2 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 4 }
]

// 1. 獲取所有訂單
const getAllOrders = async () => {
  loading.value = true
  try {
    const res = await request.get('/orders/all')
    if (res.code === 1) {
      orderList.value = res.data
    }
  } finally {
    loading.value = false
  }
}

// 2. 前端篩選 (搜尋 + 狀態過濾)
const filteredOrders = computed(() => {
  return orderList.value.filter(item => {
    // 狀態篩選
    const matchStatus = filterStatus.value === '' || item.status === filterStatus.value
    // 關鍵字搜尋 (搜員工名 或 商品名)
    const keyword = searchQuery.value.toLowerCase()
    
    // 搜尋商品名稱邏輯：只要任一商品名稱包含關鍵字即可
    const hasProduct = item.items && item.items.some(i => i.productName.toLowerCase().includes(keyword))
    
    const matchSearch = !keyword || 
                        (item.empName && item.empName.toLowerCase().includes(keyword)) || 
                        hasProduct
    return matchStatus && matchSearch
  })
})

// 修改後的更新訂單狀態 (區分普通更新與取消)
const handleUpdateStatus = (row, newStatus) => {
  let actionText = ''
  let confirmText = ''
  
  if (newStatus === 2) {
    actionText = '發貨'
    confirmText = '確定要將此訂單標記為「已發貨」嗎？'
  } else if (newStatus === 3) {
    actionText = '完成'
    confirmText = '確定訂單已完成嗎？'
  } else if (newStatus === 4) {
    actionText = '取消'
    // 提示語加強，讓管理員知道會退款
    confirmText = '確定要「取消」此訂單嗎？系統將自動退還點數並回補庫存。'
  }

  ElMessageBox.confirm(confirmText, '提示', {
    confirmButtonText: '確定',
    cancelButtonText: '取消',
    type: newStatus === 4 ? 'warning' : 'info' // 取消操作用黃色警告圖示
  }).then(async () => {
    let res;

    // 關鍵分流邏輯：
    if (newStatus === 4) {
      // 如果是取消，呼叫專門的 Cancel 接口 (包含退款邏輯)
      res = await request.put(`/orders/cancel/${row.id}`)
    } else {
      // 如果是發貨或完成，呼叫原本的狀態更新接口
      res = await request.put(`/orders/${row.id}/${newStatus}`)
    }

    if (res.code === 1) {
      ElMessage.success(`${actionText}成功`)
      // 為了確保庫存和狀態都顯示正確，建議重新加載整個列表
      // row.status = newStatus // 這是簡單更新，但為了保險起見↓
      getAllOrders() 
    } else {
      ElMessage.error(res.msg || '操作失敗')
    }
  })
}

onMounted(() => {
  getAllOrders()
})
</script>

<template>
  <div class="order-manage-container">
    <div class="toolbar">
      <el-input 
        v-model="searchQuery" 
        placeholder=" 搜尋員工或商品..." 
        style="width: 250px;" 
        clearable
      />
      
      <el-select v-model="filterStatus" placeholder="訂單狀態" style="width: 150px; margin-left: 15px;">
        <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
      </el-select>

      <el-button type="primary" @click="getAllOrders" style="margin-left: 15px;">刷新列表</el-button>
    </div>

    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="filteredOrders" v-loading="loading" stripe style="width: 100%">
        
        <el-table-column prop="id" label="訂單號" width="80" align="center" />
        
        <el-table-column label="下單員工" width="130" align="center">
          <template #default="scope">
            <span style="font-weight: bold;">{{ scope.row.empName }}</span>
          </template>
        </el-table-column>

        <el-table-column label="兌換商品" min-width="150" align="center">
          <template #default="scope">
            <div v-if="scope.row.items && scope.row.items.length > 0">
              <div v-for="(item, index) in scope.row.items" :key="index" style="margin-bottom: 5px;">
                <el-tag 
                  size="small" 
                  type="info" 
                  style="max-width: 150px; overflow: hidden; text-overflow: ellipsis; vertical-align: middle; display: inline-block;"
                >
                  {{ item.productName }}
                </el-tag>
                <span style="font-size: 12px; color: #666;"> x {{ item.quantity }}</span>
              </div>
            </div>
            <span v-else style="color: #999;">無商品資料</span>
          </template>
        </el-table-column>

        <el-table-column prop="totalPoints" label="消耗點數" width="80" sortable align="center">
          <template #default="scope">
            <span style="color: #E6A23C; font-weight: bold;">💎 {{ scope.row.totalPoints }}</span>
          </template>
        </el-table-column>

        <el-table-column label="取貨方式" width="120" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.deliveryMethod === 1" type="info" size="small" effect="plain">公司自取</el-tag>
            <el-popover v-else placement="top" :width="200" trigger="hover">
              <template #reference>
                <el-tag type="warning" size="small" effect="plain">🚚 宅配</el-tag>
              </template>
              <div style="font-size: 12px;">
                <p><strong>地址：</strong></p>
                <p>{{ scope.row.address || '無地址資訊' }}</p>
              </div>
            </el-popover>
          </template>
        </el-table-column>

        <el-table-column label="下單時間" width="130" align="center">
          <template #default="scope">
            <span style="font-size: 12px;">{{ scope.row.createTime?.replace('T', ' ') }}</span>
          </template>
        </el-table-column>

        <el-table-column label="狀態" width="100" align="center">
          <template #default="scope">
            <el-tag :type="statusMap[scope.row.status]?.type">{{ statusMap[scope.row.status]?.text }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 1" 
              type="primary" 
              size="small" 
              :icon="Van" 
              @click="handleUpdateStatus(scope.row, 2)"
            >
              發貨
            </el-button>

            <el-button 
              v-if="scope.row.status === 2" 
              type="success" 
              size="small" 
              :icon="CircleCheck" 
              @click="handleUpdateStatus(scope.row, 3)"
            >
              完成
            </el-button>

            <el-button 
              v-if="scope.row.status === 1" 
              type="danger" 
              size="small" 
              :icon="CircleClose" 
              @click="handleUpdateStatus(scope.row, 4)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>

      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.order-manage-container {
  padding: 20px;
}
.toolbar {
  background: white;
  padding: 15px;
  border-radius: 8px;
  display: flex;
  align-items: center;
}
</style>