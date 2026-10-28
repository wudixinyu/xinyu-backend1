import request from '@/utils/request'

// 查询Shopify店铺日报列表
export function listShopifyStore(query) {
  return request({
    url: '/data/shopify/list',
    method: 'get',
    params: query
  })
}

// 查询店铺日报详细
export function getShopifyStore(dailyId) {
  return request({
    url: '/data/shopify/' + dailyId,
    method: 'get'
  })
}

// 新增店铺日报
export function addShopifyStore(data) {
  return request({
    url: '/data/shopify',
    method: 'post',
    data: data
  })
}

// 修改店铺日报
export function updateShopifyStore(data) {
  return request({
    url: '/data/shopify',
    method: 'put',
    data: data
  })
}

// 删除店铺日报
export function delShopifyStore(dailyIds) {
  return request({
    url: '/data/shopify/' + dailyIds,
    method: 'delete'
  })
}

// 统计总览（指标卡片）
export function getStoreSummary(query) {
  return request({
    url: '/data/shopify/stats/summary',
    method: 'get',
    params: query
  })
}

// 按日期趋势统计（折线图）
export function getStoreTrend(query) {
  return request({
    url: '/data/shopify/stats/trend',
    method: 'get',
    params: query
  })
}

// 按销售渠道汇总统计（柱状对比/占比饼图）
export function getStoreChannel(query) {
  return request({
    url: '/data/shopify/stats/channel',
    method: 'get',
    params: query
  })
}
