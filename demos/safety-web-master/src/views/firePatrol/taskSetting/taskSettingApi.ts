import {defHttp} from '/@/utils/http/axios';
import {ContentTypeEnum} from "/@/enums/httpEnum";

export enum taskSettingApi {
  taskSetPageList = '/fireInspection/safety/taskSet/pageList', // 消防巡检-任务设置-分页列表
  taskSetAdd = '/fireInspection/safety/taskSet/add', // 消防巡检-任务设置-添加
  taskSetUpload = '/fireInspection/safety/taskSet/update', // 消防巡检-任务设置-编辑
  taskSetDel = '/fireInspection/safety/taskSet/delete', // 消防巡检-任务设置-删除
  taskSetBatchDelete = '/fireInspection/safety/taskSet/batchDelete', // 消防巡检-任务设置-批量删除
  taskSetUpdateStatus = '/fireInspection/safety/taskSet/updateStatus',// 消防巡检-任务设置-状态修改
  taskSetPageSubDimensionList = '/fireInspection/safety/taskSet/pageSubDimensionList',// 消防巡检-任务设置-任务设置检查细分维度-详情页接口
  taskSetGetDefaultTaskObject = '/fireInspection/safety/select/getDefaultTaskObject',//默认任务对象下拉框接口
  taskSetGetCustomTaskObject = '/fireInspection/safety/select/getTaskObject',//自定义任务对象下拉框接口
  taskSetImport = '/fireInspection/safety/taskSet/import',//自定义任务对象下拉框接口
  pageSubDimensionList = '/fireInspection/safety/taskSet/pageSubDimensionList',//任务设置检查细分维度-详情页接口
  taskSetDownload = '/fireInspection/safety/taskSet/download',//模板下载
}

/*任务设置检查细分维度-详情页接口*/
export const pageSubDimensionList = (params) => {
  return defHttp.get({url: taskSettingApi.pageSubDimensionList,params})
};


/*默认任务对象下拉框接口*/
export const taskSetGetDefaultTaskObject = (params) => {
  return defHttp.get({url: taskSettingApi.taskSetGetDefaultTaskObject,params})
};

/*自定义任务对象下拉框接口*/
export const taskSetGetCustomTaskObject = (params) => {
  return defHttp.get({url: taskSettingApi.taskSetGetCustomTaskObject,params})
};


/* 消防巡检-风险点台账-作业环节-分页列表 */
export const taskSetPageList = (params) => {
  return defHttp.get({
    url: taskSettingApi.taskSetPageList,
    params,
    headers: {'Content-Type': ContentTypeEnum.JSON}
  });
};
/* 消防巡检-风险点台账-作业环节-添加 */
export const taskSetAdd = (params) => {
  return defHttp.post({url: taskSettingApi.taskSetAdd, params});
};
/* 消防巡检-风险点台账-作业环节-编辑 */
export const taskSetUpload = (params) => {
  return defHttp.post({url: taskSettingApi.taskSetUpload, params});
};
/*消防巡检-风险点台账-作业环节-删除*/
export const taskSetDel = (params) => {
  return defHttp.post({url: taskSettingApi.taskSetDel, params});
};
/*消防巡检-风险点台账-作业环节-批量删除*/
export const taskSetBatchDelete = (params) => {
  return defHttp.post({url: taskSettingApi.taskSetBatchDelete, params});
};

/*消防巡检-任务设置-状态修改*/
export const taskSetUpdateStatus = (params) => {
  return defHttp.post({url: taskSettingApi.taskSetUpdateStatus, params});
};

/*消防巡检-任务设置-任务设置检查细分维度-详情页接口*/
export const taskSetPageSubDimensionList = (params) => {
  return defHttp.post({url: taskSettingApi.taskSetPageSubDimensionList, params});
};

/*消防巡检-导入模板*/
export const taskSetDownload = (params: {}) => {
  return defHttp.get({url: taskSettingApi.taskSetDownload, responseType: 'blob', params})
};

