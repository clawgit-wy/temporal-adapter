import { defHttp } from '/@/utils/http/axios';


export enum Api {
  detail = '/safety/notice/info', // 详情接口
  saveOrUpdate = '/safety/notice/saveOrUpdate', // 数据更新接口
}

export const detail = (params) => defHttp.post({ url: Api.detail, params });

export const saveOrUpdate = (params) => defHttp.post({ url: Api.saveOrUpdate, params });


