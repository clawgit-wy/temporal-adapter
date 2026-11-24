import { defHttp } from '/@/utils/http/axios';
export enum Api {
  list = '/enter/issuance/list', // 备案类型配置
  exportXls = '/enter/issuance/export', // 导出
  updateIssuance = '/enter/issuance/updateIssuance', // 下发
}

export const list = (params) => {
  return defHttp.post({ url: Api.list, params });
};

export const updateIssuance = (params = {}) => {
  return defHttp.post({ url: Api.updateIssuance, params });
};
