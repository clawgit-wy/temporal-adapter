import { defHttp } from '/@/utils/http/axios';
export enum Api {
  getDate = '/limitConfig/getData', // 获取时间
  setDate = '/limitConfig/saveOrUpdate', // 保存时间
}

export const getDate = (params) => {
  return defHttp.get({ url: Api.getDate, params });
};

export const setDate = (params) => {
  return defHttp.get({ url: Api.setDate, params });
};

