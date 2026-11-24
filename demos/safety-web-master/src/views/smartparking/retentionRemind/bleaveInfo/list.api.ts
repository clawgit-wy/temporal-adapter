import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/staffEvection/list',
  save = '/safety/staffEvection/add', // 添加
  update = '/safety/staffEvection/update', // 更新
  delete = '/safety/retention/white/deleteById',
  queryMember = '/safety/employee/list', //根据员工号获取员工姓名
  import = '/safety/staffEvection/importData',
  download = '/safety/staffEvection/download', // 下载
  groupTree= '/safety/group/selectGroupTree', // 获取组织架构
}

// 获取列表数据
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};

/**
 * 添加
 */
export const saveOrUpdate = (params, isUpdate) => {
  console.log(isUpdate.value);
  
  if (isUpdate.value) {
    return defHttp.post({url: Api.update, params})
  }
  return defHttp.post({ url: Api.save, params });
};
export const download = (params: {}) =>{
  return defHttp.get({ url: Api.download, responseType: 'blob', params })
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
export const getMemberInfo = (empNo) => {
  return defHttp.post({url: Api.queryMember, params: {empNo}})
}
// 获取组织架构
export const getGroupTree = (key:string) => {
  return defHttp.post({url: Api.groupTree, params: {key}})
}