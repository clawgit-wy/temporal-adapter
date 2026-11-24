import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  List = '/build/safety/isuuedLog/list',
  exportXls = '/build/safety/isuuedLog/exportData',
  downBySelf = '/build/safety/isuuedLog/downBySelf',//手动下发
}

/**
 * 获取门禁授权日志列表
 * @param params 查询参数
 */
export function list(params) {
  return defHttp.post({
    url: Api.List,
    params,
  });
}

/**
 * 手动下发
 */
export function downBySelf() {
  return defHttp.post({
    url: Api.downBySelf,
    params:{},
  });
}