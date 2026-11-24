import { defHttp } from '/@/utils/http/axios';
export enum Api {
  list = '/park/safety/parkPowerProcess/getPageList', // 获取大门权限申请列表
  checkGateRoleType = '/gate/safety/gateProcess/checkGateRoleType', // 根据工号获取人员大门权限角色
  
}

// 获取申请人数据
export const checkGateRoleType = (params) => {
  return defHttp.post({ url: Api.checkGateRoleType, params })
}
/**
 * 获取大门权限申请列表
 * @param params
 */
export const list = (params) => {
  return defHttp.post({ url: Api.list, params });
};


