<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { Search, Coin, User, Ticket } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const deptList = ref([]) // ✅ 新增：用來存部門列表
const pageParams = reactive({
  page: 1,
  pageSize: 10,
  name: ''
})

// ✅ 1. 獲取部門列表 (用於 ID 轉 名稱)
const getDeptList = async () => {
  const res = await request.get('/dept')
  if (res.code === 1) {
    deptList.value = res.data
  }
}

// 2. 獲取員工列表
const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/emps', { params: pageParams }) 
    if (res.code === 1) {
      tableData.value = res.data.rows
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageParams.page = 1
  loadData()
}

const handlePageChange = (page) => {
  pageParams.page = page
  loadData()
}

onMounted(() => {
  getDeptList() // ✅ 先載入部門
  loadData()    // 再載入員工
})
</script>

<template>
  <div style="padding: 20px;">
    <el-card shadow="never">
      <div style="margin-bottom: 20px; display: flex; justify-content: space-between;">
        <h2 style="margin:0; display: flex; align-items: center;">
          <el-icon style="color:#E6A23C; margin-right:8px;"><Coin /></el-icon>
          員工點數總覽
        </h2>
        
        <div style="display: flex;">
          <el-input 
            v-model="pageParams.name" 
            placeholder="搜尋員工姓名" 
            style="width: 200px; margin-right: 10px;"
            clearable
            @clear="handleSearch"
          />
          <el-button type="primary" :icon="Search" @click="handleSearch">查詢</el-button>
        </div>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        
        <el-table-column label="員工姓名" width="150">
          <template #default="scope">
            <div style="display: flex; align-items: center;">
              <el-icon style="margin-right: 5px; color: #666;"><User /></el-icon>
              <b>{{ scope.row.name }}</b>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="部門" width="150">
          <template #default="scope">
            <el-tag effect="plain" type="info">
              {{ deptList.find(d => d.id == scope.row.deptId)?.name || '未分配' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="job" label="職位" width="150" />

        <el-table-column label="當前持有點數" min-width="200" sortable prop="points">
          <template #default="scope">
            <div style="display: flex; align-items: center;">
              <span style="color: #E6A23C; font-weight: bold; font-size: 20px; margin-right: 4px;">
                {{ scope.row.points || 0 }}
              </span>
              <span style="font-size: 12px; color: #999;">點</span>
            </div>
          </template>
        </el-table-column>

      </el-table>

      <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination
          v-model:current-page="pageParams.page"
          v-model:page-size="pageParams.pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>