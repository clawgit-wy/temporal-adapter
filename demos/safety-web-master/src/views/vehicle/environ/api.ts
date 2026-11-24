import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { ContentTypeEnum } from '/@/enums/httpEnum';
const { createConfirm } = useMessage();
export enum Api {
  // list = '/sys/message/sysMessageTemplate/list',table/getDemoList
  // list = '/mock/table/getDemoList',safety/black/list
  list = '/enter/protect/selectEnterProtectPage', // 环保车辆列表
  exportXls = '/enter/protect/exportEnterProtect', // 导出
  dlt = '/enter/protect/deleteEnterProtect', // 删除
  ftList = '/enter/protect/getSearchParams', // 下拉框选择数据
}

/* 获取列表数据 */
export const list = (params) => {
  return defHttp.post({ url: Api.list, params, headers: { 'Content-Type': ContentTypeEnum.JSON } });
};
/* 导出 */
export const exportXls = Api.exportXls;
/* 删除 */
export const dlt = (params, fdInFactory, confirm = false) => {
  return new Promise((resolve, reject) => {
    const doDelete = () => {
      resolve(defHttp.post({ url: Api.dlt, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } }, { joinParamsToUrl: true }));
    };
    if (fdInFactory == '是') {
      createConfirm({
        iconType: 'warning',
        content: '车辆当前在厂区，不可删除',
        closable: true,
        // onOk: () => doDelete(),
        // onCancel: () => reject(),
      });
    } else if (confirm) {
      createConfirm({
        iconType: 'warning',
        title: '删除',
        content: '确定要删除吗？',
        onOk: () => doDelete(),
        onCancel: () => reject(),
      });
    } else {
      doDelete();
    }
  });
};

/*下拉框选择数据*/
export const ftList = (params) => {
  return defHttp.post({ url: Api.ftList, params });
};
