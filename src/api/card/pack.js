import request from '@/utils/request'

// 查询用户卡包列表
export function listPack(query) {
  return request({
    url: '/card/pack/list',
    method: 'get',
    params: query
  })
}

// 查询用户卡包详细
export function getPack(packId) {
  return request({
    url: '/card/pack/' + packId,
    method: 'get'
  })
}

// 新增用户卡包
export function addPack(data) {
  return request({
    url: '/card/pack',
    method: 'post',
    data: data
  })
}

// 修改用户卡包
export function updatePack(data) {
  return request({
    url: '/card/pack',
    method: 'put',
    data: data
  })
}

// 删除用户卡包
export function delPack(packId) {
  return request({
    url: '/card/pack/' + packId,
    method: 'delete'
  })
}

// 查询卡包内卡片明细列表
export function listPackItem(query) {
  return request({
    url: '/card/pack/item/list',
    method: 'get',
    params: query
  })
}

// 向卡包添加卡片
export function addPackItem(data) {
  return request({
    url: '/card/pack/item',
    method: 'post',
    data: data
  })
}

// 修改卡包内卡片数量
export function updatePackItem(data) {
  return request({
    url: '/card/pack/item',
    method: 'put',
    data: data
  })
}

// 删除卡包内卡片
export function delPackItem(itemIds) {
  return request({
    url: '/card/pack/item/' + itemIds,
    method: 'delete'
  })
}
