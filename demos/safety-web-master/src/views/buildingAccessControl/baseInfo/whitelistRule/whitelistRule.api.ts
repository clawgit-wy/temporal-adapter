import { defHttp } from '/@/utils/http/axios';

enum Api {
  WhitelistRuleList = '/build/safety/buildWhiteRule/pageList',
  AddWhitelistRule = '/build/safety/buildWhiteRule/add',
  DeleteWhitelistRule = '/build/safety/buildWhiteRule/deleteBatch',
}

/**
 * 获取白名单规则列表
 * @param params 
 */
export function getWhitelistRuleList(params) {
  return defHttp.post({ url: Api.WhitelistRuleList, params });
}

/**
 * 新增白名单规则
 * @param params 
 */
export function addWhitelistRule(params) {
  return defHttp.post({ url: Api.AddWhitelistRule, params });
}

/**
 * 删除白名单规则
 * @param id 
 */
export function deleteWhitelistRule(id: string) {
  return defHttp.post({ url: Api.DeleteWhitelistRule, params: {ids: id} });

}


