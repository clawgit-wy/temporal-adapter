import {defHttp} from '/@/utils/http/axios';
import {ContentTypeEnum} from "/@/enums/httpEnum";

export enum checkStandardApi {
  standardPageList = '/fireInspection/safety/standard/pageList', // 消防巡检-风险点台账-检查标准-分页列表
  standardAdd = '/fireInspection/safety/standard/add', // 消防巡检-风险点台账-检查标准-添加
  standardUpload = '/fireInspection/safety/standard/update', // 消防巡检-风险点台账-检查标准-编辑
  standardDel = '/fireInspection/safety/standard/delete', // 消防巡检-风险点台账-检查标准-删除
  standardBatchDelete = '/fireInspection/safety/standard/batchDelete', // 消防巡检-风险点台账-检查标准-批量删除
  standardExportXls = '/fireInspection/safety/standard/export',//消防巡检-风险点台账-检查标准-导出
  standardImportXls = '/fireInspection/safety/standard/import',//消防巡检-风险点台账-检查标准-导入
  standardDownload = '/fireInspection/safety/standard/download',//模板下载
}


/* 消防巡检-风险点台账-作业环节-分页列表 */
export const standardPageList = (params) => {
  return defHttp.get({
    url: checkStandardApi.standardPageList,
    params,
    headers: {'Content-Type': ContentTypeEnum.JSON}
  });
};
/* 消防巡检-风险点台账-作业环节-添加 */
export const standardAdd = (params) => {
  return defHttp.post({url: checkStandardApi.standardAdd, params});
};
/* 消防巡检-风险点台账-作业环节-编辑 */
export const standardUpload = (params) => {
  return defHttp.post({url: checkStandardApi.standardUpload, params});
};
/*消防巡检-风险点台账-作业环节-删除*/
export const standardDel = (params) => {
  return defHttp.post({url: checkStandardApi.standardDel, params});
};
/*消防巡检-风险点台账-作业环节-批量删除*/
export const standardBatchDelete = (params) => {
  return defHttp.post({url: checkStandardApi.standardBatchDelete, params});
};

/*消防巡检-导入模板*/
export const standardDownload = (params: {}) => {
  return defHttp.get({url: checkStandardApi.standardDownload, responseType: 'blob', params})
};
