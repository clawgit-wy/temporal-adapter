import {unref} from 'vue';
import {defHttp} from '/@/utils/http/axios';
import {useMessage} from '/@/hooks/web/useMessage';

export enum Api {
  getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo', // 根据工号获取人员信息
  checkBuildRoleType = '/build/safety/buildProcess/checkBuildRoleType', // 根据工号获取人员楼宇门禁角色
  validPeople = '/safety/group/validPeople', // 根据工号获取人员楼宇门禁角色
  classPeople = '/safety/employee/classPeopleForAll', // 筛选导入人员是否在管理员名下----不刷选数据了 后端校验 全部返回
  downloadEmpTemplate = '/build/safety/buildProcess/downloadEmpTemplate', // 人员导入模板
  listWithSecret = '/safety/buildPower/listWithSecret', //根据门禁编码查询详情信息
  listWithSecretBatch = '/safety/buildPower/listWithSecretBatch', //根据门禁编码查询详情信息---批量
  saveAndCreateProcess = '/build/safety/buildProcess/saveAndCreateProcess', // 保存并提交流程
  getEipRoleByProcessId = '/build/safety/buildProcess/getEipRoleByProcessId', // 重新提交时，查询责任部门对应EIP角色的接口
  showProcessDetail = '/build/safety/buildProcess/showProcessDetail', // 流程详情
  auditProcess = '/build/safety/buildProcess/auditProcess', // 流程审批
  buildConfigQuery = '/build/safety/buildConfig/query', // 授权配置

}

/**
 * 下载模板
 * @param params
 *
 */
export const downloadEmpTemplate = (params) => {
  return defHttp.get({url: Api.downloadEmpTemplate, responseType: 'blob', params})
};

// 获取申请人数据
export const getEmployeeByWorkNo = (params) => {
  return defHttp.post({url: Api.getEmployeeByWorkNo, params})
}
// 获取申请人数据
export const checkBuildRoleType = (params) => {
  return defHttp.post({url: Api.checkBuildRoleType, params})
}

/**
 * 根据门禁编码查询详情信息
 */
export const listWithSecret = (deviceCode) => {
  return defHttp.post({url: Api.listWithSecret, params: {deviceCode}});
};
/**
 * 根据门禁编码查询详情信息---批量
 */
export const listWithSecretBatch = (params) => {
  return defHttp.post({url: Api.listWithSecretBatch, params});
};
/**
 * 流程详情
 */
export const showProcessDetail = (params) => {
  return defHttp.post({url: Api.showProcessDetail, params});
};

// 保存并提交流程
export const saveAndCreateProcess = (params) => {
  return defHttp.post({
    url: Api.saveAndCreateProcess,
    params,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

// 重新提交时，查询责任部门对应EIP角色的接口
export const getEipRoleByProcessId = (params) => {
  return defHttp.post({
    url: Api.getEipRoleByProcessId + '?processId=' + params.processId,
  })
}

// 审批
export const auditProcess = (params) => {
  return defHttp.post({
    url: Api.auditProcess,
    params,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}
// 人员验证
export const validPeople = (params) => {
  return defHttp.post({
    url: Api.validPeople,
    params,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

/**
 * 筛选导入人员是否在管理员名下
 */
export const classPeople = (params) => {
  return defHttp.post({url: Api.classPeople, params});
};
/**
 * 授权配置查询
 */
export const buildConfigQuery = (params) => {
  return defHttp.post({url: Api.buildConfigQuery, params});
};
