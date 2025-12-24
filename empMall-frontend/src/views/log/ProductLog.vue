<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request' // 只需要引入 request 工具
import { Clock, User, Document } from '@element-plus/icons-vue'

// --- 數據定義 ---
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const pageParams = reactive({
  page: 1,
  pageSize: 10
})

//方法

// 獲取日誌列表
const loadData = async () => {
  loading.value = true
  try {
    // 直接調用 request
    const res = await request.get('/productLog', { 
      params: pageParams 
    })
    
    if (res.code === 1) {
      tableData.value = res.data.rows
      total.value = res.data.total
    }
  } catch (error) {
    console.error("獲取日誌失敗", error)
  } finally {
    loading.value = false
  }
}

// 分頁處理
const handleSizeChange = (val) => {
  pageParams.pageSize = val
  loadData()
}
const handleCurrentChange = (val) => {
  pageParams.page = val
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="log-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>商城業務日誌</span>
          <el-button style="float: right; padding: 3px 0" text @click="loadData">刷新</el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="#" width="60" align="center" />
        
        <el-table-column prop="operateName" label="操作人" width="150">
          <template #default="scope">
             <el-tag effect="light" round>
               <el-icon><User /></el-icon> {{ scope.row.operateName || '未知' }}
             </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="content" label="日誌內容">
          <template #default="scope">
            <div style="display: flex; align-items: center;">
               <el-icon style="margin-right: 8px; color: #E6A23C"><Document /></el-icon>
               <span style="font-weight: 500;">{{ scope.row.content }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="operateTime" label="操作時間" width="200" sortable>
           <template #default="scope">
             <span style="color: #666; font-size: 13px;">
               <el-icon><Clock /></el-icon> 
               {{ scope.row.operateTime ? scope.row.operateTime.replace('T', ' ') : '' }}
             </span>
           </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageParams.page"
          v-model:page-size="pageParams.pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.log-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}
.card-header {
  font-weight: bold;
  font-size: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>