<script setup>
import { ref, onMounted, computed } from 'vue'
import { Plus, Edit, Delete, Upload, Picture as IconPicture } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// --- 數據定義 ---
const loading = ref(false)
const tableData = ref([])
const searchQuery = ref('')
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
  category: 1, // 預設 3C
  status: 1,   // 預設上架
  stock: 50    // ✅ 新增：預設庫存 50
})

// 分類映射 (顯示用)
const categoryMap = {
  1: '📱 3C 數碼',
  2: '📎 辦公用品',
  3: '🥤 食品飲料',
  4: '🎫 電子票券'
}

// 表單驗證規則
const rules = {
  name: [{ required: true, message: '請輸入商品名稱', trigger: 'blur' }],
  pointsNeeded: [{ required: true, message: '請輸入所需點數', trigger: 'blur' }],
  category: [{ required: true, message: '請選擇分類', trigger: 'change' }],
  image: [{ required: true, message: '請輸入圖片網址', trigger: 'blur' }],
  stock: [{ required: true, message: '請輸入初始庫存', trigger: 'blur' }] // ✅ 新增：庫存驗證
}

// --- 方法 ---

// 1. 查詢列表
const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/products')
    if (res.code === 1) {
      tableData.value = res.data
    }
  } finally {
    loading.value = false
  }
}

// 前端搜尋過濾
const filteredData = computed(() => {
  if (!searchQuery.value) return tableData.value
  return tableData.value.filter(item => item.name.includes(searchQuery.value))
})

// 2. 開啟新增彈窗
const handleAdd = () => {
  dialogTitle.value = '新增商品'
  // ✅ 確保重置時包含 stock
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
  // 深拷貝，避免修改表單時直接影響表格顯示
  formData.value = JSON.parse(JSON.stringify(row))
  dialogVisible.value = true
}

// 4. 提交表單 (新增或修改)
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
        console.error(error)
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
      <el-input 
        v-model="searchQuery" 
        placeholder="搜尋商品名稱..." 
        style="width: 250px; margin-left: 15px;" 
        clearable 
      />
    </div>

    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="filteredData" v-loading="loading" stripe style="width: 100%">
        
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
            <el-option label="📱 3C 數碼" :value="1" />
            <el-option label="📎 辦公用品" :value="2" />
            <el-option label="🥤 食品飲料" :value="3" />
            <el-option label="🎫 電子票券" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="圖片連結" prop="image">
          <el-input v-model="formData.image" placeholder="請輸入圖片 URL">
            <template #prefix><el-icon><Upload /></el-icon></template>
          </el-input>
          <div v-if="formData.image" style="margin-top: 10px;">
            <img :src="formData.image" style="height: 100px; border-radius: 4px; border: 1px solid #eee;" />
          </div>
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
.manage-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
}

.header-actions {
  display: flex;
  align-items: center;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}
</style>