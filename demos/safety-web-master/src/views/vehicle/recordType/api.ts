import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
const { createConfirm } = useMessage();
export enum Api {
  list = '/dict/safetyDict/supper/getDictList', // 备案类型配置
  saveOrUpdateDict = '/dict/safetyDict/supper/saveOrUpdateDict',
  deleteDict = '/dict/safetyDict/supper/deleteDict',
}

export const list = (params) => {
  return defHttp.post({ url: Api.list, params });
};

export const saveOrUpdateDict = (params = {}) => {
  return defHttp.post({url: Api.saveOrUpdateDict, params})
}

export const deleteDict = (params = {}) => new Promise((resolve, reject) => {
  createConfirm({
    iconType: 'warning',
    title: '删除',
    content: '确定要删除吗？',
    onOk: () => resolve(defHttp.post({url: Api.deleteDict, params})),
    onCancel: () => reject(),
  })
})