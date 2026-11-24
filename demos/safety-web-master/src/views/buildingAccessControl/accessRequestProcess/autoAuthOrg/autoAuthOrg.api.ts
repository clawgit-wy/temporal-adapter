import { defHttp } from '/@/utils/http/axios';

export enum Api {
  List = '/build/safety/autoDept/list',
  Terminate = '/build/safety/autoDept/update',
  exportXls = '/buildingAccessControl/accessRequest/autoAuthOrg/export',
}

/**
 * 获取自动授权组织列表
 * @param params 查询参数
 */
export function listAutoAuthOrg(params) {
  return defHttp.post({
    url: Api.List,
    params,
  });
}

/**
 * 终止自动授权
 * @param id 记录ID
 */
export function terminateAutoAuth(id: string, autoFlag: string = '1') {
  return defHttp.post({
    url: Api.Terminate,
    params: { id, autoFlag },
  });
}
