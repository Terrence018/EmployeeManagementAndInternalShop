<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { Timer } from '@element-plus/icons-vue'

// --- 數據區 ---
const loading = ref(false)
const tableData = ref([])

const empId = localStorage.getItem('uid') || 1 

// 獲取點數歷史紀錄
const loadHistory = async () => {
  loading.value = true
  try {
    const res = await request.get(`/points/log/my/${empId}`)
    if (res.code === 1) {
      tableData.value = res.data
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadHistory()
})
</script>

<template>
  <div style="padding: 20px;">

    <el-card shadow="never">
      <template #header>
        <div style="display: flex; align-items: center;">
          <el-icon style="margin-right: 5px;"><Timer /></el-icon>
          <span>點數變動紀錄</span>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
        
        <el-table-column label="交易時間" width="180">
          <template #default="scope">
            {{ scope.row.createTime?.replace('T', ' ') }}
          </template>
        </el-table-column>

        <el-table-column label="變動點數" width="120" align="center">
          <template #default="scope">
            <span v-if="scope.row.type === 1" style="color: #67C23A; font-weight: bold; font-size: 16px;">
              +{{ scope.row.points }}
            </span>
            <span v-else style="color: #F56C6C; font-weight: bold; font-size: 16px;">
              -{{ scope.row.points }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="結餘" width="120" align="center">
          <template #default="scope">
            <span v-if="scope.row.balance !== null" style="font-weight: bold; color: #606266;">
              {{ scope.row.balance }}
            </span>
            <span v-else style="color: #ccc;">-</span>
          </template>
        </el-table-column>

        <el-table-column prop="info" label="說明 / 備註" min-width="200">
          <template #default="scope">
            {{ scope.row.info || '無備註' }}
          </template>
        </el-table-column>

        <el-table-column label="來源" width="140" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.type === 1" type="success" effect="plain">
              管理員發放
            </el-tag>
            <el-tag v-else type="warning" effect="plain">
              商城兌換
            </el-tag>
          </template>
        </el-table-column>

      </el-table>
    </el-card>

  </div>
</template>