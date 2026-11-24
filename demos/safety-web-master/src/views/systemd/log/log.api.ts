import { defHttp } from '/@/utils/http/axios';

export enum Api {
  list = '/safety/log/list', // 列表接口
  exportXls = '/safety/log/export', // 导出接口
}

/**
 * 获取日志列表
 * @param params
 * @param confirm
 */
export const list = (params) => defHttp.get({ url: Api.list, params });

/**
 * 日志列表导出
 * @param params
 *
 */
export const exportXls = Api.exportXls;
