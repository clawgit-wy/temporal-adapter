import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  fieldTree = '/safety/field/fieldChildTree', // 项目树
  groupTree= '/safety/group/getDepartName', // 获取组织架构
  listPage='/build/safety/buildInfoConfig/query',//列表
  exportData='/safety/buildPower/exportData',//导出
  updateData='/build/safety/buildInfoConfig/saveOrUpdate',//编辑
  download = '/safety/black/download', // 下载
  getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo', // 根据工号获取人员信息
}

// 获取项目字典
export const getFieldChildTree = (params) =>{
  return defHttp.post({ url: Api.fieldTree, params })
};

// 获取组织架构
export const getGroupTree = (keyWords:string) => {
  return defHttp.post({url: Api.groupTree, params: {keyWords},headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } })
}

/**
 * 出门证权限管理-分页查询
 * @param params
 */
export const getListPage = (params) => {
  console.log('请求列表', params);
  return defHttp.post({ url: Api.listPage, params }, { successMessageMode: 'none' });
};

// 编辑
export const updateData = (params) => {
  return defHttp.post({ url: Api.updateData, params })
}; 

/**
 * 下载列表
 * @param params
 *
 */
export const download = (params: {}) => {
  return defHttp.get({ url: Api.download, responseType: 'blob', params });
};

// 获取申请人数据
export const getEmployeeByWorkNo = (params) => {
  return defHttp.post({ url: Api.getEmployeeByWorkNo, params })
}