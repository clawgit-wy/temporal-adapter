import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  listPage='/build/safety/longConfirm/getOwnerList',//列表
  batchCloseBuild = '/build/safety/longConfirm/batchCloseBuild', // 全部取消
  batchSaveWhite = '/build/safety/longConfirm/batchSaveWhite', // 全部保留
  batchDealBuild = '/build/safety/longConfirm/batchDealBuild', // 取消保留确认
}


/**
 * 分页查询
 * @param params
 */
export const getListPage = (params) => {
  console.log('请求列表', params);
  return defHttp.post({ url: Api.listPage, params }, { successMessageMode: 'none' });
};

/**
 * 全部取消
 * @param params
 */
export const batchCloseBuild = (params) =>{
  return defHttp.post({ url: Api.batchCloseBuild, params })
};

/**
 * 全部保留
 * @param params
 */
export const batchSaveWhite = (params) =>{
  return defHttp.post({ url: Api.batchSaveWhite, params })
};

/**
 * 取消保留确认
 * @param params
 */
export const batchDealBuild = (params) =>{
  return defHttp.post({ url: Api.batchDealBuild, params })
};

