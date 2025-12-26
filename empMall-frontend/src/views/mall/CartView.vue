<script setup>
import { ref, onMounted, computed } from 'vue'
// 引入MapLocation Icon
import { Delete, ShoppingCart, ArrowLeft, Location, MapLocation } from '@element-plus/icons-vue' 
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useRouter } from 'vue-router'
import MapSelector from '@/components/MapSelector.vue' // 引入地圖元件

const router = useRouter()
const cartList = ref([])
const loading = ref(false)
const selectedItems = ref([]) 

// 地圖相關
const mapVisible = ref(false)
const googleApiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY

// 結帳彈窗相關數據
const checkoutDialogVisible = ref(false)
const checkoutForm = ref({
  deliveryMethod: 1, // 1: 自取, 2: 寄送
  address: ''
})

// 1. 獲取購物車數據
const getCartList = async () => {
  loading.value = true
  const uid = localStorage.getItem('uid')
  try {
    const res = await request.get(`/cart/list?empId=${uid}`)
    if (res.code === 1) cartList.value = res.data
  } finally {
    loading.value = false
  }
}

// 2. 刪除單項
const removeProduct = async (id) => {
  ElMessageBox.confirm('確定要移出購物車嗎？', '提示', { type: 'warning' })
    .then(async () => {
      const res = await request.delete(`/cart/${id}`)
      if (res.code === 1) {
        ElMessage.success('已移出購物車')
        getCartList()
      }
    }).catch(() => {})
}

// 3. 修改數量
const handleQuantityChange = async (row) => {
  await request.put('/cart/updateQuantity', { id: row.id, quantity: row.quantity })
}

// 4. 計算選中總額
const totalPoints = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + (item.pointsNeeded * item.quantity), 0)
})

// 5. 處理表格勾選
const handleSelectionChange = (val) => {
  selectedItems.value = val
}

// 6. 點擊「確認結帳」
const handleCheckout = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('請先勾選要結帳的商品')
    return
  }
  
  const myPoints = parseInt(localStorage.getItem('points') || 0)
  if (myPoints < totalPoints.value) {
    ElMessage.error(`點數不足！(擁有: ${myPoints}, 需要: ${totalPoints.value})`)
    return
  }

  // 重置表單並開啟彈窗
  checkoutForm.value = { deliveryMethod: 1, address: '' }
  checkoutDialogVisible.value = true
}

// 處理地圖回傳地址
const handleAddressSelected = (address) => {
  checkoutForm.value.address = address
}

// 7. 確認兌換執行邏輯
const confirmBatchExchange = async () => {
  if (checkoutForm.value.deliveryMethod === 2 && !checkoutForm.value.address) {
    ElMessage.warning('請輸入收貨地址')
    return
  }

  try {
    const payload = {
      empId: localStorage.getItem('uid'),
      items: selectedItems.value.map(i => ({ productId: i.productId, quantity: i.quantity })),
      deliveryMethod: checkoutForm.value.deliveryMethod,
      address: checkoutForm.value.address
    }
    
    const res = await request.post('/orders/batchExchange', payload)
    
    if (res.code === 1) {
      ElMessage.success('訂單已提交成功！')
      
      const currentPoints = parseInt(localStorage.getItem('points') || 0)
      localStorage.setItem('points', currentPoints - totalPoints.value)
      window.dispatchEvent(new Event('update-points'))
      
      checkoutDialogVisible.value = false
      router.push('/order/my')
    } else {
      ElMessage.error(res.msg || '兌換失敗')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('系統忙碌中')
  }
}

onMounted(() => getCartList())
</script>

<template>
  <div class="cart-container">
    <div class="cart-header">
      <el-button :icon="ArrowLeft" @click="router.back()" link>返回商城</el-button>
      <h2>我的購物車 ({{ cartList.length }})</h2>
    </div>

    <el-card shadow="never">
      <el-table 
        :data="cartList" 
        v-loading="loading" 
        @selection-change="handleSelectionChange"
        row-key="id"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="商品資訊" min-width="200">
          <template #default="{ row }">
            <div class="product-info">
              <el-image :src="row.productImage" class="p-img" fit="cover" />
              <div class="p-text">
                <p class="name">{{ row.productName }}</p>
                <p class="stock">庫存: {{ row.stock }}</p>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="單價" width="120" align="center">
          <template #default="{ row }">💎 {{ row.pointsNeeded }}</template>
        </el-table-column>
        
        <el-table-column label="數量" width="180" align="center">
          <template #default="{ row }">
            <el-input-number 
              v-model="row.quantity" 
              :min="1" 
              :max="row.stock" 
              size="small"
              @change="handleQuantityChange(row)" 
            />
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="danger" :icon="Delete" link @click="removeProduct(row.id)" />
          </template>
        </el-table-column>
      </el-table>

      <div class="footer-bar">
        <div class="total-info">
          已選 {{ selectedItems.length }} 件，合計：
          <span class="total-points">💎 {{ totalPoints }}</span>
        </div>
        <el-button type="primary" size="large" round @click="handleCheckout">確認結帳</el-button>
      </div>
    </el-card>

    <el-dialog v-model="checkoutDialogVisible" title="🚚 確認收貨資訊" width="450px" center>
      <div class="summary-box">
        <p>本次將兌換 <strong>{{ selectedItems.length }}</strong> 項商品</p>
        <p>總計扣除：<span style="color: #f56c6c; font-weight: bold; font-size: 18px;">{{ totalPoints }}</span> 點</p>
      </div>

      <el-form :model="checkoutForm" label-position="top" style="margin-top: 20px;">
        <el-form-item label="領取方式">
          <el-radio-group v-model="checkoutForm.deliveryMethod">
            <el-radio :label="1" border>公司自取</el-radio>
            <el-radio :label="2" border>🚚 宅配</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="收貨地址" v-if="checkoutForm.deliveryMethod === 2" required>
          <div style="display: flex; gap: 10px; width: 100%;">
            <el-input 
              v-model="checkoutForm.address" 
              placeholder="請輸入詳細地址" 
              :prefix-icon="Location"
            />
            <el-button type="success" :icon="MapLocation" @click="mapVisible = true" plain>
              地圖
            </el-button>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="checkoutDialogVisible = false">再想想</el-button>
          <el-button type="primary" @click="confirmBatchExchange">確認兌換</el-button>
        </span>
      </template>
    </el-dialog>

    <MapSelector 
      v-model:visible="mapVisible" 
      :api-key="googleApiKey"
      @confirm-address="handleAddressSelected"
    />
  </div>
</template>

<style scoped>
.cart-container { padding: 20px; max-width: 1000px; margin: 0 auto; }
.cart-header { display: flex; align-items: center; gap: 20px; margin-bottom: 20px; }
.product-info { display: flex; align-items: center; gap: 15px; }
.p-img { width: 60px; height: 60px; border-radius: 8px; border: 1px solid #eee; }
.p-text .name { font-weight: bold; margin: 0; font-size: 14px; }
.p-text .stock { font-size: 12px; color: #999; margin: 5px 0 0; }
.footer-bar { 
  margin-top: 30px; display: flex; justify-content: flex-end; 
  align-items: center; gap: 30px; border-top: 1px solid #eee; padding-top: 20px;
}
.total-points { color: #f56c6c; font-size: 24px; font-weight: bold; }

.summary-box {
  background-color: #fdf6ec;
  padding: 15px;
  border-radius: 6px;
  text-align: center;
  color: #e6a23c;
}
.summary-box p { margin: 5px 0; }
</style>