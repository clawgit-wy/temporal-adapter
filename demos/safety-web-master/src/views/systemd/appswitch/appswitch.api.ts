import { defHttp } from '/@/utils/http/axios';

export enum Api {
  list = '/safety/config/infoByType',
  save='/safety/config/saveOrUpdate',
}

/**
 * 获取信息
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};

/**
 * 编辑
 */
export const saveOrUpdate = (params) => {
  return defHttp.post({ url: Api.save, params });
};

