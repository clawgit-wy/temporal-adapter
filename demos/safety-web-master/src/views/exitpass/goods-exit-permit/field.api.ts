import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';
import { message } from 'ant-design-vue';

export enum Api {
  fieldTree = '/safety/field/fieldChildTree', // 项目树
  view = '/safety/goodsOut/process/getDetailById',
  // 获取当前节点
  getNode = '/safety/process/getNowNode',
  // 审批
  audit = '/safety/spot/process/audit',
  employeeList = '/safety/employee/list', // 获取人员列表
  getApproverList = '/safety/process/getApproverList', // 获取当前审批人
  modifyCurDealHandler = '/safety/goodsOut/process/modifyCurDealHandler', // 更新流程审批人
  getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo', // 根据工号获取人员信息
  getPersonByCondition = '/safety/goodsOut/auth/getPersonByCondition', // 获取审批人列表信息
  saveAndCreateProcess = '/safety/goodsOut/process/saveAndCreateProcess', // 保存并创建流程
  getFactoryList = '/safety/field/selectLinkageListNewName', // 获取工厂列表
  auditProcess = '/safety/goodsOut/process/auditProcess', // 审批流程
  updateTransfer = '/safety/goodsOut/process/updateTransfer', // 转派流程
  circulateProcess='/safety/goodsOut/process/circulateProcess', // 流程传阅 
  importDetailExcel= '/safety/goodsOut/process/importDetailExcel', // 导入excel，返回数据明细
  circulate = '/safety/goodsOut/process/circulateProcess', //  传阅
  getProcessId = '/safety/process/getProcessId',
  getRecentSelect = '/safety/goodsOut/auth/getRecentSelect',
  collectPerson = '/safety/goodsOut/auth/collectPerson', // 收藏
  download = '/safety/goodsOut/process/download', // 下载
  selectLinkageListNew = '/safety/field/selectLinkageListNew', // 出厂厂区
  downloadCarExcel = '/safety/goodsOut/process/downloadCarExcel', // 模版下载
  getGoodsTemplateFile = '/safety/goodsOut/process/getGoodsTemplateFile', // 获取附件列表
  downGoodsTemplateFile = '/safety/goodsOut/process/downGoodsTemplateFile', // 下载指定附件

}

export const findPerson = (params = {}) => {
  return new Promise((resolve, reject) => {
    defHttp.post({url: Api.getPersonByCondition, params}).then(res => {
      resolve(res)
    })
  })
}
/**
 * 下载模板
 * @param params
 * 
 */
export const download = (params) =>{
  return defHttp.get({ url: Api.download, responseType: 'blob', params })
};

/**
 * 下载模板
 * @param params
 * 
 */
export const downloadCarExcel = (params) =>{
  return defHttp.get({ url: Api.downloadCarExcel, responseType: 'blob', params })
};

// 保存并创建流程
export const getFactoryList = (params) => {
  return defHttp.post({ url: Api.getFactoryList, params })
}; 
// 最近选择
export const getRecentSelect = (params) => {
  return defHttp.post({ url: Api.getRecentSelect, params })
}; 
// 收藏
export const collectPerson = (params) => {
  return defHttp.post({ url: Api.collectPerson, params })
}; 
// 获取项目字典
export const fieldTree = (params) => {
  return defHttp.post({ url: Api.fieldTree, params })
};
// 获取表单详情
export const view = (params) => {
  return defHttp.post({ url: Api.view, params })
}
// 获取当前审批节点
export const getNode = (params) => {
  return defHttp.get({ url: Api.getNode, params })
}

// 审批
export const audit = (params) => {
  return defHttp.get({ url: Api.audit, params })
}

// 获取人员列表
export const employeeList = (params) => {
  return defHttp.get({ url: Api.employeeList, params })
}

// 获取申请人数据
export const getEmployeeByWorkNo = (params) => {
  return defHttp.post({ url: Api.getEmployeeByWorkNo, params })
}

// 获取审核人列表
export const getPersonByCondition = (params) => {
  return defHttp.post({ url: Api.getPersonByCondition, params })
}
// 获取当前审批人
export const getApproverList = (params) => {
  return defHttp.get({ url: Api.getApproverList, params })
}
// 审批
export const updateAndAudit = (params) => {
  return defHttp.post({ url: Api.auditProcess, params })

}

// 转派
export const updateTransfer = (params) => {
  return defHttp.post({ url: Api.updateTransfer, params })

}
// 更新
export const modifyCurDealHandler = (params) => {
  return defHttp.post({ url: Api.modifyCurDealHandler, params })
}

// 保存并创建流程
export const saveAndCreateProcess = (params) => {
  debugger
  return defHttp.post({ url: Api.saveAndCreateProcess, params, headers: { 'Content-Type': ContentTypeEnum.JSON } })
};
// 传阅
export const circulate = (params) => {
  return defHttp.post({url: Api.circulate, params})
}
// 获取processId
export const getProcessId = (params) => {
  return defHttp.get({url: Api.getProcessId, params})
}
// 获取processId
export const selectLinkageListNew = () => {
  return defHttp.get({url: Api.selectLinkageListNew})
}
// 获取附件列表
export const getGoodsTemplateFile = (params={}) => {
  return defHttp.get({url: Api.getGoodsTemplateFile, params})
}

// 下载附件
export const downGoodsTemplateFile = (params={}) => {
  debugger
    return defHttp.get({url: Api.downGoodsTemplateFile, params, responseType: 'blob'})
}