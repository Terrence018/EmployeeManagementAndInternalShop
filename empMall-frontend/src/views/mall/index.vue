<script setup>
import { ref, onMounted, computed } from 'vue'
import { Search, Goods, Location, MapLocation, ShoppingCart, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request' 
import { useRouter } from 'vue-router'
import MapSelector from '@/components/MapSelector.vue'

// 地圖相關變數
const mapVisible = ref(false)
const googleApiKey = 'AIzaSyAdkUQCnemI3Rs04MjfCR3POlDQudUSy_8'

const router = useRouter()

// 數據定義 
const loading = ref(false)
const productList = ref([]) 
const searchQuery = ref('') 
const sortType = ref('default') 
const selectedCategory = ref('') 

// 分頁相關變數
const currentPage = ref(1)
const pageSize = ref(8) // 每頁 8 筆
const total = ref(0)

const categoryOptions = [
  { label: '全部商品', value: '' },
  { label: '3C 數碼', value: 1 },
  { label: '辦公用品', value: 2 },
  { label: '食品飲料', value: 3 },
  { label: '電子票券', value: 4 },
  { label: '生活百貨', value: 5 },
  { label: '圖書雜誌', value: 6 },
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
    const res = await request.get('/products', {
      params: {
        page: currentPage.value,
        pageSize: pageSize.value,
        status: 1, // 只看上架
        // 傳送分類與搜尋字串給後端
        category: selectedCategory.value || null, 
        keyword: searchQuery.value || null
      }
    })
    
    if (res.code === 1) {
      productList.value = res.data.items 
      total.value = res.data.total
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 當分類或搜尋改變時，重置回第一頁並查詢
const handleFilterChange = () => {
  currentPage.value = 1
  getProducts()
}

// 換頁事件
const handlePageChange = (page) => {
  currentPage.value = page
  getProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 加入購物車
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
    ElMessage.error('加入失敗')
  }
}

const goToCart = () => {
  router.push('/mall/cart') 
}

const handleAddressSelected = (address) => {
  exchangeForm.value.address = address
}

// 計算屬性只負責「排序」，不再負責「過濾」
// 因為後端回傳的資料已經是過濾好的了
const filteredProducts = computed(() => {
  let res = [...productList.value] // 淺拷貝避免影響原陣列
  
  // 只處理排序 (因為後端目前只排了時間，如果你想支援點數排序，在這裡做是可以的)
  // 目前只會對「當前頁面的 8 筆」進行排序
  if (sortType.value === 'points_asc') {
    res.sort((a, b) => a.pointsNeeded - b.pointsNeeded)
  } else if (sortType.value === 'points_desc') {
    res.sort((a, b) => b.pointsNeeded - a.pointsNeeded)
  } else if (sortType.value === 'newest') {
    res.sort((a, b) => b.id - a.id)
  }
  
  return res
})

// 點擊兌換
const handleExchangeClick = (item) => {
  if (item.stock <= 0) {
    ElMessage.warning('已售罄')
    return
  }
  const myPoints = parseInt(localStorage.getItem('points') || 0)
  if (myPoints < item.pointsNeeded) {
    ElMessage.error(`點數不足！(擁有: ${myPoints})`)
    return
  }
  currentProduct.value = item
  exchangeForm.value = { deliveryMethod: 1, address: '' } 
  dialogVisible.value = true
}

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
      const currentPoints = parseInt(localStorage.getItem('points'))
      const leftPoints = currentPoints - currentProduct.value.pointsNeeded
      localStorage.setItem('points', leftPoints)
      window.dispatchEvent(new Event('update-points'))
      dialogVisible.value = false
      getProducts() 
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
        @clear="handleFilterChange"
        @keyup.enter="handleFilterChange" 
      >
        <template #append>
          <el-button :icon="Search" @click="handleFilterChange" />
        </template>
      </el-input>

      <el-select 
        v-model="selectedCategory" 
        placeholder="商品分類" 
        class="filter-item category-select"
        clearable
        @change="handleFilterChange" 
        @clear="handleFilterChange"
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
        <el-empty v-if="filteredProducts.length === 0" description="暫無相關商品" />
        
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

    <div class="pagination-container" v-if="total > 0">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="handlePageChange"
      />
    </div>

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
            <el-radio :label="1">公司自取 (行政部)</el-radio>
            <el-radio :label="2">🚚宅配</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="收貨地址" v-if="exchangeForm.deliveryMethod === 2">
          <div style="display: flex; gap: 10px; width: 100%;">
            <el-input 
              v-model="exchangeForm.address" 
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
          <el-button @click="dialogVisible = false">再想想</el-button>
          <el-button type="primary" @click="confirmExchange">確認兌換</el-button>
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
/* 樣式保持原樣 */
.mall-container { 
  padding: 20px; 
  background-color: #f5f7fa; 
  min-height: 100vh; 
}
.toolbar-container {
  display: flex; justify-content: space-between; align-items: center;
  background-color: white; padding: 15px 20px; border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05); margin-bottom: 25px;
  flex-wrap: wrap; gap: 15px;
}
.cart-btn { margin-left: auto; box-shadow: 0 4px 10px rgba(230, 162, 70, 0.3); }
.product-card {
  margin-bottom: 25px; border-radius: 12px; overflow: hidden; 
  transition: transform 0.3s; border: none; position: relative; background-color: #ffffff; 
}
.product-card::after {
  content: ''; position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background-image: url('@/assets/christmas-border.png'); 
  background-size: 100% 100%; background-repeat: no-repeat; background-position: center;
  pointer-events: none; z-index: 10; 
}
.product-card:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1); }
.image-wrapper { width: 100%; height: 200px; overflow: hidden; position: relative; background-color: #f8f8f8; }
.image { width: 100%; height: 100%; object-fit: cover; }
.stock-tag { position: absolute; top: 10px; right: 10px; background: rgba(0, 0, 0, 0.5); color: white; padding: 4px 10px; border-radius: 4px; font-size: 12px; z-index: 5; }
.out-of-stock { background: rgba(245, 108, 108, 0.9); font-weight: bold; }
.card-content { padding: 15px; }
.product-name { font-size: 16px; font-weight: bold; margin: 0 0 8px 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.product-desc { font-size: 13px; color: #999; margin: 0 0 15px 0; height: 20px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.bottom-action { display: flex; justify-content: space-between; align-items: center; }
.btn-group { display: flex; align-items: center; gap: 10px; position: relative; z-index: 11; }
.price-tag { color: #ff9900; font-weight: bold; font-size: 18px; font-family: Arial, sans-serif; }
.dialog-content { text-align: center; margin-bottom: 20px; background: #f9f9f9; padding: 15px; border-radius: 8px; }
.dialog-img { width: 100px; height: 100px; object-fit: cover; border-radius: 8px; }
.dialog-name { font-weight: bold; margin: 10px 0; }
.dialog-price span { color: red; font-weight: bold; font-size: 18px; }
.pagination-container { display: flex; justify-content: center; margin-top: 30px; padding-bottom: 20px; }
</style>