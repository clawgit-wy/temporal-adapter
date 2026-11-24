import { defHttp } from '/@/utils/http/axios';
import { QueryParams } from './personAccessDetail.data';

/**
 * API路径配置
 */
export enum Api {
  list = '/build/safety/employeePower/list',
  exportXls = '/build/safety/employeePower/exportList',
  selectGroupTree = '/safety/group/selectGroupTreeNonAsyn', // 获取组织架构
  
}

/**
 * 获取人员门禁授权明细列表
 */
export function listPersonAccessDetail(params: QueryParams) {
  return defHttp.post({ url: Api.list, params });
} 

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