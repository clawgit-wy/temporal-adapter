import { defHttp } from '/@/utils/http/axios';
export enum Api {
  list = '/enter/filingReport/reportPageList', // 备案类型配置
  exportXls = '/enter/filingReport/reportExport', // 导出
  enterStatistic = '/enter/filingReport/enterStatistic'// 统计
}

export const list = (params) => {
  return defHttp.post({ url: Api.list, params });
};

export const enterStatistic = (params) => {
  return defHttp.post({ url: Api.enterStatistic, params });
};

