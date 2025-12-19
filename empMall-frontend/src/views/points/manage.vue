<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Plus, Search, User, Timer, Coin } from '@element-plus/icons-vue'

// --- 數據區 ---
const loading = ref(false)
const tableData = ref([]) // 點數發放紀錄
const total = ref(0)
const pageParams = reactive({
  page: 1,
  pageSize: 10,
  name: '' // 搜尋紀錄中的員工姓名
})

// 彈窗相關
const dialogVisible = ref(false)
const empList = ref([]) // 🟢 存放所有員工列表 (給下拉選單用)
const form = reactive({
  empId: null,
  points: 10,
  type: 1, // 1:獎勵, 2:扣除 (看你後端定義，假設預設是發放)
  info: '' // 備註
})

// --- 方法區 ---

// 1. 獲取點數紀錄列表 (假設後端接口是 /points/log，請依實際修改)
const loadData = async () => {
  loading.value = true
  try {
    // ⚠️ 請確認你的後端查詢點數紀錄的 API 路徑
    // 如果還沒寫，這裡可能會報錯
    const res = await request.get('/points/log', { params: pageParams }) 
    if (res.code === 1) {
      tableData.value = res.data.rows
      total.value = res.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// 🟢 2. 獲取員工列表 (給下拉選單用)
const loadEmpList = async () => {
  // 這裡設一個較大的 pageSize 以獲取所有員工，或者後端有不分頁的接口更好
  const res = await request.get('/emps', { params: { page: 1, pageSize: 1000 } })
  if (res.code === 1) {
    empList.value = res.data.rows
  }
}

// 3. 開啟發放視窗
const handleAdd = () => {
  // 重置表單
  form.empId = null
  form.points = 10
  form.info = ''
  
  dialogVisible.value = true
  // 確保有點開時才確保員工名單是最新的 (也可以放在 onMounted)
  if (empList.value.length === 0) {
    loadEmpList()
  }
}

// 4. 送出發放請求
const submitForm = async () => {
  if (!form.empId) {
    ElMessage.warning('請選擇員工')
    return
  }
  if (!form.points) {
    ElMessage.warning('請輸入點數')
    return
  }

  // ⚠️ 請確認你的後端發放點數 API 路徑
  // 假設是用 POST /points 來新增紀錄並修改餘額
  const res = await request.post('/points', form)
  
  if (res.code === 1) {
    ElMessage.success('點數發放成功')
    dialogVisible.value = false
    loadData() // 刷新紀錄列表
  } else {
    ElMessage.error(res.msg || '發放失敗')
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
  loadData()
  loadEmpList() // 預先載入員工名單
})
</script>

<template>
  <div style="padding: 20px;">
    
    <el-card shadow="never" style="margin-bottom: 20px;">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2 style="margin:0; display: flex; align-items: center;">
          <el-icon style="color:#67C23A; margin-right:8px;"><Coin /></el-icon>
          點數發放與紀錄
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
          <el-button type="success" :icon="Plus" @click="handleAdd">發放點數</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        
        <el-table-column label="員工姓名" width="100">
          <template #default="scope">
            <b>{{ scope.row.empName }}</b>
          </template>
        </el-table-column>

<el-table-column label="操作人" width="100" align="center">
          <template #default="scope">
            <el-tag 
              size="small" 
              :type="scope.row.operatorName ? 'warning' : 'info'" 
              effect="plain"
            >
              {{ scope.row.operatorName || '商城兌換' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="變動點數" prop="points" align="center">
          <template #default="scope">
            <span v-if="scope.row.type === 1" style="color: #67C23A; font-weight: bold;">
              +{{ scope.row.points }}
            </span>
            <span v-else style="color: #F56C6C; font-weight: bold;">
              -{{ scope.row.points }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="info" label="備註/原因" />

        <el-table-column label="操作時間" width="180">
          <template #default="scope">
            {{ scope.row.createTime?.replace('T', ' ') }}
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

    <el-dialog v-model="dialogVisible" title="💰 發放/扣除點數" width="500px">
      <el-form label-width="100px">
        
        <el-form-item label="選擇員工">
          <el-select 
            v-model="form.empId" 
            placeholder="請搜尋或選擇員工" 
            filterable 
            style="width: 100%"
          >
            <el-option
              v-for="item in empList"
              :key="item.id"
              :label="item.name + ' (ID:' + item.id + ')'"
              :value="item.id"
            >
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                {{ item.deptName || '無部門' }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="點數數量">
          <el-input-number v-model="form.points" :min="-10000" :max="10000" />
          <div style="font-size: 12px; color: #999; margin-left: 10px;">
            (正數為發放，負數為扣除)
          </div>
        </el-form-item>

        <el-form-item label="備註原因">
          <el-input v-model="form.info" type="textarea" placeholder="例如：全勤獎金、專案獎勵..." />
        </el-form-item>

      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">確認發放</el-button>
      </template>
    </el-dialog>

  </div>
</template>