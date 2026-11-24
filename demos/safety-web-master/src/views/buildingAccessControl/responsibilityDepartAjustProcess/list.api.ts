import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';
export enum Api {
  list = '/safety/buildManage/getPageList', // 列表
  fieldChildTree = '/safety/field/fieldChildTree', // 厂区楼栋
  infoList = '/safety/spot/record/reformList', // 详情
  exportXls = '/safety/spot/record/export', // 导出
  createEIPFlow = '/safety/spot/record/createEIPFlow'
}

/**
 * 列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};

// 详情
export const infoList = (params) => {
  return defHttp.post({url: Api.infoList, params})
}
// 详情
export const createEIPFlow = (params) => {
  return defHttp.post({url: Api.createEIPFlow, params, headers: { 'Content-Type': ContentTypeEnum.JSON } })
}

export const fieldChildTree = (params) =>{
  return defHttp.post({ url: Api.fieldChildTree, params })
};