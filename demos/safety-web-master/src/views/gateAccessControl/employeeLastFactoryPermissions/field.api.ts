import { defHttp } from '/@/utils/http/axios';

export enum Api {
  tableList = '/park/safety/parkEmpPowerHistory/pageList', // 获取列表
  viewDetail = '/park/safety/parkEmpLastPowerReport/viewDetail', // 查看
  cancelPower = '/park/safety/parkEmpLastPowerReport/cancelPower', // 注销
  fieldChildTree = '/safety/field/fieldChildTree', // 岗位验证下查询子项目单独接口
}

// 获取列表
export const tableList = (params) => {
  return defHttp.post({ url: Api.tableList, params })
}
// 查看
export const viewDetail = (params) => {
  return defHttp.post({ url: Api.viewDetail, params })
}
// 查看
export const cancelPower = (params) => {
  return defHttp.post({ url: Api.cancelPower, params })
}

export const fieldChildTree = (params) =>{
  return defHttp.post({ url: Api.fieldChildTree, params })
}; 
