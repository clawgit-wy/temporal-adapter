import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/safety/dict/systemParam/listPage', // 系统参数-分页查询
  update = '/safety/dict/systemParam/update', // 系统参数-修改
  save = '/safety/dict/systemParam/add', // 系统参数-新增
  delete='/safety/dict/systemParam/batchDelete',
}

/**
 * 出门证权限管理-分页查询
 * @param params
 */
export const getListPage = (params) => {
  console.log('请求列表', params);
  return defHttp.get({ url: Api.list, params }, { successMessageMode: 'none' });
};

/**
 * 保存
 * @param params
 */
export const save = (params) => {
  return defHttp.post({ url: Api.save, params });
};

/**
 * 更新
 * @param params
 */
export const update = (params) => {
  return defHttp.post({ url: Api.update, params });
};
/**
 * 删除
 */
export const deleteItems = (params, handleSuccess) => {
  return defHttp.post({ url: Api.delete, params }).then(() => {
    handleSuccess();
  });
};
