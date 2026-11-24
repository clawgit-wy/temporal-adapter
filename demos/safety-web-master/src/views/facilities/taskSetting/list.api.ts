import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/spot/plan/list',
  save = '/safety/spot/plan/add', // 新增
  exportXls = '/safety/spot/plan/export', // 导出
  changeStatus = '/safety/spot/plan/modifyStatus', // 删除
  fieldTree = '/safety/field/fieldChildTree', // 应用项目列表
  modifyDormancy = '/safety/spot/plan/modifyDormancy'
}

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