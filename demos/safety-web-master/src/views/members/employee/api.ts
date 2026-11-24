import { unref } from 'vue';
import { defHttp, httpConfig } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/employee/list',
  updateRemark = '/safety/employee/batchUpdateRemark',
  groupTree= '/safety/group/selectGroupTree', // 获取组织架构
  dropDownBox = '/safety/employee/dropDownBox', // 获取职类、职种下拉框数据
  saveDropDownBox = '/safety/employee/saveDropDownBox', // 同步岗位等筛选项

  save = '/safety/black/add', // 添加人员
  delete = '/safety/black/delete', // 删除人员
  exportXls = '/safety/employee/export', // 导出人员
  edit = '/safety/black/update', // 编辑人员
  download = '/safety/black/download', // 下载
  tasksWebMessage = '/safety/black/tasksWebMessage', // 拉取任务
}

/**
 * 获取黑名单列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};
// 更新备注
export const updateRemark = (params) => {
  return defHttp.post({url: Api.updateRemark, data: params },)
}

/**
 * 黑名单导出
 * @param params
 * 
 */
 export const exportXls = Api.exportXls;
//  export const exportXls = (params) =>{
//   return defHttp.post({ url: Api.exportXls, params })
// } ;


// 获取人员岗位
export const getEmpPost = () => {
  return defHttp.post({ url: '/safety/empPost/getPost'})
}
// 获取组织架构
export const getGroupTree = (key:string) => {
  return defHttp.post({url: Api.groupTree, params: {key}})
}

// 获取职类、职种下拉框数据
export const getDropDownBox = () => {
  return defHttp.get({ url: Api.dropDownBox })
}

// 同步岗位等筛选项
export const saveDropDownBox = () => {
  return defHttp.post({ url: Api.saveDropDownBox });
};
