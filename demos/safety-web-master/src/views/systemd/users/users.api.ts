import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  // list = '/sys/message/sysMessageTemplate/list',table/getDemoList
  // list = '/mock/table/getDemoList',safety/black/list
  list = '/safety/user/list',
  save = '/safety/user/insertSysUser', // 添加
  delete = '/safety/user/deleteSysUserById', // 删除
  exportXls = '/safety/user/export', // 导出
  edit = '/safety/user/updateSysUserById', // 编辑
  importXls = '/safety/user/import', // 导入
  roles = '/safety/role/selectSysRoles', //获取角色列表
  download = '/safety/user/download', // 下载
  factoryList = '/safety/field/list', //获取厂区列表
}

/**
 * 获取列表
 * @param params
 *
 */
export const list = (params) => {
  return defHttp.post({ url: Api.list, params });
};

/**
 * 获取厂区列表
 * @param params
 *
 */
export const factoryList = (params) => {
  return defHttp.post({ url: Api.factoryList, params });
};

/**
 * 下载模板
 * @param params
 * 
 */
export const download = (params) =>{
  return defHttp.get({ url: Api.download, responseType: 'blob', params })
};

/**
 * 获取厂区列表
 * @param params
 *
 */
export const getRoles = (params) => {
  return defHttp.post({ url: Api.roles, params });
};

/**
 * 单个删除
 * @param params
 * @param confirm
 */
export const deleteSingle = (params, confirm = false) => {
  return new Promise((resolve, reject) => {
    const doDelete = () => {
      resolve(defHttp.post({ url: Api.delete, params }));
    };
    if (confirm) {
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

/**
 * 导出
 * @param params
 *
 */
export const exportXls = Api.exportXls;

export const importXls = Api.importXls;

/**
 * 添加
 */
export const saveOrUpdate = (params, isUpdate) => {
  if (unref(isUpdate)) {
    return defHttp.put({ url: Api.edit, params });
  } else {
    return defHttp.post({ url: Api.save, params });
  }
};
