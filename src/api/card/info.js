import request from '@/utils/request'

// 查询卡片信息列表
export function listInfo(query) {
  return request({
    url: '/card/info/list',
    method: 'get',
    params: query
  })
}

// 查询卡片信息详细
export function getInfo(cardId) {
  return request({
    url: '/card/info/' + cardId,
    method: 'get'
  })
}

// 新增卡片信息
export function addInfo(data) {
  return request({
    url: '/card/info',
    method: 'post',
    data: data
  })
}

// 修改卡片信息
export function updateInfo(data) {
  return request({
    url: '/card/info',
    method: 'put',
    data: data
  })
}

// 删除卡片信息
export function delInfo(cardId) {
  return request({
    url: '/card/info/' + cardId,
    method: 'delete'
  })
}
