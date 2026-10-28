import request from '@/utils/request'

// 查询素材列表
export function listMedia(query) {
  return request({
    url: '/ai/media/list',
    method: 'get',
    params: query
  })
}

// 查询素材详细
export function getMedia(mediaId) {
  return request({
    url: '/ai/media/' + mediaId,
    method: 'get'
  })
}

// 修改素材（重命名/分类）
export function updateMedia(data) {
  return request({
    url: '/ai/media',
    method: 'put',
    data: data
  })
}

// 删除素材
export function delMedia(mediaId) {
  return request({
    url: '/ai/media/' + mediaId,
    method: 'delete'
  })
}
