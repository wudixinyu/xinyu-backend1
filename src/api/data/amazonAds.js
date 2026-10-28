import request from '@/utils/request'

// 查询亚马逊广告日报列表
export function listAmazonAds(query) {
  return request({
    url: '/data/amazonAds/list',
    method: 'get',
    params: query
  })
}

// 查询广告日报详细
export function getAmazonAds(dailyId) {
  return request({
    url: '/data/amazonAds/' + dailyId,
    method: 'get'
  })
}

// 新增广告日报
export function addAmazonAds(data) {
  return request({
    url: '/data/amazonAds',
    method: 'post',
    data: data
  })
}

// 修改广告日报
export function updateAmazonAds(data) {
  return request({
    url: '/data/amazonAds',
    method: 'put',
    data: data
  })
}

// 删除广告日报
export function delAmazonAds(dailyIds) {
  return request({
    url: '/data/amazonAds/' + dailyIds,
    method: 'delete'
  })
}

// 统计总览（指标卡片）
export function getAdsSummary(query) {
  return request({
    url: '/data/amazonAds/stats/summary',
    method: 'get',
    params: query
  })
}

// 按日期趋势统计（折线图）
export function getAdsTrend(query) {
  return request({
    url: '/data/amazonAds/stats/trend',
    method: 'get',
    params: query
  })
}

// 按广告活动汇总统计（柱状对比/占比饼图）
export function getAdsCampaign(query) {
  return request({
    url: '/data/amazonAds/stats/campaign',
    method: 'get',
    params: query
  })
}
