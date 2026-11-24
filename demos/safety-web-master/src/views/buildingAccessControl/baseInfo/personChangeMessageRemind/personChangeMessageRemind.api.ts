import { defHttp } from '/@/utils/http/axios';

enum Api {
  MessageRemindList = '/build/safety/buildInfoRecord/query',
  UpdateMessageRemind = '/build/safety/buildInfoRecord/update',
}

/**
 * 获取人员变动消息提醒列表
 * @param params 
 */
export function getPersonChangeMessageRemindList(params) {
  return defHttp.post({ url: Api.MessageRemindList, params });
}

/**
 * 更新人员变动消息提醒
 * @param params 
 */
export function updatePersonChangeMessageRemind(params) {
  return defHttp.post({ url: Api.UpdateMessageRemind, params });
}