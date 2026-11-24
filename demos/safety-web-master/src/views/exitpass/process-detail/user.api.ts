import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  getPageList = '/safety/goodsOut/process/getPageList', // 出门证流程-获取出门证明细
  exportXls = '/safety/goodsOut/process/exportData', // 导出
  auditProcess = '/safety/goodsOut/process/auditProcess', // 出门证流程-审核
  rejectProcess = '/safety/goodsOut/process/rejectProcess', // 出门证流程-驳回
  createAbnormal = '/safety/goodsOut/eventsProcess/add',
  fieldTree = '/safety/field/fieldChildTree', // 项目树
}
/**
 * 出门证异常创建
 */
export const createAbnormal = (params) => {
  return defHttp.post({url: Api.createAbnormal, params})
}
/**
 * 出门证流程-获取出门证明细
 * @param params
 */
export const getPageList = (params) => {
  console.log('请求列表', params);
  return defHttp.post({ url: Api.getPageList, params });
};
/**
 * 出门证流程-审核
 * @param params 
 */
export const auditProcess = (params) => {
  console.log('请求列表', params);
  return defHttp.post({ url: Api.auditProcess, params });
};

/**
 * 出门证流程-驳回
 * @param params 
 */
export const rejectProcess = (params) => {
  console.log('请求列表', params);
  return defHttp.post({ url: Api.rejectProcess, params,headers: { 'Content-Type': ContentTypeEnum.JSON } });
};

// 获取项目字典
export const fieldTree = (params) => {
  return defHttp.post({ url: Api.fieldTree, params })
};
