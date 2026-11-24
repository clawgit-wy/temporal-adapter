import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  listPage = '/safety/visitor/authorization/pageTokenList',
  saveOrUpdate = '/safety/visitor/authorization/saveAndUpdate',
  exportXls = '/safety/visitor/authorization/export',
  getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo',
}

/**
 * 访客审批授权表-分页查询（POST）
 */
export const getListPage = (params) => {
  return defHttp.post({ url: Api.listPage, params }, { successMessageMode: 'none' });
};

/**
 * 新增/编辑
 */
export const saveOrUpdate = (params) => {
  return defHttp.post(
    {
      url: Api.saveOrUpdate,
      params,
      headers: { 'Content-Type': ContentTypeEnum.JSON },
    },
    { successMessageMode: 'none' }
  );
};

/**
 * 根据工号获取人员信息
 */
export const getEmployeeByWorkNo = (params) => {
  return defHttp.post({ url: Api.getEmployeeByWorkNo, params }, { successMessageMode: 'none' });
};
