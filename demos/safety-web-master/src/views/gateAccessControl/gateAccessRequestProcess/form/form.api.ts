import { defHttp } from '/@/utils/http/axios';

export enum Api {
  save = '/gate/safety/gateProcess/add', // 新增
  list = '/safety/datasend/list', // 列表接口
  getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo', // 根据工号获取人员信息
  checkBuildRoleType = '/build/safety/buildProcess/checkBuildRoleType', // 根据工号获取人员楼宇门禁角色
  getDetailById = '/park/safety/parkEmployeePower/getDetailById', // 根据userCode查询当前授权厂区
  saveAndCreateProcess = '/park/safety/parkPowerProcess/saveAndCreateProcess', // 保存并提交流程
  downloadEmpTemplate = '/park/safety/parkPowerProcess/download', // 下载模板
  getListByUserCode = '/park/safety/parkPowerProcess/getListByUserCode', // 根据userCode的集合查询车辆信息明细
  getProcessDetailById = '/park/safety/parkPowerProcess/getDetailById', // 根据id查询流程明细
  approvalProcess = '/park/safety/parkPowerProcess/approvalProcess', // 审批流程
  getFactoryById = '/park/safety/parkEmployeePower/getDetailById', // 根据userCode查询当前授权厂区
  getProcessCurDealHandler = '/safety/process/getProcessCurDealHandler', // 获取当前审批人
}

export const list = (params) => {
  return defHttp.get({ url: Api.list, responseType: 'blob', params });
};

// 获取申请人数据
export const getEmployeeByWorkNo = (params) => {
  return defHttp.post({ url: Api.getEmployeeByWorkNo, params });
};

//  根据工号获取人员楼宇门禁角色
export const checkBuildRoleType = (params) => {
  return defHttp.post({ url: Api.checkBuildRoleType, params });
};

// 根据userCode查询当前授权厂区
export const getDetailById = (params) => {
  return defHttp.post({ url: Api.getDetailById, params });
};

// 保存并提交流程
export const saveAndCreateProcess = (params) => {
  return defHttp.post({
    url: Api.saveAndCreateProcess,
    params,
    headers: {
      'Content-Type': 'application/json',
    },
  });
};

// 根据userCode的集合查询车辆信息明细
export const getListByUserCode = (params) => {
  return defHttp.post({
    url: Api.getListByUserCode,
    params,
    headers: {
      'Content-Type': 'application/json',
    },
  });
};

// 根据id查询流程明细
export const getProcessDetailById = (params) => {
  return defHttp.post({ url: Api.getProcessDetailById, params });
};

// 审批流程
export const approvalProcess = (params) => {
  return defHttp.post({
    url: Api.approvalProcess,
    params,
    headers: {
      'Content-Type': 'application/json',
    },
  });
};

// 根据userCode查询当前授权厂区
export const getFactoryById = (params) => {
  return defHttp.post({ url: Api.getFactoryById, params });
};

// 获取当前审批人
export const getProcessCurDealHandler = (bpmFlowId) => {
  return defHttp.post({ url: Api.getProcessCurDealHandler, params: { bpmFlowId } });
};
