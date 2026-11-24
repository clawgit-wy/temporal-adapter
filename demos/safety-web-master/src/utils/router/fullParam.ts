/**
 * 处理 __full__ 参数传递的公共方法
 * 需要在有路由跳转的地方引入使用
 */

import { RouteLocationRaw } from 'vue-router';

/**
 * 检查当前 URL 是否包含 __full__=true 参数
 */
export function hasFullParam(): boolean {
  // 先检查 hash 中的参数 (如 #/xxx?__full__=true)
  const hash = window.location.hash;
  const hashParams = new URLSearchParams(hash.split('?')[1] || '');
  const hashValue = hashParams.get('__full__');

  if (hashValue === 'true') {
    console.log('[hasFullParam] 从 hash 中找到 __full__=true');
    return true;
  }

  // 再检查 search 参数
  const searchParams = new URLSearchParams(window.location.search);
  const searchResult = searchParams.get('__full__') === 'true';
  console.log('[hasFullParam] search __full__=', searchParams.get('__full__'), 'result=', searchResult);
  return searchResult;
}

/**
 * 给路由对象添加 __full__ 参数（如果当前 URL 包含该参数）
 * @param route 目标路由
 * @returns 处理后的路由对象
 */
export function addFullParam(route: RouteLocationRaw): RouteLocationRaw {
  if (!hasFullParam()) {
    return route;
  }

  if (typeof route === 'string') {
    // 字符串路径，直接拼接参数
    const separator = route.includes('?') ? '&' : '?';
    return route + `${separator}__full__=true`;
  }

  return {
    ...route,
    query: {
      ...route.query,
      __full__: 'true',
    },
  };
}
