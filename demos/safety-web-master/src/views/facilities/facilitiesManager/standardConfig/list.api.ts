import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/safety/spot/rule/listPage',
  save = '/safety/spot/rule/add', // 新增
  update = '/safety/spot/rule/update', //更新
  deletes = '/safety/spot/rule/batchDelete', // 批量删除
  delete = '/safety/spot/rule/delete', // 删除
  download = '/safety/spot/rule/download', // 下载模板
  exportXls = '/safety/spot/rule/export', // 导出
  importXls = '/safety/spot/rule/import', // 导入
  fieldTree = '/safety/field/fieldChildTree', // 项目树
  updateRequire = '/safety/spot/rule/updateRequire',

  groupTree= '/safety/group/selectGroupTree', // 获取组织架构
  getEmpPost = '/safety/empPost/getPost', // 获取岗位列表
}

/**
 * 获取列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};
// 获取项目字典
export const fieldTree = (params) =>{
  return defHttp.post({ url: Api.fieldTree, params })
};
/**
 * 添加人员
 */
export const saveOrUpdate = (params, isUpdate) => {
  if (isUpdate) {
    return defHttp.post({ url: Api.update, params });
  }
  return defHttp.post({url: Api.save, params})
};

export const remove = (id, confirm=false) => {

  return new Promise((resolve, reject) => {
    const doDelete = () => {
      if (Array.isArray(id)) {
        resolve(defHttp.post({ url: Api.deletes, params: {ids: id.join()} }));
      }else{
        resolve(defHttp.post({ url: Api.delete, params: {id} }));
      }
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
// 获取组织架构
export const getGroupTree = (key:string) => {
  return defHttp.post({url: Api.groupTree, params: {key}})
}
// 获取岗位列表
export const getEmpPost = () => {
  return defHttp.get({url: Api.getEmpPost})
}
export const download = (params: {}) =>{
  return defHttp.get({ url: Api.download, responseType: 'blob', params })
};

// 列表数据更新接口
export const updateRequire = (params: {}) => {
  return defHttp.post({ url: Api.updateRequire, params })

}