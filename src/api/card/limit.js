import request from '@/utils/request'

// 查询禁限卡列表
export function listLimit(query) {
  return request({
    url: '/card/limit/list',
    method: 'get',
    params: query
  })
}

// 查询禁限卡详细
export function getLimit(limitId) {
  return request({
    url: '/card/limit/' + limitId,
    method: 'get'
  })
}

// 新增禁限卡
export function addLimit(data) {
  return request({
    url: '/card/limit',
    method: 'post',
    data: data
  })
}

// 修改禁限卡
export function updateLimit(data) {
  return request({
    url: '/card/limit',
    method: 'put',
    data: data
  })
}

// 删除禁限卡
export function delLimit(limitId) {
  return request({
    url: '/card/limit/' + limitId,
    method: 'delete'
  })
}
