import { defHttp } from '/@/utils/http/axios';
export enum Api {
  list = '/safety/goodsOut/eventsProcess/query', // 列表
  update = '/safety/goodsOut/eventsProcess/updateExceptionInfo', // 更新
  exportXls = '/safety/goodsOut/eventsProcess/exportData', // 导出
  create = '/safety/goodsOut/eventsProcess/process/saveAndCreateProcess',
  view = '/safety/goodsOut/eventsProcess/process/getProcessDetails', // 获取详情
  approve = '/safety/goodsOut/eventsProcess/process/approvalProcess',
  fieldTree = '/safety/field/fieldChildTree', // 项目树
  employeeList = '/safety/employee/list', // 获取人员列表
  fieldManageTree = '/safety/field/fieldChildTree',
}

/**
 * 列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};
export const update = () => {}
export const create = (params) => {
  return defHttp.post({ url: Api.create, params });
}

/**
 * 新增
 */
export const saveOrUpdate = (params) => {
  return defHttp.post({ url: Api.update, params });
};
export const sendApprove = (params) => {
  return defHttp.post({url: Api.approve, params, headers: {'Content-Type': 'application/json;charset=UTF-8'}})
}

// 获取项目字典
export const fieldTree = (params) =>{
  return defHttp.post({ url: Api.fieldTree, params })
};
// 获取人员列表
export const employeeList = (params) => {
  return defHttp.get({url: Api.employeeList, params})
}
export const view = (params) =>{
  return defHttp.post({ url: Api.view, params})
};
