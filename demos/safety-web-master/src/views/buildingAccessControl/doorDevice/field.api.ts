import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  save='/safety/goodsOut/auth/add',
  
  delete='/safety/goodsOut/auth/batchDelete',
  listPage='/safety/goodsOut/auth/listPage',
  importXls = '/safety/goodsOut/auth/importData',
  
  getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo', // 根据工号获取人员信息
  
  groupTree= '/safety/group/getDepartName', // 获取组织架构

  getFactoryList = '/safety/field/selectLinkageListNewName', // 获取工厂列表
  tableList = '/build/safety/buildDevice/listForAn', // 获取列表
  update='/build/safety/buildDevice/updateForAn',
  exportXls = '/build/safety/buildDevice/exportForAn', // 下载
  fieldChildTree = '/safety/field/fieldChildTree', // 岗位验证下查询子项目单独接口
  getCopyDeviceInfo = '/build/safety/buildDevice/getCopyDeviceInfo', // 克隆设备信息下拉框接口
  submitCopyData = '/build/safety/buildDevice/submitCopyData', // 克隆设备提交接口
  
}

// 获取列表
export const tableList = (params) => {
  return defHttp.post({ url: Api.tableList, params })
}

// 获取工厂列表
export const getFactoryList = (params) => {
  return defHttp.post({ url: Api.getFactoryList, params })
}; 

/**
 * 下载模板
 * @param params
 * 
 */
export const download = (params) =>{
  return defHttp.get({ url: Api.exportXls, responseType: 'blob', params })
};


/**
 * 更新
 * @param params
 */
export const update = (params) => {
  return defHttp.post({ url: Api.update, params });
};

/**
 * 更新
 * @param params
 */
export const save = (params) => {
  return defHttp.post({ url: Api.save, params });
};
/**
 * 更新
 * @param params
 */
export const groupTree = (params) => {
  return defHttp.post({ url: Api.groupTree, params });
};

export const fieldChildTree = (params) =>{
  return defHttp.post({ url: Api.fieldChildTree, params })
};
// 克隆设备信息下拉框接口
export const getCopyDeviceInfo = (params) =>{
  return defHttp.post({ url: Api.getCopyDeviceInfo, params })
};
// 克隆设备提交接口
export const submitCopyData = (params) =>{
  return defHttp.post({ url: Api.submitCopyData, params })
};