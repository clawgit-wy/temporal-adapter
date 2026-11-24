import { unref } from 'vue';
import { defHttp, httpConfig } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  // list = '/sys/message/sysMessageTemplate/list',table/getDemoList
  // list = '/mock/table/getDemoList',safety/black/list
  list = '/safety/visitor/detailList',
  save = '/safety/visitor/add', // 添加
  edit = '/safety/visitor/update', // 编辑
  exportXls = '/safety/visitor/export', // 导出
  factoryList='/safety/field/list', //获取厂区列表
}

/**
 * 获取黑名单列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
} ;

/**
 * 获取厂区列表
 * @param params
 * 
 */
 export const factoryList = (params) =>{
  return defHttp.post({ url: Api.factoryList, params })
} ;

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

/**
 * 导出
 * @param params
 * 
 */
 export const exportXls = Api.exportXls;

