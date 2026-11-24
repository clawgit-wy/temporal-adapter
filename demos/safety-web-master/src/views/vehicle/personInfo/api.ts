import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { ContentTypeEnum } from '/@/enums/httpEnum';
const { createConfirm } = useMessage();

export enum Api {
  // list = '/sys/message/sysMessageTemplate/list',table/getDemoList
  // list = '/mock/table/getDemoList',safety/black/list
  list = '/enter/register/selectEnterRegisterPage', // 分页列表
  exportXls = '/enter/register/exportEnterRegister', // 导出
  update = '/enter/register/updateEnterRegister', // 编辑修改
  dlt = '/enter/register/deleteEnterRegister', // 删除
  upload = '/pub/file/picUpload', // 权限
  detail = '/enter/auth/selectEnterAuthInfo', // 详情
}

/* 获取列表数据 */
export const list = (params) => {
  return defHttp.post({ url: Api.list, params, headers: { 'Content-Type': ContentTypeEnum.JSON } });
};
/* 导出 */
export const exportXls = Api.exportXls;
/*  新增*/
export const addUpdate = (params, isUpdate) => {
  if (unref(isUpdate)) {
    return defHttp.post({ url: Api.update, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } });
  }
  //   else {
  //     return defHttp.post({ url: Api.add, params, headers: { 'Content-Type': ContentTypeEnum.JSON } });
  //   }
};
/* 删除 */
// export const dlt = (params) => {
//   return defHttp.post({ url: Api.dlt, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } });
// };
export const dlt = (params, fdInFactory, confirm = false) => {
  return new Promise((resolve, reject) => {
    const doDelete = () => {
      resolve(defHttp.post({ url: Api.dlt, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } }, { joinParamsToUrl: true }));
    };
    if (fdInFactory == '是') {
      createConfirm({
        iconType: 'warning',
        content: '用户当前在厂区，不可删除',
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

/* 详情 */
export const detail = (params) => {
  return defHttp.post({ url: Api.detail, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } });
};
/* 图片上传 */
export const upload = (params) => {
  return defHttp.post({ url: Api.detail, params, headers: { 'Content-Type': ContentTypeEnum.FORM_DATA } });
};
/* 详情 */
export const deleteItem = (params) => {
  return defHttp.post({ url: Api.dlt, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } }, { joinParamsToUrl: false });
};
