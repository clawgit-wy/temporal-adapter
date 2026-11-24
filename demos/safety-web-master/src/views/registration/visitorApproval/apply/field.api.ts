import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo',
  saveAndUpdateProcess = '/safety/visitor/authorization/saveAndUpdateProcess',
  /** 校验员工是否为主管级以上 */
  checkEmpNo = '/safety/visitor/authorization/checkEmpNo',
  /** 校验员工是否为科长级以上 */
  checkSectionChief = '/safety/visitor/authorization/checkSectionChief',
  /** 获取当前审批节点信息 */
  getProcessCurDealHandler = '/safety/goodsOut/process/getProcessCurDealHandler',
}

/**
 * 根据工号获取人员信息
 */
export const getEmployeeByWorkNo = (params) => {
  return defHttp.post({ url: Api.getEmployeeByWorkNo, params }, { successMessageMode: 'none' });
};

/**
 * 提交授权申请（发起流程）
 */
export const saveAndUpdateProcess = (params) => {
  return defHttp.post(
    {
      url: Api.saveAndUpdateProcess,
      params,
      headers: { 'Content-Type': ContentTypeEnum.JSON },
    },
    { successMessageMode: 'none' },
  );
};

/**
 * 校验员工是否为主管级以上
 * @param empNo 员工号
 */
export const checkEmpNo = (empNo: string) => {
  return defHttp.post({ url: Api.checkEmpNo, params: { empNo } }, { successMessageMode: 'none' });
};

/**
 * 校验员工是否为科长级以上
 * @param empNo 员工号
 */
export const checkSectionChief = (empNo: string) => {
  return defHttp.post({ url: Api.checkSectionChief, params: { empNo } }, { successMessageMode: 'none' });
};

/**
 * 获取当前审批节点信息
 * @returns { curNodeId, curNodeHandler, ... }
 */
export const getProcessCurDealHandler = (params: { id: string; flowId: string }) => {
  return defHttp.post({ url: Api.getProcessCurDealHandler, params }, { successMessageMode: 'none' });
};

