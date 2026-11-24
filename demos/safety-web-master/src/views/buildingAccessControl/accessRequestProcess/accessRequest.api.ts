import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/build/safety/buildProcess/getPageList', // 获取门禁申请列表
  checkBuildRoleType = '/build/safety/buildProcess/checkBuildRoleType', // 根据工号获取人员楼宇门禁角色
  exportXls = '/build/safety/buildProcess/exportList', // 导出
  viewProcessMain = '/build/safety/buildProcess/viewProcessMain', // 中间页面
}

// 获取申请人数据
export const checkBuildRoleType = (params) => {
  return defHttp.post({ url: Api.checkBuildRoleType, params })
}
/**
 * 获取门禁申请列表
 * @param params
 */
export const list = (params) => {
  return defHttp.post({ url: Api.list, params });
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
        content: '确定要删除该申请吗？',
        onOk: () => doDelete(),
        onCancel: () => reject(),
      });
    } else {
      doDelete();
    }
  });
};

/**
 * 取消申请
 * @param params
 * @param confirm
 */
export const cancelRequest = (params, confirm = false) => {
  return new Promise((resolve, reject) => {
    const doCancel = () => {
      resolve(defHttp.post({ url: Api.cancel, params }));
    };
    if (confirm) {
      createConfirm({
        iconType: 'warning',
        title: '取消申请',
        content: '确定要取消该申请吗？',
        onOk: () => doCancel(),
        onCancel: () => reject(),
      });
    } else {
      doCancel();
    }
  });
};

/**
 * 提交申请
 * @param params
 */
export const submitRequest = (params) => {
  return defHttp.post({ url: Api.submit, params });
};

/**
 * 审批申请
 * @param params
 */
export const approveRequest = (params) => {
  return defHttp.post({ url: Api.approve, params });
};

/**
 * 驳回申请
 * @param params
 */
export const rejectRequest = (params) => {
  return defHttp.post({ url: Api.reject, params });
};

/**
 * 获取申请详情
 * @param params
 */
export const getRequestDetail = (params) => {
  return defHttp.get({ url: Api.detail, params });
};

/**
 * 添加或更新门禁申请
 */
export const saveOrUpdate = (params, isUpdate) => {
  if (unref(isUpdate)) {
    return defHttp.post({ url: Api.edit, params });
  } else {
    return defHttp.post({ url: Api.save, params });
  }
}; 


/**
 * 获取中间页面获取流程的详情
 * @param params
 */
export const viewProcessMain = (id) => {
  return defHttp.post({ 
    url: Api.viewProcessMain, 
    params: {
      processId: id
    },
    
  })
}
