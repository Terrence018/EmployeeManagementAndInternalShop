import request from '@/utils/request'

/**
 * 員工登入
 * @param {Object} data - { username, password }
 * @returns Promise
 */
export const loginApi = (data) => {
  return request.post('/login', data)
}