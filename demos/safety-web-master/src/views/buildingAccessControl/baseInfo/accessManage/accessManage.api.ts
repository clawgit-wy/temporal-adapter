import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/build/safety/buildDevice/list',
  save = '/buildingAccess/accessManage/add', // 添加门禁
  delete = '/buildingAccess/accessManage/delete', // 删除门禁
  deleteBatch = '/buildingAccess/accessManage/deleteBatch', // 批量删除门禁
  exportXls = '/buildingAccess/accessManage/export', // 导出门禁
  edit = '/build/safety/buildDevice/update', // 编辑门禁
  importXls = '/buildingAccess/accessManage/import', // 门禁导入
  download = '/buildingAccess/accessManage/download', // 下载
}

/**
 * 获取门禁列表
 * @param params
 *
 */
export const list = (params) => {
  return defHttp.post({ url: Api.list, params });
};

/**
 * 下载列表
 * @param params
 *
 */
export const download = (params: {}) => {
  return defHttp.get({ url: Api.download, responseType: 'blob', params });
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
 * 门禁导出
 * @param params
 *
 */
export const exportXls = Api.exportXls;

export const importXls = Api.importXls;

/**
 * 批量删除
 * @param params
 * @param confirm
 */
export const deleteBatch = (params, confirm = false) => {
  return new Promise((resolve, reject) => {
    const doDelete = () => {
      resolve(defHttp.delete({ url: Api.deleteBatch, params }, { joinParamsToUrl: true }));
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
 * 添加门禁
 */
export const saveOrUpdate = (params, isUpdate) => {
  if (unref(isUpdate)) {
    return defHttp.post({ url: Api.edit, params });
  } else {
    return defHttp.post({ url: Api.save, params });
  }
}; 
