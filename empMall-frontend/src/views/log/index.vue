<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { Timer, User, Monitor, Coin, View, Goods } from '@element-plus/icons-vue' // ✅ 新增 Goods 圖標

const activeTab = ref('operate') // 預設顯示操作日誌

// --- 1. 系統操作日誌數據 ---
const operateLoading = ref(false)
const operateData = ref([])
const operateTotal = ref(0)
const operateParams = reactive({ page: 1, pageSize: 10 })

// --- 2. 員工業務日誌數據 ---
const empLogLoading = ref(false)
const empLogData = ref([])
const empLogTotal = ref(0)
const empLogParams = reactive({ page: 1, pageSize: 10 })

// --- 3. 新增：商城業務日誌數據 ---
const productLogLoading = ref(false)
const productLogData = ref([])
const productLogTotal = ref(0)
const productLogParams = reactive({ page: 1, pageSize: 10 })


// --- 詳情彈窗相關數據 ---
const detailVisible = ref(false)
const currentLog = ref({})

// --- 方法區 ---

// 加載系統日誌
const loadOperateData = async () => {
  operateLoading.value = true
  try {
    const res = await request.get('/logs/operate', { params: operateParams })
    if (res.code === 1) {
      operateData.value = res.data.rows
      operateTotal.value = res.data.total
    }
  } finally { operateLoading.value = false }
}

// 加載員工日誌
const loadEmpLogData = async () => {
  empLogLoading.value = true
  try {
    const res = await request.get('/logs/emp', { params: empLogParams })
    if (res.code === 1) {
      empLogData.value = res.data.rows
      empLogTotal.value = res.data.total
    }
  } finally { empLogLoading.value = false }
}

// 加載商城日誌 
const loadProductLogData = async () => {
  productLogLoading.value = true
  try {
    const res = await request.get('/productLog', { params: productLogParams })
    if (res.code === 1) {
      productLogData.value = res.data.rows
      productLogTotal.value = res.data.total
    }
  } finally { productLogLoading.value = false }
}

// 切換 Tab 時加載對應數據
const handleTabClick = (tab) => {
  if (tab.props.name === 'operate') {
    loadOperateData()
  } else if (tab.props.name === 'emp') {
    loadEmpLogData()
  } else if (tab.props.name === 'product') {
    loadProductLogData()
  }
}

// 分頁事件
const handleOperateChange = (page) => { operateParams.page = page; loadOperateData() }
const handleEmpLogChange = (page) => { empLogParams.page = page; loadEmpLogData() }
const handleProductLogChange = (page) => { productLogParams.page = page; loadProductLogData() } // ✅ 新增

// 開啟詳情彈窗
const showDetail = (row) => {
  currentLog.value = row
  detailVisible.value = true
}

onMounted(() => {
  loadOperateData()
})
</script>

<template>
  <div style="padding: 20px;">
    <div style="margin-bottom: 20px;">
      <h2 style="margin:0;">📜 日誌管理中心</h2>
      <span style="color:#999; font-size:14px;">監控系統運行狀況、員工操作與商城變更紀錄</span>
    </div>

    <el-card shadow="never">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        
        <el-tab-pane label="系統操作日誌" name="operate">
          <el-table :data="operateData" stripe v-loading="operateLoading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column label="操作人" width="120">
              <template #default="scope">
                <el-tag effect="plain">{{ scope.row.operateEmpName || '系統/未知' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="執行方法 (Class.Method)" min-width="200">
              <template #default="scope">
                <div style="font-weight:bold; font-size:12px;">{{ scope.row.className }}</div>
                <div style="color:#666; font-size:12px;">{{ scope.row.methodName }}</div>
              </template>
            </el-table-column>
            <el-table-column label="耗時" width="100">
              <template #default="scope">
                <span :style="{color: scope.row.costTime > 1000 ? 'red' : 'green'}">
                  {{ scope.row.costTime }}ms
                </span>
              </template>
            </el-table-column>
            <el-table-column label="時間" width="180">
              <template #default="scope">{{ scope.row.operateTime?.replace('T', ' ') }}</template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right" align="center">
              <template #default="scope">
                <el-button link type="primary" :icon="View" @click="showDetail(scope.row)">詳情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div style="display:flex; justify-content:flex-end; margin-top:15px;">
            <el-pagination layout="prev, pager, next" :total="operateTotal" 
              v-model:current-page="operateParams.page" @current-change="handleOperateChange"/>
          </div>
        </el-tab-pane>

        <el-tab-pane label="員工業務日誌" name="emp">
          <el-table :data="empLogData" stripe v-loading="empLogLoading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column label="員工" width="150">
              <template #default="scope"><span style="font-weight:bold;">{{ scope.row.empName }}</span></template>
            </el-table-column>
            <el-table-column label="日誌內容" prop="info">
               <template #default="scope">{{ scope.row.info }}</template>
            </el-table-column>
            <el-table-column label="時間" width="180">
              <template #default="scope">{{ scope.row.operateTime?.replace('T', ' ') }}</template>
            </el-table-column>
          </el-table>
          <div style="display:flex; justify-content:flex-end; margin-top:15px;">
            <el-pagination layout="prev, pager, next" :total="empLogTotal" 
              v-model:current-page="empLogParams.page" @current-change="handleEmpLogChange"/>
          </div>
        </el-tab-pane>

        <el-tab-pane label="商城業務日誌" name="product">
          <el-table :data="productLogData" stripe v-loading="productLogLoading">
            <el-table-column prop="id" label="ID" width="80" />

            <el-table-column label="操作人" width="150">
              <template #default="scope">
                <el-tag effect="light" round>
                   <el-icon><User /></el-icon> {{ scope.row.operateName || '未知' }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column label="日誌內容" prop="content">
               <template #default="scope">
                 <div style="display: flex; align-items: center;">
                   <el-icon style="margin-right: 5px; color:#E6A23C"><Goods /></el-icon>
                   <span style="font-weight: 500;">{{ scope.row.content }}</span>
                 </div>
               </template>
            </el-table-column>

            <el-table-column label="操作時間" width="180">
              <template #default="scope">
                 <span style="color: #666;">
                   {{ scope.row.operateTime ? scope.row.operateTime.replace('T', ' ') : '' }}
                 </span>
              </template>
            </el-table-column>
          </el-table>

          <div style="display:flex; justify-content:flex-end; margin-top:15px;">
            <el-pagination layout="prev, pager, next" :total="productLogTotal" 
              v-model:current-page="productLogParams.page" @current-change="handleProductLogChange"/>
          </div>
        </el-tab-pane>

      </el-tabs>
    </el-card>

    <el-dialog v-model="detailVisible" title="📜 日誌詳情" width="700px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="操作人">
          <el-tag>{{ currentLog.operateEmpName || '未知' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="執行類名">{{ currentLog.className }}</el-descriptions-item>
        <el-descriptions-item label="執行方法">{{ currentLog.methodName }}</el-descriptions-item>
        <el-descriptions-item label="請求參數">
          <pre class="json-content">{{ currentLog.methodParams }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="返回值">
          <pre class="json-content">{{ currentLog.returnValue }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="執行耗時">
          <span :style="{color: currentLog.costTime > 1000 ? 'red' : 'green', fontWeight:'bold'}">
            {{ currentLog.costTime }} ms
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="操作時間">{{ currentLog.operateTime?.replace('T', ' ') }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">關閉</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped>
.json-content {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: Consolas, Monaco, 'Andale Mono', monospace;
  font-size: 13px;
  color: #444;
  max-height: 300px;
  overflow-y: auto;
}
</style>