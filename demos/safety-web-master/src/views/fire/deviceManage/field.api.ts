import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  listPage='/safety/hot/work/lensList',//列表
  exportData='/safety/hot/work/export',//导出
}

/**
 * 出门证权限管理-分页查询
 * @param params
 */
export const getListPage = (params) => {
  console.log('请求列表', params);
  return defHttp.post({ url: Api.listPage, params }, { successMessageMode: 'none' });
};
