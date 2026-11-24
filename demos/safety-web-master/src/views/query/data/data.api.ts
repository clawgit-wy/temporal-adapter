import { defHttp } from '/@/utils/http/axios';

export enum Api {
  list = '/safety/datasend/list', // 列表接口
  exportXls = '/safety/datasend/export', // 导出接口
  factoryList = '/safety/field/list', //获取厂区列表
  send = '/safety/datasend/sendAgain', //重新下发
}

/**
 * 获取日志列表
 * @param params
 * @param confirm
 */
export const list = (params) => defHttp.get({ url: Api.list, params });
/**
 * 获取厂区列表
 * @param params
 *
 */
export const factoryList = (params) => {
  return defHttp.post({ url: Api.factoryList, params });
};

/**
 * 重新下发
 * @param params
 *
 */
export const send = (params) => {
  return defHttp.post({ url: Api.send, params });
};

/**
 * 日志列表导出
 * @param params
 *
 */
export const exportXls = Api.exportXls;
