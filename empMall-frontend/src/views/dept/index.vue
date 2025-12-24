<script setup>
  import { ref,onMounted } from 'vue';
  import {queryAllApi,addApi,queryByIdApi,updateApi,deleteByIdApi} from '@/api/dept';
  import { ElMessage,ElMessageBox } from 'element-plus';

  
  //聲明鉤子函數
  onMounted(()=>{
    search();
  })
  
  const deptList = ref([])
  
  //查詢
  const search = async () =>{
    // const result = await axios.get('https://m1.apifoxmock.com/m1/7562333-7299876-default/dept')
    // if(result.data.code == 1){ //JS隱式類型轉換，所以==1可省略。0 - false、其他數字 - true 、undefined - false
    //   deptList.value = result.data.data;
    // }

    const result = await queryAllApi();
    if(result.code == 1){ //JS隱式類型轉換，所以==1可省略。0 - false、其他數字 - true 、undefined - false
      deptList.value = result.data;
    }
  }

  //Dialog對話框
  const dialogFormVisible = ref(false);
  const formTitle = ref('');
  const dept = ref({name:''});
  const deptFormRef = ref();
   
  //新增部門
  const addDept = () =>{
    dialogFormVisible.value = true;
    formTitle.value = '新增部門';
    dept.value = {name:''};

    //重置表單的效驗規則-提示信息(輸入框下方處紅字)
    if(deptFormRef.value){
      deptFormRef.value.resetFields();
    }
  }
  
  //保存部門
  const save = async () =>{ 
      //表單效驗
      if(!deptFormRef.value) return;
      deptFormRef.value.validate(async (valid) =>{ //valid 表示是否校驗通過： true - 驗證通過、false - 驗證未通過
        if(valid){ 

          let result ;
          if(dept.value.id){ //修改操作
            result = await updateApi(dept.value);
          }else{ //新增操作
            result = await addApi(dept.value);
          }

          if(result.code == 1){  //成功
            //1.提示信息(操作成功) 
            ElMessage.success('操作成功');
            //2.關閉對話框
            dialogFormVisible.value = false
            //3.查詢最新數據
            search();
          }else{ //失敗
            ElMessage.error(result.msg);
          }
        }else{ //未通過效驗
          ElMessage.error('表單效驗不通過');
        }

      });



  }

  //表單效驗
  const rules = ref({
  name: [
    { required: true, message: '部門名稱必須填入', trigger: 'blur' },
    { min: 2, max: 10, message: '部門名稱必須在2到10個字之間', trigger: 'blur' },
  ] 
})

 //編輯部門
 const edit = async (id) =>{
  formTitle.value = '編輯部門';

  //重置表單的效驗規則-提示信息(輸入框下方處紅字)
    if(deptFormRef.value){
      deptFormRef.value.resetFields();
    }

  const result = await queryByIdApi(id);
  if(result.code ==1){ //JS隱式類型轉換，所以==1可省略。0 - false、其他數字 - true 、undefined - false
    dialogFormVisible.value = true;
    dept.value = result.data;
  }

 }

  //刪除部門
  const delById = async (id) =>{
    //彈出確認框
    ElMessageBox.confirm('您確定刪除該部門嗎?','提示',
    {confirmButtonText: '確定',cancelButtonText: '取消',type: 'warning',}
    ).then(async () => { //點擊確認，後調用的函數
      const result = await deleteByIdApi(id);
      if(result.code == 1){
        ElMessage.success('刪除成功');
        search();
      }else{
        ElMessage.error(result.msg);
      }
    }).catch(() => {  //點擊取消，後調用的函數
      ElMessage.info('取消刪除');
    })
  }

</script>

<template>
  <h1>部門管理</h1>
  <div class="container">
    <el-button type="primary" @click="addDept"> + 新增部門</el-button>
  </div>
  
  <!-- 表格 -->
  <div class="container">
    <el-table :data="deptList" border style="width: 100%">
      <el-table-column type="index" label="序號"  width="100" align="center"/>
      <el-table-column prop="name" label="部門名稱" width="260" align="center"/>
      <el-table-column prop="updateTime" label="最後操作時間" width="300" align="center" />
      <el-table-column prop="address" label="操作" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" @click="edit(scope.row.id)"><el-icon><EditPen /></el-icon>編輯</el-button>  
          <el-button type="danger" size="small" @click="delById(scope.row.id)"><el-icon><CloseBold /></el-icon>刪除</el-button>
        </template>
      </el-table-column>
      
    </el-table>
  </div>

  <!-- Dialog對話框 -->
  <el-dialog v-model="dialogFormVisible" :title="formTitle" width="500">
    <!-- {{ dept }} -->
    <el-form :model="dept" :rules="rules" ref="deptFormRef" >
      <el-form-item label="部門名稱" label-width="80px" prop="name">
        <el-input v-model="dept.name"/>
      </el-form-item>

    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="save">確認</el-button>
        <el-button @click="dialogFormVisible = false">取消</el-button>
      </div>
    </template>
  </el-dialog> 

</template>

<style scoped>
.container {
  margin: 10px 0px;
}
</style>
