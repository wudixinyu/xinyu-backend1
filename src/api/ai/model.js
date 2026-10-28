import request from '@/utils/request'

// 查询AI模型列表（不含 api_key）
export function listModel(query) {
  return request({
    url: '/ai/model/list',
    method: 'get',
    params: query
  })
}

// 查询AI模型详细（含 api_key）
export function getModel(modelId) {
  return request({
    url: '/ai/model/' + modelId,
    method: 'get'
  })
}

// 新增AI模型
export function addModel(data) {
  return request({
    url: '/ai/model',
    method: 'post',
    data: data
  })
}

// 修改AI模型
export function updateModel(data) {
  return request({
    url: '/ai/model',
    method: 'put',
    data: data
  })
}

// 删除AI模型
export function delModel(modelId) {
  return request({
    url: '/ai/model/' + modelId,
    method: 'delete'
  })
}
