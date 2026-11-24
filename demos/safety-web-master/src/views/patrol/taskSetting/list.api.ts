import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/patrol/plan/list',
  save = '/safety/patrol/plan/add', // 新增
  exportXls = '/safety/patrol/plan/export', // 导出
  changeStatus = '/safety/patrol/plan/modifyStatus', // 删除
  fieldTree = '/safety/field/fieldChildTree', // 应用项目列表
  fieldManageTree = '/safety/field/fieldManageTree', // 应用项目列表
  modifyDormancy = '/safety/patrol/plan/modifyDormancy',
  selectRoleList = '/safety/role/selectSysRoles', // 查询角色列表
  fieldChildByFieldType = '/safety/field/fieldChildByFieldType', // 岗位验证下查询子项目单独接口
}
export const fieldChildByFieldType = (params) =>{
  return defHttp.post({ url: Api.fieldChildByFieldType, params })
};
/**
 * 获取黑名单列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};

export const fieldTree = (params) =>{
  return defHttp.post({ url: Api.fieldTree, params })
};
export const fieldManageTree = (params) =>{
  return defHttp.post({ url: Api.fieldManageTree, params })
};


/**
 * 添加人员
 */
export const saveOrUpdate = (params, isUpdate) => {
  return defHttp.post({ url: Api.save, params });
};

export const changeStatus = (params, confirm=false) => {

  return new Promise((resolve, reject) => {
    const doDelete = () => {
      resolve(defHttp.post({ url: Api.changeStatus, params }));
    };
    if (confirm) {
      const key = params.status ? '休眠' : '启用'
      createConfirm({
        iconType: 'warning',
        title: key,
        content: `确定要${key}吗？`,
        onOk: () => doDelete(),
        onCancel: () => reject(),
      });
    } else {
      doDelete();
    }
  });
};
// 获取组织架构
export const getGroupTree = (key:string) => {
  return defHttp.post({url: Api.groupTree, params: {key}})
}
// 获取岗位列表
export const getEmpPost = () => {
  return defHttp.get({url: Api.getEmpPost})
}
export const download = (params: {}) =>{
  return defHttp.get({ url: Api.download, responseType: 'blob', params })
};
// 批量休眠
export const modifyDormancy = (params: {}) => {
  return defHttp.post({url: Api.modifyDormancy, params})
}
// 查询角色列表
export const selectRoleList = (params: {}) => {
  return defHttp.post({url: Api.selectRoleList, params})

}