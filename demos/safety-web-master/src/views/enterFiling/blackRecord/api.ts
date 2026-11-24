import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
const { createConfirm } = useMessage();
export enum Api {
  list = '/entry/safety/entryBlackRecord/pageTokenList', // 备案类型配置
  export = '/entry/safety/entryBlackRecord/export',
}

export const list = (params) => {
  return defHttp.get({ url: Api.list, params });
};
