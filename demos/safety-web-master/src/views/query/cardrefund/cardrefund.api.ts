import { defHttp } from '/@/utils/http/axios';


export enum Api {
  // list = '/sys/message/sysMessageTemplate/list',table/getDemoList
  // list = '/mock/table/getDemoList',safety/black/list
  list = '/safety/refund/list',
  save = '/safety/refund/add', // 添加
  exportXls = '/safety/refund/export', // 导出
  factoryList='/safety/field/list', //获取厂区列表
}

/**
 * 获取黑名单列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
} ;

/**
 * 获取厂区列表
 * @param params
 * 
 */
 export const factoryList = (params) =>{
  return defHttp.post({ url: Api.factoryList, params })
} ;

/**
 * 添加人员
 */
export const save = (params) => {
  return defHttp.post({ url: Api.save, params });
};

/**
 * 导出
 * @param params
 * 
 */
 export const exportXls = Api.exportXls;

