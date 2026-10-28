import request from '@/utils/request'

// 全量同步卡片数据(cards.zip,耗时较长,单独放宽超时)
export function syncAllCards(force) {
  return request({
    url: '/card/sync/cards',
    method: 'post',
    params: { force: force },
    timeout: 600000
  })
}

// 按需补录单张卡片
export function syncSingleCard(cardId) {
  return request({
    url: '/card/sync/card/' + cardId,
    method: 'post'
  })
}

// 收集卡组系列
export function syncCardSets() {
  return request({
    url: '/card/sync/sets',
    method: 'post',
    timeout: 120000
  })
}

// 同步禁限卡表
export function syncLimitLists() {
  return request({
    url: '/card/sync/limits',
    method: 'post',
    timeout: 120000
  })
}
