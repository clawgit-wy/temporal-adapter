import { unref } from 'vue';
import { defHttp, httpConfig } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  // list = '/sys/message/sysMessageTemplate/list',table/getDemoList
  // list = '/mock/table/getDemoList',safety/black/list
  list = '/safety/black/list',
  save = '/safety/black/add', // 添加人员
  delete = '/safety/black/delete', // 删除人员
  exportXls = '/safety/black/export', // 导出人员
  edit = '/safety/black/update', // 编辑人员
  importXls = '/safety/black/import', // 人员导入
  download = '/safety/black/download', // 下载
  tasksWebMessage = '/safety/black/tasksWebMessage', // 拉取任务
  identityInfoQuerySelect = '/safety/black/querySelect', // 身份信息
}

/**
 * 获取黑名单列表
 * @param params
 *
 */
export const list = (params) => {
  return defHttp.post({ url: Api.list, params });
};

/**
 * 拉取增量数据
 * @param params
 *
 */
export const tasksWebMessage = (params) => {
  return defHttp.post({ url: Api.tasksWebMessage, params });
};


/**
 * 拉取身份信息筛选项
 * @param params
 *
 */
export const identityInfoQuerySelect = (params) => {
  return defHttp.get({ url: Api.identityInfoQuerySelect, params });
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
 * 黑名单导出
 * @param params
 *
 */
export const exportXls = Api.exportXls;
//  export const exportXls = (params) =>{
//   return defHttp.post({ url: Api.exportXls, params })
// } ;

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
 * 添加人员
 */
export const saveOrUpdate = (params, isUpdate) => {
  if (unref(isUpdate)) {
    return defHttp.put({ url: Api.edit, params });
  } else {
    return defHttp.post({ url: Api.save, params });
  }
};
