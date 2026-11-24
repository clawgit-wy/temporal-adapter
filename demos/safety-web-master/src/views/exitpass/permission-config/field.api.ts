import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  save='/safety/goodsOut/auth/add',
  update='/safety/goodsOut/auth/update',
  delete='/safety/goodsOut/auth/batchDelete',
  listPage='/safety/goodsOut/auth/listPage',
  importXls = '/safety/goodsOut/auth/importData',
  exportXls = '/safety/goodsOut/auth/exportData',
  getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo', // 根据工号获取人员信息
  fieldTree = '/safety/field/fieldChildTree', // 项目树
  download = '/safety/goodsOut/auth/download', // 下载
  groupTree= '/safety/group/getDepartName', // 获取组织架构
}
/**
 * 下载模板
 * @param params
 * 
 */
export const download = (params) =>{
  return defHttp.get({ url: Api.download, responseType: 'blob', params })
};
// 获取申请人数据
export const getEmployeeByWorkNo = (params) => {
  return defHttp.post({ url: Api.getEmployeeByWorkNo, params })
}
/**
 * 出门证权限管理-分页查询
 * @param params
 */
export const getListPage = (params) => {
  console.log('请求列表', params);
  return defHttp.get({ url: Api.listPage, params }, { successMessageMode: 'none' });
};


/**
 * 删除
 */
export const deleteItems = (params, handleSuccess) => {
  return defHttp.post({ url: Api.delete, params }).then(() => {
    handleSuccess();
  });
};


/**
 * 保存或者更新
 * @param params
 */
export const save = (params) => {
  return defHttp.post({ url: Api.save, params });
};

/**
 * 更新
 * @param params
 */
export const update = (params) => {
  return defHttp.post({ url: Api.update, params });
};

// 获取项目字典
export const getFieldChildTree = (params) =>{
  return defHttp.post({ url: Api.fieldTree, params })
};

// 获取组织架构
export const getGroupTree = (keyWords:string) => {
  return defHttp.post({url: Api.groupTree, params: {keyWords},headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } })
}