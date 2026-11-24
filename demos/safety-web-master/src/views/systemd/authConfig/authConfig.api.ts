import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

enum Api {
  getAuthConfig = '/build/safety/buildConfig/query',
  saveAuthConfig = '/build/safety/buildConfig/saveOrUpdate',
  authPageList = '/safety/visitor/authorizationConfig/pageList',
  authSaveAndUpdate = '/safety/visitor/authorizationConfig/saveAndUpdate',
  authDelete = '/safety/visitor/authorizationConfig/delete',
}

/**
 * 获取授权配置信息
 */
export function getAuthConfig() {
  return defHttp.get({ url: Api.getAuthConfig });
}

/**
 * 保存授权配置信息
 */
export function saveAuthConfig(params: any) {
  return defHttp.post({ url: Api.saveAuthConfig, params });
}

/**
 * 访客授权配置-分页列表
 */
export function getAuthPageList(params: any) {
  return defHttp.get({ url: Api.authPageList, params }, { successMessageMode: 'none' });
}

/**
 * 访客授权配置-新增或编辑
 */
export function authSaveAndUpdate(params: any) {
  return defHttp.post({
    url: Api.authSaveAndUpdate,
    params,
    headers: { 'Content-Type': ContentTypeEnum.JSON },
  });
}

/**
 * 访客授权配置-删除
 */
export function authDelete(id: string) {
  return defHttp.post({
    url: Api.authDelete,
    params: { id },
    headers: { 'Content-Type': ContentTypeEnum.JSON },
  });
}

/** 根据员工号获取员工信息 */
export const getEmployeeByWorkNo = (params) => {
  return defHttp.post({ url: '/safety/employee/getEmployeeByWorkNo', params });
};

/** 获取组织架构 */
export const getGroupTree = (keyWords: string) => {
  return defHttp.post({
    url: '/safety/group/getDepartName',
    params: { keyWords },
    headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED },
  });
};
