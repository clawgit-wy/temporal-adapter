import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  // list = '/sys/message/sysMessageTemplate/list',table/getDemoList
  // list = '/mock/table/getDemoList',safety/black/list
  list = '/safety/empower/list',
  save = '/safety/empower/add', // 添加
  delete = '/safety/empower/delete', // 删除
  exportXls = '/safety/empower/export', // 导出
  edit = '/safety/empower/update', // 编辑
  detail = '/safety/empower/view', // 详情
  importXls = '/safety/empower/import', // 导入
  factoryList = '/safety/field/list', //获取厂区列表
  device = '/safety/region/getDeviceNos',
  download = '/safety/empower/download', // 下载
}

/**
 * 获取列表
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
 * 获取详情
 * @param params
 *
 */
export const detail = (params) => {
  return defHttp.post({ url: Api.detail, params });
};

/**
 * 获取厂区列表
 * @param params
 *
 */
export const getfactoryList = (params) => {
  return defHttp.post({ url: Api.factoryList, params });
};

/**
 * 获取绑定的设备
 * @param params
 *
 */
export const device = (params) => {
  return defHttp.post({ url: Api.device, params });
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
