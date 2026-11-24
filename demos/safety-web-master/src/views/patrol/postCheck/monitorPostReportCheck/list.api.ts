import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/monitor/post/list',
  save = '/safety/carOutRule/add', // 新增
  exportXls = '/safety/monitor/post/exportData',
  delete = '/safety/carOutRule/deleteById', // 删除
  groupTree= '/safety/group/selectGroupTree', // 获取组织架构
  getEmpPost = '/safety/empPost/getPost', // 获取岗位列表
  fieldTree = '/safety/field/fieldChildTree', // 项目树
}

/**
 * 获取黑名单列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params, headers: {'Content-Type': 'application/json;charset=UTF-8'} })
};

/**
 * 添加人员
 */
export const saveOrUpdate = (params, isUpdate) => {
  return defHttp.post({ url: Api.save, params });
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
// 获取项目字典
export const fieldTree = (params) =>{
  return defHttp.post({ url: Api.fieldTree, params })
};
