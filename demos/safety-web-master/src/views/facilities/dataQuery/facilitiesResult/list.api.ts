import { defHttp } from '/@/utils/http/axios';
export enum Api {
  list = '/safety/spot/record/taskReport', // 列表
  exportXls = '/safety/spot/record/exportTaskReport', // 导出
  infoExport = '/safety/spot/record/export'
}

/**
 * 列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};
// 下载详情
export const download = (params) =>{
  return defHttp.get({ url: Api.exportXls, responseType: 'blob', params })
};
;
// 下载详情
export const downloadInfo = (params) =>{
  return defHttp.get({ url: Api.infoExport, responseType: 'blob', params })
};
;