import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  getFireApproveTree = '/safety/hot/work/getFireApproveList', // 摄像头树
  getMonitorVideoUrl = '/safety/hot/work/getMonitorVideoUrl', // 查询监控设备视频链接
}

// 获取组织架构
export const getFireApproveTree = (params) => {
  return defHttp.post({url: Api.getFireApproveTree, params,headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } })
}

// 查询监控设备视频链接
export const getMonitorVideoUrl = (params) => {
  return defHttp.post({url: Api.getMonitorVideoUrl, params,headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } })
}