import request from '@/utils/request'

// 查询卡组系列列表
export function listSet(query) {
  return request({
    url: '/card/set/list',
    method: 'get',
    params: query
  })
}

// 查询卡组系列详细
export function getSet(setId) {
  return request({
    url: '/card/set/' + setId,
    method: 'get'
  })
}

// 新增卡组系列
export function addSet(data) {
  return request({
    url: '/card/set',
    method: 'post',
    data: data
  })
}

// 修改卡组系列
export function updateSet(data) {
  return request({
    url: '/card/set',
    method: 'put',
    data: data
  })
}

// 删除卡组系列
export function delSet(setId) {
  return request({
    url: '/card/set/' + setId,
    method: 'delete'
  })
}
