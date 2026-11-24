import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  save='/safety/goodsOut/auth/add',
  update='/safety/goodsOut/auth/update',
  delete='/safety/goodsOut/auth/batchDelete',
  listPage='/safety/goodsOut/detail/getPageList',
  importXls = '/safety/goodsOut/auth/importData',
  exportXls = '/safety/goodsOut/detail/exportData',
  vaPhone = '/enter/auth/checkAuthEmpInfo', //  验证用户信息
  fieldTree = '/safety/field/fieldChildTree', // 项目树
  download = '/safety/goodsOut/auth/download', // 下载
  groupTree= '/safety/group/getDepartName', // 获取组织架构
}
// 获取项目字典
export const fieldTree = (params) => {
  return defHttp.post({ url: Api.fieldTree, params })
};
/**
 * 下载模板
 * @param params
 * 
 */
export const download = (params) =>{
  return defHttp.get({ url: Api.download, responseType: 'blob', params })
};
/* 验证用户信息 */
export const vaPhone = (params) => {
  return defHttp.post({ url: Api.vaPhone, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } });
};
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