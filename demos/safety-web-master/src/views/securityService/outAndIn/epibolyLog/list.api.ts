import { message } from 'ant-design-vue';
import { defHttp } from '/@/utils/http/axios';


export enum Api {
  list = '/safety/cooperative/outsiderPromiseProcess/findList',
}

/**
 * 获取列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};
/**
 * 获取列表无权限限制
 * @param params
 * 
 */
