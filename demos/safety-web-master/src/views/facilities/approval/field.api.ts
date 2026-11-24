import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
const { createConfirm } = useMessage();

export enum Api {
  saveAndCreateProcess = '/safety/monitor/post/saveAndCreateProcess', // 创建并发起流程
  updateProcess = '/safety/monitor/post/updateAndAudit', // 创建并发起流程
  fieldTree = '/safety/field/fieldChildTree', // 项目树
  getInfo = '/safety/monitor/post/detail',
  // 获取当前节点
  getNode = '/safety/process/getNowNode',
  // 获取字段权限
  getFieldRule = '/safety/monitor/post/getAuthFields',
  // 获取点检详情
  view = '/safety/spot/process/view',
  // 审批
  audit = '/safety/spot/process/audit',
  // 延期整改
  delay = '/safety/spot/process/delay',
  employeeList = '/safety/employee/list', // 获取人员列表
  getApproverList = '/safety/process/getApproverList', // 获取当前审批人
  circulate = '/safety/process/circulate', //  传阅
  getProcessId = '/safety/process/getProcessId'
}

export const saveAndCreateProcess = (params = {}) => {
  return defHttp.post({url: Api.saveAndCreateProcess, params, headers: {'Content-Type': 'application/json;charset=UTF-8'}})
}
export const updateProcess = (params = {}) => {
  return defHttp.post({url: Api.updateProcess, params, headers: {'Content-Type': 'application/json;charset=UTF-8'}})
}
// 获取项目字典
export const fieldTree = (params) =>{
  return defHttp.post({ url: Api.fieldTree, params })
};
// 获取表单详情
export const getInfo = (id) => {
  return defHttp.post({url: Api.getInfo, params: {id}})
}
// 获取当前审批节点
export const getNode = (params) => {
  return defHttp.get({url:Api.getNode, params})
}
// 获取字段权限
export const getFieldRule = (params) => {
  return defHttp.get({url: Api.getFieldRule, params})
}
// 获取点检详情
export const view = (params) => {
  return defHttp.get({url: Api.view, params})
}
// 审批
export const audit = (params) => {
  return defHttp.get({url: Api.audit, params})
}
// 延期整改
export const delay = (params) => {
  return defHttp.get({url: Api.delay, params})
}
// 获取人员列表
export const employeeList = (params) => {
  return defHttp.get({url: Api.employeeList, params})
}
// 获取当前审批人
export const getApproverList = (params) => {
  return defHttp.get({url: Api.getApproverList, params})
}
// 传阅
export const circulate = (params) => {
  return defHttp.post({url: Api.circulate, params})
}
// 获取processId
export const getProcessId = (params) => {
  return defHttp.get({url: Api.getProcessId, params})
}