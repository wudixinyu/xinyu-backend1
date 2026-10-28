import request from '@/utils/request'

// 查询画布列表（不含 content 大字段）
export function listCanvas(query) {
  return request({
    url: '/ai/canvas/list',
    method: 'get',
    params: query
  })
}

// 查询画布详细（含 content，供编辑器加载）
export function getCanvas(canvasId) {
  return request({
    url: '/ai/canvas/' + canvasId,
    method: 'get'
  })
}

// 新增画布（返回回传主键）
export function addCanvas(data) {
  return request({
    url: '/ai/canvas',
    method: 'post',
    data: data
  })
}

// 修改画布
export function updateCanvas(data) {
  return request({
    url: '/ai/canvas',
    method: 'put',
    data: data
  })
}

// 删除画布
export function delCanvas(canvasId) {
  return request({
    url: '/ai/canvas/' + canvasId,
    method: 'delete'
  })
}

// 发起文本生成任务
export function generateText(data) {
  return request({
    url: '/ai/generate/text',
    method: 'post',
    data: data
  })
}

// 发起图片生成任务
export function generateImage(data) {
  return request({
    url: '/ai/generate/image',
    method: 'post',
    data: data
  })
}

// 发起视频生成任务
export function generateVideo(data) {
  return request({
    url: '/ai/generate/video',
    method: 'post',
    data: data
  })
}

// 批量查询生成任务状态（轮询）
export function listGenerateTasks(taskIds) {
  return request({
    url: '/ai/generate/task/list/' + taskIds.join(','),
    method: 'get'
  })
}
