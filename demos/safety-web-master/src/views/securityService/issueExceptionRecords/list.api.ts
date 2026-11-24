import { message } from 'ant-design-vue';
import { defHttp } from '/@/utils/http/axios';


export enum Api {
  list = '/safety/log/employeeIssued/list',
  downloadLog = '/safety/log/employeeIssued/export', // 下载
  employeeIssuedView = '/safety/log/employeeIssued/view', // 查看
  reissue = '/safety/log/employeeIssued/reissue', // 重新下发
  fieldChildTree = '/safety/field/fieldChildTree', // 岗位验证下查询子项目单独接口
}

/**
 * 获取列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};

/**
 * 下载日志
 * @param params
 * 
 */
export const downloadLog = (params) =>{
  return defHttp.get({ url: Api.downloadLog, responseType: 'blob', params })
};

/**
 * 查看
 * @param params
 * 
 */
export const employeeIssuedView = (params) =>{
  return defHttp.post({ url: Api.employeeIssuedView, params })
};

/**
 * 重新下发
 * @param params
 * 
 */
export const reissue = (params) =>{
  return defHttp.post({ url: Api.reissue, params })
};

export const fieldChildTree = (params) =>{
  return defHttp.post({ url: Api.fieldChildTree, params })
};