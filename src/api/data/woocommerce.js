import request from '@/utils/request'

// 查询WooCommerce店铺日报列表
export function listWooCommerceStore(query) {
  return request({
    url: '/data/woocommerce/list',
    method: 'get',
    params: query
  })
}

// 查询店铺日报详细
export function getWooCommerceStore(dailyId) {
  return request({
    url: '/data/woocommerce/' + dailyId,
    method: 'get'
  })
}

// 新增店铺日报
export function addWooCommerceStore(data) {
  return request({
    url: '/data/woocommerce',
    method: 'post',
    data: data
  })
}

// 修改店铺日报
export function updateWooCommerceStore(data) {
  return request({
    url: '/data/woocommerce',
    method: 'put',
    data: data
  })
}

// 删除店铺日报
export function delWooCommerceStore(dailyIds) {
  return request({
    url: '/data/woocommerce/' + dailyIds,
    method: 'delete'
  })
}

// 统计总览（指标卡片）
export function getStoreSummary(query) {
  return request({
    url: '/data/woocommerce/stats/summary',
    method: 'get',
    params: query
  })
}

// 按日期趋势统计（折线图）
export function getStoreTrend(query) {
  return request({
    url: '/data/woocommerce/stats/trend',
    method: 'get',
    params: query
  })
}

// 按销售渠道汇总统计（柱状对比/占比饼图）
export function getStoreChannel(query) {
  return request({
    url: '/data/woocommerce/stats/channel',
    method: 'get',
    params: query
  })
}
