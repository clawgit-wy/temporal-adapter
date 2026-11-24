import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/retention/white/list',
  save = '/safety/retention/white/add', // 更新
  delete = '/safety/retention/white/deleteById',
  queryMember = '/safety/employee/list', //根据员工号获取员工姓名
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
