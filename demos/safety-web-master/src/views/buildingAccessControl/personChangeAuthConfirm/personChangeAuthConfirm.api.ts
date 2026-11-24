import { defHttp } from '/@/utils/http/axios';

enum Api {
  PersonChangeList = '/build/safety/departConfirm/getDepartPageList',
  //长期未进入
  LongTimeNoAccessList = '/build/safety/longConfirm/getPageList',
  // 一键加入白名单
  AddWhiteList = '/build/safety/longConfirm/batchSaveWhite',
  //长期未进入--取消授权
  CancelBuild = '/build/safety/longConfirm/batchCloseBuild',
  //部门人员变动-保留/取消授权
  BatchDealBuild = '/build/safety/departConfirm/batchDealBuild',
  //部门人员变动-保留/取消白名单
  BatchDealWhite = '/build/safety/departConfirm/batchDealWhite',
  // 部门人员变动-单个确认
  DealBuild = '/build/safety/departConfirm/dealBuild',
}

/**
 * 获取人员变更列表
 * @param params
 */
export function getPersonChangeList(params) {
  return defHttp.post({ url: Api.PersonChangeList, params });
}

/**
 * 获取长期未进入列表
 * @param params
 */
export function getLongTimeNoAccessList(params) {
  return defHttp.post({ url: Api.LongTimeNoAccessList, params });
}

/**
 * 一键加入白名单
 * @param params
 */
export function addWhiteList(params) {
  return defHttp.post({ url: Api.AddWhiteList, params });
}

/**
 * 长期未进入--取消授权
 * @param params
 */
export function cancelBuild(params) {
  return defHttp.post({ url: Api.CancelBuild, params });
}

/**
 * 部门人员变动-保留/取消授权
 * @param params
 */
export function batchDealBuild(params) {
  return defHttp.post({
    url: Api.BatchDealBuild,
    params,
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

/**
 * 部门人员变动-保留/取消白名单
 * @param params
 */
export function batchDealWhite(params) {
  return defHttp.post({ url: Api.BatchDealWhite, params });
}

/**
 * 部门人员变动-单个确认
 * @param params
 */
export function dealBuild(params) {
  return defHttp.post({ url: Api.DealBuild, params });
}