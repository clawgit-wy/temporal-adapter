import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/field/list', // 列表接口
  moveList = '/safety/field/move', // 接口
  delete = '/safety/field/delete', // 删除接口
  save = '/safety/field/add', // 添加接口
  edit = '/safety/field/update', // 添加接口
  tabs = '/safety/field/fieldManageTree', // 获取一级tabs
  saveAndCreateProcess = '/safety/monitor/post/saveAndCreateProcess', // 创建并发起流程
  selectLinkageList = '/safety/field/selectLinkageList', // 获取厂区列表
}

/**
 * 获取列表
 * @param params
 * @param confirm
 */
export const list = (params) => defHttp.post({ url: Api.list, params });

/**
 * 移动借口
 * @param params
 */
export const moveList = (params) => defHttp.post({ url: Api.moveList, params });
export const getFactoryList = () => defHttp.post({url: Api.selectLinkageList})

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
    return defHttp.post({ url: Api.edit, params });
  } else {
    return defHttp.post({ url: Api.save, params });
  }
};
export const getTabs = (params={}) => {
  return defHttp.post({url: Api.tabs, params})
}

export const saveAndCreateProcess = (params = {}) => {
  return defHttp.post({url: Api.saveAndCreateProcess, params})
}