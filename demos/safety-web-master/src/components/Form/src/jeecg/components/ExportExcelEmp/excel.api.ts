import { defHttp } from '/@/utils/http/axios';

export enum Api {
  downloadEmpTemplate = '/build/safety/buildProcess/downloadEmpTemplate', // 人员导入模板
}

/**
 * 下载模板
 * @param params
 * 
 */
export const downloadEmpTemplate = (url,params) =>{
  return defHttp.get({ url: url, responseType: 'blob', params })
};
