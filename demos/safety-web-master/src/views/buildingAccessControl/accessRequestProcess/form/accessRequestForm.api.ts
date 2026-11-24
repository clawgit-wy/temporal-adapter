import { unref } from 'vue';
import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

export enum Api {
  list = '/build/access/request/list',
  save = '/build/access/request/add',
  edit = '/build/access/request/edit',
  delete = '/build/access/request/delete',
  cancel = '/build/access/request/cancel',
  approve = '/build/access/request/approve',
  reject = '/build/access/request/reject',
  submit = '/build/access/request/submit',
  detail = '/build/access/request/detail',
  exportXls = '/build/access/request/export',
  listWithSecret = '/safety/buildPower/listWithSecret', //根据门禁编码查询详情信息
  listWithSecretBatch = '/safety/buildPower/listWithSecretBatch', //根据门禁编码查询详情信息
  getBuildByEmployee = '/build/safety/employeePower/getBuildByEmployee', //获取员工已授权门禁
  getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo', // 根据工号获取人员信息
  saveAndCreateProcess = '/build/safety/buildProcess/saveAndCreateProcess', // 保存并提交流程
  showProcessDetail = '/build/safety/buildProcess/showProcessDetail', // 获取流程详情
  viewProcessDetail = '/build/safety/buildProcess/viewProcessDetail', // 获取流程详情

  getBuildByEmployeeForDiy = '/build/safety/employeePower/getBuildByEmployeeForDiy',//查询员工信息定制接口（门禁，部门门禁管理员）
  auditProcess = '/build/safety/buildProcess/auditProcess',//楼宇门禁申请流程-审批流程（员工和非员工共用）
  partPassProcess = '/build/safety/buildProcess/auditPartProcess',//楼宇门禁申请流程-部分通过
  getProcessCurDealHandler = '/build/safety/buildProcess/getProcessCurDealHandler'//楼宇门禁-获取流程当前节点信息
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
 * 根据门禁编码查询详情信息
 */
export const listWithSecret = (deviceCode) => {
  return defHttp.post({ url: Api.listWithSecret, params: { deviceCode } });
};

/**
 * 根据门禁编码查询详情信息
 */
export const listWithSecretBatch = (deviceCodes) => {
  return defHttp.post({ url: Api.listWithSecretBatch, params: { deviceCodes} });
};

/**
 * 获取员工已授权门禁
 */
export const getBuildByEmployee = (employeeCode) => {
  return defHttp.post({ url: Api.getBuildByEmployee, params: { employeeCode } });
};


// 获取申请人数据
export const getEmployeeByWorkNo = (params) => {
  return defHttp.post({ url: Api.getEmployeeByWorkNo, params })
}


// 保存并提交流程
export const saveAndCreateProcess = (params) => {
  return defHttp.post({ 
    url: Api.saveAndCreateProcess, 
    params,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

// 获取流程详情
export const viewProcessDetail = (id,isManage) => {
  return defHttp.post({
    url: isManage ? Api.showProcessDetail : Api.showProcessDetail,
    params: {
      processId: id
    },
    
  })
}


export const getBuildByEmployeeForDiy = (employeeCode) => {
  return defHttp.post({ url: Api.getBuildByEmployeeForDiy, params: { employeeCode } });
}


// 保存并提交流程
export const auditProcess = (params) => {
  return defHttp.post({ 
    url: Api.auditProcess, 
    params,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

// 保存并提交流程
export const partPassProcess = (params) => {
  return defHttp.post({
    url: Api.partPassProcess,
    params,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

// 获取流程当前节点信息
export const getProcessCurDealHandler = (processId) => {
  return defHttp.post({ url: Api.getProcessCurDealHandler, params: { processId } })
}
