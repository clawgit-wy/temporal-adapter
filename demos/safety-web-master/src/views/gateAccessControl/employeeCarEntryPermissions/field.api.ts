import { defHttp } from '/@/utils/http/axios';

export enum Api {
  tableList = '/park/safety/parkEmpPowerReport/pageList', // 获取列表
  viewDetail = '/park/safety/parkEmpPowerReport/viewDetail', // 查看
  cancelPower = '/park/safety/parkEmpPowerReport/cancelPower', // 注销
  fieldChildTree = '/safety/field/fieldChildTree', // 岗位验证下查询子项目单独接口
  selectGroupTree = '/safety/group/selectGroupTreeNonAsyn', // 获取组织架构
  exportData = '/park/safety/parkEmpPowerReport/export', // 获取组织架构
}

// 获取列表
export const tableList = (params) => {
  return defHttp.post({ url: Api.tableList, params })
}
// 查看
export const viewDetail = (params) => {
  return defHttp.post({ url: Api.viewDetail, params })
}
// 查看
export const cancelPower = (params) => {
  return defHttp.post({ url: Api.cancelPower, params })
}

export const fieldChildTree = (params) =>{
  return defHttp.post({ url: Api.fieldChildTree, params })
};

// 获取组织架构
export const selectGroupTree = (params = {}) => {
  return defHttp.post({url: Api.selectGroupTree, params}).then((response) => {
    // 处理返回的数据，确保每个节点的 key 和 value 是一致的
    const formattedData = formatTreeData(response.dataList || []);
    return {dataList: formattedData}; // 返回格式化后的数据
  });
};

// 格式化树形数据
const formatTreeData = (data) => {
  return data.map((node) => ({
    key: node.key,
    value: node.key, // 确保 value 与 key 一致
    title: node.title,
    children: node.children ? formatTreeData(node.children) : undefined,
  }));
};