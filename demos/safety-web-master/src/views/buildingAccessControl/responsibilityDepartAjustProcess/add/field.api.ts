import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';
import { message } from 'ant-design-vue';

export enum Api {
  fieldTree = '/safety/field/fieldChildTree', // 项目树
  getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo', // 根据工号获取人员信息
  checkBuildRoleType = '/build/safety/buildProcess/checkBuildRoleType', // 根据工号获取人员楼宇门禁角色
  getGuardInfoListByDept = '/safety/buildManage/getGuardInfoListByDept', // 根据责任部门查询授权门禁
  saveAndCreateProcess = '/safety/buildManage/saveAndCreateProcess', // 保存数据创建流程
  getDetailById = '/safety/buildManage/getDetailById', // 根据id查询明细
  approvalProcess = '/safety/buildManage/approvalProcess', // 审批流程
  getRoleList = '/safety/buildManage/getRoleList', // 获取eip角色
  getApproverList = '/safety/process/getApproverList', // 获取当前审批人
  managerList = '/safety/user/list',//门禁系统管理员
  roles = '/safety/role/selectSysRoles', //获取角色列表
}

// 获取申请人数据
export const getEmployeeByWorkNo = (params) => {
  return defHttp.post({ url: Api.getEmployeeByWorkNo, params })
}

// 获取申请人数据
export const checkBuildRoleType = (params) => {
  return defHttp.post({ url: Api.checkBuildRoleType, params })
}

// 获取项目字典
export const fieldTree = (params) => {
  return defHttp.post({ url: Api.fieldTree, params })
};

// 根据责任部门查询授权门禁
export const getGuardInfoListByDept = (params) => {
  return defHttp.post({ url: Api.getGuardInfoListByDept, params ,headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } })
};

// 保存并创建流程
export const saveAndCreateProcess = (params) => {
  return defHttp.post({ url: Api.saveAndCreateProcess, params, headers: { 'Content-Type': ContentTypeEnum.JSON } })
};

// 获取表单详情
export const getDetailById = (params) => {
  return defHttp.post({ url: Api.getDetailById, params })
}

// 获取eip角色
export const getRoleList = (params) => {
  return defHttp.post({ url: Api.getRoleList, params })
}

// 审批
export const approvalProcess = (params) => {
  return defHttp.post({ url: Api.approvalProcess, params, headers: { 'Content-Type': ContentTypeEnum.JSON }  })

}

// 获取当前审批人
export const getApproverList = (params) => {
  return defHttp.get({ url: Api.getApproverList, params })
}

//门禁系统管理员
export const managerList = (params) => {
  return defHttp.post({ url: Api.managerList, params });
};

export const getRoles = (params) => {
  return defHttp.post({ url: Api.roles, params });
};