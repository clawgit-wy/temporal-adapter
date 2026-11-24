import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  // list = '/sys/message/sysMessageTemplate/list',table/getDemoList
  // list = '/mock/table/getDemoList',safety/black/list
  list = '/safety/role/selectRoleList',
  save = '/safety/role/insertSysRole', // 添加
  delete = '/safety/role/deleteSysRole', // 删除
  edit = '/safety/role/updateSysRole', // 编辑
  detail = '/safety/role/getRoleHasPerms', // 详情/
  tree = '/safety/tree/getZTreeRoot', // 获取菜单树
  setPerm = '/safety/role/setRolePerms', // 设置权限
  exportXls = '/safety/role/export', // 导出
  importXls = '/safety/role/import', // 导入
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
 * 获取权限列表
 * @param params
 *
 */
export const getTree = (params) => {
  return defHttp.post({ url: Api.tree, params });
};

/**
 * 设置权限
 * @param params
 *
 */
export const setPerm = (params) => {
  return defHttp.post({ url: Api.setPerm, params });
};

/**
 * 获取详情
 * @param params
 *
 */
export const detail = (params) => {
  return defHttp.post({ url: Api.detail, params });
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
