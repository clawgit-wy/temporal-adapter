import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';
export enum Api {
  list = '/entry/safety/companyRegister/pageTokenList', // 列表查询
  delete = '/entry/safety/companyRegister/delete', // 删除
  approve = '/entry/safety/companyRegister/saveAndUpdate', // 审批通过
  export = '/entry/safety/companyRegister/export', // 导出
}

// 列表查询
export const list = (params) => {
  return defHttp.post({ url: Api.list, params });
};

// 删除
export const deleteRecord = (params) => {
  return defHttp.post({ url: Api.delete, params, headers: { 'Content-Type': ContentTypeEnum.JSON } });
};

// 审批通过/驳回
export const approveOrReject = (params) => {
  return defHttp.post({
    url: Api.approve,
    params,
    headers: {
      'Content-Type': ContentTypeEnum.JSON,
    },
  });
};
