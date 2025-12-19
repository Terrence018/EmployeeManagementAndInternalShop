<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Delete, Edit, Refresh, Upload, User, Suitcase, Phone, Lock, Message } from '@element-plus/icons-vue' // ✅ 引入 Message 圖標
import request from '@/utils/request'
import { getEmpPage, addEmp, updateEmp, getEmpById, deleteEmp } from '@/api/emp.js'

// --- 1. 搜尋與分頁數據 ---
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageParams = reactive({
  page: 1,
  pageSize: 10,
  name: '',
  gender: '',
  begin: '', 
  end: ''    
})
const dateRange = ref([])

// 部門列表 
const deptList = ref([])

// --- 2. 彈窗與表單數據 ---
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const formData = ref({
  id: null,
  username: '',
  password: '', 
  name: '',
  gender: '',
  image: '',
  phone: '',
  email: '', // ✅ 新增：初始化 email 欄位
  job: '',
  salary: null,
  entryDate: '',
  deptId: '',
  exprList: [] 
})

// 密碼驗證規則
const validatePass = (rule, value, callback) => {
  if (!formData.value.id && !value) {
    callback(new Error('新增員工時，密碼為必填項'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '請輸入帳號', trigger: 'blur' }],
  password: [{ required:true, validator: validatePass, trigger: 'blur' }],
  name: [{ required: true, message: '請輸入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '請選擇性別', trigger: 'change' }],
  phone: [{ required: true, message: '請輸入手機號碼', trigger: 'blur' }],
  // ✅ 新增：Email 驗證規則 (必填 + 格式檢查)
  email: [
    { required: true, message: '請輸入電子信箱', trigger: 'blur' },
    { type: 'email', message: '請輸入正確的信箱格式', trigger: 'blur' }
  ],
  entryDate: [{ required: true, message: '請選擇入職日期', trigger: 'change' }],
  deptId: [{ required: true, message: '請選擇部門', trigger: 'change' }]
}

const selectedIds = ref([])

// --- 方法區 ---

const getDeptList = async () => {
  const res = await request.get('/dept') 
  if (res.code === 1) {
    deptList.value = res.data
  }
}

const loadData = async () => {
  loading.value = true
  try {
    if (dateRange.value && dateRange.value.length === 2) {
      pageParams.begin = dateRange.value[0]
      pageParams.end = dateRange.value[1]
    } else {
      pageParams.begin = ''
      pageParams.end = ''
    }

    const res = await getEmpPage(pageParams)
    
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

const handleReset = () => {
  pageParams.name = ''
  pageParams.gender = ''
  dateRange.value = []
  handleSearch()
}

const handleSizeChange = (val) => {
  pageParams.pageSize = val
  loadData()
}
const handleCurrentChange = (val) => {
  pageParams.page = val
  loadData()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// --- 新增 / 編輯 邏輯 ---

const openDialog = async (row = null) => {
  dialogVisible.value = true
  dialogTitle.value = row ? '編輯員工' : '新增員工'

  await nextTick()
  formRef.value?.clearValidate() 

  if (row) {
    // --- 編輯模式 ---
    const res = await getEmpById(row.id)
    if (res.code === 1) {
      formData.value = res.data
      if (!formData.value.exprList) formData.value.exprList = []
    }
  } else {
    // --- 新增模式 ---
    formData.value = {
      id: null,
      username: '',
      password: '',
      name: '',
      gender: '',
      image: '',
      phone: '',
      email: '', // ✅ 初始化為空
      job: '',
      salary: null,
      entryDate: '',
      deptId: '',
      exprList: [] 
    }
  }
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        const submitData = { ...formData.value }

        if (submitData.id && !submitData.password) {
           delete submitData.password
        }

        if (submitData.id) {
          res = await updateEmp(submitData)
        } else {
          res = await addEmp(submitData)
        }

        if (res.code === 1) {
          ElMessage.success('操作成功')
          dialogVisible.value = false
          loadData()
        } else {
          ElMessage.error(res.msg || '操作失敗')
        }
      } catch (e) {
        console.error(e)
        ElMessage.error('系統錯誤')
      }
    }
  })
}

const addExprItem = () => {
  formData.value.exprList.push({
    begin: '',
    end: '',
    company: '',
    job: ''
  })
}

const removeExprItem = (index) => {
  formData.value.exprList.splice(index, 1)
}

// --- 刪除邏輯 ---

const handleDelete = (ids) => {
  ElMessageBox.confirm('確認要刪除選中的員工數據嗎？', '警告', {
    confirmButtonText: '確定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const idsStr = ids.join(',')
    const res = await deleteEmp(idsStr)
    
    if (res.code === 1) {
      ElMessage.success('刪除成功')
      loadData()
    } else {
      ElMessage.error(res.msg || '刪除失敗')
    }
  }).catch(() => {})
}

onMounted(() => {
  getDeptList()
  loadData()
})
</script>

<template>
  <div class="emp-container">
    
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="pageParams" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="pageParams.name" placeholder="輸入員工姓名" clearable />
        </el-form-item>
        
        <el-form-item label="性別">
          <el-select v-model="pageParams.gender" placeholder="選擇性別" clearable style="width: 120px;">
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="入職日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="開始日期"
            end-placeholder="結束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查詢</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" style="margin-top: 15px;">
      <div class="table-toolbar">
        <el-button type="primary" :icon="Plus" @click="openDialog(null)">新增員工</el-button>
        <el-button 
          type="danger" 
          :icon="Delete" 
          :disabled="selectedIds.length === 0"
          @click="handleDelete(selectedIds)"
        >
          批量刪除
        </el-button>
      </div>

      <el-table 
        :data="tableData" 
        stripe 
        style="width: 100%; margin-top: 15px;" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        
        <el-table-column label="姓名" width="150">
          <template #default="scope">
            <div style="display: flex; align-items: center;">
              <el-avatar shape="square" :size="40" :src="scope.row.image" v-if="scope.row.image" />
              <el-avatar shape="square" :size="40" :icon="User" v-else />
              <span style="margin-left: 10px; font-weight: bold;">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="性別" width="60" align="center">
          <template #default="scope">
            <span v-if="scope.row.gender === 1">男</span>
            <span v-else-if="scope.row.gender === 2">女</span>
            <span v-else>未知</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="email" label="電子信箱" width="180" show-overflow-tooltip />

        <el-table-column prop="job" label="職位" width="100" />
        
        <el-table-column label="部門" width="100">
          <template #default="scope">
             {{ deptList.find(d => d.id == scope.row.deptId)?.name || '未分配' }}
          </template>
        </el-table-column>

        <el-table-column prop="entryDate" label="入職日期" width="110" sortable />
        
        <el-table-column prop="updateTime" label="最後修改時間" width="170" sortable>
           <template #default="scope">
             {{ scope.row.updateTime ? scope.row.updateTime.replace('T', ' ') : '' }}
           </template>
        </el-table-column>

        <el-table-column label="操作" min-width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" :icon="Edit" @click="openDialog(scope.row)">編輯</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete([scope.row.id])">刪除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageParams.page"
          v-model:page-size="pageParams.pageSize"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="700px" 
      top="5vh"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="80px" label-position="right">
        
        <div class="section-title">
          <el-icon><User /></el-icon> 基本資訊
        </div>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="帳號" prop="username">
              <el-input v-model="formData.username" placeholder="登入帳號" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密碼" prop="password">
              <el-input 
                v-model="formData.password" 
                type="password" 
                show-password 
                :placeholder="formData.id ? '若不修改請留空' : '請設定登入密碼'"
              >
                 <template #prefix><el-icon><Lock /></el-icon></template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" placeholder="員工姓名" />
            </el-form-item>
          </el-col>
           <el-col :span="12">
            <el-form-item label="性別" prop="gender">
              <el-select v-model="formData.gender" placeholder="請選擇" style="width: 100%;">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="信箱" prop="email">
              <el-input v-model="formData.email" placeholder="請輸入電子信箱 (用於找回密碼)">
                 <template #prefix><el-icon><Message /></el-icon></template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手機" prop="phone">
               <el-input v-model="formData.phone" placeholder="請輸入手機號碼">
                 <template #prefix><el-icon><Phone /></el-icon></template>
               </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部門" prop="deptId">
              <el-select v-model="formData.deptId" placeholder="請選擇部門" style="width: 100%;">
                <el-option v-for="dept in deptList" :key="dept.id" :label="dept.name" :value="dept.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="職位" prop="job">
              <el-input v-model="formData.job" placeholder="例如: 工程師" />
            </el-form-item>
          </el-col>
           <el-col :span="12">
            <el-form-item label="入職日期" prop="entryDate">
              <el-date-picker 
                v-model="formData.entryDate" 
                type="date" 
                value-format="YYYY-MM-DD" 
                placeholder="選擇日期"
                style="width: 100%;" 
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="薪資">
               <el-input-number 
                 v-model="formData.salary" 
                 :min="0" 
                 controls-position="right"
                 style="width: 100%;" 
                 placeholder="輸入薪資"
               />
            </el-form-item>
          </el-col>
          <el-col :span="12">
             <el-form-item label="頭像URL">
              <div style="display: flex; width: 100%; align-items: flex-start;">
                <el-input v-model="formData.image" placeholder="圖片連結" style="flex: 1; margin-right: 5px;">
                  <template #prefix><el-icon><Upload /></el-icon></template>
                </el-input>
                <div class="avatar-preview">
                  <img v-if="formData.image" :src="formData.image" />
                  <el-icon v-else class="avatar-placeholder-icon"><User /></el-icon>
                </div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="section-title" style="margin-top: 20px;">
          <el-icon><Suitcase /></el-icon> 工作經歷
          <el-button type="primary" link :icon="Plus" @click="addExprItem" style="float: right;">添加經歷</el-button>
        </div>

        <el-table :data="formData.exprList" border style="width: 100%" size="small">
          <el-table-column label="開始日期" width="150">
            <template #default="scope">
              <el-date-picker v-model="scope.row.begin" type="date" value-format="YYYY-MM-DD" style="width: 100%;" placeholder="開始" />
            </template>
          </el-table-column>
          
          <el-table-column label="結束日期" width="150">
            <template #default="scope">
              <el-date-picker v-model="scope.row.end" type="date" value-format="YYYY-MM-DD" style="width: 100%;" placeholder="結束" />
            </template>
          </el-table-column>
          
          <el-table-column label="公司名稱">
            <template #default="scope">
              <el-input v-model="scope.row.company" placeholder="公司名稱" />
            </template>
          </el-table-column>
          
          <el-table-column label="職位">
            <template #default="scope">
              <el-input v-model="scope.row.job" placeholder="擔任職位" />
            </template>
          </el-table-column>

          <el-table-column label="操作" width="50" align="center">
            <template #default="scope">
              <el-button type="danger" link :icon="Delete" @click="removeExprItem(scope.$index)"></el-button>
            </template>
          </el-table-column>
        </el-table>

      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">確認保存</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped>
.emp-container { padding: 20px; background-color: #f5f7fa; min-height: 100vh; }
.search-card { margin-bottom: 20px; }
.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
.section-title { font-size: 16px; font-weight: bold; color: #303133; border-left: 4px solid #409EFF; padding-left: 10px; margin-bottom: 15px; display: flex; align-items: center; justify-content: space-between; }
.avatar-preview { width: 40px; height: 40px; border-radius: 4px; background-color: #f0f2f5; display: flex; align-items: center; justify-content: center; overflow: hidden; border: 1px solid #dcdfe6; flex-shrink: 0; }
.avatar-preview img { width: 100%; height: 100%; object-fit: cover; }
.avatar-placeholder-icon { font-size: 20px; color: #909399; }
</style>