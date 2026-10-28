import request from '@/utils/request'

// 查询Facebook广告日报列表
export function listFacebookAds(query) {
  return request({
    url: '/data/facebookAds/list',
    method: 'get',
    params: query
  })
}

// 查询广告日报详细
export function getFacebookAds(dailyId) {
  return request({
    url: '/data/facebookAds/' + dailyId,
    method: 'get'
  })
}

// 新增广告日报
export function addFacebookAds(data) {
  return request({
    url: '/data/facebookAds',
    method: 'post',
    data: data
  })
}

// 修改广告日报
export function updateFacebookAds(data) {
  return request({
    url: '/data/facebookAds',
    method: 'put',
    data: data
  })
}

// 删除广告日报
export function delFacebookAds(dailyIds) {
  return request({
    url: '/data/facebookAds/' + dailyIds,
    method: 'delete'
  })
}

// 统计总览（指标卡片）
export function getAdsSummary(query) {
  return request({
    url: '/data/facebookAds/stats/summary',
    method: 'get',
    params: query
  })
}

// 按日期趋势统计（折线图）
export function getAdsTrend(query) {
  return request({
    url: '/data/facebookAds/stats/trend',
    method: 'get',
    params: query
  })
}

// 按广告活动汇总统计（柱状对比/占比饼图）
export function getAdsCampaign(query) {
  return request({
    url: '/data/facebookAds/stats/campaign',
    method: 'get',
    params: query
  })
}
