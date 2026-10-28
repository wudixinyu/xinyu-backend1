import request from '@/utils/request'
import { getToken } from '@/utils/auth'

// 知识库流式问答（SSE）。回调：onSources(list) / onDelta(text) / onDone(costMs) / onError(msg)
export function chatKbStream(data, { onSources, onDelta, onDone, onError }) {
  return fetch(import.meta.env.VITE_APP_BASE_API + '/ai/kb/chat/stream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + getToken()
    },
    body: JSON.stringify(data)
  }).then(response => {
    if (!response.ok || !response.body) {
      throw new Error('服务响应异常，HTTP ' + response.status)
    }
    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buf = ''
    const handlePayload = (payload) => {
      if (payload.type === 'sources') onSources && onSources(payload.data || [])
      else if (payload.type === 'delta') onDelta && onDelta(payload.data || '')
      else if (payload.type === 'done') onDone && onDone(payload.data || 0)
      else if (payload.type === 'error') onError && onError(payload.data || '问答失败')
    }
    const pump = () => reader.read().then(({ done, value }) => {
      if (done) return
      buf += decoder.decode(value, { stream: true })
      let idx
      while ((idx = buf.indexOf('\n\n')) >= 0) {
        const frame = buf.slice(0, idx)
        buf = buf.slice(idx + 2)
        const dataLine = frame.split('\n').find(l => l.startsWith('data:'))
        if (!dataLine) continue
        try { handlePayload(JSON.parse(dataLine.slice(5))) } catch (e) { /* 忽略坏帧 */ }
      }
      return pump()
    })
    return pump()
  })
}

// 查询知识库列表
export function listKb(query) {
  return request({
    url: '/ai/kb/list',
    method: 'get',
    params: query
  })
}

// 查询知识库详细
export function getKb(kbId) {
  return request({
    url: '/ai/kb/' + kbId,
    method: 'get'
  })
}

// 新增知识库
export function addKb(data) {
  return request({
    url: '/ai/kb',
    method: 'post',
    data: data
  })
}

// 修改知识库
export function updateKb(data) {
  return request({
    url: '/ai/kb',
    method: 'put',
    data: data
  })
}

// 删除知识库
export function delKb(kbId) {
  return request({
    url: '/ai/kb/' + kbId,
    method: 'delete'
  })
}

// 模型服务探活
export function kbHealth() {
  return request({
    url: '/ai/kb/health',
    method: 'get'
  })
}

// 知识库RAG问答（本地模型生成较慢，单独放宽超时）
export function chatKb(data) {
  return request({
    url: '/ai/kb/chat',
    method: 'post',
    data: data,
    timeout: 180000
  })
}

// 查询知识库文档列表
export function listDocument(query) {
  return request({
    url: '/ai/kb/document/list',
    method: 'get',
    params: query
  })
}

// 上传文档（同步解析入库，耗时较长）
export function uploadDocument(kbId, file) {
  const data = new FormData()
  data.append('kbId', kbId)
  data.append('file', file)
  return request({
    url: '/ai/kb/document/upload',
    method: 'post',
    data: data,
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 300000
  })
}

// 删除知识库文档
export function delDocument(docIds) {
  return request({
    url: '/ai/kb/document/' + docIds,
    method: 'delete'
  })
}

// 重建知识库索引（全量重新向量化，耗时较长）
export function rebuildIndex(kbId) {
  return request({
    url: '/ai/kb/document/rebuild/' + kbId,
    method: 'post',
    timeout: 600000
  })
}

// 获取当前生效的模型配置
export function getKbModelConfig() {
  return request({
    url: '/ai/kb/model/config',
    method: 'get'
  })
}

// 保存模型配置（运行时立即生效）
export function updateKbModelConfig(data) {
  return request({
    url: '/ai/kb/model/config',
    method: 'put',
    data: data,
    timeout: 60000
  })
}

// 拉取服务端可用模型列表（params: { provider, customApiId, baseUrl, apiKey }）
export function listRemoteModels(params) {
  return request({
    url: '/ai/kb/model/models',
    method: 'get',
    params: params,
    timeout: 30000
  })
}

// 查询自定义API列表
export function listKbCustomApis(query) {
  return request({
    url: '/ai/kb/model/custom-apis',
    method: 'get',
    params: query
  })
}

// 新增自定义API
export function addKbCustomApi(data) {
  return request({
    url: '/ai/kb/model/custom-apis',
    method: 'post',
    data: data
  })
}

// 修改自定义API
export function updateKbCustomApi(data) {
  return request({
    url: '/ai/kb/model/custom-apis',
    method: 'put',
    data: data
  })
}

// 删除自定义API
export function delKbCustomApi(ids) {
  return request({
    url: '/ai/kb/model/custom-apis/' + ids,
    method: 'delete'
  })
}

// 模型服务探活
export function kbModelHealth() {
  return request({
    url: '/ai/kb/model/health',
    method: 'get'
  })
}
