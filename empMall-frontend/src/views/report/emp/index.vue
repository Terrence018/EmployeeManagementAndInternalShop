<script setup>
import { ref, onMounted } from 'vue'
// 1. 引入 Chart.js 核心元件
import { 
  Chart as ChartJS, 
  ArcElement, // 圓餅圖用
  BarElement, // 長條圖用
  LineElement, // 折線圖用
  PointElement, // 折線圖的點
  CategoryScale, // X軸
  LinearScale,   // Y軸
  Tooltip, 
  Legend,
  Title
} from 'chart.js'

// 2. 引入 Vue-Chartjs 的組件
import { Pie, Bar, Line } from 'vue-chartjs'
import request from '@/utils/request'

// 3. 引入圖示
import { 
  DataAnalysis, User, OfficeBuilding, 
  Money, Trophy, TrendCharts 
} from '@element-plus/icons-vue'

// 4. 註冊所有需要的 Chart.js 元件
ChartJS.register(
  ArcElement, BarElement, LineElement, PointElement, 
  CategoryScale, LinearScale, Tooltip, Legend, Title
)

// --- 數據定義 ---
const loading = ref(false)

// 各個圖表的數據 Ref
const genderData = ref({ labels: [], datasets: [] })
const deptData = ref({ labels: [], datasets: [] })
const salaryData = ref({ labels: [], datasets: [] })
const rankData = ref({ labels: [], datasets: [] })
const trendData = ref({ labels: [], datasets: [] })

// --- 圖表選項設定 (Options) ---

// 1. 圓餅圖通用選項 (隱藏過大的 Legend，避免佔位)
const pieOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { position: 'bottom' }
  }
}

// 2. 點數排行 (橫向長條圖)
const rankOptions = {
  indexAxis: 'y', // 讓長條圖變成橫向
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false } // 排行榜不需要圖例
  },
  scales: {
    x: { beginAtZero: true } // X軸從0開始
  }
}

// 3. 入職趨勢 (折線圖)
const trendOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false }
  },
  scales: {
    y: { beginAtZero: true, ticks: { stepSize: 1 } } // Y軸確保是整數
  }
}

// 顏色池
const colorPalette = [
  '#409EFF', '#67C23A', '#E6A23C', '#F56C6C', 
  '#909399', '#36cfc9', '#9254de', '#f759ab'
]

// 方法 

// 初始化：一次獲取所有數據
const initDashboard = async () => {
  loading.value = true
  try {
    // 使用 Promise.all 並行請求 5 個接口，速度更快
    const [resGender, resDept, resSalary, resRank, resTrend] = await Promise.all([
      request.get('/emps/report/gender'),
      request.get('/emps/report/dept'),
      request.get('/emps/report/salary'),
      request.get('/emps/report/pointsRank'),
      request.get('/emps/report/entryTrend')
    ])

    // 分別處理數據
    if (resGender.code === 1) genderData.value = processPieData(resGender.data)
    if (resDept.code === 1) deptData.value = processPieData(resDept.data)
    if (resSalary.code === 1) salaryData.value = processPieData(resSalary.data)
    if (resRank.code === 1) processRankData(resRank.data)
    if (resTrend.code === 1) processTrendData(resTrend.data)

  } catch (error) {
    console.error("獲取統計數據失敗", error)
  } finally {
    loading.value = false
  }
}

// 處理圓餅圖數據 (性別、部門、薪資)
const processPieData = (rawData) => {
  if (!rawData || rawData.length === 0) return { labels: [], datasets: [] }
  
  const labels = rawData.map(item => item.categoryName)
  const data = rawData.map(item => item.count)
  const bgColors = rawData.map((_, i) => colorPalette[i % colorPalette.length])

  return {
    labels,
    datasets: [{
      backgroundColor: bgColors,
      data: data,
      borderWidth: 2,
      borderColor: '#fff'
    }]
  }
}

// 處理排行榜數據 (長條圖)
const processRankData = (rawData) => {
  if (!rawData || rawData.length === 0) return
  
  rankData.value = {
    labels: rawData.map(item => item.categoryName), // 員工姓名
    datasets: [{
      label: '持有點數',
      data: rawData.map(item => item.count),
      backgroundColor: '#E6A23C', // 金色
      borderRadius: 4
    }]
  }
}

// 處理趨勢圖數據 (折線圖)
const processTrendData = (rawData) => {
  if (!rawData || rawData.length === 0) return

  trendData.value = {
    labels: rawData.map(item => item.categoryName), // 年份
    datasets: [{
      label: '入職人數',
      data: rawData.map(item => item.count),
      borderColor: '#409EFF',
      backgroundColor: 'rgba(64, 158, 255, 0.2)',
      fill: true, // 填充下方區域
      tension: 0.4 // 平滑曲線
    }]
  }
}

onMounted(() => {
  initDashboard()
})
</script>

<template>
  <div class="dashboard-container" v-loading="loading">
    
    <div class="page-header">
      <el-icon :size="28" color="#409EFF"><DataAnalysis /></el-icon>
      <h2>員工數據戰情室</h2>
    </div>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header"><el-icon><OfficeBuilding /></el-icon> 部門人數分佈</div>
          </template>
          <div class="chart-box">
            <Pie v-if="deptData.labels.length" :data="deptData" :options="pieOptions" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header"><el-icon><User /></el-icon> 性別比例</div>
          </template>
          <div class="chart-box">
            <Pie v-if="genderData.labels.length" :data="genderData" :options="pieOptions" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header"><el-icon><Money /></el-icon> 薪資區間分佈</div>
          </template>
          <div class="chart-box">
            <Pie v-if="salaryData.labels.length" :data="salaryData" :options="pieOptions" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      
      <el-col :span="14">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header"><el-icon><TrendCharts /></el-icon> 年度入職成長趨勢</div>
          </template>
          <div class="chart-box lg">
            <Line v-if="trendData.labels.length" :data="trendData" :options="trendOptions" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header"><el-icon color="#E6A23C"><Trophy /></el-icon> 員工點數財富榜 Top 10</div>
          </template>
          <div class="chart-box lg">
            <Bar v-if="rankData.labels.length" :data="rankData" :options="rankOptions" />
          </div>
        </el-card>
      </el-col>

    </el-row>

  </div>
</template>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 80px);
}

.page-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 25px;
  padding-left: 10px;
}
.page-header h2 {
  margin: 0;
  color: #303133;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
  gap: 8px;
}

.chart-box {
  height: 250px; /* 圓餅圖的高度 */
  position: relative;
}

.chart-box.lg {
  height: 350px; /* 折線圖和長條圖的高度，給高一點比較好看 */
}
</style>