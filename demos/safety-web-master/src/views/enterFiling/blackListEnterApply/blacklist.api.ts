import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo', // 根据工号获取人员信息
  saveAndCreateProcess = '/entry/safety/entryBlackRecord/saveAndUpdate', // 保存并提交流程
  getProcessDetailById = '/entry/safety/entryBlackRecord/getDetail', // 根据id查询流程明细
  approvalProcess = '/enter/safety/blacklistEnter/approvalProcess', // 审批流程
  importBlacklist = '/entry/safety/entryBlackRecord/import', // 导入黑名单
  downloadBlacklistTemplate = '/entry/safety/entryBlackRecord/download', // 下载模板
  getProcessCurDealHandler = '/safety/process/getProcessCurDealHandler', // 获取当前审批人
  //根据导入批次号查询二级黑名单列表
  getBlacklistByBatchNo = '/safety/entry/entryConfig/getBlankForVersionNo',
  checkAuthEmpInfo = '/enter/auth/checkAuthEmpInfoNew', // 获取登录人备案厂区及授权时间
  save = '/entry/safety/entryBlackRecord/checkInfo',//新增、编辑
  ftList = '/enter/auth/getSearchParams', // 获取查询条件数据
}

// 获取申请人数据
export const getEmployeeByWorkNo = (params) => {
  return defHttp.post({ url: Api.getEmployeeByWorkNo, params });
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

// 根据id查询流程明细
export const getProcessDetailById = (params) => {
  return defHttp.get({ url: Api.getProcessDetailById, params });
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

// 获取当前审批人
export const getProcessCurDealHandler = (bpmFlowId) => {
  return defHttp.post({ url: Api.getProcessCurDealHandler, params: { bpmFlowId } });
};

//根据导入批次号查询二级黑名单列表
export const getBlacklistByBatchNo = (params) => {
  return defHttp.post({ url: Api.getBlacklistByBatchNo, params });
};
// 校验认证信息
export const checkAuthEmpInfo = (params) =>{
  return defHttp.post({ url: Api.checkAuthEmpInfo, params })
};
/**
 * 添加或更新人员
 */
export const saveOrUpdate = (params) => {
  // 新增和编辑都使用同一个接口
  return defHttp.post({ url: Api.save, params ,headers: {
    'Content-Type': 'application/json',
  },});
};

/* 获取查询条件数据 */
export const ftList = (params) => {
  return defHttp.post({ url: Api.ftList, params, headers: { 'Content-Type': ContentTypeEnum.JSON } });
};