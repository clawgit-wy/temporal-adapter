import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/retention/notice/list',
  save = '/safety/retention/notice/add', // 更新
  delete = '/safety/retention/notice/deleteById',
}

/**
 * 列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
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
      resolve(defHttp.post({ url: Api.delete, params: {noticeId: params.id} }));
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
