import {defHttp} from '/@/utils/http/axios';
import {ContentTypeEnum} from '/@/enums/httpEnum';
import {Api} from "/@/views/vehicle/permission/api";

export enum workPhaseApi {
  pageList = '/fireInspection/safety/workStep/pageList', // 消防巡检-风险点台账-作业环节-分页列表
  workStepAdd = '/fireInspection/safety/workStep/add', // 消防巡检-风险点台账-作业环节-添加
  workStepUpload = '/fireInspection/safety/workStep/update', // 消防巡检-风险点台账-作业环节-编辑
  workStepDel = '/fireInspection/safety/workStep/delete', // 消防巡检-风险点台账-作业环节-删除
  workStepBatchDelete = '/fireInspection/safety/workStep/batchDelete', // 消防巡检-风险点台账-作业环节-批量删除
  getWorkDept = '/fireInspection/safety/select/getWorkDept', // 消防巡检-下拉框-作业部门下拉框接口
  exportXls = '/fireInspection/safety/workStep/export',//消防巡检-风险点台账-作业环节-导出
  importXls = '/fireInspection/safety/workStep/import',//消防巡检-风险点台账-作业环节-导入
  download = '/fireInspection/safety/workStep/download',//模板下载
}


/* 消防巡检-风险点台账-作业环节-分页列表 */
export const pageList = (params) => {
  return defHttp.get({
    url: workPhaseApi.pageList,
    params,
    headers: {'Content-Type': ContentTypeEnum.JSON}
  });
};
/* 消防巡检-风险点台账-作业环节-添加 */
export const workStepAdd = (params) => {
  return defHttp.post({url: workPhaseApi.workStepAdd, params});
};
/* 消防巡检-风险点台账-作业环节-编辑 */
export const workStepUpload = (params) => {
  return defHttp.post({url: workPhaseApi.workStepUpload, params});
};
/*消防巡检-风险点台账-作业环节-删除*/
export const workStepDel = (params) => {
  return defHttp.post({url: workPhaseApi.workStepDel, params});
};
/*消防巡检-风险点台账-作业环节-批量删除*/
export const workStepBatchDelete = (params) => {
  return defHttp.post({url: workPhaseApi.workStepBatchDelete, params});
};
/*消防巡检-下拉框-作业部门下拉框接口*/
export const getWorkDept = () => {
  return defHttp.get({url: workPhaseApi.getWorkDept});
};

/*消防巡检-导入模板*/
export const workStepDownload = (params: {}) => {
  return defHttp.get({url: workPhaseApi.download, responseType: 'blob', params})
};

// 获取组织架构
export const selectGroupTree = (params = {}) => {
  return defHttp.post({url: Api.selectGroupTree, params}).then((response) => {
    // 处理返回的数据，确保每个节点的 key 和 value 是一致的
    const formattedData = formatTreeData(response.dataList || []);
    return {dataList: formattedData}; // 返回格式化后的数据
  });
};

// 格式化树形数据
const formatTreeData = (data) => {
  return data.map((node) => ({
    key: node.key,
    value: node.key, // 确保 value 与 key 一致
    title: node.title,
    children: node.children ? formatTreeData(node.children) : undefined,
  }));
};
