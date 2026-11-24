import { unref } from 'vue';
import { defHttp, httpConfig } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  // list = '/sys/message/sysMessageTemplate/list',table/getDemoList
  // list = '/mock/table/getDemoList',safety/black/list
  list = '/safety/external/list',
  save = '/safety/external/addOrUpdate', // 添加人员
  delete = '/safety/external/delete', // 删除人员
  exportXls = '/safety/external/export', // 导出人员
  edit = '/safety/external/addOrUpdate', // 编辑人员
  download = '/safety/staffEvection/download', // 下载模板

}

/**
 * 获取列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};


/**
 * 下载列表
 * @param params
 * 
 */
// export const download = (params: {}) =>{
//   return defHttp.get({ url: Api.download, responseType: 'blob', params })
// };

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

/**
 * 添加人员
 */
export const saveOrUpdate = (params) => {
  return defHttp.post({ url: Api.save, params });
};


export const download = (params: {}) =>{
  return defHttp.get({ url: Api.download, responseType: 'blob', params })
};