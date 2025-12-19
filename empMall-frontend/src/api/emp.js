import request from '@/utils/request'

// 1. 分頁查詢員工
export function getEmpPage(params) {
  return request.get('/emps', { params })
}

// 2. 新增員工
export function addEmp(data) {
  return request.post('/emps', data)
}

// 3. 修改員工
export function updateEmp(data) {
  return request.put('/emps', data)
}

// 4. 根據ID查詢員工 (詳情)
export function getEmpById(id) {
  return request.get(`/emps/${id}`)
}

// 5. 刪除員工 (批量)
// 注意：這裡接收的是已經逗號分隔的字串，或者我們可以在這裡處理 join
export function deleteEmp(ids) {
  return request.delete('/emps', { params: { ids } })
}