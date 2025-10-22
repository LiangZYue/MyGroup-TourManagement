import request from '@/utils/request'

// 查询旅游推荐列表
export function listRecommend(query) {
  return request({
    url: '/system/recommend/list',
    method: 'get',
    params: query
  })
}

// 查询旅游推荐详细
export function getRecommend(id) {
  return request({
    url: '/system/recommend/' + id,
    method: 'get'
  })
}

// 新增旅游推荐
export function addRecommend(data) {
  return request({
    url: '/system/recommend',
    method: 'post',
    data: data
  })
}

// 修改旅游推荐
export function updateRecommend(data) {
  return request({
    url: '/system/recommend',
    method: 'put',
    data: data
  })
}

// 删除旅游推荐
export function delRecommend(id) {
  return request({
    url: '/system/recommend/' + id,
    method: 'delete'
  })
}

// 导出旅游推荐
export function exportRecommend(query) {
  return request({
    url: '/system/recommend/export',
    method: 'get',
    params: query
  })
}

// 基于偏好获取推荐
export function getRecommendByPreference(params) {
  return request({
    url: '/system/recommend/preference',
    method: 'get',
    params: params
  })
}

// 基于协同过滤获取推荐
export function getRecommendByCollaborative(userId) {
  return request({
    url: '/system/recommend/collaborative/' + userId,
    method: 'get'
  })
}