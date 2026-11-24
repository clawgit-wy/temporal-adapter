import { defHttp } from '/@/utils/http/axios';


export enum Api {
  list = '/safety/employeeprivatecar/listPage',
  import = '/safety/employeeprivatecar/importData',
  download = '/safety/employeeprivatecar/downLoadTemplet', // 下载
}

// 获取列表数据
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};

/**
 * 下载模板
 */

export const download = (params: {}) =>{
  return defHttp.get({ url: Api.download, responseType: 'blob', params })
};
