import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/car/white/list',
  save = '/safety/car/white/update', // 添加 更新
  delete = '/safety/car/white/delete',
  queryMember = '/safety/employee/list', //根据员工号获取员工姓名
  import = '/safety/car/white/import',
  download = '/safety/car/white/download', // 下载
}

// 获取列表数据
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};

/**
 * 添加
 */
export const saveOrUpdate = (params, isUpdate) => {
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
