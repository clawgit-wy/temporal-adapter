import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { ContentTypeEnum } from '/@/enums/httpEnum';

const { createConfirm } = useMessage();

export enum Api {
  // list = '/sys/message/sysMessageTemplate/list',table/getDemoList
  // list = '/mock/table/getDemoList',safety/black/list
  list = '/enter/filing/selectEnterFilingHistoryPage', // 历史车辆备案列表
  exportXls = '/enter/filing/exportEnterFilingHistory', // 导出历史车辆备案
  ftList = '/enter/filing/getSearchParams', // 备案厂区
  fieldTree = '/safety/field/fieldChildTree', // 项目树
}

/**
 * 获取备案厂区
 * @param params
 *
 */
export const ftList = (params) => {
  return defHttp.post({ url: Api.ftList, params });
};
/* 获取历史车辆备案列表数据 */
export const list = (params) => {
  return defHttp.post({ url: Api.list, params, headers: { 'Content-Type': ContentTypeEnum.JSON } });
};
// 获取车辆类型
export const fieldTree = () => {
  return defHttp.post({ url: Api.fieldTree, params: {fieldType: 'temporaryTransMana_cllx_0', topFlag: false} });
}
/* 导出 */
export const exportXls = Api.exportXls;
