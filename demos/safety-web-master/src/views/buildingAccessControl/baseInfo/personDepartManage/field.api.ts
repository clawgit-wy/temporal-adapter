import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  fieldTree = '/safety/field/fieldChildTree', // 项目树
  groupTree= '/safety/group/getDepartName', // 获取组织架构
  listPage='/safety/buildPower/list',//列表
  exportData='/safety/buildPower/exportData',//导出
  updateData='/safety/buildPower/updateData',//编辑
  deleteData='/safety/buildPower/delete',//编辑
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
// 编辑
export const deleteData = (params) => {
  return defHttp.post({ url: Api.deleteData, params })
}; 