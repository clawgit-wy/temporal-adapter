import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
const { createConfirm } = useMessage();

export enum Api {
  fieldTree = '/safety/field/fieldChildTree', // 项目树
  view = '/safety/patrol/record/getRecordDetails',
  // 获取当前节点
  getNode = '/safety/process/getNowNode',
  // 审批
  audit = '/safety/spot/process/audit',
  employeeList = '/safety/employee/list', // 获取人员列表
  getApproverList = '/safety/process/getApproverList', // 获取当前审批人
  updateAndAudit = '/safety/patrol/plan/audit',
  updateTransfer = '/safety/patrol/plan/updateTransfer',
  modifyCurDealHandler = '/safety/patrol/plan/modifyCurDealHandler', // 更新流程审批人
  getDealHistoryList = '/safety/patrol/record/getDealHistoryList', // 获取处理记录
  getHistoryChange = '/safety/patrol/record/getPageList', // 获取修改历史记录
}

export const getHistoryChange = (params) =>{
  return defHttp.post({ url: Api.getHistoryChange, params })
};

// 获取项目字典
export const fieldTree = (params) =>{
  return defHttp.post({ url: Api.fieldTree, params })
};
// 获取表单详情
export const view = (params) => {
  return defHttp.post({url: Api.view, params})
}
// 获取当前审批节点
export const getNode = (params) => {
  return defHttp.get({url:Api.getNode, params})
}

// 审批
export const audit = (params) => {
  return defHttp.get({url: Api.audit, params})
}

// 获取人员列表
export const employeeList = (params) => {
  return defHttp.get({url: Api.employeeList, params})
}
// 获取当前审批人
export const getApproverList = (params) => {
  return defHttp.get({url: Api.getApproverList, params})
}
// 审批
export const updateAndAudit = (params) => {
  return defHttp.post({url: Api.updateAndAudit, params})

}

// 转派
export const updateTransfer = (params) => {
  return defHttp.post({url: Api.updateTransfer, params})

}
// 更新
export const modifyCurDealHandler = (params) => {
  return defHttp.post({url: Api.modifyCurDealHandler, params})
}
// 获取历史记录
export const getDealHistoryList = (params) => {
  return defHttp.post({url: Api.getDealHistoryList, params})
}