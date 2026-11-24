import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/carOutRule/list',
  perList = '/safety/personCarOutRule/pageList',
  save = '/safety/carOutRule/add', // 新增
  perSave = '/safety/personCarOutRule/add', // 新增
  perUpdate = '/safety/personCarOutRule/update', // 新增
  delete = '/safety/carOutRule/deleteById', // 删除
  perDelete = '/safety/personCarOutRule/delete', // 删除
  groupTree= '/safety/group/selectGroupTree', // 获取组织架构
  getEmpPost = '/safety/empPost/getPost', // 获取岗位列表
  download = '/safety/staffEvection/download', // 下载模板
  searchPerson = '/safety/employee/list', // 下载模板
  downAll = '/safety/carOutRule/downAll'
}

/**
 * 获取黑名单列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};
export const perList = (params) =>{
  return defHttp.post({ url: Api.perList, params })
};
/**
 * 添加人员
 */
export const saveOrUpdate = (params, isUpdate) => {
  return defHttp.post({ url: Api.save, params });
};
export const searchPerson = (params) => {
  return defHttp.post({ url: Api.searchPerson, params });
};

export const perSave = (params, isUpdate) => {
  if (isUpdate) {
    return defHttp.post({ url: Api.perUpdate, params });
  }else{
    return defHttp.post({ url: Api.perSave, params });
  }
};

export const perDelete = (params, confirm=false) => {

  return new Promise((resolve, reject) => {
    const doDelete = () => {
      resolve(defHttp.post({ url: Api.perDelete, params }));
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
export const remove = (params, confirm=false) => {

  return new Promise((resolve, reject) => {
    const doDelete = () => {
      resolve(defHttp.post({ url: Api.delete, params }));
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
export const downAll = () => {
  return defHttp.post({url: Api.downAll})
}