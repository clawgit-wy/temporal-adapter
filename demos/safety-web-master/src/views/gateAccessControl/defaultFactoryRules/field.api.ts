import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  tableList = '/park/safety/parkPowerRule/list', // 获取列表
  update='/park/safety/parkPowerRule/saveOrUpdate',
  delete='/park/safety/parkPowerRule/deleteBatch',
  fieldChildTree = '/safety/field/fieldChildTree', // 岗位验证下查询子项目单独接口
}

// 获取列表
export const tableList = (params) => {
  return defHttp.post({ url: Api.tableList, params })
}

/**
 * 更新
 * @param params
 */
export const update = (params) => {
  return defHttp.post({ url: Api.update, params });
};
/**
 * 更新
 * @param params
 */
export const deleteItem = (params) => {
  return defHttp.post({ url: Api.delete, params });
};


export const fieldChildTree = (params) =>{
  return defHttp.post({ url: Api.fieldChildTree, params })
};