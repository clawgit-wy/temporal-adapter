import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();
export enum Api {
  list = '/safety/entry/entryConfig/pageTokenList', // 备案类型配置
  fieldChildTree = '/safety/field/fieldChildTree', // 岗位验证下查询子项目单独接口
  getDictEnterType = '/safety/entry/entryConfig/getDictEnterType', // 备案类型
  checkAuthEmpInfo = '/enter/auth/checkAuthEmpInfoNew', // 获取登录人备案厂区及授权时间
  batchUpdate = '/safety/entry/entryConfig/batchUpdate', // 批量修改
  batchAudit = '/safety/entry/entryConfig/batchAudit', // 批量审批
  batchExtension = '/safety/entry/entryConfig/batchExtension', // 批量续期
  exportInfoUrl = '/safety/entry/entryConfig/export',//导出入厂备案人员信息表
  save = '/safety/entry/entryConfig/save',//新增、编辑
  delete = '/safety/entry/entryConfig/logoutPermission',//删除
  importDataRecord = '/safety/entry/entryConfig/importData', // 导入人员信息表
  downloadTemplate = '/safety/entry/entryConfig/download', // 下载模板
  exportFailUrl='/safety/entry/entryConfig/exportFailData',//导出失败表
  updateEntryMethod='/safety/entry/entryConfig/updateEntryMethod',//入厂方式
  getEntryMethod='/safety/entry/entryConfig/getEntryMethod'//获取入厂方式
}

export const list = (params) => {
  return defHttp.post({ url: Api.list, params });
};

export const fieldChildTree = (params) =>{
  return defHttp.post({ url: Api.fieldChildTree, params })
};
export const getDictEnterType = (params) =>{
  return defHttp.post({ url: Api.getDictEnterType, params })
};
export const checkAuthEmpInfo = (params) =>{
  return defHttp.post({ url: Api.checkAuthEmpInfo, params })
};
export const batchUpdate = (params) =>{
  return defHttp.post({ url: Api.batchUpdate, params })
};
export const batchAudit = (params) =>{
  return defHttp.post({ url: Api.batchAudit, params })
};
export const batchExtension = (params) =>{
  return defHttp.post({ url: Api.batchExtension, params })
};
export const deleteItem = (params) =>{
  return defHttp.post({ url: Api.delete, params ,headers: {
    'Content-Type': 'application/json'
  }})
};
export const updateEntryMethod = (params) =>{
  return defHttp.post({ url: Api.updateEntryMethod, params ,headers: {
    'Content-Type': 'application/json'
  }})
};
export const getEntryMethod = (params) =>{
  return defHttp.post({ url: Api.getEntryMethod, params ,headers: {
    'Content-Type': 'application/json'
  }})
};


/**
 * 添加或更新人员
 */
export const saveOrUpdate = (params) => {
  // 新增和编辑都使用同一个接口
  return defHttp.post({ url: Api.save, params });
};
