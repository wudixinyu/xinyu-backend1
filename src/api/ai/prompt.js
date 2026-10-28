import request from '@/utils/request'

// 查询AI提示词列表
export function listPrompt(query) {
  return request({
    url: '/ai/prompt/list',
    method: 'get',
    params: query
  })
}

// 查询AI提示词详细
export function getPrompt(promptId) {
  return request({
    url: '/ai/prompt/' + promptId,
    method: 'get'
  })
}

// 新增AI提示词
export function addPrompt(data) {
  return request({
    url: '/ai/prompt',
    method: 'post',
    data: data
  })
}

// 修改AI提示词
export function updatePrompt(data) {
  return request({
    url: '/ai/prompt',
    method: 'put',
    data: data
  })
}

// 删除AI提示词
export function delPrompt(promptId) {
  return request({
    url: '/ai/prompt/' + promptId,
    method: 'delete'
  })
}
