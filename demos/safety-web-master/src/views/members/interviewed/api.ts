import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/visitorSet/list',
  save = '/safety/visitorSet/add', // 添加
  edit = '/safety/visitorSet/update', // 编辑
  delete = '/safety/visitorSet/delete', // 删除
}

export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};

/**
 * 单个删除
 * @param params
 * @param confirm
 */
 export const deleteSingle = (params, confirm = false) => {
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

/**
 * 添加
 */
export const saveOrUpdate = (params, isUpdate) => {
  if (unref(isUpdate)) {
    return defHttp.put({ url: Api.edit, params });
  } else {
    return defHttp.post({ url: Api.save, params });
  }
};

// 获取人员岗位
export const getEmpPost = () => {
  return defHttp.post({ url: '/safety/empPost/getPost'})
}