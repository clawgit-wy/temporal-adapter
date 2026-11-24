import { defHttp } from '/@/utils/http/axios';
export enum Api {
  list = '/safety/validate/environment/listPage', // 列表
  infoList = '/safety/spot/record/reformList', // 详情
  exportXls = '/safety/validate/environment/export', // 导出
}

/**
 * 列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};
