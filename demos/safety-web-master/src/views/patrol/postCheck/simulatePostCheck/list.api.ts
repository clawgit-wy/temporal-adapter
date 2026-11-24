import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/validate/scenePost/listPage',
  save = '/safety/validate/scenePost/add', // 新增
  exportXls = '/safety/validate/scenePost/export',
  view = '/safety/validate/scenePost/view', // 获取详情
  update = '/safety/validate/scenePost/update', // 更新

  fieldTree = '/safety/field/fieldChildTree', // 项目树
  employeeList = '/safety/employee/list', // 获取人员列表
}

/**
 * 获取列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params})
};

/**
 * 新增
 */
export const saveOrUpdate = (params) => {
  return defHttp.post({ url: Api.save, params });
};


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
export const update = (params) =>{
  return defHttp.post({ url: Api.update, params})
};