<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { 
  DataLine, Goods, PieChart, TrendCharts, DataAnalysis 
} from '@element-plus/icons-vue'

// 1. 引入 Chart.js
import { 
  Chart as ChartJS, ArcElement, BarElement, LineElement, PointElement, 
  CategoryScale, LinearScale, Tooltip, Legend, Title 
} from 'chart.js'
import { Pie, Bar, Line } from 'vue-chartjs'

// 2. 註冊 Chart.js
ChartJS.register(ArcElement, BarElement, LineElement, PointElement, CategoryScale, LinearScale, Tooltip, Legend, Title)

// 數據區 
const loading = ref(false)
const tableData = ref([])
const top10Data = ref({ labels: [], datasets: [] })
const categoryData = ref({ labels: [], datasets: [] })
const trendData = ref({ labels: [], datasets: [] })

// --- 圖表選項 (高度都設定為響應式) ---
const pieOptions = { 
  responsive: true, 
  maintainAspectRatio: false, 
  plugins: { legend: { position: 'right', labels: { boxWidth: 12, padding: 10 } } } 
}

const barOptions = { 
  indexAxis: 'y', 
  responsive: true, 
  maintainAspectRatio: false, 
  plugins: { legend: { display: false } },
  scales: { x: { beginAtZero: true, ticks: { stepSize: 1, precision: 0 } }, y: { grid: { display: false } } }
}

const lineOptions = { 
  responsive: true, 
  maintainAspectRatio: false, 
  plugins: { legend: { display: false } }, 
  scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } }, x: { grid: { display: false } } } 
}

const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#36cfc9', '#9254de']

// --- 方法區 ---
const loadDashboard = async () => {
  loading.value = true
  try {
    // 平行請求所有數據
    const [resTop10, resCat, resTrend, resTable] = await Promise.all([
      request.get('/orders/report/top10'),
      request.get('/orders/report/category'),
      request.get('/orders/report/trend'),
      request.get('/goods/stats', { params: { orderBy: 1 } }) // 表格數據
    ])

    if (resTop10.code === 1) processTop10(resTop10.data)
    if (resCat.code === 1) processCategory(resCat.data)
    if (resTrend.code === 1) processTrend(resTrend.data)
    if (resTable.code === 1) tableData.value = resTable.data

  } catch (e) {
    console.error("載入失敗", e)
  } finally {
    loading.value = false
  }
}

// 資料處理函數
const processTop10 = (data) => {
  if (!data) return
  top10Data.value = {
    labels: data.map(i => i.categoryName),
    datasets: [{ label: '銷售量', data: data.map(i => i.count), backgroundColor: '#F56C6C', borderRadius: 4, barThickness: 15 }]
  }
}

const processCategory = (data) => {
  if (!data) return
  categoryData.value = {
    labels: data.map(i => i.categoryName),
    datasets: [{ data: data.map(i => i.count), backgroundColor: data.map((_, i) => colors[i % colors.length]) }]
  }
}

const processTrend = (data) => {
  if (!data) return
  trendData.value = {
    labels: data.map(i => i.categoryName),
    datasets: [{ label: '訂單數', data: data.map(i => i.count), borderColor: '#409EFF', backgroundColor: 'rgba(64, 158, 255, 0.1)', fill: true, tension: 0.4, pointRadius: 3 }]
  }
}

onMounted(() => {
  loadDashboard()
})
</script>

<template>
  <div class="stats-container" v-loading="loading">
    
    <div class="page-header">
      <el-icon :size="24" color="#409EFF"><DataAnalysis /></el-icon>
      <h2>商品銷售數據中心</h2>
    </div>

    <el-row :gutter="15" class="row-spacing">
      <el-col :span="15">
        <el-card shadow="hover" class="chart-card small-card">
          <template #header>
            <div class="card-header"><el-icon color="#409EFF"><TrendCharts /></el-icon> 近 7 日交易活躍度</div>
          </template>
          <div class="chart-box-sm">
            <Line v-if="trendData.labels.length" :data="trendData" :options="lineOptions" />
            <el-empty v-else description="暫無數據" :image-size="60" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="9">
        <el-card shadow="hover" class="chart-card small-card">
          <template #header>
            <div class="card-header"><el-icon color="#67C23A"><PieChart /></el-icon> 銷售類別佔比</div>
          </template>
          <div class="chart-box-sm">
            <Pie v-if="categoryData.labels.length" :data="categoryData" :options="pieOptions" />
            <el-empty v-else description="暫無數據" :image-size="60" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="15">
      
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header"><el-icon color="#F56C6C"><Goods /></el-icon> 熱銷 Top 10</div>
          </template>
          <div class="chart-box-lg">
            <Bar v-if="top10Data.labels.length" :data="top10Data" :options="barOptions" />
            <el-empty v-else description="暫無數據" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <el-icon color="#409EFF"><DataLine /></el-icon> 最新銷售明細
              <span class="sub-title">(依時間排序)</span>
            </div>
          </template>
          
          <div class="table-container">
            <el-table :data="tableData" stripe size="small" style="width: 100%" height="100%">
              <el-table-column type="index" label="#" width="50" align="center" />
              
              <el-table-column label="商品名稱" min-width="180" show-overflow-tooltip>
                <template #default="scope">
                  <div style="display: flex; align-items: center;">
                    <el-image 
                      style="width: 30px; height: 30px; border-radius: 4px; margin-right: 8px; border: 1px solid #eee;"
                      :src="scope.row.image" fit="cover"
                    />
                    <span style="font-weight: bold;">{{ scope.row.name }}</span>
                  </div>
                </template>
              </el-table-column>

              <el-table-column prop="totalSold" label="銷量" width="100" align="center">
                <template #default="scope">
                   <span style="color: #409EFF; font-weight: bold;">{{ scope.row.totalSold }}</span> 
                </template>
              </el-table-column>

              <el-table-column prop="lastSaleTime" label="時間" width="150" align="center">
                <template #default="scope">
                  <span v-if="scope.row.lastSaleTime" style="font-size: 12px; color: #666;">
                    {{ scope.row.lastSaleTime.replace('T', ' ') }}
                  </span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>

    </el-row>

  </div>
</template>

<style scoped>
.stats-container { 
  padding: 15px; 
  background-color: #f5f7fa; 
  /* 讓整個頁面盡量不出現瀏覽器捲軸，若內容太多才滾動 */
  height: calc(100vh - 84px); 
  overflow-y: auto; 
}

.page-header { 
  display: flex; align-items: center; gap: 10px; margin-bottom: 15px; 
}
.page-header h2 { margin: 0; color: #303133; font-size: 20px; }

.row-spacing { margin-bottom: 15px; }

.chart-card { height: 100%; border-radius: 8px; }

/* 標題樣式 */
.card-header { display: flex; align-items: center; gap: 6px; font-weight: bold; font-size: 14px; }
.sub-title { font-size: 12px; color: #909399; margin-left: auto; font-weight: normal; }

/* 圖表高度控制 */
/* 上半部：矮一點 */
.chart-box-sm { height: 200px; padding: 5px; }

.chart-box-lg { height: 350px; padding: 5px; }
.table-container { height: 350px; } /* 固定表格高度，讓它內部滾動 */

/* Element Plus Card 樣式微調 */
:deep(.el-card__header) {
  padding: 10px 15px; /* 縮小標題 padding */
  border-bottom: 1px solid #ebeef5;
}
:deep(.el-card__body) {
  padding: 10px; /* 縮小內容 padding */
}
</style>