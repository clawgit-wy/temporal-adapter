import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/spot/device/list',
  add = '/safety/spot/device/add', // 新增
  update = '/safety/spot/device/update', // 更新
  delete = '/safety/spot/device/batchDelete', // 删除
  templateUrl = '/safety/spot/device/download', // 导入模板地址
  exportXls = '/safety/spot/device/export', // 数据导出
  importXls = '/safety/spot/device/import', // 数据导入
  setEmp = '/safety/spot/device/updateCheckEmp', // 一键应用临时点检人
  bathSetEmp = '/safety/spot/device/batchUpdateCheckEmp', // 一键应用点检人
  cancelEmp = '/safety/spot/device/batchCancelCheckEmp', // 一键取消临时点检人
  fieldTree = '/safety/field/fieldChildTree', // 项目树
  spotList = '/safety/spot/rule/list', // 巡检标准列表
}

/**
 * 获取黑名单列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};

// 获取项目字典
export const fieldTree = (params) =>{
  return defHttp.post({ url: Api.fieldTree, params })
};

/**
 * 添加
 */
export const saveOrUpdate = (params, isUpdate) => {
  if (isUpdate) {
    return defHttp.post({url: Api.update, params})
  }
  return defHttp.post({ url: Api.add, params });
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


export const download = (params: {}) =>{
  return defHttp.get({ url: Api.templateUrl, responseType: 'blob', params })
};
export const spotList = (params) => {
  return defHttp.post({ url: Api.spotList, params });
}
// 一键应用临时点检人
export const setEmp = (params) => {
  return defHttp.post({url: Api.setEmp, params})
}
// 一键应用点检人
export const bathSetEmp = (params) => {
  return defHttp.post({url: Api.bathSetEmp, params})
}

// 一键取消临时点检人
export const cancelEmp = (params) => {
  return new Promise((resolve, reject) => {
    const doCancel = () => {
      resolve(defHttp.post({url: Api.cancelEmp, params}));
    };
      createConfirm({
        iconType: 'warning',
        title: '取消临时点检人',
        content: '确定要取消临时点检人吗？',
        onOk: () => doCancel(),
        onCancel: () => reject(),
      });
  });
};
