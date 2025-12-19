import request from '@/utils/request'

//查詢全部部門數據
export const queryAllApi = () =>{
  return request.get('/dept')
}

//新增
export const addApi = (dept) =>{
  return request.post('/dept',dept)
}

//根據ID查詢部門
export const queryByIdApi = (id) =>{
  return request.get(`/dept/${id}`)
}

//修改
export const updateApi = (dept) =>{
  return request.put('/dept',dept)
}

//刪除
export const deleteByIdApi = (id) =>{
  return request.delete(`/dept/${id}`)
}
 
