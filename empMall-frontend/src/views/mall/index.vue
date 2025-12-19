<script setup>
import { ref, onMounted, computed } from 'vue'
import { Search, Goods, Location, ShoppingCart, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request' 
import { useRouter } from 'vue-router'

const router = useRouter()

// --- 數據定義 ---
const loading = ref(false)
const productList = ref([]) 
const searchQuery = ref('') 
const sortType = ref('default') 
const selectedCategory = ref('') 

const categoryOptions = [
  { label: '全部商品', value: '' },
  { label: '📱 3C 數碼', value: 1 },
  { label: '📎 辦公用品', value: 2 },
  { label: '🥤 食品飲料', value: 3 },
  { label: '🎫 電子票券', value: 4 },
]

// 兌換彈窗相關
const dialogVisible = ref(false)
const currentProduct = ref({}) 
const exchangeForm = ref({
  deliveryMethod: 1, 
  address: ''
})

// --- 方法定義 ---

// 1. 獲取商品列表
const getProducts = async () => {
  loading.value = true
  try {
    const res = await request.get('/products')
    if (res.code === 1) {
      productList.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 2. 加入購物車方法
const addToCart = async (product) => {
  try {
    const uid = localStorage.getItem('uid')
    const res = await request.post('/cart/add', {
      empId: uid,
      productId: product.id,
      quantity: 1
    })
    if (res.code === 1) {
      ElMessage.success(`${product.name} 已加入購物車`)
    } else {
      ElMessage.error(res.msg || '加入購物車失敗')
    }
  } catch (error) {
    ElMessage.error('加入失敗，請聯繫管理員')
  }
}

// 3. 跳轉到購物車頁面
const goToCart = () => {
  router.push('/mall/cart') 
}

// 計算屬性：前端搜尋過濾
const filteredProducts = computed(() => {
  let res = productList.value
  res = res.filter(item => item.status === 1)
  if (searchQuery.value) {
    res = res.filter(item => item.name.includes(searchQuery.value))
  }
  if (selectedCategory.value !== '') {
    res = res.filter(item => item.category === selectedCategory.value)
  }
  if (sortType.value === 'points_asc') {
    res = [...res].sort((a, b) => a.pointsNeeded - b.pointsNeeded)
  } else if (sortType.value === 'points_desc') {
    res = [...res].sort((a, b) => b.pointsNeeded - a.pointsNeeded)
  } else if (sortType.value === 'newest') {
    res = [...res].sort((a, b) => b.id - a.id)
  }
  return res
})

// 點擊兌換按鈕
const handleExchangeClick = (item) => {
  if (item.stock <= 0) {
    ElMessage.warning('該商品目前已售罄，無法兌換！')
    return
  }
  const myPoints = parseInt(localStorage.getItem('points') || 0)
  if (myPoints < item.pointsNeeded) {
    ElMessage.error(`您的點數不足！(擁有: ${myPoints}, 需要: ${item.pointsNeeded})`)
    return
  }
  currentProduct.value = item
  exchangeForm.value = { deliveryMethod: 1, address: '' } 
  dialogVisible.value = true
}

// 提交兌換請求
const confirmExchange = async () => {
  if (exchangeForm.value.deliveryMethod === 2 && !exchangeForm.value.address) {
    ElMessage.warning('請輸入收貨地址')
    return
  }
  try {
    const uid = localStorage.getItem('uid')
    const payload = {
      empId: uid,
      productId: currentProduct.value.id,
      deliveryMethod: exchangeForm.value.deliveryMethod,
      address: exchangeForm.value.address
    }
    const res = await request.post('/orders/exchange', payload)
    if (res.code === 1) {
      ElMessage.success(`成功兌換：${currentProduct.value.name}`)
      
      // 更新本地點數快取並觸發同步事件
      const currentPoints = parseInt(localStorage.getItem('points'))
      const leftPoints = currentPoints - currentProduct.value.pointsNeeded
      localStorage.setItem('points', leftPoints)
      window.dispatchEvent(new Event('update-points'))
      
      dialogVisible.value = false
      getProducts() // 刷新庫存顯示
    } else {
      ElMessage.error(res.msg || '兌換失敗')
    }
  } catch (error) {
    ElMessage.error('系統忙碌中')
  }
}

onMounted(() => {
  getProducts()
})
</script>

<template>
  <div class="mall-container">
    <div class="toolbar-container">
      <el-input
        v-model="searchQuery"
        placeholder="🔍 搜尋商品..."
        class="filter-item search-input"
        clearable
      />

      <el-select 
        v-model="selectedCategory" 
        placeholder="商品分類" 
        class="filter-item category-select"
        clearable
      >
        <el-option
          v-for="opt in categoryOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>

      <el-radio-group v-model="sortType" class="filter-item">
        <el-radio-button label="default">預設</el-radio-button>
        <el-radio-button label="points_asc">點數 ↓</el-radio-button>
        <el-radio-button label="points_desc">點數 ↑</el-radio-button>
      </el-radio-group>

      <el-button 
        type="warning" 
        :icon="ShoppingCart" 
        @click="goToCart" 
        circle 
        size="large"
        class="cart-btn"
      />
    </div>

    <el-skeleton :loading="loading" animated :count="3">
      <template #default>
        <el-empty v-if="filteredProducts.length === 0" description="暫無上架商品" />
        
        <el-row :gutter="25">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in filteredProducts" :key="item.id">
            <el-card class="product-card" shadow="hover" :body-style="{ padding: '0px' }">
              <div class="image-wrapper">
                <img :src="item.image" class="image" lazy />
                <div class="stock-tag out-of-stock" v-if="item.stock <= 0">已售罄</div>
                <div class="stock-tag" v-else>剩餘庫存: {{ item.stock }}</div>
              </div>

              <div class="card-content">
                <h3 class="product-name">{{ item.name }}</h3>
                <p class="product-desc">{{ item.description || '暫無描述' }}</p>
                <div class="bottom-action">
                  <span class="price-tag">💎 {{ item.pointsNeeded }}</span>
                  <div class="btn-group">
                    <el-button type="warning" plain circle :icon="Plus" @click="addToCart(item)" :disabled="item.stock <= 0" />
                    <el-button type="primary" round @click="handleExchangeClick(item)" :disabled="item.stock <= 0">兌換</el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </template>
    </el-skeleton>

    <el-dialog v-model="dialogVisible" title="🎁 確認兌換資訊" width="400px" center>
      <div class="dialog-content">
        <img :src="currentProduct.image" class="dialog-img" />
        <p class="dialog-name">{{ currentProduct.name }}</p>
        <p class="dialog-price">本次消費：<span>{{ currentProduct.pointsNeeded }}</span> 點</p>
        <p style="font-size: 12px; color: #999;">目前剩餘庫存：{{ currentProduct.stock }}</p>
      </div>

      <el-form :model="exchangeForm" label-position="top">
        <el-form-item label="領取方式">
          <el-radio-group v-model="exchangeForm.deliveryMethod">
            <el-radio :label="1">🏢 公司自取 (行政部)</el-radio>
            <el-radio :label="2">🚚 寄送到家</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="收貨地址" v-if="exchangeForm.deliveryMethod === 2">
          <el-input v-model="exchangeForm.address" placeholder="請輸入詳細地址" :prefix-icon="Location" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">再想想</el-button>
          <el-button type="primary" @click="confirmExchange">確認兌換</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.cart-btn {
  margin-left: auto;
  box-shadow: 0 4px 10px rgba(230, 162, 70, 0.3);
}
.btn-group { display: flex; align-items: center; gap: 10px; }
.mall-container { padding: 10px; }
.toolbar-container {
  display: flex; justify-content: space-between; align-items: center;
  background-color: white; padding: 15px 20px; border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05); margin-bottom: 25px;
  flex-wrap: wrap; gap: 15px;
}
.product-card {
  margin-bottom: 25px; border-radius: 12px; overflow: hidden;
  transition: transform 0.3s; border: none;
}
.product-card:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1); }
.image-wrapper { width: 100%; height: 200px; overflow: hidden; position: relative; background-color: #f8f8f8; }
.image { width: 100%; height: 100%; object-fit: cover; }
.stock-tag { position: absolute; top: 10px; right: 10px; background: rgba(0, 0, 0, 0.5); color: white; padding: 4px 10px; border-radius: 4px; font-size: 12px; }
.out-of-stock { background: rgba(245, 108, 108, 0.9); font-weight: bold; }
.card-content { padding: 15px; }
.product-name { font-size: 16px; font-weight: bold; margin: 0 0 8px 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.product-desc { font-size: 13px; color: #999; margin: 0 0 15px 0; height: 20px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.bottom-action { display: flex; justify-content: space-between; align-items: center; }
.price-tag { color: #ff9900; font-weight: bold; font-size: 18px; font-family: Arial, sans-serif; }
.dialog-content { text-align: center; margin-bottom: 20px; background: #f9f9f9; padding: 15px; border-radius: 8px; }
.dialog-img { width: 100px; height: 100px; object-fit: cover; border-radius: 8px; }
.dialog-name { font-weight: bold; margin: 10px 0; }
.dialog-price span { color: red; font-weight: bold; font-size: 18px; }
</style>