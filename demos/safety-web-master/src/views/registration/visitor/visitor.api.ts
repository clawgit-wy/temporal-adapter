import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  // list = '/sys/message/sysMessageTemplate/list',table/getDemoList
  // list = '/mock/table/getDemoList',safety/black/list
  list = '/safety/visitor/list',
  save = '/safety/visitor/add', // 添加
  edit = '/safety/visitor/update', // 编辑
  detail = '/safety/visitor/view', // 详情
  factoryList = '/safety/field/list', //获取厂区列表
  viewIdCar = '/safety/visitor/viewIdCar', // 根据身份证号获取数据
  viewHairpinNo = '/safety/visitor/viewHairpinNo', // 根绝卡号获取数据
  uploadFile = '/safety/visitor/picUpload', // 上传文件
  filedList = '/safety/field/list', // 列表接口
  sheetList = '/safety/sheet/list',
  getCardFactoryList = '/safety/field/getCardFactoryList',
  roleSend = '/safety/visitor/sendPermissionByPerson',
  roleCancel = '/safety/visitor/cancelPermissionByPerson',
  openOrClose = '/safety/openClose/setOpenStatus',
  getOenOrCloseStaus = '/safety/openClose/getOpenInfo',
  sendPermissionByPersonList = '/safety/visitor/sendPermissionByPersonList',
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
 * 获取列表
 * @param params
 *
 */
export const getfiledList = (params) => {
  return defHttp.post({ url: Api.filedList, params });
};

/**
 * 获取列表
 * @param params
 *
 */
export const getSheetList = (params) => {
  return defHttp.post({ url: Api.sheetList, params });
}


/**
 * 获取列表
 * @param params
 *
 */
export const viewIdCar = (params) => {
  return defHttp.post({ url: Api.viewIdCar, params });
};

export const viewHairpinNo = (params) => {
  return defHttp.post({ url: Api.viewHairpinNo, params });
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
 * 获取厂区列表
 * @param params
 *
 */
export const factoryList = (params) => {
  return defHttp.post({ url: Api.factoryList, params });
};

/**
 * 添加人员
 */
export const saveOrUpdate = (params, isUpdate) => {
  if (unref(isUpdate)) {
    return defHttp.post({ url: Api.edit, params });
  } else {
    return defHttp.post({ url: Api.save, params });
  }
};

export const uploadFile = (params, success) => {
  return defHttp.uploadFile({ url: Api.uploadFile }, params, { success });
};

export const getCardFactoryList = (params) => {
  return defHttp.get({url: Api.getCardFactoryList, params})
}
export const roleSend = (params) => {
  return new Promise((resolve, reject) => {
    const doDelete = () => {
      resolve(defHttp.post({ url: Api.roleSend, params }));
    };
      createConfirm({
        iconType: 'warning',
        title: '权限下发',
        content: '确定要权限下发吗？',
        onOk: () => doDelete(),
        onCancel: () => reject(),
      });
  });
};
export const roleCancel = (params) => {
  return new Promise((resolve, reject) => {
    const doDelete = () => {
      resolve(defHttp.post({ url: Api.roleCancel, params }));
    };
      createConfirm({
        iconType: 'warning',
        title: '注销权限',
        content: '确定要注销权限吗？',
        onOk: () => doDelete(),
        onCancel: () => reject(),
      });
  });
};

export const openOrClose = (params) => {
  return defHttp.get({url: Api.openOrClose, params})
}
export const sendPermissionByPersonList = (params) => {
  return defHttp.get({url: Api.sendPermissionByPersonList, params})
}
export const getOenOrCloseStaus = (params) => {
  return defHttp.get({url: Api.getOenOrCloseStaus, params})
}
