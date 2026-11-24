import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  listPage='/build/safety/buildWhiteList/list',//列表
  delete='/build/safety/buildWhiteList/delete',//删除
  save='/build/safety/buildWhiteList/save',//新增
  importData='/build/safety/buildWhiteList/importData',//导入
  exportData='/build/safety/buildWhiteList/exportData',//导出
  download = '/build/safety/buildWhiteList/download', // 下载
  getEmployeeByWorkNo = '/build/safety/buildWhiteList/getEmployeeByWorkNo', // 根据工号获取人员信息
}

/**
 * 出门证权限管理-分页查询
 * @param params
 */
export const getListPage = (params) => {
  console.log('请求列表', params);
  return defHttp.post({ url: Api.listPage, params }, { successMessageMode: 'none' });
};

/**
 * 下载列表
 * @param params
 *
 */
export const download = (params: {}) => {
  return defHttp.get({ url: Api.download, responseType: 'blob', params });
};
/**
 * 删除
 * @param params
 *
 */
export const deleteItem = (params: {}) => {
  return defHttp.post({ url: Api.delete, params })
};
/**
 * 保存
 * @param params
 *
 */
export const saveItem = (params: {}) => {
  return defHttp.post({ url: Api.save, params })
};

// 获取申请人数据
export const getEmployeeByWorkNo = (params) => {
  return defHttp.post({ url: Api.getEmployeeByWorkNo, params })
}