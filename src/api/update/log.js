import request from '@/utils/request'

// 查询更新日志列表
export function listLog(query) {
  return request({
    url: '/update/log/list',
    method: 'get',
    params: query
  })
}

// 查询更新日志详细
export function getLog(logId) {
  return request({
    url: '/update/log/' + logId,
    method: 'get'
  })
}

// 新增更新日志
export function addLog(data) {
  return request({
    url: '/update/log',
    method: 'post',
    data: data
  })
}

// 修改更新日志
export function updateLog(data) {
  return request({
    url: '/update/log',
    method: 'put',
    data: data
  })
}

// 删除更新日志
export function delLog(logId) {
  return request({
    url: '/update/log/' + logId,
    method: 'delete'
  })
}
