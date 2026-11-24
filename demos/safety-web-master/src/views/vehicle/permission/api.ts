import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { ContentTypeEnum } from '/@/enums/httpEnum';

const { createConfirm } = useMessage();

export enum Api {
  // list = '/sys/message/sysMessageTemplate/list',table/getDemoList
  // list = '/mock/table/getDemoList',safety/black/list
  list = '/enter/auth/selectEnterAuthPage', // 备案权限管理列表
  exportXls = '/enter/filing/exportEnterFilingHistory', // 导出
  fcList = '/enter/auth/selectAuthFactory', // 获取备案厂区
  add = '/enter/auth/insertEnterAuth', // 新增
  update = '/enter/auth/updateEnterAuth', // 编辑修改
  dlt = '/enter/auth/deleteEnterAuth', // 删除
  auths = '/enter/auth/selectEnterAuthDict', // 权限
  detail = '/enter/auth/selectEnterAuthInfo', // 详情
  ftList = '/enter/auth/getSearchParams', // 获取查询条件数据
  vaPhone = '/enter/auth/checkAuthEmpInfo', //  验证用户信息
  selectGroupTree = '/safety/group/selectGroupTreeNonAsyn', // 获取组织架构
  updatePowerOrg = '/enter/auth/updatePowerOrg', // 更改组织架构权限
}
/* 验证用户信息 */
export const vaPhone = (params) => {
  return defHttp.post({ url: Api.vaPhone, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } });
};
/**
 * 获取备案厂区
 * @param params
 *
 */
export const fcList = (params) => {
  return defHttp.post({ url: Api.fcList, params });
};
/* 获取备案权限管理列表数据 */
export const list = (params) => {
  return defHttp.post({ url: Api.list, params, headers: { 'Content-Type': ContentTypeEnum.JSON } });
};
/* 导出 */
export const exportXls = Api.exportXls;
/*  新增*/
export const addUpdate = (params, isUpdate) => {
  if (unref(isUpdate)) {
    return defHttp.post({ url: Api.update, params, headers: { 'Content-Type': ContentTypeEnum.JSON } });
  } else {
    return defHttp.post({ url: Api.add, params, headers: { 'Content-Type': ContentTypeEnum.JSON } });
  }
};
/* 删除 */
export const dlt = (params, confirm = false) => {
  return new Promise((resolve, reject) => {
    const doDelete = () => {
      resolve(defHttp.post({ url: Api.dlt, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } }, { joinParamsToUrl: true }));
    };
    if (confirm) {
      createConfirm({
        iconType: 'warning',
        title: '删除',
        content: '确定要删除吗？',
        onOk: () => doDelete(),
        onCancel: () => reject(),
      });
    } else {
      doDelete();
    }
  });
};

/* 操作权限 */
export const auths = (params) => {
  return defHttp.post({ url: Api.auths, params });
};
/* 详情 */
export const detail = (params) => {
  return defHttp.post({ url: Api.detail, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } });
};
/* 获取查询条件数据 */
export const ftList = (params) => {
  return defHttp.post({ url: Api.ftList, params, headers: { 'Content-Type': ContentTypeEnum.JSON } });
};

// 获取组织架构
export const selectGroupTree = (params = {}) => {
  return defHttp.post({ url: Api.selectGroupTree, params });
};

export const updatePowerOrg = (params = {}) => {
  return defHttp.post({ url: Api.updatePowerOrg, params });
};