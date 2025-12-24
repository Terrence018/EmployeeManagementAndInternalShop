<script setup>
import { ref, onMounted } from 'vue' 
import { Plus, Edit, Delete, Picture as IconPicture, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// --- 數據定義 ---
const loading = ref(false)
const tableData = ref([])

// 篩選條件
const searchQuery = ref('') // 搜尋關鍵字
const selectedCategory = ref('') // 分類篩選變數

// 分頁變數
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 彈窗控制
const dialogVisible = ref(false)
const dialogTitle = ref('新增商品')

// 表單數據
const formRef = ref(null)
const formData = ref({
  id: null,
  name: '',
  image: '',
  description: '',
  pointsNeeded: 100,
  category: 1, 
  status: 1,   
  stock: 50    
})

// 分類選項 (給上方篩選下拉選單用)
const categoryOptions = [
  { label: '全部分類', value: '' }, // 空字串代表查全部
  { label: '3C 數碼', value: 1 },
  { label: '辦公用品', value: 2 },
  { label: '食品飲料', value: 3 },
  { label: '電子票券', value: 4 },
  { label: '生活百貨', value: 5 },
  { label: '圖書雜誌', value: 6 },
]

// 分類映射 (給表格顯示用)
const categoryMap = {
  1: '3C 數碼',
  2: '辦公用品',
  3: '食品飲料',
  4: '電子票券',
  5: '生活百貨',
  6: '圖書雜誌'
}

// AWS 上傳設定
const uploadAction = 'http://localhost:8080/upload' 
const uploadHeaders = {
  token: localStorage.getItem('token') 
}

const handleUploadSuccess = (response) => {
  if (response.code === 1) {
    formData.value.image = response.data 
    ElMessage.success('圖片上傳成功！')
  } else {
    ElMessage.error(response.msg || '上傳失敗')
  }
}

const beforeUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('圖片必須是 JPG 或 PNG 格式!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 5) {
    ElMessage.error('圖片大小不能超過 5MB!')
    return false
  }
  return true
}

const rules = {
  name: [{ required: true, message: '請輸入商品名稱', trigger: 'blur' }],
  pointsNeeded: [{ required: true, message: '請輸入所需點數', trigger: 'blur' }],
  category: [{ required: true, message: '請選擇分類', trigger: 'change' }],
  image: [{ required: true, message: '請上傳商品圖片', trigger: 'change' }], 
  stock: [{ required: true, message: '請輸入初始庫存', trigger: 'blur' }]
}

//方法

// 1. 查詢列表 
const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/products', {
      params: {
        page: currentPage.value,
        pageSize: pageSize.value,
        keyword: searchQuery.value || null, // 傳送關鍵字
        category: selectedCategory.value || null // ✅ 傳送分類 ID
      }
    })
    
    if (res.code === 1) {
      tableData.value = res.data.items 
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

// 當篩選條件改變時 (分類切換 / 搜尋輸入)
const handleFilterChange = () => {
  currentPage.value = 1 // 重置回第一頁
  getList()
}

// 換頁事件
const handlePageChange = (page) => {
  currentPage.value = page
  getList()
}

// 2. 開啟新增彈窗
const handleAdd = () => {
  dialogTitle.value = '新增商品'
  formData.value = { 
    id: null, 
    name: '', 
    image: '', 
    description: '', 
    pointsNeeded: 100, 
    category: 1, 
    status: 1,
    stock: 50 
  }
  dialogVisible.value = true
}

// 3. 開啟編輯彈窗
const handleEdit = (row) => {
  dialogTitle.value = '編輯商品'
  formData.value = JSON.parse(JSON.stringify(row))
  dialogVisible.value = true
}

// 4. 提交表單
const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res;
        if (formData.value.id) {
          res = await request.put('/products', formData.value)
        } else {
          res = await request.post('/products', formData.value)
        }

        if (res.code === 1) {
          ElMessage.success(formData.value.id ? '修改成功' : '新增成功')
          dialogVisible.value = false
          getList() 
        } else {
          ElMessage.error(res.msg || '操作失敗')
        }
      } catch (error) {
        ElMessage.error('系統錯誤')
      }
    }
  })
}

// 5. 刪除商品
const handleDelete = (id) => {
  ElMessageBox.confirm('確定要刪除此商品嗎？', '警告', {
    confirmButtonText: '確定刪除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await request.delete(`/products/${id}`)
    if (res.code === 1) {
      ElMessage.success('刪除成功')
      getList()
    } else {
      ElMessage.error(res.msg || '刪除失敗')
    }
  })
}

// 6. 切換上下架狀態
const handleStatusChange = async (row) => {
  try {
    const res = await request.put('/products', row)
    if (res.code === 1) {
      ElMessage.success(row.status === 1 ? '已上架' : '已下架')
    } else {
      row.status = row.status === 1 ? 0 : 1 
      ElMessage.error('狀態更新失敗')
    }
  } catch (e) {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('網絡錯誤')
  }
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="manage-container">
    <div class="header-actions">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增商品</el-button>
      
      <div style="display: flex; gap: 15px; margin-left: 20px;">
        
        <el-select 
          v-model="selectedCategory" 
          placeholder="篩選分類" 
          style="width: 150px;"
          clearable
          @change="handleFilterChange"
          @clear="handleFilterChange"
        >
          <el-option
            v-for="opt in categoryOptions"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </el-select>

        <div style="display: flex; gap: 10px;">
          <el-input 
            v-model="searchQuery" 
            placeholder="搜尋商品名稱..." 
            style="width: 200px;" 
            clearable 
            @clear="handleFilterChange"
            @keyup.enter="handleFilterChange"
          />
          <el-button :icon="Search" circle @click="handleFilterChange" />
        </div>
      </div>
    </div>

    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        
        <el-table-column prop="id" label="ID" width="70" align="center" />
        
        <el-table-column label="圖片" width="100" align="center">
          <template #default="scope">
            <el-image 
              style="width: 60px; height: 60px; border-radius: 4px;" 
              :src="scope.row.image" 
              :preview-src-list="[scope.row.image]"
              fit="cover"
              preview-teleported
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><IconPicture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>

        <el-table-column prop="name" label="商品名稱" min-width="150" />
        
        <el-table-column label="分類" width="120">
          <template #default="scope">
            <el-tag effect="plain">{{ categoryMap[scope.row.category] || '未知' }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="所需點數" width="110" sortable prop="pointsNeeded">
          <template #default="scope">
            <span style="color: #E6A23C; font-weight: bold;">{{ scope.row.pointsNeeded }}</span>
          </template>
        </el-table-column>

        <el-table-column label="庫存" width="100" align="center" prop="stock" sortable>
          <template #default="scope">
            <el-tag :type="scope.row.stock <= 5 ? 'danger' : (scope.row.stock <= 20 ? 'warning' : 'success')">
              {{ scope.row.stock }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="狀態" width="90" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>

        <el-table-column label="操作" width="160" align="center">
          <template #default="scope">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">編輯</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(scope.row.id)">刪除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="display: flex; justify-content: flex-end; margin-top: 20px;">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>

    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="商品名稱" prop="name">
          <el-input v-model="formData.name" placeholder="請輸入商品名稱" />
        </el-form-item>
        <el-form-item label="當前庫存" prop="stock">
          <el-input-number v-model="formData.stock" :min="0" :precision="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="所需點數" prop="pointsNeeded">
          <el-input-number v-model="formData.pointsNeeded" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品分類" prop="category">
          <el-select v-model="formData.category" placeholder="請選擇分類" style="width: 100%;">
            <el-option label="3C 數碼" :value="1" />
            <el-option label="辦公用品" :value="2" />
            <el-option label="食品飲料" :value="3" />
            <el-option label="電子票券" :value="4" />
            <el-option label="生活百貨" :value="5" />
            <el-option label="圖書雜誌" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品圖片" prop="image">
          <el-upload
            class="avatar-uploader"
            :action="uploadAction"
            name="image" 
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="formData.image" :src="formData.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="formData.description" type="textarea" rows="3" />
        </el-form-item>
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
/* 樣式保持不變 */
.manage-container { padding: 20px; background-color: #fff; border-radius: 8px; }
.header-actions { display: flex; align-items: center; }
.image-slot { display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; background: #f5f7fa; color: #909399; }
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}
.avatar-uploader .el-upload:hover { border-color: var(--el-color-primary); }
.avatar-uploader-icon { font-size: 28px; color: #8c939d; width: 150px; height: 150px; text-align: center; line-height: 150px; }
.avatar { width: 150px; height: 150px; display: block; object-fit: cover; }
</style>