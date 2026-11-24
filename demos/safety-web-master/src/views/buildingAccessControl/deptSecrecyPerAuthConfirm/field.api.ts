import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  listPage='/build/safety/departConfirm/getPageList',//列表
  batchAcrossConfirm = '/build/safety/departConfirm/batchAcrossConfirm', // 一键确认取消
}


/**
 * 出门证权限管理-分页查询
 * @param params
 */
export const getListPage = (params) => {
  console.log('请求列表', params);
  return defHttp.post({ url: Api.listPage, params }, { successMessageMode: 'none' });
};

// 一键确认取消
export const batchAcrossConfirm = (params) => {
  return defHttp.post({ url: Api.batchAcrossConfirm, params })
}