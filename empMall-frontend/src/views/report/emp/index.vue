<script setup>
import { ref, onMounted, watch } from 'vue'
// 1. 引入 Chart.js 核心元件和 Vue 封裝元件
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Pie } from 'vue-chartjs'
import request from '@/utils/request'
import { PieChart, DataAnalysis, User, OfficeBuilding } from '@element-plus/icons-vue'

// 2. 註冊 Chart.js 必須的元件 (圓餅圖需要 ArcElement, Tooltip, Legend)
ChartJS.register(ArcElement, Tooltip, Legend)

// --- 數據定義 ---
const loading = ref(false)
const currentType = ref('gender') // 當前統計類型: gender 或 dept

// Chart.js 需要的數據格式
const chartData = ref({
  labels: [],
  datasets: [{
    backgroundColor: [],
    data: []
  }]
})

// Chart.js 的選項設定
const chartOptions = ref({
  responsive: true,
  maintainAspectRatio: false, // 允許自定義高度
  plugins: {
    legend: {
      position: 'bottom', // 圖例放下面
      labels: {
        font: {
          size: 14
        }
      }
    }
  }
})

// 預定義一些好看的顏色池
const colorPalette = [
  '#409EFF', '#67C23A', '#E6A23C', '#F56C6C', 
  '#909399', '#36cfc9', '#9254de', '#f759ab'
]

// --- 方法 ---

// 獲取統計數據
const fetchData = async () => {
  loading.value = true
  try {
    // 根據目前選擇的類型決定呼叫哪個 API
    const url = currentType.value === 'gender' ? '/emps/report/gender' : '/emps/report/dept'
    const res = await request.get(url)
    
    if (res.code === 1) {
      processDataToChart(res.data)
    }
  } catch (error) {
    console.error("獲取統計數據失敗", error)
  } finally {
    loading.value = false
  }
}

// 將後端資料轉換為 Chart.js 需要的格式
const processDataToChart = (rawData) => {
  const labels = []
  const data = []
  const backgroundColor = []

  // 如果後端回傳空陣列，需處理防呆
  if (!rawData || rawData.length === 0) {
    chartData.value = { labels: [], datasets: [] }
    return
  }

  rawData.forEach((item, index) => {
    labels.push(item.categoryName) // 例如: ["男", "女"]
    data.push(item.count)          // 例如: [10, 5]
    // 循環使用顏色池
    backgroundColor.push(colorPalette[index % colorPalette.length])
  })

  // 更新圖表數據 reactive 物件
  chartData.value = {
    labels: labels,
    datasets: [{
      backgroundColor: backgroundColor,
      data: data,
      borderWidth: 2,
      borderColor: '#ffffff'
    }]
  }
}

// 監聽切換按鈕，變動時重新抓資料
watch(currentType, () => {
  fetchData()
})

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="report-container">
    
    <el-card shadow="never" class="control-panel">
      <div class="panel-header">
        <el-icon :size="24" color="#409EFF"><DataAnalysis /></el-icon>
        <span style="margin-left: 10px; font-weight: bold; font-size: 18px;">員工結構分析</span>
      </div>
      
      <div class="chart-controls">
        <el-radio-group v-model="currentType" size="large">
          <el-radio-button label="gender">
            <el-icon><User /></el-icon> 性別分佈
          </el-radio-button>
          <el-radio-button label="dept">
            <el-icon><OfficeBuilding /></el-icon> 部門分佈
          </el-radio-button>
        </el-radio-group>
        <span class="tips">點擊切換查看不同維度的統計資訊</span>
      </div>
    </el-card>

    <el-card shadow="hover" style="margin-top: 20px;" v-loading="loading">
      <div class="chart-wrapper">
        <Pie 
          v-if="chartData.labels && chartData.labels.length > 0"
          :data="chartData" 
          :options="chartOptions" 
        />
        
        <el-empty v-else description="暫無統計數據，請確認後端是否有資料" />
      </div>
    </el-card>

  </div>
</template>

<style scoped>
.report-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 80px);
}

.control-panel {
  margin-bottom: 20px;
}

.panel-header {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  margin-bottom: 20px;
}

.chart-controls {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.tips {
  font-size: 12px;
  color: #909399;
}

.chart-wrapper {
  /* 這裡控制圖表的高度，maintainAspectRatio: false 時生效 */
  height: 500px; 
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}
</style>