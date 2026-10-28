import request from '@/utils/request'

// 查询用户收藏列表
export function listCollection(query) {
  return request({
    url: '/card/collection/list',
    method: 'get',
    params: query
  })
}

// 查询用户收藏详细
export function getCollection(collectId) {
  return request({
    url: '/card/collection/' + collectId,
    method: 'get'
  })
}

// 删除用户收藏
export function delCollection(collectId) {
  return request({
    url: '/card/collection/' + collectId,
    method: 'delete'
  })
}

// 切换当前用户对某卡片的收藏状态(返回 true=已收藏 false=已取消)
export function toggleCollection(cardId) {
  return request({
    url: '/card/collection/mine/toggle',
    method: 'post',
    data: { cardId: cardId }
  })
}

// 查询当前用户收藏的卡片密码集合
export function listMyCollectIds() {
  return request({
    url: '/card/collection/mine/ids',
    method: 'get'
  })
}
