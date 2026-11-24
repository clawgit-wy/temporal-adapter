import { defHttp } from '/@/utils/http/axios';
export enum Api {
  list = '/safety/patrol/record/pageList', // 列表
  exportXls = '/safety/patrol/record/export', // 导出
}

/**
 * 列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};

