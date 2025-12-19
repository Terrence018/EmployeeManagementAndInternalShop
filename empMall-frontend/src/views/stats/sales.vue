<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { Timer, Trophy, DataLine } from '@element-plus/icons-vue'

// --- 數據區 ---
const loading = ref(false)
const tableData = ref([])
const orderBy = ref(1) // 1=近期銷售(預設), 2=銷量排行

// --- 方法區 ---

// 載入數據
const loadData = async () => {
  loading.value = true
  try {
    // 呼叫後端 API
    const res = await request.get('/goods/stats', { 
      params: { orderBy: orderBy.value } 
    })
    
    if (res.code === 1) {
      tableData.value = res.data
    }
  } finally {
    loading.value = false
  }
}

// 切換排序模式
const handleSortChange = (val) => {
  orderBy.value = val
  loadData()
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<template>
  <div style="padding: 20px;">
    
    <el-card shadow="never" style="margin-bottom: 20px;">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <h2 style="margin: 0; display: flex; align-items: center;">
            <el-icon style="margin-right: 8px; color: #409EFF;"><DataLine /></el-icon>
            商品銷售總紀錄
          </h2>
          <div style="color: #909399; font-size: 14px; margin-top: 5px; margin-left: 32px;">
            統計所有商品的累計銷量與最近交易時間
          </div>
        </div>
        
        <el-radio-group v-model="orderBy" @change="handleSortChange">
          <el-radio-button :value="1">
            <el-icon style="margin-right:4px"><Timer /></el-icon> 近期銷售
          </el-radio-button>
          <el-radio-button :value="2">
            <el-icon style="margin-right:4px"><Trophy /></el-icon> 銷量排行
          </el-radio-button>
        </el-radio-group>
      </div>
    </el-card>

    <el-card shadow="never">
      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        
        <el-table-column type="index" label="排名" width="80" align="center">
          <template #default="scope">
            <div v-if="orderBy === 2">
              <span v-if="scope.$index === 0" style="font-size: 24px;">🥇</span>
              <span v-else-if="scope.$index === 1" style="font-size: 24px;">🥈</span>
              <span v-else-if="scope.$index === 2" style="font-size: 24px;">🥉</span>
              <span v-else style="font-weight: bold; color: #606266;">{{ scope.$index + 1 }}</span>
            </div>
            <span v-else>{{ scope.$index + 1 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="商品名稱" min-width="250">
          <template #default="scope">
            <div style="display: flex; align-items: center;">
              <el-image 
                style="width: 50px; height: 50px; border-radius: 6px; margin-right: 15px; border: 1px solid #eee;"
                :src="scope.row.image" 
                :preview-src-list="[scope.row.image]"
                preview-teleported
                fit="cover"
              >
                <template #error>
                  <div style="width: 100%; height: 100%; background: #f5f7fa; display: flex; align-items: center; justify-content: center; color: #909399;">
                    無圖
                  </div>
                </template>
              </el-image>
              
              <span style="font-weight: bold; font-size: 15px;">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="totalSold" label="總銷量" width="180" align="center" sortable>
          <template #default="scope">
             <span style="font-size: 18px; color: #409EFF; font-weight: bold; font-family: Arial;">
               {{ scope.row.totalSold }}
             </span> 
             <span style="font-size: 12px; color: #999; margin-left: 4px;">單</span>
          </template>
        </el-table-column>

        <el-table-column prop="lastSaleTime" label="最近銷售時間" width="200" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.lastSaleTime" type="info" effect="plain">
              {{ scope.row.lastSaleTime.replace('T', ' ') }}
            </el-tag>
            <span v-else style="color: #ccc;">-- 暫無銷售 --</span>
          </template>
        </el-table-column>

      </el-table>
    </el-card>
  </div>
</template>