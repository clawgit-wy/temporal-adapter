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
  employeeList = '/safety/employee/list', // 获取人员列表
  getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo', // 根据工号获取人员信息
}

export const getEmployeeByWorkNo = (params) => {
  return defHttp.post({ url: Api.getEmployeeByWorkNo, params })
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
// 获取人员列表
export const employeeList = (params) => {
  return defHttp.get({url: Api.employeeList, params})
}