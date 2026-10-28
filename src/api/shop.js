import request from '@/utils/request'

// ==================== 店铺设置 ====================

// 查询店铺列表
export function listShopStore(query) {
  return request({
    url: '/shop/store/list',
    method: 'get',
    params: query
  })
}

// 查询启用店铺下拉选项
export function getShopStoreOptions() {
  return request({
    url: '/shop/store/options',
    method: 'get'
  })
}

// 查询店铺详细
export function getShopStore(storeId) {
  return request({
    url: '/shop/store/' + storeId,
    method: 'get'
  })
}

// 新增店铺
export function addShopStore(data) {
  return request({
    url: '/shop/store',
    method: 'post',
    data: data
  })
}

// 修改店铺
export function updateShopStore(data) {
  return request({
    url: '/shop/store',
    method: 'put',
    data: data
  })
}

// 删除店铺
export function delShopStore(storeIds) {
  return request({
    url: '/shop/store/' + storeIds,
    method: 'delete'
  })
}

// 测试店铺连接
export function testShopStore(storeId) {
  return request({
    url: '/shop/store/test/' + storeId,
    method: 'post',
    timeout: 60000
  })
}

// ==================== Woo订单 ====================

// 查询订单列表
export function listWooOrder(query) {
  return request({
    url: '/shop/woo/order/list',
    method: 'get',
    params: query
  })
}

// 查询订单详细
export function getWooOrder(orderId) {
  return request({
    url: '/shop/woo/order/' + orderId,
    method: 'get'
  })
}

// 同步订单（storeId为空时同步全部启用店铺）
export function syncWooOrder(storeId) {
  return request({
    url: '/shop/woo/order/sync',
    method: 'post',
    params: { storeId },
    timeout: 300000
  })
}

// 删除订单
export function delWooOrder(orderIds) {
  return request({
    url: '/shop/woo/order/' + orderIds,
    method: 'delete'
  })
}

// ==================== Woo商品 ====================

// 查询商品列表
export function listWooProduct(query) {
  return request({
    url: '/shop/woo/product/list',
    method: 'get',
    params: query
  })
}

// 查询商品详细
export function getWooProduct(productId) {
  return request({
    url: '/shop/woo/product/' + productId,
    method: 'get'
  })
}

// 查询商品详情（含店铺端描述，编辑时使用）
export function getWooProductDetail(productId) {
  return request({
    url: '/shop/woo/product/detail/' + productId,
    method: 'get',
    timeout: 60000
  })
}

// 新增商品（写回WooCommerce）
export function addWooProduct(data) {
  return request({
    url: '/shop/woo/product',
    method: 'post',
    data: data,
    timeout: 120000
  })
}

// 修改商品（写回WooCommerce）
export function updateWooProduct(data) {
  return request({
    url: '/shop/woo/product',
    method: 'put',
    data: data,
    timeout: 120000
  })
}

// 同步商品（storeId为空时同步全部启用店铺）
export function syncWooProduct(storeId) {
  return request({
    url: '/shop/woo/product/sync',
    method: 'post',
    params: { storeId },
    timeout: 300000
  })
}

// 删除商品
export function delWooProduct(productIds) {
  return request({
    url: '/shop/woo/product/' + productIds,
    method: 'delete'
  })
}

// ==================== Woo文章 ====================

// 查询文章列表
export function listWooPost(query) {
  return request({
    url: '/shop/woo/post/list',
    method: 'get',
    params: query
  })
}

// 查询文章详细（含正文）
export function getWooPost(postId) {
  return request({
    url: '/shop/woo/post/' + postId,
    method: 'get'
  })
}

// 同步文章（storeId为空时同步全部启用店铺）
export function syncWooPost(storeId) {
  return request({
    url: '/shop/woo/post/sync',
    method: 'post',
    params: { storeId },
    timeout: 300000
  })
}

// 删除文章
export function delWooPost(postIds) {
  return request({
    url: '/shop/woo/post/' + postIds,
    method: 'delete'
  })
}

// ==================== Webhooks ====================

// 查询Webhook列表
export function listShopWebhook(query) {
  return request({
    url: '/shop/webhook/list',
    method: 'get',
    params: query
  })
}

// 查询Webhook详细
export function getShopWebhook(webhookId) {
  return request({
    url: '/shop/webhook/' + webhookId,
    method: 'get'
  })
}

// 新增Webhook
export function addShopWebhook(data) {
  return request({
    url: '/shop/webhook',
    method: 'post',
    data: data,
    timeout: 120000
  })
}

// 修改Webhook
export function updateShopWebhook(data) {
  return request({
    url: '/shop/webhook',
    method: 'put',
    data: data,
    timeout: 120000
  })
}

// 启停Webhook
export function changeShopWebhookStatus(webhookId, status) {
  return request({
    url: '/shop/webhook/changeStatus',
    method: 'put',
    data: { webhookId: webhookId, status: status },
    timeout: 60000
  })
}

// 删除Webhook
export function delShopWebhook(webhookIds) {
  return request({
    url: '/shop/webhook/' + webhookIds,
    method: 'delete',
    timeout: 120000
  })
}

// 从店铺同步Webhook
export function syncShopWebhook(storeId) {
  return request({
    url: '/shop/webhook/sync',
    method: 'post',
    params: { storeId },
    timeout: 300000
  })
}
