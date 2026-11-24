import { defHttp } from '/@/utils/http/axios';
export enum Api {
  list = '/safety/goodsOut/valid/getPageList', // 列表
  exportXls = '/safety/patrol/record/export', // 导出
  update = '/safety/goodsOut/valid/updateDetails',
  fieldManageTree = '/safety/field/fieldChildTree',
  getGoodsDetail = '/safety/goodsOut/valid/getDetailInfoAll'
}

/**
 * 列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};


/**
 * 更新
 * @param params
 * 
 */
export const update = (params) =>{
  return defHttp.post({ url: Api.update, params })
};
/**
 * 字段查询
 */

export const fieldManageTree = (params) =>{
  return defHttp.post({ url: Api.fieldManageTree, params })
};
/**
 * 查询获取明细
 */
export const getGoodsDetail = (params) =>{
  return defHttp.post({ url: Api.getGoodsDetail, params })
};