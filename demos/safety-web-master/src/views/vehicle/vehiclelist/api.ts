import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';
import { ContentTypeEnum } from '/@/enums/httpEnum';
const { createConfirm, createMessage } = useMessage();
// const { closeCurrent, refreshPage } = useTabs();
export enum Api {
  list = '/enter/filing/selectEnterFilingPage', // 车辆备案列表
  ftList = '/enter/filing/getSearchParams', // 获取查询条件数据
  delete = '/enter/filing/deleteEnterFiling', // 删除
  exportXls = '/enter/filing/exportEnterFiling', // 导出
  importXls = '/enter/filing/importEnterFiling', // 导入
  download = '/enter/filing/download', // 下载
  facList = '/enter/filing/selectFilingFactory', // 备案厂区
  approve = '/enter/filing/approveEnterFiling', // 审批
  detail = '/enter/filing/selectEnterFilingInfo',
  auths = '/enter/auth/selectEnterAuth',
  fcList = '/enter/auth/selectAuthFactory', // 审核可选厂区
  batchApproval = '/enter/filing/approveEnterFilingBatch', // 批量审批
  getEnterProtectByCarNo = '/enter/protect/getEnterProtectByCarNo', // 车牌查询车辆信息
  selectEnterRegisterByPhone = '/enter/register/selectEnterRegisterByPhone', // 手机号查询入厂人员信息
  factoryList = '/safety/field/selectLinkageList', //获取厂区列表
  checkAuthEmpInfo = '/enter/auth/checkAuthEmpInfo', // 获取当前人员信息
  insertEnterFiling = '/enter/filing/insertEnterFiling', // 添加备案
  updateEnterFiling = '/enter/filing/updateEnterFiling', // 更新备案
  verifyBeforeApprove = '/enter/filing/verifyBeforeApprove', // 审批前校验
  fieldTree = '/safety/field/fieldChildTree', // 项目树
}
/* 批量审批 */
export const batchApproval = (params) => {
  return defHttp.post({ url: Api.batchApproval, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } });
};

/* 审核可选厂区*/
export const fcList = (params) => {
  return defHttp.post({ url: Api.fcList, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } });
};

/* 详情 */
export const detail = (params) => {
  return defHttp.post({ url: Api.detail, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } });
};

/* 查询可操作权限 */
export const auths = (params) => {
  return defHttp.post({ url: Api.auths, params, headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED } });
};

/**
 * 获取备案厂区
 * @param params
 *
 */
export const facList = (params) => {
  return defHttp.post({ url: Api.facList, params });
};
/* 获取车辆备案列表数据 */
export const list = (params) => {
  return defHttp.post({ url: Api.list, params, headers: { 'Content-Type': ContentTypeEnum.JSON } });
};
/* 导出 */
export const exportXls = Api.exportXls;
/* 导入 */

export const importXls = Api.importXls;
/* 获取查询条件数据 */
export const ftList = (params) => {
  return defHttp.post({ url: Api.ftList, params, headers: { 'Content-Type': ContentTypeEnum.JSON } });
};


/**
 * 下载列表
 * @param params
 *
 */
export const download = (params: {}) => {
  return defHttp.get({ url: Api.download, responseType: 'blob', params });
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
/* 审批 */
export const approve = (params, e, closeApprove, router) => {
  return new Promise((resolve, reject) => {
    const doApprove = () => {
      resolve(defHttp.post({ url: Api.approve, params, headers: { 'Content-Type': ContentTypeEnum.JSON } }));
    };
    if (e == 2) {
      createConfirm({
        iconType: 'warning',
        title: '驳回',
        content: '确定要驳回吗？',
        onOk: () => doApprove(),
        onCancel: () => reject(),
      });
    } else if (e == 3) {
      createConfirm({
        iconType: 'warning',
        title: '废弃',
        content: '确定要废弃吗？',
        onOk: () => doApprove(),
        onCancel: () => reject(),
      });
    } else {
      doApprove();
    }
  })
    .then(() => {
      console.log('处理成功返回列表页');
      //  审核通过返回列表页方法
      createMessage.success('审核通过', 3);
      closeApprove();
    })
    .catch((error) => {
      console.log(error, '错误');
    });
};

// /**
//  * 批量删除
//  * @param params
//  * @param confirm
//  */
// export const deleteBatch = (params, confirm = false) => {
//   return new Promise((resolve, reject) => {
//     const doDelete = () => {
//       resolve(defHttp.delete({ url: Api.deleteBatch, params }, { joinParamsToUrl: true }));
//     };
//     if (confirm) {
//       createConfirm({
//         iconType: 'warning',
//         title: '删除',
//         content: '确定要删除吗？',
//         onOk: () => doDelete(),
//         onCancel: () => reject(),
//       });
//     } else {
//       doDelete();
//     }
//   });
// };


// 查询车辆信息
export const getEnterProtectByCarNo = (params) => {
  return defHttp.get({ url: Api.getEnterProtectByCarNo, params });
};

// 手机哈查询入厂人员信息
export const selectEnterRegisterByPhone = (params) => {
  return defHttp.get({ url: Api.selectEnterRegisterByPhone, params });
};

/**
 * 获取厂区列表
 * @param params
 *
 */
export const factoryList = (params={}) => {
  return defHttp.post({ url: Api.factoryList, params });
};

// 获取当前人员信息
export const checkAuthEmpInfo = (params={}) => {
  return defHttp.post({ url: Api.checkAuthEmpInfo, params });
};
// 添加备案
export const insertEnterFiling = (params) => {
  return defHttp.post({ url: Api.insertEnterFiling, params, headers: { 'Content-Type': ContentTypeEnum.JSON }  });
}
// 更新备案
export const updateEnterFiling = (params) => {
  return defHttp.post({ url: Api.updateEnterFiling, params, headers: { 'Content-Type': ContentTypeEnum.JSON }  });
}
// 审批前校验
export const verifyBeforeApprove = (params) => {
  return defHttp.post({ url: Api.verifyBeforeApprove, params, headers: { 'Content-Type': ContentTypeEnum.JSON }  });
}
// 获取车辆类型
export const fieldTree = () => {
  return defHttp.post({ url: Api.fieldTree, params: {fieldType: 'temporaryTransMana_cllx_0', topFlag: false} });
}