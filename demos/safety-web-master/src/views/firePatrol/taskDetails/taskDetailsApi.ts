import {defHttp} from '/@/utils/http/axios';

export enum taskDetailsApi {
  taskInfoPageList = '/fireInspection/safety/taskInfo/pageList', // 分页数据列表查询接口
  taskDetailsPageList = '/fireInspection/safety/taskInfo/pageDetailList', // 分页数据列表查询接口
  taskDetailsExportXls = '/fireInspection/safety/taskInfo/export',//数据导出接口
}

/* 分页数据列表查询接口*/
export const taskInfoPageList = (params) => {
  return defHttp.get({url: taskDetailsApi.taskInfoPageList,params})
};
/* 分页数据列表查询接口*/
export const taskDetailsPageList = (params) => {
  return defHttp.get({url: taskDetailsApi.taskDetailsPageList,params})
};


