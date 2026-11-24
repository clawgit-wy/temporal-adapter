import { unref } from 'vue';
import { defHttp, httpConfig } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  // list = '/sys/message/sysMessageTemplate/list',table/getDemoList
  // list = '/mock/table/getDemoList',safety/black/list
  list = '/safety/region/list',
  save = '/safety/region/add', // 添加
  delete = '/safety/region/delete', // 删除
  exportXls = '/safety/region/export', // 导出
  edit = '/safety/region/update', // 编辑
  importXls = '/safety/region/import', // 导入
  factoryList = '/safety/field/selectLinkageList', //获取厂区列表
  download = '/safety/region/download', // 下载
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
 * 下载模板
 * @param params
 * 
 */
export const download = (params) =>{
  return defHttp.get({ url: Api.download, responseType: 'blob', params })
};

/**
 * 获取厂区列表
 * @param params
 *
 */
export const factoryList = (params) => {
  return defHttp.post({ url: Api.factoryList, params });
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

export const importXls = Api.importXls;

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
