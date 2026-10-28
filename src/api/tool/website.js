import request from '@/utils/request'

// 查询工具使用网站列表
export function listWebsite(query) {
  return request({
    url: '/tool/website/list',
    method: 'get',
    params: query
  })
}

// 查询工具使用网站详细
export function getWebsite(toolId) {
  return request({
    url: '/tool/website/' + toolId,
    method: 'get'
  })
}

// 新增工具使用网站
export function addWebsite(data) {
  return request({
    url: '/tool/website',
    method: 'post',
    data: data
  })
}

// 修改工具使用网站
export function updateWebsite(data) {
  return request({
    url: '/tool/website',
    method: 'put',
    data: data
  })
}

// 删除工具使用网站
export function delWebsite(toolId) {
  return request({
    url: '/tool/website/' + toolId,
    method: 'delete'
  })
}
