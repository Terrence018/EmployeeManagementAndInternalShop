<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { Timer, Location, Goods } from '@element-plus/icons-vue'

// 定義數據
const loading = ref(false)
const orderList = ref([])

// 狀態對應表 (1:待發貨, 2:已發貨, 3:已完成)
const statusMap = {
  1: { text: '⏳ 待發貨', type: 'warning' },
  2: { text: '🚚 已發貨', type: 'primary' },
  3: { text: '✅ 已完成', type: 'success' },
  4: { text: '❌ 已取消', type: 'info' }
}

// 獲取訂單列表
const getMyOrders = async () => {
  loading.value = true
  try {
    const uid = localStorage.getItem('uid')
    // 呼叫後端: OrdersController.listMyOrders
    const res = await request.get(`/orders/my/${uid}`)
    
    if (res.code === 1) {
      orderList.value = res.data
    }
  } catch (error) {
    console.error('獲取訂單失敗', error)
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ').substring(0, 19)
}

// 初始化
onMounted(() => {
  getMyOrders()
})
</script>

<template>
  <div class="order-container">
    <div class="page-header">
      <h2>📦 我的兌換紀錄</h2>
      <span class="subtitle">查看您的歷史訂單與發貨狀態</span>
    </div>

    <el-card shadow="never">
      <el-table :data="orderList" style="width: 100%" v-loading="loading" stripe>
        
        <el-table-column prop="id" label="訂單號" width="80" align="center" />

        <el-table-column label="兌換商品" min-width="140">
          <template #default="scope">
            <div v-if="scope.row.items && scope.row.items.length > 0">
              <div v-for="(item, index) in scope.row.items" :key="index" class="order-item-row">
                <el-avatar 
                  shape="square" 
                  :size="40" 
                  :src="item.image" 
                  style="margin-right: 10px; flex-shrink: 0;"
                >
                  <img src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png"/>
                </el-avatar>
                
                <div>
                  <div style="font-weight: bold; font-size: 14px;">{{ item.productName }}</div>
                  <div style="font-size: 12px; color: #888;">數量: x{{ item.quantity }}</div>
                </div>
              </div>
            </div>
            <span v-else style="color: #999;">無商品資料</span>
          </template>
        </el-table-column>

        <el-table-column label="消耗總點數" width="120" align="center">
          <template #default="scope">
            <span style="color: #E6A23C; font-weight: bold;">💎 {{ scope.row.totalPoints }}</span>
          </template>
        </el-table-column>

        <el-table-column label="兌換時間" width="180" align="center">
          <template #default="scope">
            <div style="display: flex; align-items: center; justify-content: center;">
              <el-icon><Timer /></el-icon>
              <span style="margin-left: 5px; font-size: 13px;">{{ formatDate(scope.row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="收貨資訊" min-width="150">
          <template #default="scope">
            <div v-if="scope.row.deliveryMethod === 1">
              <el-tag type="info" size="small" effect="plain">🏢 公司自取</el-tag>
            </div>
            <div v-else>
              <el-popover placement="top" :width="200" trigger="hover">
                <template #reference>
                  <el-tag type="warning" size="small" effect="plain">🚚 寄送到家</el-tag>
                </template>
                <div style="font-size: 12px;">
                  <p><strong>地址：</strong></p>
                  <p>{{ scope.row.address }}</p>
                </div>
              </el-popover>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="狀態" width="100" align="center">
          <template #default="scope">
            <el-tag :type="statusMap[scope.row.status]?.type || 'info'" effect="dark">
              {{ statusMap[scope.row.status]?.text || '未知' }}
            </el-tag>
          </template>
        </el-table-column>

      </el-table>
      
      <el-empty v-if="!loading && orderList.length === 0" description="您還沒有兌換任何商品喔！快去商城逛逛吧" />
    </el-card>
  </div>
</template>

<style scoped>
.order-container { padding: 20px; }
.page-header { margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 24px; color: #303133; }
.subtitle { font-size: 14px; color: #909399; margin-top: 5px; display: block; }
.order-item-row { display: flex; align-items: center; margin-bottom: 8px; padding-bottom: 8px; border-bottom: 1px dashed #eee; }
.order-item-row:last-child { margin-bottom: 0; padding-bottom: 0; border-bottom: none; }
</style>